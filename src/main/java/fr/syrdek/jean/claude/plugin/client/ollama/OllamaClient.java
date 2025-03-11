/**
 * 
 */
package fr.syrdek.jean.claude.plugin.client.ollama;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.google.gson.Gson;

import fr.syrdek.jean.claude.plugin.client.LlmClient;
import fr.syrdek.jean.claude.plugin.client.LlmErrorListener;
import fr.syrdek.jean.claude.plugin.client.LlmHistoryChangeListener;
import fr.syrdek.jean.claude.plugin.client.RuntimeHttpException;
import fr.syrdek.jean.claude.plugin.client.ollama.OllamaMessage.OllamaRole;
import fr.syrdek.jean.claude.plugin.config.JeanClaudeConfig;

/**
 * Client interrogeant un backend ollama.
 */
public class OllamaClient implements LlmClient {

  private final Gson gson = new Gson();
  private final HttpClient client = HttpClient.newBuilder().build();
  private volatile boolean stop = false;

  private BlockingQueue<OllamaRequest> questionsQueue = new LinkedBlockingQueue<OllamaRequest>();
  private Thread backgroundWorker = null;

  private List<OllamaMessage> history = new ArrayList<OllamaMessage>();
  private LlmHistoryChangeListener historyChangeListener;
  private LlmErrorListener errorsListener;
  private JeanClaudeConfig config;

  /**
   * Creates a client calling the specified url.
   * 
   * @param config The current configuration.
   */
  public OllamaClient(final JeanClaudeConfig config) {
    System.out.println("Building Ollama client with URL: " + config.url + " and model: " + config.defaultModel);
    this.config = config;
    start();
  }

  @Override
  public void clearHistory() {
    history.clear();
  }

  @Override
  public void ask(final String msg) {
    ask(msg, config.defaultModel);
  }

  @Override
  public void ask(final String msg, final String model) {
    history.add(new OllamaMessage(msg));
    notifyChange();

    questionsQueue.offer(new OllamaRequest(model, Collections.unmodifiableList(history)));
  }

  @Override
  public synchronized void start() {
    if (backgroundWorker == null) {
      this.backgroundWorker = new Thread(this::processQueuedRequests);
      this.backgroundWorker.setDaemon(true);
      this.backgroundWorker.start();
      this.stop = false;
    }
  }

  @Override
  public synchronized void stop() {
    if (backgroundWorker != null) {
      this.stop = true;
      backgroundWorker.interrupt();
    }
  }

  /**
   * Process all requests in the queue.
   */
  private void processQueuedRequests() {
    try {
      while (!stop) {
        final OllamaRequest request = questionsQueue.poll(1, TimeUnit.SECONDS);
        if (request == null) {
          continue;
        }

        recordAssistantResponse("@@Waiting@@" + config.waitMessage);

        try {
          Stream<String> aggregatedStream = aggregatedStream(
              postRequest(gson.toJson(request))
                  .body()
                  // Convertit l'objet en OllamaResponse.
                  .map((String txt) -> gson.fromJson(txt, OllamaResponse.class))
                  .filter(Objects::nonNull)
                  // Récupère le contenu du message.
                  .map((OllamaResponse r) -> r.message.content));

          if (config.hideReflection) {
            aggregatedStream = filterThinkingModel(
                aggregatedStream,
                "@@Waiting@@" + config.reflectionMessage);
          }

          aggregatedStream.forEachOrdered(this::recordAssistantResponse);
        } catch (RuntimeException e) {
          e.printStackTrace();
          final StringBuilder messageBuilder = new StringBuilder();
          messageBuilder.append(e.toString());

          Throwable t = e.getCause();
          while (t != null) {
            messageBuilder.append("\nCaused by ").append(t.toString());
            t = t.getCause();
          }
          notifyError(messageBuilder.toString());
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    stop = false;
  }

  /**
   * Registers the assistant response.
   * 
   * @param chatResponse The response send by the assistant.
   * @return The current history.
   */
  private synchronized List<OllamaMessage> recordAssistantResponse(String chatResponse) {
    if (history.isEmpty()) {
      history.add(new OllamaMessage(OllamaRole.ASSISTANT, chatResponse));
      notifyChange();
      return history;
    }

    final OllamaMessage lastMessage = history.get(history.size() - 1);
    if (OllamaRole.ASSISTANT.equals(lastMessage.role)) {
      lastMessage.content = chatResponse;
    } else {
      history.add(new OllamaMessage(OllamaRole.ASSISTANT, chatResponse));
    }

    notifyChange();
    return history;
  }

  /**
   * Notifies clients that an error occurred.
   * 
   * @param error The error message.
   */
  private void notifyError(final String error) {
    if (this.errorsListener != null) {
      this.errorsListener.onError(error);
    }
  }

  /**
   * Notifies clients that the conversation history changed.
   */
  private void notifyChange() {
    if (this.historyChangeListener != null) {
      this.historyChangeListener.onChange(Collections.unmodifiableList(history));
    }
  }

  /**
   * Sends a request to Ollama.
   * 
   * @param json The json to send.
   * @return A stream of LLM responses.
   */
  private HttpResponse<Stream<String>> postRequest(final String json) {
    try {
      System.out.println("Ollama request to " + config.url + "/api/chat : " + json);
      final HttpRequest request = HttpRequest.newBuilder(URI.create(config.url + "/api/chat"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(json))
          .build();
      return checkResponse(client.send(request, BodyHandlers.ofLines()));
    } catch (final IOException e) {
      throw new RuntimeHttpException(e);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeHttpException(e);
    }
  }

  @Override
  public void setHistoryListener(LlmHistoryChangeListener onChange) {
    this.historyChangeListener = onChange;
  }

  @Override
  public void setErrorListener(LlmErrorListener onError) {
    this.errorsListener = onError;
  }

  public static void main(String args[]) throws Throwable {
    final JeanClaudeConfig config = new JeanClaudeConfig();
    config.url = "http://localhost:11434";
    config.defaultModel = "deepseek-r1:14b";

    final OllamaClient client = new OllamaClient(config);
    final Gson gson = new Gson();
    client.setErrorListener((String error) -> {
      System.out.println("ERROR: " + error);
    });
    client.setHistoryListener((List<OllamaMessage> chatHistory) -> {
      System.out.println(gson.toJson(chatHistory));
    });
    client.ask("Write a python code that tells if a number given in parameter is prime");
    client.ask("Write the same code in java");

    Thread.sleep(10000l);
  }
}

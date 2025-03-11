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

/**
 * Client interrogeant un backend ollama.
 */
public class OllamaClient implements LlmClient {

  private final Gson gson = new Gson();
  private final HttpClient client = HttpClient.newBuilder().build();
  private volatile boolean stop = false;
  private final String defaultModel;

  private BlockingQueue<OllamaRequest> questionsQueue = new LinkedBlockingQueue<OllamaRequest>();
  private Thread backgroundWorker = null;

  private List<OllamaMessage> history = new ArrayList<OllamaMessage>();
  private LlmHistoryChangeListener historyChangeListener;
  private LlmErrorListener errorsListener;
  private String url;

  /**
   * Creates a client calling the specified url.
   * 
   * @param url          The URL to call.
   * @param defaultModel
   */
  public OllamaClient(final String url, String defaultModel) {
    System.out.println("Building Ollama client with URL: " + url + " and model: " + defaultModel);
    this.defaultModel = defaultModel;
    this.url = url;
    start();
  }

  @Override
  public void clearHistory() {
    history.clear();
  }

  @Override
  public void ask(final String msg) {
    ask(msg, this.defaultModel);
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

        recordAssistantResponse("@@Waiting@@Sending request, and waiting far aknowledgement...");

        try {
          filterThinkingModel(
              aggregatedStream(
                  postRequest(gson.toJson(request))
                      .body()
                      // Convertit l'objet en OllamaResponse.
                      .map((String txt) -> gson.fromJson(txt, OllamaResponse.class))
                      .filter(Objects::nonNull)
                      // Récupère le contenu du message.
                      .map((OllamaResponse r) -> r.message.content)),
              "@@Waiting@@Request received. I'm thinking about it...")
                  .forEachOrdered(this::recordAssistantResponse);
        } catch (RuntimeException e) {
          e.printStackTrace();
          notifyError(e.getMessage());
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
      System.out.println("Ollama request to " + url + "/api/chat : " + json);
      final HttpRequest request = HttpRequest.newBuilder(URI.create(url + "/api/chat"))
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
    final OllamaClient client = new OllamaClient("http://localhost:11434", "deepseek-r1:14b");
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

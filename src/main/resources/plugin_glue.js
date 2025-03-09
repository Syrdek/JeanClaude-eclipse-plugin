var simulate_jc_change = function(content) {
	let history = jc.history;
	if (history.length > 0) {
		history[history.length-1]["content"] = content;
		jc.setChat(history);
		return true;
	}
	return false;
}

var plugin_copy = function(text) {
	navigator.clipboard.writeText(text);
}

var plugin_clear = function() {
	jc.clear();
}

var plugin_tell = function(text) {
	jc.appendChat({"role":"user", "content":text})
	setTimeout(function(){
		jc.appendChat({"role":"assistant", "content":"@@waiting@@I'm reading you"})
		setTimeout(function(){
			if (simulate_jc_change("@@waiting@@I think about it")) {
				setTimeout(function(){
					simulate_jc_change(`Bonjour mon ami, comment allez-vous ?\n
Un petit exemple de code :
\`\`\`java
private List<OllamaMessage> history = new ArrayList<OllamaMessage>();
private String url;
\`\`\`

Un moyen exemple de code :
\`\`\`java
public OllamaClient(final String url) {
  this.url = url;
}
\`\`\`

Un gros exemple de code :
\`\`\`java
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
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.google.gson.Gson;

import fr.syrdek.jean.claude.plugin.client.LlmClient;
import fr.syrdek.jean.claude.plugin.client.RuntimeHttpException;
import fr.syrdek.jean.claude.plugin.client.ollama.OllamaMessage.OllamaRole;

/**
 * Client interrogeant un backend ollama.
 */
public class OllamaClient implements LlmClient {

  private static final Gson GSON = new Gson();
  private final HttpClient client = HttpClient.newBuilder().build();
  private final String ollamaModel = "deepseek-r1:14b";

  private List<OllamaMessage> history = new ArrayList<OllamaMessage>();
  private String url;

  public OllamaClient(final String url) {
    this.url = url;
  }

  public void clearHistory() {
    history.clear();
  }

  public CompletableFuture<Stream<List<OllamaMessage>>> predict(final String msg) {
    history.add(new OllamaMessage(msg));

    final OllamaRequest request = new OllamaRequest(ollamaModel, history);
    return new CompletableFuture<HttpResponse<Stream<String>>>()
        .completeAsync(() -> postRequest(GSON.toJson(request)))
        .thenApply((HttpResponse<Stream<String>> r) -> filterThinkingModel(
            aggregatedStream(
                r.body()
                    // Convert json text to OllamaResponse
                    .map((String txt) -> GSON.fromJson(txt, OllamaResponse.class))
                    // Takes response's message
                    .map((OllamaResponse rsp) -> rsp.message)
                    // Ignores null
                    .filter(Objects::nonNull)
                    // Take the massage content
                    .map((OllamaMessage m) -> m.content)
                    // Ignores null
                    .filter(Objects::nonNull)),
            "@@Waiting@@Request received. I'm thinking about it...")
                // Saves the respone in history
                .map(this::recordAssistantResponse));
  }

  private List<OllamaMessage> recordAssistantResponse(String chatResponse) {
    if (history.isEmpty()) {
      history.add(new OllamaMessage(OllamaRole.ASSISTANT, chatResponse));
      return history;
    }

    final OllamaMessage lastMessage = history.get(history.size() - 1);
    if (OllamaRole.ASSISTANT.equals(lastMessage.role)) {
      lastMessage.content = chatResponse;
    } else {
      history.add(new OllamaMessage(OllamaRole.ASSISTANT, chatResponse));
    }

    return history;
  }

  private HttpResponse<Stream<String>> postRequest(final String json) {
    try {
      System.out.println("REQ /run/predict : " + json);
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

  public static void main(String args[]) throws Throwable {
    final OllamaClient client = new OllamaClient("http://localhost:11434");
    client.predict("Write a python code that tells if a number given in parameter is prime")
        .get()
        .forEach((List<OllamaMessage> l) -> System.out.println(GSON.toJson(l)));
    client.predict("Write the same code in java")
        .get()
        .forEach((List<OllamaMessage> l) -> System.out.println(GSON.toJson(l)));
  }
}

\`\`\`

J'espere que cela vous convient...
`);
				}, 2000);
			}
		}, 3000);
	}, 100);
}
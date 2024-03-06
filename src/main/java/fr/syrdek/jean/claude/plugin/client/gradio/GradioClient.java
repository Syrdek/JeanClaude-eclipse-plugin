/**
 *
 */
package fr.syrdek.jean.claude.plugin.client.gradio;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.google.gson.Gson;

/**
 *
 */
public class GradioClient {

  private final HttpClient client = HttpClient.newBuilder().build();
  private final Gson gson = new Gson();

  private final String sessionHash;
  private final String url;

  public static final String generateSessionhash(final int size) {
    final String alphanumeric = "abcdefghijklmnopqrstuvwxyz0123456789";
    final Random rand = new Random();
    final StringBuilder sb = new StringBuilder(size);
    for (int i = 0; i < size; i++) {
      sb.append(alphanumeric.charAt(rand.nextInt(alphanumeric.length())));
    }
    return sb.toString();
  }

  public GradioClient(final String url) {
    sessionHash = generateSessionhash(10);
    this.url = url;
  }

  public CompletableFuture<Stream<PredictEvent>> predict(final String msg) {
    final CompletableFuture<HttpResponse<String>> cf = new CompletableFuture<>();
    return cf.completeAsync(
        // D'abord un predict avec le message.
        () -> postPredict(generatePredictJson(msg, 1)))
        .thenApplyAsync(
            // Suivi 'un predict null.
            (HttpResponse<String> r) -> postPredict(generatePredictJson(null, 2)))
        .thenApplyAsync(
            // Suivi d'un join.
            (HttpResponse<String> r) -> postJoin(generatePredictJson(null, 3)))
        .thenApplyAsync(
            // Suivi d'un data.
            (HttpResponse<String> r) -> postData())
        .thenApply((HttpResponse<Stream<String>> r) -> r.body()
            .filter((String evt) -> evt.startsWith("data:"))
            .map((String evt) -> evt.substring(5).trim())
            .map((String evt) -> gson.fromJson(evt, PredictEvent.class)));
  }

  private String generatePredictJson(final String msg, final int fnIndex) {
    final PredictRequest request = new PredictRequest();
    request.setSessionHash(sessionHash);
    request.setFnIndex(fnIndex);
    if (msg != null) {
      request.getData().add(msg);
    } else {
      request.getData().add(null);
      request.getData().add(null);
    }
    return gson.toJson(request);
  }

  private HttpResponse<String> postPredict(final String json) {
    try {
      System.out.println("REQ /run/predict : " + json);
      final HttpRequest request = HttpRequest.newBuilder(URI.create(url + "/run/predict"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(json))
          .build();
      return checkResponse(client.send(request, BodyHandlers.ofString()));
    } catch (final IOException e) {
      throw new RuntimeHttpException(e);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeHttpException(e);
    }
  }

  private HttpResponse<String> postJoin(final String json) {
    try {
      System.out.println("REQ /queue/join : " + json);
      final HttpRequest request = HttpRequest.newBuilder(URI.create(url + "/queue/join"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(json))
          .build();
      return checkResponse(client.send(request, BodyHandlers.ofString()));

    } catch (final IOException e) {
      throw new RuntimeHttpException(e);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeHttpException(e);
    }
  }

  private HttpResponse<Stream<String>> postData() {
    try {
      System.out.println("REQ /queue/data : " + sessionHash);
      final HttpRequest request = HttpRequest.newBuilder(URI.create(url + "/queue/data?session_hash=" + sessionHash))
          .GET()
          .build();
      return checkResponse(client.send(request, BodyHandlers.ofLines()));
    } catch (final IOException e) {
      throw new RuntimeHttpException(e);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeHttpException(e);
    }
  }

  private <T> HttpResponse<T> checkResponse(final HttpResponse<T> response) {
    if (response.statusCode() > 299) {
      throw new RuntimeHttpException(response);
    }
    return response;
  }

  public static void main(String args[]) throws Throwable {
    final GradioClient gradioClient = new GradioClient("http://localhost:7890");
    final Gson gson = new Gson();
    gradioClient.predict("Write a python code that tells if a number given in parameter is prime")
        .get()
        .map(PredictEvent::getGeneratedResponse)
        .forEach((String s) -> System.out.println(s));
    gradioClient.predict("Write the same code n java")
        .get()
        .map(PredictEvent::getGeneratedResponse)
        .forEach((String s) -> System.out.println(s));
  }
}

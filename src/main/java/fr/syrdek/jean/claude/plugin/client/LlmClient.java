package fr.syrdek.jean.claude.plugin.client;

import java.net.http.HttpResponse;
import java.util.stream.Stream;

/**
 * An LLM common interface.
 * 
 * @author cedric
 */
public interface LlmClient {

  /**
   * Listens for chat history changes.
   * 
   * @param onChange The listener to register.
   */
  void setHistoryListener(final LlmHistoryChangeListener onChange);

  /**
   * Listens for LLM errors.
   * 
   * @param onError The listener to register.
   */
  void setErrorListener(final LlmErrorListener onError);

  /**
   * Asks for a chat prediction. This action should trigger the historyChangeListener registered.
   * 
   * @param msg The message to send.
   */
  void ask(String msg);

  /**
   * Asks for a chat prediction. This action should trigger the historyChangeListener registered.
   * 
   * @param msg   The message to send.
   * @param model The name of a specific model ti use for this request.
   */
  void ask(String msg, String model);

  /**
   * Clears conversation history. This action should trigger the historyChangeListener registered.
   */
  void clearHistory();

  /**
   * Starts any background processing.
   */
  void start();

  /**
   * Gracefully stops any background processing.
   */
  void stop();

  /**
   * Permet de vérifier que la réponse n'est pas une erreur.
   * 
   * @param <T>      Le type de réponse reçu.
   * @param response La réponse à vérifier.
   * @return La réponse vérifiée.
   * @throws RuntimeHttpException Si la requête HTTP a échoué.
   */
  default <T> HttpResponse<T> checkResponse(final HttpResponse<T> response) {
    if (response.statusCode() > 299) {
      throw new RuntimeHttpException(response);
    }
    return response;
  }

  /**
   * Retourne un flux texte aggrégé.<br>
   * Par exemple, si le flux en entrée produit "a", "b", "c", l flux en sortie retourne "a", "ab", "abc".
   * 
   * @param responseStream Le flux texte à traiter.
   * @return Le flux texte aggrégé.
   */
  default Stream<String> aggregatedStream(final Stream<String> responseStream) {
    final StringBuilder builder = new StringBuilder();
    return responseStream
        .map((String str) -> builder.append(str))
        .map(StringBuilder::toString);
  }

  /**
   * Filtre la partie de réflexion d'un modèle type deepseek R1.
   * 
   * @param responseStream      Le flux de réponse aggrégé.
   * @param thinkingPlaceholder Le message à renvoyer si le modèle est en cours de pensée. Peut être nul puis filtré si besoin.
   * @return Le flux de réponse dans lequel la partie réflexion est filtré.
   */
  default Stream<String> filterThinkingModel(final Stream<String> responseStream, final String thinkingPlaceholder) {
    return responseStream.map((String str) -> {
      if (str.startsWith("<think>")) {
        final int idx = str.indexOf("</think>");
        if (idx >= 0) {
          return str.substring(idx + 8);
        }
        return thinkingPlaceholder;
      }
      return str;
    }).distinct();
  }
}
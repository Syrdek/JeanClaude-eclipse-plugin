package fr.syrdek.jean.claude.plugin.client;

/**
 * Listens for LLM errors.
 * 
 * @author cedric
 */
public interface LlmErrorListener {

  /**
   * Notifies that an error occurred.
   * 
   * @param error The error.
   */
  void onError(String error);

}

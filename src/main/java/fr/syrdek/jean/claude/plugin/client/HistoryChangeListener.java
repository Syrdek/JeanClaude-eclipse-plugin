/**
 * @author cedric
 */
package fr.syrdek.jean.claude.plugin.client;

import java.util.List;

import fr.syrdek.jean.claude.plugin.client.ollama.OllamaMessage;

/**
 * Listens to LLM chat history events.
 * 
 * @author cedric
 */
public interface HistoryChangeListener {

  /**
   * Notifies that the chat history changed.
   * 
   * @param chatHistory The new chat history.
   */
  void onChange(List<OllamaMessage> chatHistory);

  /**
   * Notifies that an error occurred.
   * 
   * @param error The error.
   */
  void onError(String error);
}

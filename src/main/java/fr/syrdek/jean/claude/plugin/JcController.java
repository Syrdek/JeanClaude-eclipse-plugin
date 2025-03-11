/**
 * 
 */
package fr.syrdek.jean.claude.plugin;

import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import fr.syrdek.jean.claude.plugin.client.LlmClient;
import fr.syrdek.jean.claude.plugin.client.ollama.OllamaClient;
import fr.syrdek.jean.claude.plugin.client.ollama.OllamaMessage;
import fr.syrdek.jean.claude.plugin.config.JeanClaudeConfig;
import fr.syrdek.jean.claude.plugin.view.ChatView;

/**
 * 
 */
public class JcController {
  private static LlmClient CLIENT;
  static {
    setConfiguration(Activator.getConfiguration());
  }

  public static ChatView activateChatView() {
    try {
      return (ChatView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ChatView.ID);
    } catch (PartInitException e) {
      throw new RuntimeException(e);
    }
  }

  public static void ask(final String predict) {
    CLIENT.ask(predict);
  }

  public static void clear() {
    CLIENT.clearHistory();
  }

  private JcController() {
    super();
  }

  public static void setConfiguration(final JeanClaudeConfig config) {
    if (CLIENT != null) {
      CLIENT.stop();
    }

    CLIENT = new OllamaClient(config.url, config.defaultModel);
    CLIENT.setErrorListener((String error) -> Display.getDefault().asyncExec(() -> {
      activateChatView().setError(error);
    }));
    CLIENT.setHistoryListener((List<OllamaMessage> chatHistory) -> {
      Display.getDefault().asyncExec(() -> {
        activateChatView().setConversationHistory(chatHistory);
      });
    });
    activateChatView().applyTheme(config.theme);
  }
}

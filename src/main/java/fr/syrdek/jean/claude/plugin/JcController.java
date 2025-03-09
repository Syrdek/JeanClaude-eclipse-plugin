/**
 * 
 */
package fr.syrdek.jean.claude.plugin;

import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import fr.syrdek.jean.claude.plugin.client.HistoryChangeListener;
import fr.syrdek.jean.claude.plugin.client.LlmClient;
import fr.syrdek.jean.claude.plugin.client.ollama.OllamaClient;
import fr.syrdek.jean.claude.plugin.client.ollama.OllamaMessage;
import fr.syrdek.jean.claude.plugin.view.ChatView;

/**
 * 
 */
public class JcController {
  private static LlmClient CLIENT;
  static {
    setUrl(Activator.getUrl(), Activator.getType());
  }

  public static ChatView activateChatView() {
    try {
      return (ChatView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ChatView.ID);
    } catch (PartInitException e) {
      throw new RuntimeException(e);
    }
  }

  public static void predict(final String predict) {
    CLIENT.ask(predict);
  }

  public static void clear() {
    CLIENT.clearHistory();
  }

  public static void explainCode(final String code) {
    predict(Activator.getExplainPhrase(code));
  }

  public static void commenterCode(final String code) {
    predict(Activator.getCommentPhrase(code));
  }

  public static void genererTest(final String code) {
    predict(Activator.getTestPhrase(code));
  }

  public static void verifierCode(String code) {
    predict(Activator.getCheckPhrase(code));
  }

  private JcController() {
    super();
  }

  public static void setUrl(String url, String type) {
    if (CLIENT != null) {
      CLIENT.stop();
    }

    CLIENT = new OllamaClient(url);
    CLIENT.setHistoryListener(new HistoryChangeListener() {

      @Override
      public void onError(String error) {
        Display.getDefault().asyncExec(() -> {
          activateChatView().setError(error);
        });
      }

      @Override
      public void onChange(List<OllamaMessage> chatHistory) {
        Display.getDefault().asyncExec(() -> {
          activateChatView().setConversationHistory(chatHistory);
        });
      }
    });
  }
}

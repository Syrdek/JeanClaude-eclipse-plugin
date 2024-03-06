/**
 * 
 */
package fr.syrdek.jean.claude.plugin;

import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import fr.syrdek.jean.claude.plugin.client.gradio.GradioClient;
import fr.syrdek.jean.claude.plugin.client.gradio.PredictEvent;
import fr.syrdek.jean.claude.plugin.view.ChatView;

/**
 * 
 */
public class JcController {
  private static GradioClient CLIENT = new GradioClient(Activator.getUrl());

  public static ChatView activateChatView() {
    try {
      return (ChatView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ChatView.ID);
    } catch (PartInitException e) {
      throw new RuntimeException(e);
    }
  }

  public static void predict(final String predict) {
    final ChatView view = activateChatView();
    view.setWaiting();

    CLIENT.predict(predict)
        .thenAccept((Stream<PredictEvent> stream) -> {
          stream.map(PredictEvent::getGeneratedResponse)
              .filter(Objects::nonNull)
              .forEach((final String mkdown) -> Display.getDefault().asyncExec(() -> {
                view.setMarkdown(mkdown);
              }));
        }).exceptionally((Throwable t) -> {
          view.setError(t.getMessage());
          return null;
        });
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

  public static void setUrl(String url) {
    CLIENT = new GradioClient(url);
  }
}

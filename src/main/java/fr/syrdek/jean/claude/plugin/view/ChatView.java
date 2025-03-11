package fr.syrdek.jean.claude.plugin.view;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;

import com.google.gson.Gson;

import fr.syrdek.jean.claude.plugin.Activator;
import fr.syrdek.jean.claude.plugin.client.ollama.OllamaMessage;

public class ChatView extends ViewPart {

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "fr.syrdek.jean.claude.plugin.view.ChatView";
  private final Gson gson = new Gson();
  private MarkdownViewer markdown;

  @Inject
  IWorkbench workbench;

  @Override
  public void createPartControl(Composite parent) {
    final FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
    parent.setLayout(fillLayout);

    markdown = new MarkdownViewer(parent, SWT.MULTI, Activator.getConfiguration().theme);
  }

  public void setConversationHistory(final List<OllamaMessage> messages) {
    markdown.setConversationHistory(gson.toJson(messages));
  }

  public void applyTheme(final String theme) {
    markdown.applyTheme(theme);
  }

  @Override
  public void setFocus() {
    markdown.getBrowser().setFocus();
  }

  public void setError(String message) {
    markdown.setError(message);
  }
}

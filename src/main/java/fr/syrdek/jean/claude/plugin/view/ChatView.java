package fr.syrdek.jean.claude.plugin.view;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;

public class ChatView extends ViewPart {

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "fr.syrdek.jean.claude.plugin.view.ChatView";

  private MarkdownViewer markdown;

  @Inject
  IWorkbench workbench;

  @Override
  public void createPartControl(Composite parent) {
    final FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
    parent.setLayout(fillLayout);

    markdown = new MarkdownViewer(parent, SWT.MULTI);
  }

  public void setMarkdown(final String text) {
    markdown.setMarkdown(text);
  }

  @Override
  public void setFocus() {
    markdown.getBrowser().setFocus();
  }

  public void setError(String message) {
    markdown.setError(message);
  }

  public void setWaiting() {
    markdown.setWaiting();
  }
}

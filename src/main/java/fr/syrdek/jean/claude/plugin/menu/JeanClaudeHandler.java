package fr.syrdek.jean.claude.plugin.menu;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import fr.syrdek.jean.claude.plugin.JcController;

public class JeanClaudeHandler {

  public ITextSelection getCurrentSelection() {
    IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    if (part instanceof ITextEditor) {
      final ITextEditor editor = (ITextEditor) part;
      IDocumentProvider prov = editor.getDocumentProvider();
      IDocument doc = prov.getDocument(editor.getEditorInput());
      ISelection sel = editor.getSelectionProvider().getSelection();
      if (sel instanceof TextSelection) {
        ITextSelection textSel = (ITextSelection) sel;
        return textSel;
      }
    }
    return null;
  }

  @Execute
  public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell s) throws Exception {
    final ITextSelection currentSelection = getCurrentSelection();
    if (currentSelection != null) {
      JcController.explainCode(currentSelection.getText());
    }
  }
}

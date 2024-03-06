package fr.syrdek.jean.claude.plugin.menu;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.widgets.Shell;

import fr.syrdek.jean.claude.plugin.JcController;

public class TestHandler extends JeanClaudeHandler {

  @Execute
  public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell s) throws Exception {
    final ITextSelection currentSelection = getCurrentSelection();
    if (currentSelection != null) {
      JcController.genererTest(currentSelection.getText());
    }
  }
}

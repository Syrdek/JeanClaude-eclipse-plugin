/**
 * @author cedric
 */
package fr.syrdek.jean.claude.plugin.menu;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.google.gson.Gson;

import fr.syrdek.jean.claude.plugin.Activator;
import fr.syrdek.jean.claude.plugin.JcController;
import fr.syrdek.jean.claude.plugin.config.JeanClaudeAction;
import fr.syrdek.jean.claude.plugin.config.JeanClaudeConfig;
import fr.syrdek.jean.claude.plugin.pref.PreferenceConstants;

/**
 * 
 * @author cedric
 */
public class JeanClaudeDynamicMenu extends ContributionItem {
  private final Gson gson = new Gson();

  public JeanClaudeDynamicMenu() {
  }

  public JeanClaudeDynamicMenu(String id) {
    super(id);
  }

  @Override
  public void fill(Menu menu, int index) {
    final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
    final JeanClaudeConfig config = gson.fromJson(preferenceStore.getString(PreferenceConstants.CONFIG_PARAM), JeanClaudeConfig.class);

    if (config.actions.isEmpty()) {
      return;
    }

    int i = 0;
    for (JeanClaudeAction action : config.actions) {
      final MenuItem menuItem = new MenuItem(menu, SWT.CHECK, i++);
      menuItem.setText(action.title);
      menuItem.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) {
          final ITextSelection currentSelection = getCurrentSelection();
          if (currentSelection != null) {
            JcController.ask(action.template.replace("[SELECTED_TEXT]", currentSelection.getText()));
          }
        }
      });
    }
  }

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
}

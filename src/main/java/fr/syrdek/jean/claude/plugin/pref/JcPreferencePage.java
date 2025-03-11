package fr.syrdek.jean.claude.plugin.pref;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import fr.syrdek.jean.claude.plugin.Activator;
import fr.syrdek.jean.claude.plugin.JcController;

/**
 * This class represents a preference page that is contributed to the Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we can use the field support built
 * into JFace that allows us to create a page that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that belongs to the main plug-in class. That way, preferences can be accessed directly via
 * the preference store.
 */

public class JcPreferencePage
    extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

  public JcPreferencePage() {
    super(GRID);
    setPreferenceStore(Activator.getDefault().getPreferenceStore());
    setDescription("JeanClaude plugin configuration");
  }

  /**
   * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various types of preferences. Each field editor knows how to save and
   * restore itself.
   */
  public void createFieldEditors() {
    addField(
        new StringFieldEditor(PreferenceConstants.URL_PARAM, PreferenceConstants.URL_DESC, getFieldEditorParent()));
    addField(
        new StringFieldEditor(PreferenceConstants.MODEL_PARAM, PreferenceConstants.MODEL_DESC, getFieldEditorParent()));
    addField(
        new StringFieldEditor(PreferenceConstants.THEME_PARAM, PreferenceConstants.THEME_DESC, getFieldEditorParent()));
    addField(
        new JeanClaudeJsonFieldEditor(PreferenceConstants.CONFIG_PARAM, PreferenceConstants.CONFIG_DESC, getFieldEditorParent()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
   */
  public void init(IWorkbench workbench) {
  }

  @Override
  public boolean performOk() {
    JcController.setConfiguration(Activator.getConfiguration());
    return super.performOk();
  }

}
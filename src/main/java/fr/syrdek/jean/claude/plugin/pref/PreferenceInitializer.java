package fr.syrdek.jean.claude.plugin.pref;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import fr.syrdek.jean.claude.plugin.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
   */
  public void initializeDefaultPreferences() {
    final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    store.setDefault(PreferenceConstants.P_URL, PreferenceConstants.DEFAULT_URL);
    store.setDefault(PreferenceConstants.P_TYPE, PreferenceConstants.DEFAULT_TYPE);
    store.setDefault(PreferenceConstants.P_EXPLAIN_TPL, PreferenceConstants.DEFAULT_EXPLAIN_TPL);
    store.setDefault(PreferenceConstants.P_COMMENT_TPL, PreferenceConstants.DEFAULT_COMMENT_TPL);
    store.setDefault(PreferenceConstants.P_TEST_TPL, PreferenceConstants.DEFAULT_TEST_TPL);
    store.setDefault(PreferenceConstants.P_CHECK_TPL, PreferenceConstants.DEFAULT_CHECK_TPL);
  }

}

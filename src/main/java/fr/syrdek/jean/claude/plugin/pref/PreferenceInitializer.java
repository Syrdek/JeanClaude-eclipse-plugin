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
    store.setDefault(PreferenceConstants.URL_PARAM, PreferenceConstants.URL_DEFAULT);
    store.setDefault(PreferenceConstants.MODEL_PARAM, PreferenceConstants.MODEL_DEFAULT);
    store.setDefault(PreferenceConstants.THEME_PARAM, PreferenceConstants.THEME_DEFAULT);
    store.setDefault(PreferenceConstants.CONFIG_PARAM, PreferenceConstants.CONFIG_DEFAULT);
  }

}

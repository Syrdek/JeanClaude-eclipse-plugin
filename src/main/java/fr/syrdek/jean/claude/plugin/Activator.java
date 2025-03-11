/**
 * 
 */
package fr.syrdek.jean.claude.plugin;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.google.gson.Gson;

import fr.syrdek.jean.claude.plugin.config.JeanClaudeConfig;
import fr.syrdek.jean.claude.plugin.pref.PreferenceConstants;

/**
 * 
 */
public class Activator extends AbstractUIPlugin {

  private final static Activator activator = new Activator();

  /**
   * 
   */
  public Activator() {
  }

  public static Activator getDefault() {
    return activator;
  }

  public static JeanClaudeConfig getConfiguration() {
    final IPreferenceStore store = getDefault().getPreferenceStore();
    final JeanClaudeConfig config = new Gson().fromJson(store.getString(PreferenceConstants.CONFIG_PARAM), JeanClaudeConfig.class);
    config.url = store.getString(PreferenceConstants.URL_PARAM);
    config.defaultModel = store.getString(PreferenceConstants.MODEL_PARAM);
    config.theme = store.getString(PreferenceConstants.THEME_PARAM);
    return config;
  }
}

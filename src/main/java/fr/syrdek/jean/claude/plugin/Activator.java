/**
 * 
 */
package fr.syrdek.jean.claude.plugin;

import org.eclipse.ui.plugin.AbstractUIPlugin;

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

  public static String getUrl() {
    return getDefault().getPreferenceStore().getString(PreferenceConstants.P_URL);
  }

  public static String getExplainPhrase(final String code) {
    return getDefault().getPreferenceStore().getString(PreferenceConstants.P_EXPLAIN_TPL).replace("[SELECTED_TEXT]", code);
  }

  public static String getCommentPhrase(final String code) {
    return getDefault().getPreferenceStore().getString(PreferenceConstants.P_COMMENT_TPL).replace("[SELECTED_TEXT]", code);
  }

  public static String getTestPhrase(final String code) {
    return getDefault().getPreferenceStore().getString(PreferenceConstants.P_TEST_TPL).replace("[SELECTED_TEXT]", code);
  }

  public static String getCheckPhrase(final String code) {
    return getDefault().getPreferenceStore().getString(PreferenceConstants.P_CHECK_TPL).replace("[SELECTED_TEXT]", code);
  }
}

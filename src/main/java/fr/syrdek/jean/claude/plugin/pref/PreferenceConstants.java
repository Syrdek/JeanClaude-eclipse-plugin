package fr.syrdek.jean.claude.plugin.pref;

import fr.syrdek.jean.claude.plugin.view.HtmlPackager;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

  public static final String URL_PARAM = "jeanclaudeUrl";
  public static final String URL_DESC = "Ollama backend URL";
  public static final String URL_DEFAULT = "http://localhost:11434";

  public static final String MODEL_PARAM = "jeanclaudeDefaultModel";
  public static final String MODEL_DESC = "Ollama default model";
  public static final String MODEL_DEFAULT = "deepseek-r1";

  public static final String THEME_PARAM = "jeanclaudeTheme";
  public static final String THEME_DESC = "View theme (dark or light)";
  public static final String THEME_DEFAULT = "dark";

  public static final String CONFIG_PARAM = "jeanclaudeConfig";
  public static final String CONFIG_DESC = "Menu configuration";
  public static final String CONFIG_DEFAULT = HtmlPackager.getResourceFileAsString("defaultConfig.json");

}

/**
 * @author cedric
 */
package fr.syrdek.jean.claude.plugin.config;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Action available in Jean-Claude menu.
 * 
 * @author cedric
 */
public class JeanClaudeAction {
  @SerializedName("title")
  public String title;
  @SerializedName("template")
  public String template;
  @SerializedName("model")
  public String model;

  /**
   * @return true if this configuration is valid, false else.
   */
  public boolean isValid() {
    return StringUtils.isNotBlank(title) && StringUtils.isNotBlank(template);
  }
}

package fr.syrdek.jean.claude.plugin.config;

/**
 * 
 * @author cedric
 */

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Handles plugin configuration.
 * 
 * @author cedric
 */
public class JeanClaudeConfig {
  @SerializedName("actions")
  public List<JeanClaudeAction> actions;
  public String url;
  public String defaultModel;
  public String theme;

  /**
   * @return true if the actions in this configuration are valid, false else.
   */
  public boolean isActionsValid() {
    if (actions == null || actions.isEmpty()) {
      return false;
    }

    for (JeanClaudeAction action : actions) {
      if (!action.isValid()) {
        return false;
      }
    }

    return true;
  }
}

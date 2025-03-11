package fr.syrdek.jean.claude.plugin.config;

/**
 * 
 * @author cedric
 */

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Handles plugin configuration.
 * 
 * @author cedric
 */
public class JeanClaudeConfig {
  public static final String DEFAULT_REFLECTION_MESSAGE = "Request received. I'm thinking about it...";
  public static final String DEFAULT_WAIT_MESSAGE = "Sending request, and waiting for acknowledgement...";

  @SerializedName("actions")
  public List<JeanClaudeAction> actions;
  @SerializedName("waitMessage")
  public String waitMessage;
  @SerializedName("reflectionMessage")
  public String reflectionMessage;
  @SerializedName("hideReflection")
  public boolean hideReflection;

  public transient String url;
  public transient String defaultModel;
  public transient String theme;

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

  public static JeanClaudeConfig parse(final String text) {
    final Gson gson = new Gson();
    final JeanClaudeConfig config = gson.fromJson(text, JeanClaudeConfig.class);
    if (StringUtils.isBlank(config.waitMessage)) {
      config.waitMessage = DEFAULT_WAIT_MESSAGE;
    }
    if (StringUtils.isBlank(config.reflectionMessage)) {
      config.reflectionMessage = DEFAULT_REFLECTION_MESSAGE;
    }
    return config;
  }

  public String toString() {
    return new Gson().toJson(this);
  }
}

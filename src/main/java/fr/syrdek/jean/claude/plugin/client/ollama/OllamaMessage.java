/**
 * 
 */
package fr.syrdek.jean.claude.plugin.client.ollama;

import com.google.gson.annotations.SerializedName;

/**
 * Représentation d'une requête de prédiction ollama.
 */
public class OllamaMessage {
  public enum OllamaRole {
    @SerializedName("system")
    SYSTEM,
    @SerializedName("user")
    USER,
    @SerializedName("assistant")
    ASSISTANT,
    @SerializedName("tool")
    TOOL;
  }

  @SerializedName("role")
  public OllamaRole role;
  @SerializedName("content")
  public String content;

  public OllamaMessage(OllamaRole role, String content) {
    super();
    this.role = role;
    this.content = content;
  }

  public OllamaMessage(String content) {
    this(OllamaRole.USER, content);
  }

  public OllamaMessage() {
    super();
  }

  @Override
  public String toString() {
    return "OllamaMessage [role=" + role + ", content=" + content + "]";
  }
}

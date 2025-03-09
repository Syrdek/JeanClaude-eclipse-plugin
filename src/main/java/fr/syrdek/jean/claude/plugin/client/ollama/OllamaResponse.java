/**
 * 
 */
package fr.syrdek.jean.claude.plugin.client.ollama;

import com.google.gson.annotations.SerializedName;

/**
 * Représentation d'une requête de prédiction ollama.
 */
public class OllamaResponse {
  @SerializedName("model")
  public String model;
  @SerializedName("created_at")
  public String createdAt;
  @SerializedName("message")
  public OllamaMessage message;
  @SerializedName("done")
  public boolean done = false;

  @SerializedName("total_duration")
  public long totalDuration;
  @SerializedName("load_duration")
  public long loadDuration;
  @SerializedName("prompt_eval_count")
  public long promptEvalCount;
  @SerializedName("prompt_eval_duration")
  public long promptEvalDuration;
  @SerializedName("eval_count")
  public long evalCount;
  @SerializedName("eval_duration")
  public long evalDuration;

  public OllamaResponse() {
    super();
  }

  @Override
  public String toString() {
    return "OllamaResponse [model=" + model + ", createdAt=" + createdAt + ", message=" + message + ", done=" + done + ", totalDuration=" + totalDuration + ", loadDuration="
        + loadDuration + ", promptEvalCount=" + promptEvalCount + ", promptEvalDuration=" + promptEvalDuration + ", evalCount=" + evalCount + ", evalDuration=" + evalDuration
        + "]";
  }
}

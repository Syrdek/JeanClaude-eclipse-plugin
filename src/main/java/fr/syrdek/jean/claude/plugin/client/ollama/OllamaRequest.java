/**
 * 
 */
package fr.syrdek.jean.claude.plugin.client.ollama;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Représentation d'une requête de prédiction ollama.
 */
public class OllamaRequest {
  @SerializedName("model")
  public String model;
  @SerializedName("messages")
  public List<OllamaMessage> messages = new ArrayList<OllamaMessage>();
  @SerializedName("format")
  public String format;
  public boolean stream = true;

  public OllamaRequest() {
    super();
  }

  public OllamaRequest(String model, String message) {
    this();
    this.model = model;
    this.messages.add(new OllamaMessage(message));
  }

  public OllamaRequest(String model, List<OllamaMessage> messages) {
    this();
    this.model = model;
    this.messages.addAll(messages);
  }

  @Override
  public String toString() {
    return "OllamaRequest [model=" + model + ", messages=" + messages + ", format=" + format + ", stream=" + stream + "]";
  }
}

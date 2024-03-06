package fr.syrdek.jean.claude.plugin.client.gradio;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;

public class PredictEvent {
  @SerializedName("msg")
  private String message;
  @SerializedName("event_id")
  private String eventId;
  private JsonObject output;
  private int rank;
  @SerializedName("eta")
  private double eta;
  @SerializedName("rank_eta")
  private double rankEta;
  @SerializedName("queue_size")
  private int queueSize;
  private boolean success;

  public String getGeneratedResponse() {
    try {
      if (output == null) {
        return null;
      }

      final Object elt = JsonPath.compile("$['data'][0][-1][-1]").read(output.toString());
      if (elt == null) {
        return null;
      }
      return String.valueOf(elt);
    } catch (JsonPathException e) {
      return null;
    }
  }

  /**
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * @param message the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * @return the eventId
   */
  public String getEventId() {
    return eventId;
  }

  /**
   * @param eventId the eventId to set
   */
  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  /**
   * @return the output
   */
  public JsonObject getOutput() {
    return output;
  }

  /**
   * @param output the output to set
   */
  public void setOutput(JsonObject output) {
    this.output = output;
  }

  /**
   * @return the rank
   */
  public int getRank() {
    return rank;
  }

  /**
   * @param rank the rank to set
   */
  public void setRank(int rank) {
    this.rank = rank;
  }

  /**
   * @return the eta
   */
  public double getEta() {
    return eta;
  }

  /**
   * @param eta the eta to set
   */
  public void setEta(double eta) {
    this.eta = eta;
  }

  /**
   * @return the rankEta
   */
  public double getRankEta() {
    return rankEta;
  }

  /**
   * @param rankEta the rankEta to set
   */
  public void setRankEta(double rankEta) {
    this.rankEta = rankEta;
  }

  /**
   * @return the queueSize
   */
  public int getQueueSize() {
    return queueSize;
  }

  /**
   * @param queueSize the queueSize to set
   */
  public void setQueueSize(int queueSize) {
    this.queueSize = queueSize;
  }

  /**
   * @return the success
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * @param success the success to set
   */
  public void setSuccess(boolean success) {
    this.success = success;
  }
}

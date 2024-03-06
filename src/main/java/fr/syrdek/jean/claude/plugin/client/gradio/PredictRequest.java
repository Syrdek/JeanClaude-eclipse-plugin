/**
 * 
 */
package fr.syrdek.jean.claude.plugin.client.gradio;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 */
public class PredictRequest {
  // {data: ["Hello"], event_data: null, fn_index: 1, trigger_id: 7, session_hash: "c7zlek328a"}
  private List<Object> data = new ArrayList<>();
  @SerializedName("event_data")
  private String eventData;
  @SerializedName("fn_index")
  private int fnIndex = 1;
  @SerializedName("trigger_id")
  private int triggerId = 1;
  @SerializedName("session_hash")
  private String sessionHash;

  /**
   * @return the data
   */
  public List<Object> getData() {
    return data;
  }

  /**
   * @param data the data to set
   */
  public void setData(List<Object> data) {
    this.data = data;
  }

  /**
   * @return the eventData
   */
  public String getEventData() {
    return eventData;
  }

  /**
   * @param eventData the eventData to set
   */
  public void setEventData(String eventData) {
    this.eventData = eventData;
  }

  /**
   * @return the fnIndex
   */
  public int getFnIndex() {
    return fnIndex;
  }

  /**
   * @param fnIndex the fnIndex to set
   */
  public void setFnIndex(int fnIndex) {
    this.fnIndex = fnIndex;
  }

  /**
   * @return the triggerId
   */
  public int getTriggerId() {
    return triggerId;
  }

  /**
   * @param triggerId the triggerId to set
   */
  public void setTriggerId(int triggerId) {
    this.triggerId = triggerId;
  }

  /**
   * @return the sessionHash
   */
  public String getSessionHash() {
    return sessionHash;
  }

  /**
   * @param sessionHash the sessionHash to set
   */
  public void setSessionHash(String sessionHash) {
    this.sessionHash = sessionHash;
  }
}

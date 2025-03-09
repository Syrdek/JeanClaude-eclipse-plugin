package fr.syrdek.jean.claude.plugin.client;

import java.net.http.HttpResponse;

public class RuntimeHttpException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public RuntimeHttpException() {
    super();
  }

  public RuntimeHttpException(String message, Throwable cause) {
    super(message, cause);
  }

  public RuntimeHttpException(String message) {
    super(message);
  }

  public RuntimeHttpException(Throwable cause) {
    super(cause);
  }

  public RuntimeHttpException(int code, String message) {
    super(code + " : " + message);
  }

  public RuntimeHttpException(HttpResponse<?> response) {
    this(response.statusCode(), String.valueOf(response.body()));
  }
}

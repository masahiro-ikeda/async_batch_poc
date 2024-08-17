package asyncbatchpoc.application.exception;

/**
 * アプリケーション起因の例外.
 * リトライ不可.
 */
public class ApplicationException extends RuntimeException {

  public ApplicationException(Throwable cause, String message) {
    super(message, cause);
  }
}

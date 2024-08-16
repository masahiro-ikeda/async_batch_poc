package asyncbatchpoc.domain;

/**
 * キューイングメッセージ.
 */
public class CreateKeywordTreeMessage {
  private String messageId;
  private String keyword;
  private int retryCount;

  public static CreateKeywordTreeMessage newMessage(String messageId, String keyword) {
    CreateKeywordTreeMessage createKeywordTreeMessage = new CreateKeywordTreeMessage();
    createKeywordTreeMessage.messageId = messageId;
    createKeywordTreeMessage.keyword = keyword;
    createKeywordTreeMessage.retryCount = 0;
    return createKeywordTreeMessage;
  }

  public String messageId() {
    return messageId;
  }

  public String keyword() {
    return keyword;
  }

  public void retry() {
    this.retryCount++;
  }

  public boolean canRetry() {
    return retryCount < 3; // 3回以上はリトライできない
  }
}

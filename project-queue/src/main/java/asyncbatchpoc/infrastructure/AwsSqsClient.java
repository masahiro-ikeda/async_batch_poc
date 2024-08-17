package asyncbatchpoc.infrastructure;

import asyncbatchpoc.domain.CreateKeywordTreeMessage;
import asyncbatchpoc.domain.MessageQueueClient;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.List;

@Component
public class AwsSqsClient implements MessageQueueClient {

  @Value("${aws.sqs.endpoint}")
  private String endpoint;

  @Value("${aws.sqs.accountId}")
  private String accountId;

  @Value("${aws.sqs.queueName}")
  private String queueName;

  @Value("${aws.sqs.maxNumberOfMessages}")
  private Integer maxNumberOfMessages;

  @Value("${aws.sqs.waitTimeSeconds}")
  private Integer waitTimeSeconds;

  private final SqsClient sqsClient;

  AwsSqsClient(SqsClient sqsClient) {
    this.sqsClient = sqsClient;
  }

  /**
   * メッセージを受信する.
   */
  public List<CreateKeywordTreeMessage> poll() {
    ReceiveMessageRequest receiveMessageRequest =
        ReceiveMessageRequest
            .builder()
            .queueUrl(queueUrl())
            .maxNumberOfMessages(maxNumberOfMessages)
            .waitTimeSeconds(waitTimeSeconds)
            .build();
    List<Message> received = sqsClient.receiveMessage(receiveMessageRequest).messages();

    Gson gson = new Gson();
    List<CreateKeywordTreeMessage> createKeywordTreeMessages = received.stream()
        .map(message -> gson.fromJson(message.body(), CreateKeywordTreeMessage.class))
        .toList();

    // 受信メッセージを削除
    for (Message message : received) {
      DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
          .queueUrl(queueUrl())
          .receiptHandle(message.receiptHandle())
          .build();
      sqsClient.deleteMessage(deleteMessageRequest);
    }

    return createKeywordTreeMessages;
  }

  /**
   * メッセージを送信する（リトライ用）.
   */
  public void push(CreateKeywordTreeMessage message) {
    Gson gson = new Gson();
    SendMessageRequest request =
        SendMessageRequest
            .builder()
            .queueUrl(queueUrl())
            .messageBody(gson.toJson(message))
            .build();

    sqsClient.sendMessage(request);
  }

  private String queueUrl() {
    String format = "%s/%s/%s";
    return String.format(format, endpoint, accountId, queueName);
  }
}

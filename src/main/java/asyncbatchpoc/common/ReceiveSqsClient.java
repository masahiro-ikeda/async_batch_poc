package asyncbatchpoc.common;

import asyncbatchpoc.consumer.batch.ReceiveQueueClient;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.List;

@Component
public class ReceiveSqsClient implements ReceiveQueueClient {

  private final SqsClient sqsClient;

  ReceiveSqsClient(SqsClient sqsClient) {
    this.sqsClient = sqsClient;
  }

  /**
   * メッセージを受信する.
   */
  public List<CreateKeywordTreeMessage> receive() {
    ReceiveMessageRequest receiveMessageRequest =
        ReceiveMessageRequest
            .builder()
            .queueUrl("http://localhost:4566/000000000000/create-keyword-tree-queue")
            .maxNumberOfMessages(10)
            .waitTimeSeconds(5)
            .build();
    List<Message> received = sqsClient.receiveMessage(receiveMessageRequest).messages();

    Gson gson = new Gson();
    List<CreateKeywordTreeMessage> createKeywordTreeMessages = received.stream()
        .map(message -> gson.fromJson(message.body(), CreateKeywordTreeMessage.class))
        .toList();

    // 受信メッセージを削除
    for (Message message : received) {
      DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
          .queueUrl("http://localhost:4566/000000000000/create-keyword-tree-queue")
          .receiptHandle(message.receiptHandle())
          .build();
      sqsClient.deleteMessage(deleteMessageRequest);
    }

    return createKeywordTreeMessages;
  }

  /**
   * メッセージを送信する（リトライ用）.
   */
  public void send(CreateKeywordTreeMessage message) {
    Gson gson = new Gson();
    SendMessageRequest request =
        SendMessageRequest
            .builder()
            .queueUrl("http://localhost:4566/000000000000/create-keyword-tree-queue")
            .messageBody(gson.toJson(message))
            .build();

    sqsClient.sendMessage(request);
  }
}

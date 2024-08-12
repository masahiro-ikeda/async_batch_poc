package asyncbatchpoc.common;

import asyncbatchpoc.publisher.SendQueueClient;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
public class SendSqsClient implements SendQueueClient {

  private final SqsClient sqsClient;

  SendSqsClient(SqsClient sqsClient) {
    this.sqsClient = sqsClient;
  }

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

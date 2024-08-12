package asyncbatchpoc.common;

import asyncbatchpoc.publisher.SendQueueClient;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
public class SendSqsClient implements SendQueueClient {

  @Value("${aws.sqs.endpoint}")
  private String endpoint;

  @Value("${aws.sqs.accountId}")
  private String accountId;

  @Value("${aws.sqs.queueName}")
  private String queueName;

  private final SqsClient sqsClient;

  SendSqsClient(SqsClient sqsClient) {
    this.sqsClient = sqsClient;
  }

  public void send(CreateKeywordTreeMessage message) {
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

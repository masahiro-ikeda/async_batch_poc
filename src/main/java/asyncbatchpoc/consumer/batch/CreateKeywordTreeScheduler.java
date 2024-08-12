package asyncbatchpoc.consumer.batch;

import asyncbatchpoc.common.CreateKeywordTreeMessage;
import asyncbatchpoc.common.ReceiveSqsClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateKeywordTreeScheduler {

  private final ReceiveSqsClient receiveSqsClient;
  private final CreateKeywordTreeHandler createKeywordTreeHandler;

  CreateKeywordTreeScheduler(ReceiveSqsClient receiveSqsClient, CreateKeywordTreeHandler createKeywordTreeHandler) {
    this.receiveSqsClient = receiveSqsClient;
    this.createKeywordTreeHandler = createKeywordTreeHandler;
  }

  @Scheduled(fixedRate = 3000)
  public void execute() {
    List<CreateKeywordTreeMessage> messages = receiveSqsClient.receive();

    for (CreateKeywordTreeMessage message : messages) {
      createKeywordTreeHandler.handle(message);
    }
  }
}

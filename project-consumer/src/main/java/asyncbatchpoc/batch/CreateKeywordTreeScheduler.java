package asyncbatchpoc.batch;

import asyncbatchpoc.domain.CreateKeywordTreeMessage;
import asyncbatchpoc.domain.MessageQueueClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateKeywordTreeScheduler {

  private final MessageQueueClient messageQueueClient;
  private final CreateKeywordTreeHandler createKeywordTreeHandler;

  CreateKeywordTreeScheduler(MessageQueueClient messageQueueClient, CreateKeywordTreeHandler createKeywordTreeHandler) {
    this.messageQueueClient = messageQueueClient;
    this.createKeywordTreeHandler = createKeywordTreeHandler;
  }

  @Scheduled(fixedRate = 3000)
  public void execute() {
    List<CreateKeywordTreeMessage> messages = messageQueueClient.poll();

    for (CreateKeywordTreeMessage message : messages) {
      createKeywordTreeHandler.handle(message);
    }
  }
}

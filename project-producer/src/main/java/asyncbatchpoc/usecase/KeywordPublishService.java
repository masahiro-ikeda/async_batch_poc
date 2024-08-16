package asyncbatchpoc.usecase;


import asyncbatchpoc.domain.CreateKeywordTreeMessage;
import asyncbatchpoc.domain.MessageQueueClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KeywordPublishService {

  private final MessageQueueClient messageQueueClient;

  KeywordPublishService(MessageQueueClient messageQueueClient) {
    this.messageQueueClient = messageQueueClient;
  }

  public void publish(String keyword) {
    CreateKeywordTreeMessage message = CreateKeywordTreeMessage.newMessage(UUID.randomUUID().toString(), keyword);
    messageQueueClient.send(message);
  }
}

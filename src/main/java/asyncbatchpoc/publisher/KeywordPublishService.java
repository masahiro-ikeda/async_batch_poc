package asyncbatchpoc.publisher;

import asyncbatchpoc.common.CreateKeywordTreeMessage;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KeywordPublishService {

  private final SendQueueClient sendQueueClient;

  public KeywordPublishService(SendQueueClient sendQueueClient) {
    this.sendQueueClient = sendQueueClient;
  }

  public void publish(String keyword) {
    CreateKeywordTreeMessage message = CreateKeywordTreeMessage.newMessage(UUID.randomUUID().toString(), keyword);
    sendQueueClient.send(message);
  }
}

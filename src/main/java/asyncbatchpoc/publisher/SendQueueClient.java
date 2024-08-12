package asyncbatchpoc.publisher;

import asyncbatchpoc.common.CreateKeywordTreeMessage;

public interface SendQueueClient {
  void send(CreateKeywordTreeMessage message);
}

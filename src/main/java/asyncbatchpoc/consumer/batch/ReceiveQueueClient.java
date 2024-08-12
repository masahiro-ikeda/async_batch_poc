package asyncbatchpoc.consumer.batch;

import asyncbatchpoc.common.CreateKeywordTreeMessage;

import java.util.List;

public interface ReceiveQueueClient {

  List<CreateKeywordTreeMessage> receive();

  void send(CreateKeywordTreeMessage message);
}

package asyncbatchpoc.domain;

import java.util.List;

public interface MessageQueueClient {

  List<CreateKeywordTreeMessage> receive();

  void send(CreateKeywordTreeMessage message);
}

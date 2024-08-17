package asyncbatchpoc.domain;

import java.util.List;

public interface MessageQueueClient {

  List<CreateKeywordTreeMessage> poll();

  void push(CreateKeywordTreeMessage message);
}

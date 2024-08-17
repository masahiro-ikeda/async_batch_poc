package asyncbatchpoc.batch;

import asyncbatchpoc.application.CreateKeywordTreeService;
import asyncbatchpoc.application.exception.ApplicationException;
import asyncbatchpoc.domain.CreateKeywordTreeMessage;
import asyncbatchpoc.domain.MessageQueueClient;
import com.google.gson.Gson;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class CreateKeywordTreeHandler extends Thread {

  private final CreateKeywordTreeService createKeywordTreeService;
  private final MessageQueueClient messageQueueClient;

  CreateKeywordTreeHandler(CreateKeywordTreeService createKeywordTreeService, MessageQueueClient messageQueueClient) {
    this.createKeywordTreeService = createKeywordTreeService;
    this.messageQueueClient = messageQueueClient;
  }

  @Async
  public void handle(CreateKeywordTreeMessage message) {
    Logger logger = Logger.getLogger("CreateKeywordTreeHandler");

    Gson gson = new Gson();
    logger.info("Start Batch. [" + gson.toJson(message) + "]");

    try {
      createKeywordTreeService.execute(message.keyword());
    } catch (ApplicationException e) {
      // リトライ不可
      String format = "An unrecoverable exception has occurred. message=[%s] cause=[%s]";
      logger.log(Level.SEVERE, String.format(format, gson.toJson(message), e.getMessage()));
    } catch (RuntimeException e) {
      if (message.canRetry()) {
        String format = "Retry. message=[%s] cause=[%s]";
        logger.log(Level.WARNING, String.format(format, gson.toJson(message), e.getMessage()));
        message.retry();
        messageQueueClient.send(message);
      } else {
        String format = "Retry limit exceeded. message=[%s] cause=[%s]";
        logger.log(Level.SEVERE, String.format(format, gson.toJson(message), e.getMessage()));
      }
    }

    logger.info("End Batch. [" + gson.toJson(message) + "]");
  }
}

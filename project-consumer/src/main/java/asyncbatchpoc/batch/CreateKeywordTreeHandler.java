package asyncbatchpoc.batch;

import asyncbatchpoc.domain.CreateKeywordTreeMessage;
import asyncbatchpoc.domain.MessageQueueClient;
import asyncbatchpoc.usecase.CreateKeywordTreeService;
import asyncbatchpoc.usecase.exception.ApplicationException;
import com.google.gson.Gson;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
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
  public CompletableFuture<CreateKeywordTreeMessage> handle(CreateKeywordTreeMessage message) {
    Gson gson = new Gson();
    Logger logger = Logger.getLogger("AsyncBatchHandler");
    logger.info("Start Batch. [" + gson.toJson(message) + "]");

    try {
      createKeywordTreeService.execute(message.keyword());
    } catch (ApplicationException e) {
      // リトライ不可
      String format = "An unrecoverable exception has occurred. message=[%s] cause=[%s]";
      logger.log(Level.SEVERE, String.format(format, gson.toJson(message), e.getMessage()));
      return CompletableFuture.failedFuture(e);
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
      return CompletableFuture.failedFuture(e);
    }

    logger.info("End Batch. [" + gson.toJson(message) + "]");
    return CompletableFuture.completedFuture(message);
  }
}

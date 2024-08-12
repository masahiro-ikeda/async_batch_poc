package asyncbatchpoc.publisher;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KeywordPublishController {

  private final KeywordPublishService keywordPublishService;

  KeywordPublishController(KeywordPublishService keywordPublishService) {
    this.keywordPublishService = keywordPublishService;
  }

  @PostMapping(value = "/keyword")
  public void receive(@Validated @RequestBody KeywordPublishForm form) {
    keywordPublishService.publish(form.keyword);
  }
}

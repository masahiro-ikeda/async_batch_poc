package asyncbatchpoc.presentation;

import asyncbatchpoc.application.KeywordPublishService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KeywordController {

  private final KeywordPublishService keywordPublishService;

  KeywordController(KeywordPublishService keywordPublishService) {
    this.keywordPublishService = keywordPublishService;
  }

  @PostMapping(value = "/keyword")
  public void post(@Validated @RequestBody KeywordForm form) {
    keywordPublishService.publish(form.keyword);
  }
}

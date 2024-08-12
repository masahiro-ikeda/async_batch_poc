package asyncbatchpoc.publisher;

import jakarta.validation.constraints.NotEmpty;

public class KeywordPublishForm {
  @NotEmpty
  public String keyword;
}

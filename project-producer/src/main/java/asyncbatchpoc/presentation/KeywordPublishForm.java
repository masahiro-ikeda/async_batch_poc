package asyncbatchpoc.presentation;

import jakarta.validation.constraints.NotEmpty;

public class KeywordPublishForm {
  @NotEmpty
  public String keyword;
}

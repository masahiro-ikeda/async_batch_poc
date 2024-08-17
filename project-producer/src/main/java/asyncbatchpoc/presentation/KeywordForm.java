package asyncbatchpoc.presentation;

import jakarta.validation.constraints.NotEmpty;

public class KeywordForm {
  @NotEmpty
  public String keyword;
}

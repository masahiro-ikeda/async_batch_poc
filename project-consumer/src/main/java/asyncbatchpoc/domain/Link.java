package asyncbatchpoc.domain;

/**
 * 関連リンク.
 */
public class Link {
  private final String word;
  private final String url;

  public Link(String word, String url) {
    this.word = word;
    this.url = url;
  }

  public String word() {
    return word;
  }
}

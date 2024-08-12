package asyncbatchpoc.consumer.infrastructure;

import asyncbatchpoc.consumer.application.exception.ApplicationException;
import asyncbatchpoc.consumer.domain.Link;
import asyncbatchpoc.consumer.domain.LinkRepository;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class LinkRepositoryImpl implements LinkRepository {

  private static final String WIKIPEDIA_URL_FORMAT = "https://ja.wikipedia.org/wiki/%s";
  private static final String WIKIPEDIA_LINK_REGEX = "^/wiki/.*$";

  /**
   * wikipediaのページにhttpアクセスして関連リンクを取得する.
   */
  @Override
  public List<Link> findByKeyword(String keyword) {
    Logger logger = Logger.getLogger("LinkRepositoryImpl");
    String url = String.format(WIKIPEDIA_URL_FORMAT, keyword);

    Document document;
    try {
      document = Jsoup.connect(url).get();
    } catch (HttpStatusException e) {
      if (e.getStatusCode() == 404) {
        String message = String.format("[%s] は存在しないキーワードです。", keyword);
        throw new ApplicationException(e.getCause(), message);
      } else {
        throw new RuntimeException("キーワードの取得に失敗しました。", e.getCause());
      }
    } catch (IOException e) {
      throw new RuntimeException("キーワードの取得に失敗しました。", e.getCause());
    }

    Element mwParserOutput = document.select("div.mw-parser-output").first();
    // 直下で最初の<p>タグコンテンツを抽出
    Optional<Element> firstParagraph = mwParserOutput.children().stream().filter(e -> e.is("p")).findFirst();
    if (firstParagraph.isEmpty()) {
      return List.of(); // <p>が存在しない場合
    }
    Elements links = firstParagraph.get().select("a[href]");
    // wikipediaリンクのみ抽出
    List<Element> wikipediaLinks = links.stream()
        .filter(link -> link.attr("href").matches(WIKIPEDIA_LINK_REGEX))
        .toList();

    List<Link> result = wikipediaLinks.stream()
        .map(link -> new Link(link.attr("title"), link.attr("href")))
        .toList();

    logger.info(String.format("Keyword=%s, result=[%s]", keyword, String.join(",", result.stream().map(Link::word).toList())));

    return result;
  }
}

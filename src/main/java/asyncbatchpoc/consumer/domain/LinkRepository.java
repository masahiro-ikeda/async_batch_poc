package asyncbatchpoc.consumer.domain;

import java.util.List;

public interface LinkRepository {

  /**
   * 関連リンクを取得する.
   */
  List<Link> findByKeyword(String keyword);
}

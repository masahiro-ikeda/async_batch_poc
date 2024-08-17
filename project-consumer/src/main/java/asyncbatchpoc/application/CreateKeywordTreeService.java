package asyncbatchpoc.application;

import asyncbatchpoc.domain.KeywordTree;
import asyncbatchpoc.domain.KeywordTreeRepository;
import asyncbatchpoc.domain.Link;
import asyncbatchpoc.domain.LinkRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * キーワード探索サービス.
 */
@Service
public class CreateKeywordTreeService {

  /* 探索の回数上限 */
  @Value("${app.searchLimit}")
  private int searchLimit;

  /* 探索の間隔（ミリ秒） */
  @Value("${app.searchInterval}")
  private int searchInterval;

  private final LinkRepository linkRepository;
  private final KeywordTreeRepository keywordTreeRepository;

  public CreateKeywordTreeService(LinkRepository linkRepository, KeywordTreeRepository keywordTreeRepository) {
    this.linkRepository = linkRepository;
    this.keywordTreeRepository = keywordTreeRepository;
  }

  /**
   * キーワード探索を実行する.
   */
  public void execute(String searchWord) {
    KeywordTree keywordTree = KeywordTree.rootNode(searchWord);

    // 幅優先探索で関連キーワードを探索する
    Queue<KeywordTree> searchKeywordQueue = new LinkedList<>();
    searchKeywordQueue.add(keywordTree);
    int count = 0;
    while (!searchKeywordQueue.isEmpty() && count < searchLimit) {
      // 探索は1秒間隔で行う
      if (count != 0) {
        try {
          Thread.sleep(searchInterval);
        } catch (InterruptedException e) {
          throw new RuntimeException(e.getCause());
        }
      }

      KeywordTree currentNode = searchKeywordQueue.poll();
      if (currentNode.isStopSearch()) {
        continue;
      }
      List<Link> result = linkRepository.findByKeyword(currentNode.keyword());
      currentNode.addSearchedKeywords(result, keywordTree.collectSearchedKeywords());
      searchKeywordQueue.addAll(currentNode.childNodes());
      count++;
    }

    // 永続化
    keywordTreeRepository.store(keywordTree);
  }

}

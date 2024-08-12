package asyncbatchpoc.consumer.domain;

public interface KeywordTreeRepository {

  /**
   * 関連キーワード探索結果を永続化する.
   */
  void store(KeywordTree keywordTree);
}

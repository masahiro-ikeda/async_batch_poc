package asyncbatchpoc.consumer.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 関連キーワードのツリー構造を多分木で構築するモデル.
 */
public class KeywordTree {
  private final String keyword;
  private final int treeDepth;
  /* 探索が行われたかどうか */
  private boolean hasSearched;
  /* 探索済みのキーワードかどうか */
  private final boolean isDuplicated;
  /* キーワードが探索中止条件に合致するかどうか */
  private final boolean isMatchStopCondition;
  private List<KeywordTree> childNodes;

  /* 次の条件に合致した場合は以降の探索を中止 */
  private static final List<String> STOP_SEARCH_CONDITIONS = Arrays.asList(
      "^.*語$",
      "^.*学$"
  );

  private KeywordTree(String keyword, int treeDepth, boolean hasSearched, boolean isDuplicated, boolean isMatchStopCondition, List<KeywordTree> childNodes) {
    this.keyword = keyword;
    this.treeDepth = treeDepth;
    this.hasSearched = hasSearched;
    this.isDuplicated = isDuplicated;
    this.isMatchStopCondition = isMatchStopCondition;
    this.childNodes = childNodes;
  }

  public static KeywordTree rootNode(String keyword) {
    return new KeywordTree(
        keyword,
        0,
        false,
        false,
        false,
        new ArrayList<>()
    );
  }

  public String keyword() {
    return keyword;
  }

  public int treeDepth() {
    return treeDepth;
  }

  public List<KeywordTree> childNodes() {
    return childNodes;
  }

  /**
   * 探索した関連キーワードを追加する.
   */
  public void addSearchedKeywords(List<Link> links, List<String> searchedWords) {
    List<KeywordTree> childTreeNodes = links.stream().map(
        link -> new KeywordTree(
            link.word(),
            this.treeDepth() + 1,
            false,
            searchedWords.contains(link.word()),
            STOP_SEARCH_CONDITIONS.stream().anyMatch(regex -> link.word().matches(regex)),
            new ArrayList<>()
        )
    ).toList();
    this.childNodes.addAll(childTreeNodes);
    this.hasSearched = true;
  }

  /**
   * 配下のノードから探索済みキーワードを取得する.
   */
  public List<String> collectSearchedKeywords() {
    List<String> result = new ArrayList<>();
    result.add(this.keyword);
    for (KeywordTree childNode : childNodes) {
      result.addAll(childNode.collectSearchedKeywords());
    }
    return result;
  }

  /**
   * 探索を中止するかどうか.
   */
  public boolean isStopSearch() {
    return isDuplicated || isMatchStopCondition;
  }

  public boolean hasSearched() {
    return hasSearched;
  }

  public boolean isDuplicated() {
    return isDuplicated;
  }

  public boolean isMatchStopCondition() {
    return isMatchStopCondition;
  }
}

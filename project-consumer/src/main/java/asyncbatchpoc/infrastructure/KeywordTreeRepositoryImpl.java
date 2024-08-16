package asyncbatchpoc.infrastructure;


import asyncbatchpoc.domain.KeywordTree;
import asyncbatchpoc.domain.KeywordTreeRepository;
import org.springframework.stereotype.Repository;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Stack;
import java.util.logging.Logger;

@Repository
public class KeywordTreeRepositoryImpl implements KeywordTreeRepository {

  private static final String OUTPUT_FILE_FORMAT = "output/%s_%s.txt";

  /**
   * 作成した関連キーワードツリーをファイル出力して永続化する.
   */
  @Override
  public void store(KeywordTree keywordTree) {
    Logger logger = Logger.getLogger("KeywordTreeRepositoryImpl");
    Stack<KeywordTree> output = new Stack<>(); // 深さ優先で出力するため
    output.push(keywordTree);

    String outputPath = String.format(OUTPUT_FILE_FORMAT, keywordTree.keyword(), LocalDateTime.now());
    try (FileOutputStream fos = new FileOutputStream(outputPath);
         OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
         BufferedWriter writer = new BufferedWriter(osw)) {
      while (!output.isEmpty()) {
        KeywordTree currentNode = output.pop();
        writer.write(generateLine(currentNode));
        writer.newLine();

        // 出力順が反転しないように
        Collections.reverse(currentNode.childNodes());
        for (KeywordTree node : currentNode.childNodes()) {
          output.push(node);
        }
      }
      logger.info(String.format("output: %s", outputPath));
    } catch (IOException e) {
      throw new RuntimeException("ファイル出力に失敗しました。", e.getCause());
    }
  }

  private static final String PREFIX = "- ";
  private static final String INDENT = "    ";
  private static final String STOP_SEARCH_SUFFIX = "$";
  private static final String DUPLICATED_SUFFIX = "@";
  private static final String NOT_SEARCHED_SUFFIX = "$";

  /**
   * 1行分ずつ出力フォーマットに整形する.
   */
  private String generateLine(KeywordTree keywordTree) {
    StringBuilder builder = new StringBuilder();
    builder.append(INDENT.repeat(keywordTree.treeDepth())); // 階層に応じたインデントを付与
    builder.append(PREFIX);
    builder.append(keywordTree.keyword());

    if (keywordTree.isMatchStopCondition()) {
      builder.append(STOP_SEARCH_SUFFIX);
    } else if (keywordTree.isDuplicated()) {
      builder.append(DUPLICATED_SUFFIX);
    } else if (!keywordTree.hasSearched()) {
      builder.append(NOT_SEARCHED_SUFFIX);
    }
    return builder.toString();
  }
}

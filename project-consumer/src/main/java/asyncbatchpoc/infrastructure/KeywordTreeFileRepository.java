package asyncbatchpoc.infrastructure;


import asyncbatchpoc.domain.KeywordTree;
import asyncbatchpoc.domain.KeywordTreeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

@Repository
public class KeywordTreeFileRepository implements KeywordTreeRepository {

  /**
   * 作成した関連キーワードツリーをファイル出力して永続化する.
   */
  @Override
  public void store(KeywordTree keywordTree) {
    Logger logger = Logger.getLogger("KeywordTreeFileRepository");

    String firstKeyword = keywordTree.keyword();
    Stack<KeywordTree> stack = new Stack<>(); // 深さ優先で出力するため
    stack.push(keywordTree);

    List<String> outputs = new ArrayList<>();
    while (!stack.isEmpty()) {
      KeywordTree currentNode = stack.pop();
      outputs.add(generateLine(currentNode));

      // 出力順が反転しないように
      Collections.reverse(currentNode.childNodes());
      for (KeywordTree node : currentNode.childNodes()) {
        stack.push(node);
      }
    }
    fileWrite(outputs, firstKeyword, logger);
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

  /* ファイル出力先パス */
  @Value("${app.output.pathFormat}")
  private String outputPathFormat;

  /**
   * ファイル出力する.
   */
  private void fileWrite(List<String> outputs, String firstKeyword, Logger logger) {
    String outputPath = String.format(outputPathFormat, firstKeyword, LocalDateTime.now());
    try (FileOutputStream fos = new FileOutputStream(outputPath);
         OutputStreamWriter osw = new OutputStreamWriter(fos);
         BufferedWriter writer = new BufferedWriter(osw)) {
      for (String output : outputs) {
        writer.write(output);
        writer.newLine();
      }
    } catch (IOException e) {
      throw new RuntimeException("ファイル出力に失敗しました。", e.getCause());
    }
    logger.info(String.format("output file: %s", outputPath));
  }
}

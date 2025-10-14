package dsl;

import org.junit.jupiter.api.Test;

class JslParserTest {

  @Test
  void json() throws Exception {
    lib.json.tool.Jsl.main("test/dsl/example.jsl");
  }
}

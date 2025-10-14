package dsl;

import org.junit.jupiter.api.Test;

class JbmlParserTest {

  @Test
  void json() throws Exception {
    lib.json.tool.Jbml.main("test/dsl/example.jbml");
  }
}

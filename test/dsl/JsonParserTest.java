package dsl;

import org.junit.jupiter.api.Test;

class JsonParserTest {

  @Test
  void json() throws Exception {
    lib.json.tool.Json.main("test/dsl/example.json");
  }
}
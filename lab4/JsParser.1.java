public class JsParser {
  private ArrayList<Token> tokens;

  JsParser(ArrayList<Token> tokenSequence) {
    this.tokens = tokenSequence;
    this.tokStack = new Stack<>();
  }

  private void parse() {
    statementList();
  }

  private void statementList() {
    statement();
    statementList();
  }

  private void statement() {
  }
}
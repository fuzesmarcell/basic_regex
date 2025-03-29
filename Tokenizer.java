public class Tokenizer {
    String input;
    int pos = 0;

    Token currToken = new Token(TokenKind.EOF);

    public Tokenizer(String input) {
        this.input = input;
    }

    public boolean isToken(TokenKind kind) {
        return currToken.kind == kind;
    }

    public void expectToken(TokenKind kind) {
        if (isToken(kind)) {
            nextToken();
        } else {
            throw new Error("Unexpected token: " + currToken);
        }
    }

    public boolean matchToken(TokenKind kind) {
        if (isToken(kind)) {
            nextToken();
            return true;
        } else {
            return false;
        }
    }

    public void nextToken() {
        if (pos >= input.length()) {
            currToken = new Token(TokenKind.EOF);
            return;
        }

        Token result = new Token(TokenKind.EOF);
        switch (input.charAt(pos)) {
            case '(':
                result.kind = TokenKind.ParenOpen;
                ++pos;
                break;
            case ')':
                result.kind = TokenKind.ParenClose;
                ++pos;
                break;
            case '+':
                result.kind = TokenKind.Plus;
                ++pos;
                break;
            case '*':
                result.kind = TokenKind.Star;
                ++pos;
                break;
            case '$':
                result.kind = TokenKind.Dollar;
                ++pos;
                break;
            default:
                int endPos = pos;
                // TODO: Handle other characters and escape characters
                for (int i = pos; i < input.length(); ++i) {
                    char c = input.charAt(i);
                    if (Character.isDigit(c) || Character.isLetter(c)) {
                        ++endPos;
                    } else {
                        break;
                    }
                }

                result.kind = TokenKind.String;
                result.value = input.substring(pos, endPos);

                pos = endPos;

                break;
        }

        currToken = result;
    }
}

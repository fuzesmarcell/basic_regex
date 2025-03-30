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

    public boolean isTokenNext(TokenKind kind) {
        int oldPos = pos;
        Token oldToken = currToken;

        nextToken();

        boolean result = currToken.kind == kind;

        // rewind back
        pos = oldPos;
        currToken = oldToken;

        return result;
    }

    public void nextToken() {
        if (pos >= input.length()) {
            currToken = new Token(TokenKind.EOF);
            return;
        }

        Token result = new Token(TokenKind.EOF);
        char c = input.charAt(pos);
        switch (c) {
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
            case '^':
                result.kind = TokenKind.Hat;
                ++pos;
                break;
            default:
                if (currToken.kind == TokenKind.Hat && Character.isDigit(c)) {
                    result.kind = TokenKind.Number;
                    int endPos = pos+1;
                    for (int i = pos+1; i < input.length(); ++i) {
                        if (!Character.isDigit(input.charAt(i))) {
                            endPos = i;
                            break;
                        }
                    }

                    String num = input.substring(pos, endPos);
                    result.val = Integer.parseInt(num);
                    pos = endPos;
                } else {
                    result.kind = TokenKind.String;

                    int endPos = pos;
                    do {
                        char nextC = input.charAt(endPos);
                        if (nextC == '(' || nextC == ')' ||
                            nextC == '*' || nextC == '+' ||
                            nextC == '^' || nextC == '$') {
                            break;
                        }
                    } while (++endPos < input.length());

                    if (endPos - pos > 1) {
                        if (endPos < input.length()) {
                            char nextC = input.charAt(endPos);
                            if (nextC == '*') {
                                endPos -= 1;
                            }
                        }
                    }

                    result.str = input.substring(pos, endPos);
                    pos = endPos;
                }

                break;
        }

        currToken = result;
    }
}

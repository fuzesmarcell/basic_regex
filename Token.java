public class Token {
    TokenKind kind;

    String str;
    Integer val;

    public Token(TokenKind kind) {
        this.kind = kind;
        str = "";
        val = 0;
    }

    public String toString() {
        return switch (kind) {
            case TokenKind.EOF -> "EOF";
            case TokenKind.String -> str;
            case TokenKind.Number -> val.toString();
            case TokenKind.ParenOpen -> "(";
            case TokenKind.ParenClose -> ")";
            case TokenKind.Dollar -> "$";
            case TokenKind.Star -> "*";
            case TokenKind.Plus -> "+";
            case TokenKind.Hat -> "^";
            default -> "Uknown token";
        };
    }
}

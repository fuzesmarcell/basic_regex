public class Token {
    TokenKind kind;
    String value;

    public Token(TokenKind kind) {
        this.kind = kind;
        value = "";
    }

    public Token(TokenKind kind, String value) {
        this.kind = kind;
        this.value = value;
    }

    public String toString() {
        return switch (kind) {
            case TokenKind.EOF -> "EOF";
            case TokenKind.String -> value;
            case TokenKind.ParenOpen -> "(";
            case TokenKind.ParenClose -> ")";
            case TokenKind.Dollar -> "$";
            case TokenKind.Star -> "*";
            case TokenKind.Plus -> "+";
            default -> "Uknown token";
        };
    }
}

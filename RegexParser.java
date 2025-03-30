import java.util.*;

public class RegexParser {
    Tokenizer tokenizer;

    public RegexParser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.tokenizer.nextToken();
    }

    public NFA parseExpr3() {
        if (tokenizer.isToken(TokenKind.String)) {
            Integer q0 = 0;
            Map<Integer, Map<Character, Set<Integer>>> d = new HashMap<>();

            for (int i = 0; i < tokenizer.currToken.value.length(); i++) {
                Character c = tokenizer.currToken.value.charAt(i);

                Map<Character, Set<Integer>> innerMap = new HashMap<>();
                innerMap.put(c, new HashSet<>(Set.of(i+1)));

                d.put(i, innerMap);
            }

            Integer endState = tokenizer.currToken.value.length();
            d.put(endState, null);

            NFA nfa = new NFA(q0, d, new HashSet<>(Set.of(endState)));

            tokenizer.nextToken();

            return nfa;

        } else if (tokenizer.matchToken(TokenKind.ParenOpen)) {
            NFA nfa = parseExpr();
            tokenizer.expectToken(TokenKind.ParenClose);
            return nfa;
        } else {
            throw new Error("Unexpected token: " + tokenizer.currToken);
        }
    }

    public NFA parseExpr2() {
        NFA a = parseExpr3();
        while (tokenizer.isToken(TokenKind.ParenOpen) || tokenizer.isToken(TokenKind.String)) {
            TokenKind kind = tokenizer.currToken.kind;

            if (kind == TokenKind.ParenOpen) {
                tokenizer.nextToken();
                NFA b = parseExpr();
                tokenizer.expectToken(TokenKind.ParenClose);
                if (tokenizer.isToken(TokenKind.Star)) {
                    tokenizer.nextToken();
                    b = NFA.kleeneIteration(b);
                }
                a = NFA.concat(a, b);
            } else {
                NFA b = parseExpr3();
                a = NFA.concat(a, b);
            }
        }

        return a;
    }

    public NFA parseExpr1() {
        NFA a = parseExpr2();
        while (tokenizer.matchToken(TokenKind.Star)) {
            a = NFA.kleeneIteration(a);
            if (!tokenizer.isToken(TokenKind.EOF)) {
                a = NFA.concat(a, parseExpr());
            }
        }

        return a;
    }

    public NFA parseExpr0() {
        NFA a = parseExpr1();
        while (tokenizer.isToken(TokenKind.Plus)) {
            TokenKind kind = tokenizer.currToken.kind;
            tokenizer.nextToken();
            NFA b = parseExpr1();
            a = NFA.union(a, b);
        }

        return a;
    }

    // expr3 = String | '(' expr ')'
    // expr2 = expr3(*, expr3)
    // expr1 = expr2(cat, expr2)
    // expr0 = expr1(+ expr1)
    // expr = expr0
    public NFA parseExpr() {
        return parseExpr0();
    }
}

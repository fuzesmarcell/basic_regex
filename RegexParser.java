import java.util.*;

public class RegexParser {
    Tokenizer tokenizer;

    public RegexParser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.tokenizer.nextToken();
    }

    public NFA parseExpr3() {
        if (tokenizer.isToken(TokenKind.String) || tokenizer.isToken(TokenKind.Dollar)) {
            TokenKind kind = tokenizer.currToken.kind;

            if (kind == TokenKind.String) {
                Integer q0 = 0;

                Map<Integer, Map<Character, Set<Integer>>> d = new HashMap<>();

                for (int i = 0; i < tokenizer.currToken.str.length(); i++) {
                    Character c = tokenizer.currToken.str.charAt(i);

                    Map<Character, Set<Integer>> innerMap = new HashMap<>();
                    innerMap.put(c, new HashSet<>(Set.of(i+1)));

                    d.put(i, innerMap);
                }

                Integer endState = tokenizer.currToken.str.length();
                d.put(endState, null);

                tokenizer.nextToken();

                return new NFA(q0, d, new HashSet<>(Set.of(endState)));
            } else {
                // epsilon case, build epsilon NFA
                Integer q0 = 0;
                var F = new HashSet<>(Set.of(q0));
                Map<Integer, Map<Character, Set<Integer>>> d = new HashMap<>();
                d.put(q0, null);

                tokenizer.nextToken();

                return new NFA(q0, d, F);
            }
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
        while (tokenizer.matchToken(TokenKind.Star)) {
            a = NFA.kleeneIteration(a);
            if (!tokenizer.isToken(TokenKind.EOF)) {
                a = NFA.concat(a, parseExpr());
            }
        }

        return a;
    }

    public NFA parseExpr1() {
        NFA a = parseExpr2();
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
                NFA b = parseExpr2();
                a = NFA.concat(a, b);
            }
        }

        return a;
    }

    public NFA parseExpr0() {
        NFA a = parseExpr1();
        while (tokenizer.isToken(TokenKind.Plus) || tokenizer.isToken(TokenKind.Hat)) {
            TokenKind kind = tokenizer.currToken.kind;
            tokenizer.nextToken();
            if (kind == TokenKind.Hat) {
                int iters = tokenizer.currToken.val;
                tokenizer.expectToken(TokenKind.Number);
                NFA b = new NFA(a.q0, a.d, a.F);
                for (int i = 0; i < iters-1; i++) {
                    a = NFA.concat(a, b);
                }
            } else {
                NFA b = parseExpr1();
                a = NFA.union(a, b);
            }
        }

        return a;
    }

    // iteration > concat|powerof > union
    public NFA parseExpr() {
        return parseExpr0();
    }
}

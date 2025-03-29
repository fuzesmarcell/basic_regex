public class Main {

    public static void testCase(NFA nfa, String E, boolean expected) {
        boolean matched = nfa.execute(E);
        System.out.printf("\tTest: \"%s\" %s\n", E, (expected == matched) ? "PASS" : "FAIL\n");
    }

    public static void main(String[] args) {
        {
            String input = "0+1";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();

            testCase(nfa, "0", true);
            testCase(nfa, "1", true);
            testCase(nfa, "2", false);
            testCase(nfa, "11", false);
            testCase(nfa, "01", false);
        }

        {
            String input = "00+1";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();

            testCase(nfa, "0", false);
            testCase(nfa, "1", true);
            testCase(nfa, "00", true);
            testCase(nfa, "10", false);
            testCase(nfa, "01", false);
            testCase(nfa, "11", false);
        }

    }
}

// parse expression
// expr2 [( expr)]
// expr1 [*]
// expr0 [+]
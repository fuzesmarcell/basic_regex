public class Main {

    public static void testCase(NFA nfa, String E, boolean expected) {
        boolean matched = nfa.execute(E);
        System.out.printf("\tTest: \"%s\" %s\n", E, (expected == matched) ? "PASS" : "FAIL\n");
        if (expected != matched) {
            throw new RuntimeException("Test failed");
        }
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

        {
            String input = "ab+cba";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();

            testCase(nfa, "ab", true);
            testCase(nfa, "cba", true);
            testCase(nfa, "abc", false);
            testCase(nfa, "ba", false);
            testCase(nfa, "abcba", false);
        }

        {
            String input = "ab+cba+bb";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();

            testCase(nfa, "ab", true);
            testCase(nfa, "cba", true);
            testCase(nfa, "bb", true);
            testCase(nfa, "bbb", false);
            testCase(nfa, "ba", false);
            testCase(nfa, "abcba", false);
            testCase(nfa, "abcbabb", false);
        }

        {
            String input = "hello";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();

            testCase(nfa, "hello", true);
            testCase(nfa, "helo", false);
        }
    }
}
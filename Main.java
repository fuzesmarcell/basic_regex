public class Main {

    public static void testCase(NFA nfa, String E, boolean expected) {
        boolean matched = nfa.execute(E);
        System.out.printf("\tTest: \"%s\" %s\n", E, (expected == matched) ? "PASS" : "FAIL\n");
        if (expected != matched) {
            nfa.debugDumpGraphViz();
            throw new RuntimeException("Test failed");
        }
    }

    public static void unionTests() {
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

        {
            String input = "(ab)+(cba)+(bb)";
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
    }

    public static void miscTests() {
        {
            String input = "(a+b)(c)";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();

            testCase(nfa, "ac", true);
            testCase(nfa, "bc", true);
            testCase(nfa, "abc", false);
            testCase(nfa, "cc", false);
        }

        {
            String input = "(a+b+c)(d+f)";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();

            testCase(nfa, "ad", true);
            testCase(nfa, "af", true);
            testCase(nfa, "bd", true);
            testCase(nfa, "bf", true);
            testCase(nfa, "cd", true);
            testCase(nfa, "cf", true);
            testCase(nfa, "abf", false);
            testCase(nfa, "ag", false);
        }
    }

    public static void concatTests() {
        {
            String input = "a(b)";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();

            testCase(nfa, "ab", true);
            testCase(nfa, "ba", false);
            testCase(nfa, "abab", false);
            testCase(nfa, "b", false);
            testCase(nfa, "a", false);
        }

        {
            String input = "((aa))";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();

            testCase(nfa, "aa", true);
            testCase(nfa, "ba", false);
            testCase(nfa, "bb", false);
        }

        {
            String input = "(b)(((aa)))";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();

            testCase(nfa, "baa", true);
            testCase(nfa, "bba", false);
            testCase(nfa, "bbb", false);
        }

        {
            String input = "(hello)(world)";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();

            testCase(nfa, "helloworld", true);
            testCase(nfa, "heloworld", false);
            testCase(nfa, "helloworlD", false);
        }
    }

    public static void main(String[] args) {

        {
            String input = "0(01+10)1";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();

            testCase(nfa, "0011", true);
            testCase(nfa, "0101", true);
            testCase(nfa, "1010", false);
            testCase(nfa, "001", false);
        }

        {
            String input = "(0+1)*";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();

            testCase(nfa, "", true);
            testCase(nfa, "0111", true);
            testCase(nfa, "011010101", true);
        }

        {
            String input = "0(0+1)*1";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();

            testCase(nfa, "01", true);
            testCase(nfa, "0111", true);
            testCase(nfa, "011010101", true);
            testCase(nfa, "101010100011001", false);
        }

        {
            String input = "(00)*";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();

            testCase(nfa, "00", true);
            testCase(nfa, "0000", true);
            testCase(nfa, "00000000", true);
            testCase(nfa, "0", false);
            testCase(nfa, "000", false);
            testCase(nfa, "11", false);
            testCase(nfa, "10", false);
        }

        {
            String input = "((0*1)(0*1))*0*";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();

            testCase(nfa, "11", true);
            testCase(nfa, "0110", true);
            testCase(nfa, "101", true);
            testCase(nfa, "10100101", true);
            testCase(nfa, "10100100", false);
            testCase(nfa, "10", false);
            testCase(nfa, "1", false);
        }

        // TODO: Special character tests!
        // TODO: Epsilon character handling!

        concatTests();
        unionTests();
        miscTests();
    }
}
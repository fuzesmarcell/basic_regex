import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

        {
            String input = "$";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();

            testCase(nfa, "", true);
            testCase(nfa, "$", false);
            testCase(nfa, "a", false);
            testCase(nfa, "b", false);
            testCase(nfa, "asdfqewr", false);
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

    public static void pptTests() {
        {
            String input = "0(01+10)1+$";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();

            testCase(nfa, "0011", true);
            testCase(nfa, "0101", true);
            testCase(nfa, "", true);
            testCase(nfa, "1010", false);
            testCase(nfa, "001", false);
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
            testCase(nfa, "", false);
            testCase(nfa, "10", false);
            testCase(nfa, "1000001", false);
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
            String input = "(0+$)(10)*(1+$)";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();
            testCase(nfa, "01", true);
            testCase(nfa, "10", true);
            testCase(nfa, "1010", true);
            testCase(nfa, "0101", true);
            testCase(nfa, "011", false);
            testCase(nfa, "0011", false);
            testCase(nfa, "010110101", false);
        }

        {
            String input = "(0^2)*";
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
            String input = "((0*1)^2)*0*";
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

        {
            String input = "(0*10*10*1)*0*";
            System.out.printf("Test Suite: \"%s\"\n", input);

            Tokenizer tokenizer = new Tokenizer(input);
            RegexParser parser = new RegexParser(tokenizer);

            NFA nfa = parser.parseExpr();

            testCase(nfa, "111", true);
            testCase(nfa, "111111", true);
            testCase(nfa, "11111", false);
            testCase(nfa, "1", false);
            testCase(nfa, "00001110000", true);
            testCase(nfa, "0100100010010110", true);
            testCase(nfa, "10001100001001", false);
            testCase(nfa, "0110", false);
        }
    }

    public static void main(String[] args) throws IOException {
        // TODO: Handle all characters (escape characters)

        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print("Enter a operation [test, re (regex), q (quit)]: ");
            String s = r.readLine();
            if (s.equals("test")) {
                concatTests();
                unionTests();
                miscTests();
                pptTests();
            } else if (s.equals("q")) {
                break;
            } else if (s.equals("re")) {
                System.out.print("Enter your regex: ");
                String re = r.readLine();

                Tokenizer tokenizer = new Tokenizer(re);
                RegexParser parser = new RegexParser(tokenizer);
                NFA nfa = parser.parseExpr();

                System.out.println("test or dump vizualization?");
                String m = r.readLine();
                if (m.equals("test")) {
                    while (true) {
                        System.out.print("Test abc: ");
                        String input = r.readLine();
                        if (input.equals("q")) {
                            break;
                        }

                        System.out.printf("Recognized: %s\n", nfa.execute(input));
                    }
                } else {
                    nfa.debugDumpGraphViz();
                }
            }
        }
    }
}
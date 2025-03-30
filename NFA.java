import java.util.*;

public class NFA {
    Integer q0; // start state
    Set<Integer> F; // accept states
    Map<Integer, Map<Character, Set<Integer>>> d;

    public NFA(Integer q0, Map<Integer, Map<Character, Set<Integer>>> d, Set<Integer> F) {
        this.q0 = q0;
        this.d = d;
        this.F = F;
    }

    public boolean execute(int q, int pos, String E) {
        boolean isEndState = F.contains(q);
        if (pos >= E.length()) {
            return isEndState;
        }

        Character c = E.charAt(pos);

        if (d.get(q) == null) {
            return false;
        }

        if (!d.get(q).containsKey(c) && !d.get(q).containsKey('\0')) {
            return false;
        }

        Set<Integer> epsilonStates = d.get(q).get('\0');
        Set<Integer> states = d.get(q).get(c);

        if (states == null && epsilonStates == null) {
            return false;
        }

        if (epsilonStates != null) {
            for (Integer state : epsilonStates) {
                if (execute(state, pos, E)) {
                    return true;
                }
            }
        }

        if (states != null) {
            for (Integer state : states) {
                if (execute(state, ++pos, E)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean execute(String E) {
        Integer q = q0;
        Integer pos = 0;
        return execute(q, pos, E);
    }

    public static Map<Integer, Map<Character, Set<Integer>>> combine(NFA a, NFA b, StateMapping stm) {
        Map<Integer, Map<Character, Set<Integer>>> d = new HashMap<>();
        for (Map.Entry<Integer, Map<Character, Set<Integer>>> entry : a.d.entrySet()) {
            Integer aq = entry.getKey();
            Integer q = stm.getStateA(aq);

            Map<Character, Set<Integer>> m = entry.getValue();
            if (m != null) {
                for (Map.Entry<Character, Set<Integer>> e : m.entrySet()) {
                    Set<Integer> s = new HashSet<>();
                    for (Integer state : e.getValue()) {
                        s.add(stm.getStateA(state));
                    }

                    e.setValue(s);
                }
            }

            d.put(q, m);
        }

        for (Map.Entry<Integer, Map<Character, Set<Integer>>> entry : b.d.entrySet()) {
            Integer bq = entry.getKey();
            Integer q = stm.getStateB(bq);

            Map<Character, Set<Integer>> m = entry.getValue();
            if (m != null) {
                for (Map.Entry<Character, Set<Integer>> e : m.entrySet()) {
                    Set<Integer> s = new HashSet<>();
                    for (Integer state : e.getValue()) {
                        s.add(stm.getStateB(state));
                    }

                    e.setValue(s);
                }
            }

            d.put(q, entry.getValue());
        }

        return d;
    }

    public static NFA union(NFA a, NFA b) {
        StateMapping stm = new StateMapping(a, b);

        Integer q0 = stm.getNextFreeState();

        var d = combine(a, b, stm);

        Map<Character, Set<Integer>> innerMap = new HashMap<>();
        innerMap.put('\0', new HashSet<>(Set.of(stm.getStateA(a.q0), stm.getStateB(b.q0))));
        d.put(q0, innerMap);

        Set<Integer> F = new HashSet<>();
        for (Integer q : a.F) {
            F.add(stm.getStateA(q));
        }

        for (Integer q : b.F) {
            F.add(stm.getStateB(q));
        }

        NFA nfa = new NFA(q0, d, F);
        return nfa;
    }

    public static NFA concat(NFA a, NFA b) {
        StateMapping stm = new StateMapping(a, b);
        var d = combine(a, b, stm);

        Integer q0 = stm.getStateA(a.q0);

        Integer startStateB = stm.getStateB(b.q0);
        for (Integer endStateA : a.F) {
            Integer q = stm.getStateA(endStateA);

            var jumps = d.get(q);
            if (jumps == null) {
                Map<Character, Set<Integer>> innerMap = new HashMap<>();
                innerMap.put('\0', new HashSet<>(Set.of(startStateB)));
                d.put(q, innerMap);
            } else {
                if (jumps.containsKey('\0')) {
                    jumps.get('\0').add(startStateB);
                } else {
                    jumps.put('\0', Set.of(startStateB));
                }
            }
        }

        Set<Integer> F = new HashSet<>();
        for (Integer q : b.F) {
            F.add(stm.getStateB(q));
        }

        return new NFA(q0, d, F);
    }

    public static NFA kleeneIteration(NFA a) {
        Set<Integer> states = new HashSet<>();
        for (Map.Entry<Integer, Map<Character, Set<Integer>>> entry : a.d.entrySet()) {
            states.add(entry.getKey());
        }

        Integer q0 = Collections.max(states)+1;

        NFA result = new NFA(a.q0, a.d, a.F) ;
        result.q0 = q0;
        result.F.add(result.q0);

        Map<Character, Set<Integer>> innerMap = new HashMap<>();
        innerMap.put('\0', new HashSet<>(Set.of(a.q0)));
        result.d.put(q0, innerMap);

        for (Integer q : a.F) {
            Map<Character, Set<Integer>> im = new HashMap<>();
            im.put('\0', new HashSet<>(Set.of(a.q0)));
            result.d.put(q, im);
        }

        return result;
    }

    public void debugDumpGraphViz() {
        System.out.println("rankdir=LR");
        System.out.println("node [shape = plaintext]; start;");
        System.out.println("node [shape = circle]");
        for (Map.Entry<Integer, Map<Character, Set<Integer>>> entry : d.entrySet()) {
            Integer q = entry.getKey();
            if (!F.contains(q)) {
                System.out.println(q);
            }
        }
        System.out.println("node [shape = doublecircle]");
        for (Integer q : F) {
            System.out.println(q);
        }

        System.out.printf("start -> %d\n", q0);

        for (Map.Entry<Integer, Map<Character, Set<Integer>>> entry : d.entrySet()) {
            Map<Character, Set<Integer>> m = entry.getValue();
            if (m != null) {
                for (Map.Entry<Character, Set<Integer>> e : m.entrySet()) {
                    for (Integer q : e.getValue()) {
                        Character c = e.getKey();
                        if (c == '\0') {
                            c = 'ë';
                        }
                        System.out.printf("%d -> %d [label = %c]\n", entry.getKey(), q, c);
                    }
                }
            }
        }
    }
}

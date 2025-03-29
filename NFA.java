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

    public static NFA union(NFA a, NFA b) {
        Map<Integer, Integer> aMap = new HashMap<>();
        Map<Integer, Integer> bMap = new HashMap<>();
        Set<Integer> states = new HashSet<>();

        for (Map.Entry<Integer, Map<Character, Set<Integer>>> entry : a.d.entrySet()) {
            Integer q = entry.getKey();
            if (states.contains(q)) {
                Integer maxq = Collections.max(states);
                aMap.put(q, maxq+1);
                states.add(maxq+1);
            } else {
                states.add(q);
                aMap.put(q, q);
            }
        }

        for (Map.Entry<Integer, Map<Character, Set<Integer>>> entry : b.d.entrySet()) {
            Integer q = entry.getKey();
            if (states.contains(q)) {
                Integer maxq = Collections.max(states);
                bMap.put(q, maxq+1);
                states.add(maxq+1);
            } else {
                states.add(q);
                bMap.put(q, q);
            }
        }

        Integer q0 = Collections.max(states) + 1;
        Map<Integer, Map<Character, Set<Integer>>> d = new HashMap<>();

        for (Map.Entry<Integer, Map<Character, Set<Integer>>> entry : a.d.entrySet()) {
            Integer aq = entry.getKey();
            Integer q = aMap.get(aq);

            Map<Character, Set<Integer>> m = entry.getValue();
            if (m != null) {
                for (Map.Entry<Character, Set<Integer>> e : m.entrySet()) {
                    Set<Integer> s = new HashSet<>();
                    for (Integer state : e.getValue()) {
                        s.add(aMap.get(state));
                    }

                    e.setValue(s);
                }
            }

            d.put(q, m);
        }

        for (Map.Entry<Integer, Map<Character, Set<Integer>>> entry : b.d.entrySet()) {
            Integer bq = entry.getKey();
            Integer q = bMap.get(bq);

            Map<Character, Set<Integer>> m = entry.getValue();
            if (m != null) {
                for (Map.Entry<Character, Set<Integer>> e : m.entrySet()) {
                    Set<Integer> s = new HashSet<>();
                    for (Integer state : e.getValue()) {
                        s.add(bMap.get(state));
                    }

                    e.setValue(s);
                }
            }

            d.put(q, entry.getValue());
        }

        Map<Character, Set<Integer>> innerMap = new HashMap<>();
        innerMap.put('\0', new HashSet<>(Set.of(aMap.get(a.q0), bMap.get(b.q0))));
        d.put(q0, innerMap);

        Set<Integer> F = new HashSet<>();
        for (Integer q : a.F) {
            F.add(aMap.get(q));
        }

        for (Integer q : b.F) {
            F.add(bMap.get(q));
        }

        NFA nfa = new NFA(q0, d, F);
        return nfa;
    }

    public static NFA concat(NFA a, NFA b) {
        // epsilon jumps from all end positions of a
        // go into all end positions of b
        // we have to make sure to have unique state values!
        // end states will be only from b

        Map<Integer, Integer> aMap = new HashMap<>();
        Map<Integer, Integer> bMap = new HashMap<>();
        Set<Integer> states = new HashSet<>();

        for (Map.Entry<Integer, Map<Character, Set<Integer>>> entry : a.d.entrySet()) {
            Integer q = entry.getKey();
            if (states.contains(q)) {
                Integer maxq = Collections.max(states);
                aMap.put(q, maxq+1);
                states.add(maxq+1);
            } else {
                states.add(q);
                aMap.put(q, q);
            }
        }

        for (Map.Entry<Integer, Map<Character, Set<Integer>>> entry : b.d.entrySet()) {
            Integer q = entry.getKey();
            if (states.contains(q)) {
                Integer maxq = Collections.max(states);
                bMap.put(q, maxq+1);
                states.add(maxq+1);
            } else {
                states.add(q);
                bMap.put(q, q);
            }
        }

        Map<Integer, Map<Character, Set<Integer>>> d = new HashMap<>();
        ArrayList<Integer> aEndStatesList = new ArrayList<>();
        for (Map.Entry<Integer, Map<Character, Set<Integer>>> entry : a.d.entrySet()) {
            Integer aq = entry.getKey();
            Integer q = aMap.get(aq);

            if (Arrays.asList(a.F).contains(aq)) {
                aEndStatesList.add(q);
            }

            d.put(q, entry.getValue());
        }

        for (Map.Entry<Integer, Map<Character, Set<Integer>>> entry : b.d.entrySet()) {
            Integer bq = entry.getKey();
            Integer q = bMap.get(bq);
            d.put(q, entry.getValue());
        }

        Integer bStartState = bMap.get(b.q0);

        for (int i = 0; i < aEndStatesList.size(); i++) {
            Integer endState = aEndStatesList.get(i);
            Map<Character, Set<Integer>> s = d.computeIfAbsent(endState, k -> new HashMap<>());

            if (s.containsKey('\0')) {
                s.get('\0').add(bStartState);
            } else {
                s.put('\0', new HashSet<>(Set.of(bStartState)));
            }
        }

        Integer q0 = aMap.get(a.q0);

        Set<Integer> F = new HashSet<>();
        for (Integer q : b.F) {
            F.add(bMap.get(q));
        }

        NFA nfa = new NFA(q0, d, F); // TODO: This is incorrect we need to convert b.F to be the new id's
        return nfa;
    }
}

import java.util.*;

public class StateMapping {
    Map<Integer, Integer> aMap;
    Map<Integer, Integer> bMap;
    Set<Integer> states;

    public StateMapping(NFA a, NFA b) {
        aMap = new HashMap<>();
        bMap = new HashMap<>();
        states = new HashSet<>();

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
    }

    public Integer getNextFreeState() {
        Integer q = Collections.max(states) + 1;
        states.add(q);
        return q;
    }

    public Integer getStateA(Integer qa) {
        return aMap.get(qa);
    }

    public Integer getStateB(Integer qb) {
        return bMap.get(qb);
    }
}

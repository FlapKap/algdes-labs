package gorilla;

import java.util.Map;

public class CostMatrix {

    private final Map<Character, Map<Character, Integer>> costMapping;

    public CostMatrix(Map<Character, Map<Character, Integer>> costMapping) {
        this.costMapping = costMapping;
    }

    /**
     * Get the cost from converting from one char to another
     *
     * @param from the char we're rotating from
     * @param to   the char we're rotating to
     * @return the cost of rotating
     */
    Integer getCost(char from, char to) {
        return costMapping.get(from).get(to);
    }
}

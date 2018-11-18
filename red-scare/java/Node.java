public class Node {
    boolean isRed;
    String label;
    boolean isSource;
    boolean isSink;

    public Node(boolean isRed, String label, boolean isSource, boolean isSink) {
        this.isRed = isRed;
        this.label = label;
        this.isSource = isSource;
        this.isSink = isSink;
    }
}

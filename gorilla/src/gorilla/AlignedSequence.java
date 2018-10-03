package gorilla;

/**
 * An immutable class for containing the results of a leftAlignment aligner
 */
public class AlignedSequence {
    public final Species left;
    public final Species right;
    public final String leftAlignment;
    public final String rightAlignment;
    public final Integer cost;

    /**
     * Constructor
     * NB: should only be used to store finalized output
     *
     * @param left     the species compared from
     * @param right    the species compared to
     * @param leftAlignment the final protein leftAlignment (with possible gaps characters '*')
     * @param cost     the final cost of the protein alignment
     */
    public AlignedSequence(Species left, Species right, String leftAlignment, String rightAlignment, Integer cost) {
        this.left = left;
        this.right = right;
        this.leftAlignment = leftAlignment;
        this.rightAlignment = rightAlignment;
        this.cost = cost;
    }
}

package gorilla;

/**
 * An immutable class for containing the results of a sequence aligner
 */
public class AlignedSequence {
    final Species left;
    final Species right;
    final String sequence;
    final Integer cost;

    /**
     * Constructor
     * NB: should only be used to store finalized output
     *
     * @param left     the species compared from
     * @param right    the species compared to
     * @param sequence the final protein sequence (with possible gaps characters '*')
     * @param cost     the final cost of the protein alignment
     */
    public AlignedSequence(Species left, Species right, String sequence, Integer cost) {
        this.left = left;
        this.right = right;
        this.sequence = sequence;
        this.cost = cost;
    }
}

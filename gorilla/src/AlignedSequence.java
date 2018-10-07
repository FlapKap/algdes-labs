package gorilla;

/**
 * An immutable class for containing the results of a leftAlignment aligner
 */
public class AlignedSequence {
    public final Species source;
    public final Species destination;
    public final String leftAlign;
    public final String rightAlign;
    public final Integer cost;

    /**
     * Constructor
     * NB: should only be used to store finalized output
     *
     * @param source     the species compared from
     * @param destination    the species compared to
     * @param cost     the final cost of the protein alignment
     */
    public AlignedSequence(Species source, Species destination, String leftAlign, String rightAlign, Integer cost) {
        this.source = source;
        this.destination = destination;
        this.leftAlign = leftAlign;
        this.rightAlign = rightAlign;
        this.cost = cost;
    }
}

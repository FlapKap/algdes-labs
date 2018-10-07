package gorilla;

import util.jon.Triplet;

import java.util.List;

public interface SequenceAligner {
    /**
     * Takes a costMatrix and a speciesList and aligns every species with every other species.
     *
     * @param costMatrix  a CostMatrix
     * @param speciesList the list of species to align.
     * @return A list of aligned sequences.
     */
    List<AlignedSequence> alignSequences(CostMatrix costMatrix, List<Species> speciesList);
}

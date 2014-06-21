package edu.sikora.ca.neighbourhoods;

import edu.sikora.ca.space.Space;

/**
 * @author Kamil Sikora
 *         Data: 21.06.14
 */
public class MooreNeighbourhood extends Neighbourhood {
    public MooreNeighbourhood(boolean pmPeriodicBorderCondition, Space pmAutomataSpace) {
        super(pmAutomataSpace, pmPeriodicBorderCondition);
    }
}

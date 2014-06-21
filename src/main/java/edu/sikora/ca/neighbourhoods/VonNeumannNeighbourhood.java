package edu.sikora.ca.neighbourhoods;

import edu.sikora.ca.Point;
import edu.sikora.ca.space.Space;

import java.util.Vector;

/**
 * @author Kamil Sikora
 *         Data: 21.06.14
 */
public class VonNeumannNeighbourhood extends Neighbourhood {
    public VonNeumannNeighbourhood(boolean pmPeriodicBorderCondition, Space pmAutomataSpace) {
        super(pmAutomataSpace, pmPeriodicBorderCondition);
    }

    @Override
    public Vector<Point> calculateNeighboursCoord(int pmX, int pmY) {
        return calculateNearestNeighbours(pmX, pmY);
    }
}

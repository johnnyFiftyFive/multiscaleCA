package edu.sikora.ca.neighbourhoods;

import edu.sikora.ca.Point;
import edu.sikora.ca.cells.Cell;
import edu.sikora.ca.cells.Inclusion;
import edu.sikora.ca.space.Space;

import java.util.Vector;

/**
 * @author Kamil Sikora
 *         Data: 21.06.14
 */
public class MooreNeighbourhood extends Neighbourhood {
    public MooreNeighbourhood(boolean pmPeriodicBorderCondition, Space pmAutomataSpace) {
        super(pmAutomataSpace, pmPeriodicBorderCondition);
    }

    /**
     * Get information about recrystalized neighbours.
     *
     * @param pmX x coord
     * @param pmY y coord
     * @return neighbourhood info - empty if there are no recrystalized cells around
     */
    public NeighbourhoodInfo getRecrystalizedNeighbourhoodInfo(int pmX, int pmY) {
        NeighbourhoodInfo lvNI = new NeighbourhoodInfo();

        Vector<Point> points = calculateNeighboursCoord(pmX, pmY);
        for (Point p : points) {
            final Cell currentCell = mSpace.getState()[p.y][p.x];
            if (!(currentCell instanceof Inclusion) && currentCell.isAlive() && !currentCell.isDisabled() && currentCell.isRecrystalized())
                lvNI.addCell(currentCell);
        }

        return lvNI;
    }
}

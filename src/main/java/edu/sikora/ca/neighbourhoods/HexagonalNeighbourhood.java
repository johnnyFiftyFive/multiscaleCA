package edu.sikora.ca.neighbourhoods;

import edu.sikora.ca.Point;
import edu.sikora.ca.cells.Cell;
import edu.sikora.ca.cells.Inclusion;
import edu.sikora.ca.space.Space;

import java.util.Random;
import java.util.Vector;

/**
 * @author Kamil Sikora
 *         Data: 21.06.14
 */
public class HexagonalNeighbourhood extends Neighbourhood {
    public HexagonalNeighbourhood(boolean pmPeriodicBorderCondition, Space pmAutomataSpace) {
        super(pmAutomataSpace, pmPeriodicBorderCondition);
    }

    @Override
    public Cell newCellState(int pmX, int pmY) {
        final Cell lvCell = mSpace.getState()[pmY][pmX];

        Random lvRandom = new Random();
        boolean[] lvMask;

        if (lvRandom.nextBoolean())
            lvMask = new boolean[]{true, true, false, true, true, true, false, true};
        else
            lvMask = new boolean[]{false, true, true, true, false, true, true, true};


        NeighbourhoodInfo lvNI = new NeighbourhoodInfo();

        int lvCounter = 0;
        Vector<Point> points = calculateNeighboursCoord(pmX, pmY);
        for (Point p : points) {
            final Cell currentCell = mSpace.getState()[p.y][p.x];
            if (!(currentCell instanceof Inclusion) && lvMask[lvCounter] & currentCell.isAlive())
                lvNI.addCell(currentCell);
            ++lvCounter;
        }

        return !lvCell.isAlive() && lvNI.getTotalCount() > 0 ? new Cell(true, lvNI.getMarkerOfLargestCount()) : lvCell;
    }
}

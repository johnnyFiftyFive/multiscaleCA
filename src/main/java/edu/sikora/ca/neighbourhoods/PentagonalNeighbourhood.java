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
public class PentagonalNeighbourhood extends Neighbourhood {
    private final boolean[][] masks = {{true, true, false, true, true, true, false, true},
            {false, true, true, true, true, true, false, false},
            {true, true, true, true, false, false, false, true},
            {true, true, false, false, false, true, true, true}};

    public PentagonalNeighbourhood(boolean pmPeriodicBorderCondition, Space pmAutomataSpace) {
        super(pmAutomataSpace, pmPeriodicBorderCondition);
    }

    @Override
    public Cell newCellState(int pmX, int pmY) {
        final Cell lvCell = mSpace.getState()[pmY][pmX];

        Random lvRandom = new Random();
        int lvChoice = lvRandom.nextInt(4);

        boolean[] lvMask = masks[lvChoice];

        NeighbourhoodInfo lvNI = new NeighbourhoodInfo();

        int counter = 0;
        Vector<Point> points = calculateNeighboursCoord(pmX, pmY);
        for (Point p : points) {
            final Cell currentCell = mSpace.getState()[p.y][p.x];
            if (!(currentCell instanceof Inclusion) && lvMask[counter] & currentCell.isAlive())
                lvNI.addCell(currentCell);
            ++counter;
        }

        return !lvCell.isAlive() && lvNI.getTotalCount() > 0 ? new Cell(true, lvNI.getMarkerOfLargestCount()) : lvCell;
    }
}

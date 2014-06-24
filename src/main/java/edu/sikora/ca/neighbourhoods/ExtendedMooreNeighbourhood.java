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
public class ExtendedMooreNeighbourhood extends Neighbourhood {
    private final double PROBABILITY = 0.5;

    public ExtendedMooreNeighbourhood(boolean pmPeriodicBorderCondition, Space pmAutomataSpace) {
        super(pmAutomataSpace, pmPeriodicBorderCondition);
    }

    @Override
    public Cell newCellState(int pmX, int pmY) {
        final Cell lvCell = mSpace.getState()[pmY][pmX];

        NeighbourhoodInfo lvAllNI = getNeighbourhoodInfo(pmX, pmY);

        if (lvAllNI.getLargestCount() >= 5)
            return !lvCell.isAlive() && lvAllNI.getTotalCount() > 0 ? new Cell(true, lvAllNI.getMarkerOfLargestCount()) : lvCell;

        NeighbourhoodInfo lvNI = getNearestMooreInfo(pmX, pmY);
        if (lvNI.getLargestCount() >= 3)
            return !lvCell.isAlive() && lvNI.getTotalCount() > 0 ? new Cell(true, lvNI.getMarkerOfLargestCount()) : lvCell;

        lvNI = getFurtherMooreInfo(pmX, pmY);
        if (lvNI.getLargestCount() >= 3)
            return !lvCell.isAlive() && lvNI.getTotalCount() > 0 ? new Cell(true, lvNI.getMarkerOfLargestCount()) : lvCell;

        Random lvRandom = new Random(System.currentTimeMillis());
        return !lvCell.isAlive() && lvAllNI.getTotalCount() > 0 && lvRandom.nextDouble() < PROBABILITY ? new Cell(true, lvAllNI.getMarkerOfLargestCount()) : lvCell;
    }

    private NeighbourhoodInfo getFurtherMooreInfo(int pmX, int pmY) {
        NeighbourhoodInfo lvNI = new NeighbourhoodInfo();

        Vector<Point> points = calculateFurtherNeighbours(pmX, pmY);
        for (Point p : points) {
            final Cell currentCell = mSpace.getState()[p.y][p.x];
            if (!(currentCell instanceof Inclusion) && currentCell.isAlive() && !currentCell.isDisabled())
                lvNI.addCell(currentCell);
        }

        return lvNI;
    }

    private NeighbourhoodInfo getNearestMooreInfo(int pmX, int pmY) {
        NeighbourhoodInfo lvNI = new NeighbourhoodInfo();

        Vector<Point> points = calculateNearestNeighbours(pmX, pmY);
        for (Point p : points) {
            final Cell currentCell = mSpace.getState()[p.y][p.x];
            if (!(currentCell instanceof Inclusion) && currentCell.isAlive() && !currentCell.isDisabled())
                lvNI.addCell(currentCell);
        }

        return lvNI;
    }
}

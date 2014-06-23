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
public abstract class Neighbourhood {
    protected final Space mSpace;
    protected final int mHeight;
    protected final int mWidth;
    protected boolean mPeriodic;

    public Neighbourhood(final Space pmSpace, boolean pmPeriodic) {
        mSpace = pmSpace;
        mHeight = pmSpace.getHeight();
        mWidth = pmSpace.getWidth();

        mPeriodic = pmPeriodic;
    }

    /**
     * Calculates cell neighbours coordinates basing. Supports periodic and non periodic bonduary condition as well.
     *
     * @param pmX width index of cell
     * @param pmY height index of cell
     * @return list with neighbours coordinates
     */
    public Vector<Point> calculateNeighboursCoord(final int pmX, final int pmY) {
        Vector<Point> points = new Vector<Point>();

        points.addAll(calculateNearestNeighbours(pmX, pmY));
        points.addAll(calculateFurtherNeighbours(pmX, pmY));

        return points;
    }

    public Vector<Point> calculateNearestNeighbours(final int pmX, final int pmY) {
        Vector<Point> lvPoints = new Vector<Point>();

        if (mPeriodic) {
            lvPoints.add(new Point(pmX, ((pmY - 1) % mHeight + mHeight) % mHeight));                                        // upper cell
            lvPoints.add(new Point(((pmX + 1) % mWidth + mWidth) % mWidth, pmY));                                           // right cell
            lvPoints.add(new Point(pmX, ((pmY + 1) % mHeight + mHeight) % mHeight));                                        // lower cell
            lvPoints.add(new Point(((pmX - 1) % mWidth + mWidth) % mWidth, pmY));                                           // left cell
        } else {
            if (pmY > 0)
                lvPoints.add(new Point(pmX, pmY - 1));                                                                              // upper cell
            if (pmX < mWidth - 2)
                lvPoints.add(new Point(pmX + 1, pmY));                                                                              // right cell
            if (pmY < mHeight - 2)
                lvPoints.add(new Point(pmX, pmY + 1));                                                                              // lower cell
            if (pmX > 0)
                lvPoints.add(new Point(pmX - 1, pmY));                                                                              // left cell

        }

        return lvPoints;
    }

    public Vector<Point> calculateFurtherNeighbours(final int pmX, final int pmY) {
        Vector<Point> lvPoints = new Vector<Point>();

        if (mPeriodic) {
            lvPoints.add(new Point(((pmX - 1) % mWidth + mWidth) % mWidth, ((pmY - 1) % mHeight + mHeight) % mHeight));     // left upper cell
            lvPoints.add(new Point(((pmX + 1) % mWidth + mWidth) % mWidth, ((pmY - 1) % mHeight + mHeight) % mHeight));     // right upper cell
            lvPoints.add(new Point(((pmX + 1) % mWidth + mWidth) % mWidth, ((pmY + 1) % mHeight + mHeight) % mHeight));     // right lower cell
            lvPoints.add(new Point(((pmX - 1) % mWidth + mWidth) % mWidth, ((pmY + 1) % mHeight + mHeight) % mHeight));     // left lower cell
        } else {
            if (pmX > 0 && pmY > 0)
                lvPoints.add(new Point(pmX - 1, pmY - 1));                                                                               // left upper cell
            if (pmX < mWidth - 2 && pmY > 0)
                lvPoints.add(new Point(pmX + 1, pmY - 1));                                                                              // right upper cell
            if (pmX < mWidth - 2 && pmY < mHeight - 2)
                lvPoints.add(new Point(pmX + 1, pmY + 1));                                                                              // right lower cell
            if (pmX > 0 && pmY < mHeight - 2)
                lvPoints.add(new Point(pmX - 1, pmY + 1));                                                                              // left lower cell
        }

        return lvPoints;
    }

    /**
     * Determines cell state from its neighbourhood.
     *
     * @param pmX x coord of investigated cell
     * @param pmY y coord of investigated cell
     * @return cell with new or old state
     */
    public Cell newCellState(int pmX, int pmY) {
        Cell lvCell = mSpace.getState()[pmY][pmX];

        NeighbourhoodInfo lvNI = getNeighbourhoodInfo(pmX, pmY);

        return !lvCell.isAlive() && lvNI.getTotalCount() > 0 ? new Cell(true, lvNI.getMarkerOfLargestCount()) : lvCell;
    }

    /**
     * Fetches info about surrounding cells.
     *
     * @param pmX x coord of investigated cell
     * @param pmY y coord of investigated cell
     * @return information about surrounding cells
     */
    public NeighbourhoodInfo getNeighbourhoodInfo(int pmX, int pmY) {
        NeighbourhoodInfo lvNI = new NeighbourhoodInfo();

        Vector<Point> points = calculateNeighboursCoord(pmX, pmY);
        for (Point p : points) {
            final Cell currentCell = mSpace.getState()[p.y][p.x];
            if (!(currentCell instanceof Inclusion) && currentCell.isAlive() && !currentCell.isDisabled())
                lvNI.addCell(currentCell);
        }

        return lvNI;
    }

    public boolean isPeriodic() {
        return mPeriodic;
    }
}

package edu.sikora.ca.space;

import edu.sikora.ca.Constants;
import edu.sikora.ca.Point;
import edu.sikora.ca.cells.Cell;
import edu.sikora.ca.neighbourhoods.Neighbourhood;

import java.awt.*;
import java.util.HashMap;
import java.util.Observable;
import java.util.Random;

/**
 * @author Kamil Sikora
 *         Data: 20.06.14
 */
public class Space extends Observable implements Runnable {
    public final static double BOLZMANN = 0.00008617332;
    private int mHeight;
    private int mWidth;
    private int mTemperature = 720;
    private int mMCStates = 50;
    private HashMap<Long, Color> mMarkers;
    private TaskType mTaskType;
    private Neighbourhood mNeighbourhood;
    /**
     * If true, calculates next CA state.
     */
    private boolean mWorking;
    /**
     * If true, keeps thread alive.
     */
    private boolean mStayAlive;
    private Cell[][] mState;

    public Space(int pmHeight, int pmWidth, TaskType pmTaskType) {
        mHeight = pmHeight;
        mWidth = pmWidth;
        mTaskType = pmTaskType;

        mStayAlive = true;
        mMarkers = new HashMap<Long, Color>();
        mMarkers.put(Constants.INCLUSION_COLOR_ID, Constants.INCLUSION_COLOR);
        mMarkers.put(Constants.EMPTY_CELL_ID, Color.white);

        mState = new Cell[pmHeight][pmWidth];
        if (TaskType.GRAIN_GROWTH.equals(pmTaskType))
            populateSpace();
        if (TaskType.MONTE_CARLO.equals(pmTaskType))
            generateMCSpace();
    }

    /**
     * Randomly places grain for MC growth.
     */
    private void generateMCSpace() {
        //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * Fills state array with empty cells.
     */
    private void populateSpace() {
        for (int i = 0; i < mHeight; ++i)
            for (int j = 0; j < mWidth; ++j)
                mState[i][j] = new Cell(false, Constants.EMPTY_CELL_ID);
    }

    public void placeNewGrain(Point pmPoint) {

    }

    /**
     * Places grains randomly in space.
     *
     * @param pmGrainCount quantity of grains to place.
     */
    public void randomPlacement(final int pmGrainCount) {
        Random r = new Random(System.currentTimeMillis());
        for (int i = 0; i < pmGrainCount; ++i) {
            int x = r.nextInt(mWidth);
            int y = r.nextInt(mHeight);
            final long lvMarker = addNewMarker();
            mState[y][x] = new Cell(true, lvMarker);
        }
    }

    /**
     * Places grains in equal distance between them.
     *
     * @param pmGrainCount quantity of grains to place.
     */
    public void uniformPlacement(final int pmGrainCount) {
        int lvTemp = (int) Math.sqrt(pmGrainCount);
        int lvCols = lvTemp;
        int lvRows = lvTemp;
        int lvDiff = pmGrainCount - lvTemp * lvTemp;
        if (lvDiff != 0) {
            if (lvDiff % 2 == 0) {
                lvRows += lvDiff / 2;
                lvCols += lvDiff / 2;
            } else
                lvRows += lvDiff;
        }
        int lvRowSpan = mHeight / lvRows;
        int lvColSpan = mWidth / lvCols;

        int grainCount = 0;
        for (int i = 0; i < lvRows; ++i)
            for (int j = 0; j < lvCols; ++j) {
                if (grainCount == pmGrainCount)
                    break;
                int x = lvRowSpan / 2 + i * lvRowSpan;
                int y = lvColSpan / 2 + j * lvColSpan;
                mState[x][y] = new Cell(true, addNewMarker());
            }
    }

    /**
     * Adds new marker to list.
     *
     * @return unique cell marker.
     */
    private Long addNewMarker() {
        Random r = new Random(System.currentTimeMillis());
        boolean lvUnique = false;

        long lvMarker = 0L;
        while (!lvUnique) {
            Color lvColor = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
            lvMarker = r.nextLong();
            if (!mMarkers.containsKey(lvMarker)) {
                mMarkers.put(lvMarker, lvColor);
                lvUnique = true;
            }
        }

        return lvMarker;
    }

    @Override
    public void run() {
        setWorking(true);
        while (mStayAlive)
            if (mWorking) {

                if (TaskType.GRAIN_GROWTH.equals(mTaskType))
                    nextState();
                if (TaskType.MONTE_CARLO.equals(mTaskType))
                    nextMCStep();


                setChanged();
                notifyObservers();
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }

    /**
     * Calculates next MC step.
     */
    private void nextMCStep() {
        //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * Calculates next CA state.
     */
    private void nextState() {
        Cell[][] newSpace = new Cell[mHeight][mWidth];

        for (int i = 0; i < mHeight; ++i)
            for (int j = 0; j < mWidth; ++j)
                newSpace[i][j] = mNeighbourhood.newCellState(j, i, mState);
        mState = newSpace;
    }

    /**
     * It sets threads staying alive flag.
     */
    public void kill() {
        mStayAlive = false;
    }

    /**
     * Sets flag for job flow.
     *
     * @param pmWorking true continues job, false pauses it.
     */
    public void setWorking(final boolean pmWorking) {
        mWorking = pmWorking;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        return mWidth;
    }

    public Cell[][] getState() {
        return mState;
    }

    public Color getColor(final Long marker) {
        return mMarkers.get(marker);
    }
}

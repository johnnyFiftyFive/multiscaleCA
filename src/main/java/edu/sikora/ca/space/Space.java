package edu.sikora.ca.space;

import edu.sikora.ca.Constants;
import edu.sikora.ca.Point;
import edu.sikora.ca.cells.Cell;
import edu.sikora.ca.cells.Inclusion;
import edu.sikora.ca.neighbourhoods.MooreNeighbourhood;
import edu.sikora.ca.neighbourhoods.Neighbourhood;
import edu.sikora.ca.neighbourhoods.NeighbourhoodInfo;

import java.awt.*;
import java.util.HashMap;
import java.util.Observable;
import java.util.Random;
import java.util.Vector;

/**
 * @author Kamil Sikora
 *         Data: 20.06.14
 */
public class Space extends Observable implements Runnable {
    public final static double BOLZMANN = 0.00008617332;
    private final double JGB = 1.0;
    private Long mLastRecrystalizedColor;
    private int mHeight;
    private int mWidth;
    private double mTemperature = 720;
    private double mKbT = BOLZMANN * mTemperature;
    private int mMCStates = 50;
    private int mNucleiNumber = 50;
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
    private NucleationType mNucleationType;
    private boolean mHeterogeneusNucleation;

    public Space(int pmHeight, int pmWidth, TaskType pmTaskType) {
        mHeight = pmHeight;
        mWidth = pmWidth;
        mTaskType = pmTaskType;

        mStayAlive = true;
        mMarkers = new HashMap<Long, Color>();
        mMarkers.put(Constants.INCLUSION_COLOR_ID, Constants.INCLUSION_COLOR);
        mMarkers.put(Constants.EMPTY_CELL_ID, Color.white);
        mMarkers.put(Constants.RECRYSTALIZED_COLOR_ID, Constants.RECRYSTALIZED_COLOR);
        mLastRecrystalizedColor = Constants.RECRYSTALIZED_COLOR_ID;

        mState = new Cell[pmHeight][pmWidth];
        if (TaskType.GRAIN_GROWTH.equals(pmTaskType))
            populateSpace();
        if (TaskType.MONTE_CARLO.equals(pmTaskType))
            generateMCSpace();
    }

    /**
     * Returns darkened color.
     *
     * @param color    color to modify
     * @param fraction desired intensivity
     * @return new color
     */
    public static Color darkenColor(Color color, double fraction) {

        int red = (int) Math.round(Math.max(0, color.getRed() - 255 * fraction));
        int green = (int) Math.round(Math.max(0, color.getGreen() - 255 * fraction));
        int blue = (int) Math.round(Math.max(0, color.getBlue() - 255 * fraction));

        int alpha = color.getAlpha();

        return new Color(red, green, blue, alpha);
    }

    /**
     * Randomly places grain for MC growth.
     */
    private void generateMCSpace() {
        Random lvRandom = new Random(System.currentTimeMillis());

        mMarkers = new HashMap<Long, Color>();
        mMarkers.put(Constants.INCLUSION_COLOR_ID, Constants.INCLUSION_COLOR);
        mState = new Cell[mHeight][mWidth];

        for (int i = 0; i < mMCStates; ++i)
            addNewMarker();

        Long[] lvMarkers = new Long[mMCStates];

        lvMarkers = mMarkers.keySet().toArray(lvMarkers);

        for (int i = 0; i < mHeight; ++i)
            for (int j = 0; j < mWidth; ++j)
                mState[i][j] = new Cell(true, lvMarkers[lvRandom.nextInt(2000) % mMCStates]);
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

    /**
     * Adds new marker to list, in recrystalization color spectrum.
     *
     * @return unique cell marker.
     */
    private Long addNewRecrMarker() {
        Random lvRandom = new Random(System.currentTimeMillis());
        boolean lvUnique = false;

        Long lvNewMarker = null;
        while (!lvUnique) {
            lvNewMarker = lvRandom.nextLong();
            if (!mMarkers.containsKey(lvNewMarker))
                lvUnique = true;
        }

        Color lvNewColor = darkenColor(mMarkers.get(mLastRecrystalizedColor), 0.90);

        mMarkers.put(lvNewMarker, lvNewColor);
        mLastRecrystalizedColor = lvNewMarker;

        return lvNewMarker;
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
                if (TaskType.SRX.equals(mTaskType))
                    nextSRcStep();

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

    private void nextSRcStep() {
        distributeNucleons();
    }

    /**
     * Distributes new nucleons across the space during static recrystalization process.
     */
    private void distributeNucleons() {
        if (NucleationType.Constant.equals(mNucleationType)) {
            distributeNucleons(mNucleiNumber);
            mNucleiNumber = 0;
        }
    }

    private void distributeNucleons(final int pmNucleiNumber) {
        if (mHeterogeneusNucleation)
            distributeNucleonsHeterogenously(pmNucleiNumber);
        else
            distributeNucleonsHomogenously(pmNucleiNumber);
    }

    private void distributeNucleonsHomogenously(final int pmNucleiNumber) {
        Random lvRandom = new Random(System.currentTimeMillis());

        for (int i = 0; i < pmNucleiNumber; ++i) {
            boolean lvRecrystalized = false;
            int lvX = 0;
            int lvY = 0;
            while (!lvRecrystalized) {
                lvX = lvRandom.nextInt(mWidth);
                lvY = lvRandom.nextInt(mHeight);
                if (!mState[lvY][lvX].isRecrystalized())
                    lvRecrystalized = true;
            }

            final Long lvNewMarker = addNewRecrMarker();
            mState[lvY][lvX] = new Cell(true, true, lvNewMarker);
        }
    }

    private void distributeNucleonsHeterogenously(final int pmNucleiNumber) {
        Random lvRandom = new Random(System.currentTimeMillis());
        Vector<Point> lvBorderGrains = findBorderGrains();

        for (int i = 0; i < pmNucleiNumber; ++i) {
            Point lvPoint = null;
            while (lvPoint == null) {
                Point lvSelectedPoint = lvBorderGrains.get(lvRandom.nextInt(lvBorderGrains.size()));
                if (!mState[lvSelectedPoint.y][lvSelectedPoint.x].isRecrystalized())
                    lvPoint = lvSelectedPoint;
            }

            final Long lvNewMarker = addNewRecrMarker();
            mState[lvPoint.y][lvPoint.x] = new Cell(true, true, lvNewMarker);
        }
    }

    /**
     * Calculates next MC step.
     */
    private synchronized void nextMCStep() {
        Vector<Point> lvBorderGrains = findBorderGrains();
        Random lvRandom = new Random(System.currentTimeMillis());

        Cell[][] lvNewSpace = mState.clone();

        for (Point lvBorderGrain : lvBorderGrains) {
            Cell lvCurrentCell = lvNewSpace[lvBorderGrain.y][lvBorderGrain.x];
            NeighbourhoodInfo lvNI = mNeighbourhood.getNeighbourhoodInfo(lvBorderGrain.x, lvBorderGrain.y);


            Vector<Long> lvMarkers = lvNI.getNeighbourMarkers();
            if (lvMarkers.isEmpty())
                continue;
            int lvEnergy = lvNI.calculateEnergy(lvCurrentCell.getMarker());

            Long lvNewMarker = lvMarkers.get(lvRandom.nextInt(lvMarkers.size()));
            int lvNewEnergy = lvNI.calculateEnergy(lvNewMarker);

            int lvEnergyDiff = lvNewEnergy - lvEnergy;
            if (lvEnergyDiff <= 0) {
                lvCurrentCell.setMarker(lvNewMarker);
                continue;
            }

            if (lvRandom.nextDouble() < Math.exp(-1 * lvEnergyDiff / mKbT))
                lvCurrentCell.setMarker(lvNewMarker);

        }
        mState = lvNewSpace;
    }

    /**
     * Calculates next CA state.
     */
    private void nextState() {
        Cell[][] newSpace = new Cell[mHeight][mWidth];

        for (int i = 0; i < mHeight; ++i)
            for (int j = 0; j < mWidth; ++j)
                newSpace[i][j] = mNeighbourhood.newCellState(j, i);
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

    public void setNeighbourhood(Neighbourhood pmNeighbourhood) {
        mNeighbourhood = pmNeighbourhood;
    }

    /**
     * Method places inclusions in new space.
     *
     * @param pmInclusionNumber number of inclusions to place
     */
    public void placeInclusions(int pmInclusionNumber) {
        Random lvRandom = new Random(System.currentTimeMillis());
        for (int i = 0; i < pmInclusionNumber; i++) {
            int x = lvRandom.nextInt(mHeight);
            int y = lvRandom.nextInt(mWidth);
            Vector<Point> lvPoints = mNeighbourhood.calculateNeighboursCoord(x, y);

            mState[y][x] = new Inclusion();
            for (Point lvPoint : lvPoints) {
                mState[lvPoint.y][lvPoint.x] = new Inclusion();
            }
        }
    }

    public Vector<Point> findBorderGrains() {
        Vector<Point> lvPoints = new Vector<Point>();
        MooreNeighbourhood lvMoore = new MooreNeighbourhood(mNeighbourhood.isPeriodic(), this);

        for (int i = 0; i < mHeight; ++i) {
            for (int j = 0; j < mWidth; ++j) {
                if (!mState[i][j].isAlive())
                    continue;

                final Long lvCurrentCell = mState[i][j].getMarker();
              /*  if (!lvCurrentCell.equals(mState[i][((j + 1) % mWidth + mWidth) % mWidth].getMarker())
                        || !lvCurrentCell.equals(mState[((i + 1) % mHeight + mHeight) % mHeight][((j + 1) % mWidth + mWidth) % mWidth].getMarker())
                        || !lvCurrentCell.equals(mState[((i + 1) % mHeight + mHeight) % mHeight][j].getMarker())) {
                    lvPoints.add(new Point(j, i));
                }*/
                Vector<Point> lvNeighbours = lvMoore.calculateNeighboursCoord(j, i);
                for (Point lvNeighbour : lvNeighbours) {
                    if (!lvCurrentCell.equals(mState[lvNeighbour.y][lvNeighbour.x].getMarker()))
                        lvPoints.add(new Point(j, i));
                }
            }
        }

        return lvPoints;
    }

    /**
     * Disables all cells characterized by particular marker.
     *
     * @param pmSelectedMarker marker to disable.
     */
    public synchronized void setAllDisabled(final Long pmSelectedMarker) {
        for (int i = 0; i < mHeight; ++i)
            for (int j = 0; j < mWidth; ++j)
                if (mState[i][j].getMarker().equals(pmSelectedMarker))
                    mState[i][j].setDisabled(true);

        setChanged();
        notifyObservers();
    }

    /**
     * Heterogenously distributes energy across the space.
     */
    public void distributeEnergy() {
        Vector<Point> lvBorderGrains = findBorderGrains();
        for (Point lvBorderGrain : lvBorderGrains) {
            mState[lvBorderGrain.y][lvBorderGrain.x].setEnergy(100);
        }

    }

    public void setTemperature(double pmTemperature) {
        mTemperature = pmTemperature;
        mKbT = BOLZMANN * mTemperature;
    }

    public double getTemperature() {
        return mTemperature;
    }

    public void setTemperature(int pmTemperature) {
        mTemperature = pmTemperature;
    }

    public void setGeneratedGrains(int pmGeneratedGrains) {
        mMCStates = pmGeneratedGrains;
    }

    public void setGrainGrowth() {
        mTaskType = TaskType.GRAIN_GROWTH;
    }

    public void setMonteCarlo() {
        mTaskType = TaskType.MONTE_CARLO;
    }

    public void setSRX() {
        mTaskType = TaskType.SRX;
    }

    public void setNucleiNumber(int pmNucleiNumber) {
        mNucleiNumber = pmNucleiNumber;
    }

    public NucleationType getNucleationType() {
        return mNucleationType;
    }

    public void setNucleationType(NucleationType pmNucleationType) {
        mNucleationType = pmNucleationType;
    }

    public boolean isHeterogeneusNucleation() {
        return mHeterogeneusNucleation;
    }

    public void setHeterogeneusNucleation(boolean pmHeterogeneusNucleation) {
        mHeterogeneusNucleation = pmHeterogeneusNucleation;
    }
}

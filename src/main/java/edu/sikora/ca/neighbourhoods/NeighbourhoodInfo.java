package edu.sikora.ca.neighbourhoods;

import edu.sikora.ca.cells.Cell;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * @author Kamil Sikora
 *         Data: 13.04.14
 */
public class NeighbourhoodInfo {
    private HashMap<Long, Integer> mCells;
    private int mTotalCount = 0;

    NeighbourhoodInfo() {
        mCells = new HashMap<Long, Integer>();
    }

    public void addCell(final Cell cell) {
        if (mCells.containsKey(cell.getMarker())) {
            Integer count = mCells.get(cell.getMarker());
            mCells.put(cell.getMarker(), ++count);
        } else
            mCells.put(cell.getMarker(), 1);

        ++mTotalCount;
    }

    public int getCountOf(final Cell cell) {
        return mCells.get(cell.getMarker()) == null ? 0 : mCells.get(cell.getMarker());
    }

    public long getMarkerOfLargestCount() {
        Long lvMarker = null;
        int lvBiggestCount = 0;

        Set<Map.Entry<Long, Integer>> lvEntries = mCells.entrySet();
        for (Map.Entry<Long, Integer> lvEntry : lvEntries)
            if (lvEntry.getValue() > lvBiggestCount) {
                lvBiggestCount = lvEntry.getValue();
                lvMarker = lvEntry.getKey();
            }

        return lvMarker;
    }

    public int getLargestCount() {
        int lvBiggestCount = 0;

        Set<Map.Entry<Long, Integer>> lvEntries = mCells.entrySet();
        for (Map.Entry<Long, Integer> lvEntry : lvEntries)
            if (lvEntry.getValue() > lvBiggestCount) {
                lvBiggestCount = lvEntry.getValue();
            }

        return lvBiggestCount;
    }

    public int getmTotalCount() {
        return mTotalCount;
    }

    /**
     * @return information how many cells of specific kind surrounds investigated cell.
     */
    public HashMap<Long, Integer> getCellInfo() {
        return mCells;
    }

    public int calculateEnergy(final Long pmMarker) {
        Integer lvCount = mCells.get(pmMarker);

        return 8 - (lvCount != null ? lvCount : 0);
    }

    public Vector<Long> getNeighbourMarkers() {
        return new Vector<Long>(mCells.keySet());
    }
}

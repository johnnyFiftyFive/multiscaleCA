package edu.sikora.ca.cells;

import java.util.Random;

/**
 * @author Kamil Sikora
 *         Data: 11.03.14
 */
public class Cell {
    private boolean mAlive;
    private boolean mDisabled = false;
    private boolean mRecrystalized = false;
    private Long mMarker;
    private int mEnergy = 0;

    @Deprecated
    public Cell(boolean alive) {
        this.mAlive = alive;
        Random r = new Random(System.currentTimeMillis());
        mMarker = new Long(r.nextLong());
    }

    public Cell(boolean alive, Long pmMarker) {
        this.mAlive = alive;
        this.mMarker = pmMarker;
    }

    public Cell(boolean pmAlive, boolean pmRecrystalized, Long pmMarker) {
        this.mAlive = pmAlive;
        this.mRecrystalized = pmRecrystalized;
        this.mMarker = pmMarker;
    }

    public boolean isAlive() {
        return mAlive;
    }

    public void setAlive(boolean alive) {
        this.mAlive = alive;
    }

    @Override
    public String toString() {
        return mAlive ? "X" : "×";
    }

    public Long getMarker() {
        return mMarker;
    }

    public void setMarker(Long pmMarker) {
        mMarker = pmMarker;
    }

    public boolean isDisabled() {
        return mDisabled;
    }

    public void setDisabled(boolean pmDisabled) {
        mDisabled = pmDisabled;
    }

    public int getEnergy() {
        return mEnergy;
    }

    public void setEnergy(int pmEnergy) {
        mEnergy = pmEnergy;
    }

    public boolean isRecrystalized() {
        return mRecrystalized;
    }

    public void setRecrystalized(final boolean pmRecrystalized) {
        mRecrystalized = pmRecrystalized;
    }
}

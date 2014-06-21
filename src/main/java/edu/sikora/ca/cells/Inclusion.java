package edu.sikora.ca.cells;

import edu.sikora.ca.Constants;

/**
 * @author Kamil Sikora
 *         Data: 11.03.14
 */
public class Inclusion extends Cell {
    public Inclusion() {
        super(true, Constants.INCLUSION_COLOR_ID);
    }

    /**
     * Inclusion does not change during process, so it's alive.
     *
     * @param alive
     */
    @Override
    public void setAlive(boolean alive) {
        super.setAlive(true);
    }
}

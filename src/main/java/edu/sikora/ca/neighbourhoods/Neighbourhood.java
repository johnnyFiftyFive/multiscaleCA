package edu.sikora.ca.neighbourhoods;

import edu.sikora.ca.cells.Cell;
import edu.sikora.ca.space.Space;

import java.util.Observable;
import java.util.Observer;

/**
 * @author Kamil Sikora
 *         Data: 21.06.14
 */
public abstract class Neighbourhood {
    private Space mSpace;

    public Cell newCellState(int pmX, int pmY, Cell[][] pmState) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}

package edu.sikora.ca.view;

import edu.sikora.ca.cells.Cell;
import edu.sikora.ca.space.Space;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * @author Kamil Sikora
 *         Data: 21.03.14
 */
public class SpaceCanvas extends JPanel implements Observer {
    private static final int MAX_WIDTH = 1500;
    private static final int MAX_HEIGHT = 800;
    private int cellWidth;
    private int cellHeight;
    private int width;
    private int height;
    private Mode mCanvasMode;
    private Space space;

    public SpaceCanvas(final Space space) {
        this.space = space;
        this.width = space.getWidth();
        this.height = space.getHeight();

        assignNewCellDimensions(width, height);

        mCanvasMode = Mode.PLACE_GRAIN;

        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setDoubleBuffered(true);
        addMouseListener(new CanvasClickListener());
    }

    private void assignNewCellDimensions(final int width, final int height) {
        int yDim = MAX_HEIGHT / height;
        int xDim = MAX_WIDTH / width;

        if (yDim == 0)
            yDim = 1;


        while (yDim * width > MAX_WIDTH && yDim != 1) {
            --yDim;
        }

        cellWidth = cellHeight = yDim;

        setPreferredSize(new Dimension(yDim * width, yDim * height));
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        final Cell[][] state = space.getState();


//        int cordDown = rowHeight /*- 1*/; //(rows < 90 ? rowHt - 1 : rowHt);
        for (int i = 0; i < space.getHeight(); ++i)
            for (int j = 0; j < space.getWidth(); ++j) {
                g.setColor(state[i][j].isAlive() ? space.getColor(state[i][j].getMarker()) : Color.white);
                g.fillRect(i * cellWidth, j * cellHeight, cellWidth, cellHeight);
            }

      /*  Vector<edu.sikora.ca.Point> lvBorderGrains = space.findBorderGrains();
        g.setColor(edu.sikora.ca.Constants.BORDER_COLOR);
        for (edu.sikora.ca.Point lvBorderGrain : lvBorderGrains) {

            g.fillRect(lvBorderGrain.y * cellHeight, lvBorderGrain.x * cellWidth, cellWidth, cellHeight);
        }*/

    }

    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }

    private edu.sikora.ca.Point calculateSpacePoint(final Point pmPoint) {
        int x = (int) (pmPoint.getX() / cellWidth);
        int y = (int) (pmPoint.getY() / cellHeight);

        edu.sikora.ca.Point lvPoint = new edu.sikora.ca.Point(y, x);

        return lvPoint;
    }

    public void setCanvasMode(Mode pmCanvasMode) {
        mCanvasMode = pmCanvasMode;
    }

    public enum Mode {
        PLACE_GRAIN,
        PLACE_INCLUSION,
        SELECT_GRAIN
    }

    private class CanvasClickListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            edu.sikora.ca.Point lvPoint = calculateSpacePoint(e.getPoint());
            System.out.println(e.getPoint());
            System.out.println(lvPoint);

            if (Mode.PLACE_GRAIN.equals(mCanvasMode)) {
                space.placeNewGrain(lvPoint);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

    }
}

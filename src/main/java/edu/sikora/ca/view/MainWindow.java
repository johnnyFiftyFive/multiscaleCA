package edu.sikora.ca.view;

import edu.sikora.ca.Constants;
import edu.sikora.ca.space.Space;
import edu.sikora.ca.space.neighbourhoods.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Observer;

/**
 * @author Kamil Sikora
 *         Data: 13.04.14
 */
public class MainWindow {
    private JPanel sizePanel;
    private JPanel spacePanel;
    private JTextField widthField;
    private JTextField heightField;
    private JComboBox neighbourhoodsBox;
    private JPanel mainPanel;
    private JRadioButton periodicRB;
    private JRadioButton absorbentRB;
    private JTextField initialGrainCountField;
    private JPanel buttonPanel;
    private JButton startButton;
    private JPanel canvasPanel;
    private JButton stopButton;
    private JTextField mInclusionsField;
    private JButton resetButton;
    private JTextField mTemperatureField;
    private JTextField mGeneratedGrainsField;
    private JRadioButton rbtGrainGrowth;
    private JRadioButton rbtMonteCarlo;
    private JRadioButton rbtSRX;
    private Space automataSpace;
    private Thread mSpaceThread;

    public MainWindow() {
        final JFrame frame = new JFrame("MainWindow");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        populateComboboxes();

        frame.pack();
        frame.setVisible(true);


        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mSpaceThread == null) {

                    canvasPanel.remove(spacePanel);
                    automataSpace.deleteObserver((Observer) spacePanel);

                    HashMap<String, Object> spaceParameters = getParametersFromComponents();
                    Integer width = (Integer) spaceParameters.get(Constants.WIDTH);
                    Integer height = (Integer) spaceParameters.get(Constants.HEIGHT);

                    Space.TaskType lvTaskType = Space.TaskType.GRAIN_GROWTH;
                    if (rbtMonteCarlo.isSelected())
                        lvTaskType = Space.TaskType.MONTE_CARLO;
                    if (rbtSRX.isSelected())
                        lvTaskType = Space.TaskType.SRX;

                    automataSpace = new Space(height, width, lvTaskType);
                    automataSpace.randomPlacement(Integer.parseInt(initialGrainCountField.getText()));

                    boolean lvPeriodicBorderCondition = true;
                    if (absorbentRB.isSelected())
                        lvPeriodicBorderCondition = false;

                    NeighbourhoodEnum lvSelectedNeighbourhood = (NeighbourhoodEnum) neighbourhoodsBox.getSelectedItem();
                    if (NeighbourhoodEnum.VonNeumann.equals(lvSelectedNeighbourhood))
                        automataSpace.setNeighbourhood(new VonNeumannNeighbourhood(height, width, lvPeriodicBorderCondition, automataSpace));
                    if (NeighbourhoodEnum.Moore.equals(lvSelectedNeighbourhood))
                        automataSpace.setNeighbourhood(new MooreNeighbourhood(height, width, lvPeriodicBorderCondition, automataSpace));
                    if (NeighbourhoodEnum.ExtendedMoore.equals(lvSelectedNeighbourhood))
                        automataSpace.setNeighbourhood(new ExtendedMooreNeighbourhood(height, width, lvPeriodicBorderCondition, automataSpace));
                    if (NeighbourhoodEnum.Pentagonal.equals(lvSelectedNeighbourhood))
                        automataSpace.setNeighbourhood(new PentagonalNeighbourhood(height, width, lvPeriodicBorderCondition, automataSpace));
                    if (NeighbourhoodEnum.Hexagonal.equals(lvSelectedNeighbourhood))
                        automataSpace.setNeighbourhood(new HexagonalNeighbourhood(height, width, lvPeriodicBorderCondition, automataSpace));

                    spacePanel = new SpaceCanvas(automataSpace);
                    automataSpace.placeInclusions(Integer.parseInt(mInclusionsField.getText()));
                    automataSpace.setTemperature(Double.parseDouble(mTemperatureField.getText()));
                    automataSpace.setGeneratedGrains(Integer.parseInt(mGeneratedGrainsField.getText()));
                    automataSpace.addObserver((SpaceCanvas) spacePanel);

                    canvasPanel.add(spacePanel, BorderLayout.CENTER);
                    canvasPanel.validate();
                    frame.pack();

                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    resetButton.setEnabled(false);
                    mSpaceThread = new Thread(automataSpace);
                    mSpaceThread.start();
                } else {
                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    automataSpace.setWorking();
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                resetButton.setEnabled(true);

                automataSpace.setNotWorking();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                automataSpace.kill();
                mSpaceThread = null;
            }
        });

        ActionListener lvFunctionListener = new

                ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (rbtGrainGrowth.isSelected())
                            automataSpace.setGrainGrowth();
                        if (rbtMonteCarlo.isSelected())
                            automataSpace.setMonteCarlo();
                        if (rbtSRX.isSelected())
                            automataSpace.setSRX();
                    }
                };
        rbtGrainGrowth.addActionListener(lvFunctionListener);
        rbtMonteCarlo.addActionListener(lvFunctionListener);
        rbtSRX.addActionListener(lvFunctionListener);
    }

    public static void main(String[] args) {
        new MainWindow();
    }

    private void createUIComponents() {
        widthField = new LimitedJTextField("10", 5);
        heightField = new JTextField("10", 5);
        initialGrainCountField = new LimitedJTextField("5", 5);
        mInclusionsField = new LimitedJTextField("0", 5);
        mTemperatureField = new LimitedJTextField("720", 5);
        mGeneratedGrainsField = new LimitedJTextField("50", 5);

        automataSpace = new Space(100, 100, Space.TaskType.GRAIN_GROWTH);
        automataSpace.setNeighbourhood(new HexagonalNeighbourhood(100, 100, true, automataSpace));
        spacePanel = new SpaceCanvas(automataSpace);
        automataSpace.addObserver((SpaceCanvas) spacePanel);
    }

    private HashMap<String, Object> getParametersFromComponents() {
        HashMap<String, Object> parameters = new HashMap<String, Object>();

        parameters.put(Constants.WIDTH, Integer.valueOf(widthField.getText()));
        parameters.put(Constants.HEIGHT, Integer.valueOf(heightField.getText()));
        parameters.put(Constants.INITIAL_GRAIN_COUNT, Integer.valueOf(widthField.getText()));
        parameters.put(Constants.NEIGHBOURHOOD, Integer.valueOf(widthField.getText()));
        parameters.put(Constants.BONDUARY_CONDITION, Integer.valueOf(widthField.getText()));

        return parameters;
    }

    private void populateComboboxes() {
        for (NeighbourhoodEnum e : NeighbourhoodEnum.values())
            neighbourhoodsBox.addItem(e);
    }
}

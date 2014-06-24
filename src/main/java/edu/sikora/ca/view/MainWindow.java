package edu.sikora.ca.view;

import edu.sikora.ca.Constants;
import edu.sikora.ca.neighbourhoods.*;
import edu.sikora.ca.space.NucleationType;
import edu.sikora.ca.space.Space;
import edu.sikora.ca.space.TaskType;

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
    private JRadioButton rbRandomPlacement;
    private JRadioButton rbUniformPlacement;
    private JPanel plSRX;
    private JButton btEnergyDistribution;
    private JRadioButton rbHomogoneus;
    private JRadioButton rbHeterogenous;
    private JTextField tfNucleiNumber;
    private JComboBox cbNucleation;
    private Space automataSpace;
    private Thread mSpaceThread;

    public MainWindow() {
        final JFrame frame = new JFrame("MainWindow");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        populateComboboxes();
//        unlockSrxPanel(false);

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

                    TaskType lvTaskType = TaskType.GRAIN_GROWTH;
                    if (rbtMonteCarlo.isSelected())
                        lvTaskType = TaskType.MONTE_CARLO;
                    if (rbtSRX.isSelected()) {
                        lvTaskType = TaskType.SRX;
                        automataSpace.distributeEnergyHeterogenously();
                    }

                    automataSpace = new Space(height, width, lvTaskType);
                    if (rbRandomPlacement.isSelected())
                        automataSpace.randomPlacement(Integer.parseInt(initialGrainCountField.getText()));
                    else
                        automataSpace.uniformPlacement(Integer.parseInt(initialGrainCountField.getText()));

                    boolean lvPeriodicBorderCondition = true;
                    if (absorbentRB.isSelected())
                        lvPeriodicBorderCondition = false;

                    NeighbourhoodEnum lvSelectedNeighbourhood = (NeighbourhoodEnum) neighbourhoodsBox.getSelectedItem();
                    if (NeighbourhoodEnum.VonNeumann.equals(lvSelectedNeighbourhood))
                        automataSpace.setNeighbourhood(new VonNeumannNeighbourhood(lvPeriodicBorderCondition, automataSpace));
                    if (NeighbourhoodEnum.Moore.equals(lvSelectedNeighbourhood))
                        automataSpace.setNeighbourhood(new MooreNeighbourhood(lvPeriodicBorderCondition, automataSpace));
                    if (NeighbourhoodEnum.ExtendedMoore.equals(lvSelectedNeighbourhood))
                        automataSpace.setNeighbourhood(new ExtendedMooreNeighbourhood(lvPeriodicBorderCondition, automataSpace));
                    if (NeighbourhoodEnum.Pentagonal.equals(lvSelectedNeighbourhood))
                        automataSpace.setNeighbourhood(new PentagonalNeighbourhood(lvPeriodicBorderCondition, automataSpace));
                    if (NeighbourhoodEnum.Hexagonal.equals(lvSelectedNeighbourhood))
                        automataSpace.setNeighbourhood(new HexagonalNeighbourhood(lvPeriodicBorderCondition, automataSpace));

                    spacePanel = new SpaceCanvas(automataSpace);
                    automataSpace.placeInclusions(Integer.parseInt(mInclusionsField.getText()));
                    automataSpace.setTemperature(Double.parseDouble(mTemperatureField.getText()));
                    automataSpace.setGeneratedGrains(Integer.parseInt(mGeneratedGrainsField.getText()));
                    automataSpace.setNucleiNumber(Integer.parseInt(tfNucleiNumber.getText()));
                    automataSpace.setNucleationType((NucleationType) cbNucleation.getSelectedItem());
                    automataSpace.setHeterogeneusNucleation(rbHeterogenous.isSelected());
                    automataSpace.addObserver((SpaceCanvas) spacePanel);

                    canvasPanel.add(spacePanel, BorderLayout.CENTER);
                    canvasPanel.validate();
                    frame.pack();

                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    resetButton.setEnabled(false);
                    mSpaceThread = new Thread(automataSpace);
                    mSpaceThread.start();
                    ((SpaceCanvas) spacePanel).setCanvasMode(SpaceCanvas.Mode.SELECT_GRAIN);
                } else {
                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    automataSpace.setWorking(true);
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                resetButton.setEnabled(true);

                automataSpace.setWorking(false);
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
        ActionListener listener = new

                ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (rbHomogoneus.isSelected())
                            automataSpace.setHeterogeneusNucleation(false);
                        else
                            automataSpace.setHeterogeneusNucleation(true);
                    }
                };
        rbHomogoneus.addActionListener(listener);
        rbHeterogenous.addActionListener(listener);
        btEnergyDistribution.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((SpaceCanvas) spacePanel).switchEnergyView();
            }
        });
    }

    public static void main(String[] args) {
        new MainWindow();
    }

    /**
     * Changes access to SRX panel components.
     *
     * @param pmLock true for lock components, false to enable.
     */
    private void unlockSrxPanel(final boolean pmLock) {
        Component[] lvComponents = plSRX.getComponents();
        for (Component lvComponent : lvComponents) {
            lvComponent.setEnabled(pmLock);
        }

    }

    private void createUIComponents() {
        widthField = new LimitedJTextField("200", 5);
        heightField = new JTextField("200", 5);
        initialGrainCountField = new LimitedJTextField("100", 5);
        mInclusionsField = new LimitedJTextField("0", 5);
        mTemperatureField = new LimitedJTextField("720", 5);
        mGeneratedGrainsField = new LimitedJTextField("50", 5);
        tfNucleiNumber = new LimitedJTextField("50", 5);

        automataSpace = new Space(100, 100, TaskType.GRAIN_GROWTH);
        automataSpace.setNeighbourhood(new MooreNeighbourhood(true, automataSpace));
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

        for (NucleationType lvNucleationType : NucleationType.values()) {
            cbNucleation.addItem(lvNucleationType);
        }

    }
}

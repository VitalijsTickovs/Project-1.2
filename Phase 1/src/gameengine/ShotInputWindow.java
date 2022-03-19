package gameengine;

import Data_storage.InputPanel;
import Data_storage.Vector2;
import GUI.InterfaceFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ShotInputWindow {
    public Game game;
    public boolean isOpen;

    /**
     * Constructor. Creates a new ShotInput
     */
    public ShotInputWindow() {
        isOpen = false;
    }

    /**
     * Opens a JFrame window with a velocity input option
     */
    public void openWindow() {
        JFrame frame = createFrame();
        JPanel mainPanel = new JPanel();
        InputPanel xInputPanel = createRowXVelocityInputPanel();
        InputPanel yInputPanel = createRowYVelocityInputPanel();
        // Set up shoot button row
        JPanel buttonPanel = new JPanel();
        JButton shootButton = createShootButton(xInputPanel,yInputPanel,frame);
        frame.dispose();

        mainPanel.add(xInputPanel.panel, BorderLayout.NORTH);
        mainPanel.add(yInputPanel.panel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JFrame createFrame() {
        Vector2 frameSize = new Vector2(300, 170);
        Vector2 framePosition = new Vector2(frameSize.x, game.frame.getY() + game.frame.getHeight());
        JFrame frame = InterfaceFactory.createFrame("Input shot velocity", frameSize, false, framePosition, game);
        isOpen = true;

        return frame;
    }

    private InputPanel createRowXVelocityInputPanel() {
        return InterfaceFactory.createInputPanel("x-velocity = ", 20);
    }

    private InputPanel createRowYVelocityInputPanel() {
        return InterfaceFactory.createInputPanel("y-velocity = ", 20);
    }

    private JButton createShootButton(InputPanel xInputPanel, InputPanel yInputPanel, JFrame frame) {
        ActionListener buttonAction = createShootActionListener(xInputPanel, yInputPanel, frame);
        JButton shootButton = InterfaceFactory.createButton("SHOOT!", buttonAction, frame);
        frame.add(shootButton);
        return shootButton;
    }

    private ActionListener createShootActionListener(InputPanel xInputPanel, InputPanel yInputPanel, JFrame frame) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    game.shot = readShotVector(xInputPanel, yInputPanel);
                    frame.setVisible(false);
                    frame.dispose();
                    isOpen = false;
                } catch (Exception e) {
                    System.out.println("Velocities must be floating point values.");
                }
            }
        };

    }

    private Vector2 readShotVector(InputPanel xInputPanel, InputPanel yInputPanel) {
        Vector2 shotVector = new Vector2();
        shotVector.x = Double.parseDouble(xInputPanel.textField.getText());
        shotVector.y = Double.parseDouble(yInputPanel.textField.getText());
        return shotVector;
    }
}

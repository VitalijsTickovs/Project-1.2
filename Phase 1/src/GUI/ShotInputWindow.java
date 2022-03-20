package GUI;

import Data_storage.InputPanel;
import Data_storage.Vector2;
import gameengine.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ShotInputWindow {
    public Game game;
    public boolean isOpen;

    private JFrame frame;
    private boolean wasCreated = false;

    // region Startup
    /**
     * Constructor. Creates a new ShotInput
     */
    public ShotInputWindow(Game game) {
        isOpen = false;
        this.game = game;
    }
    // endregion

    /**
     * Creates a JFrame window with a velocity input option
     */
    public void openWindow() {
        if (!wasCreated) {
            createWindow();
            wasCreated = true;
        }
        if (!isOpen) {
            frame.setVisible(true);
            isOpen = true;
        }
    }

    private void createWindow() {
        frame = createFrame();
        JPanel mainPanel = new JPanel();
        InputPanel xInputPanel = createXVelocityInputPanel();
        InputPanel yInputPanel = createYVelocityInputPanel();
        // Set up shoot button row
        JPanel buttonPanel = new JPanel();
        JButton shootButton = createShootButton(xInputPanel, yInputPanel, frame);
        buttonPanel.add(shootButton);
        frame.dispose();

        mainPanel.add(xInputPanel.panel, BorderLayout.NORTH);
        mainPanel.add(yInputPanel.panel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);

    }

    private JFrame createFrame() {
        Vector2 frameSize = new Vector2(300, 170);
        Vector2 framePosition = new Vector2(frameSize.x * 2, game.frame.getY() + game.frame.getHeight());
        JFrame frame = InterfaceFactory.createFrame("Input shot velocity", frameSize, false, framePosition, game);
        isOpen = true;

        return frame;
    }

    private InputPanel createXVelocityInputPanel() {
        return InterfaceFactory.createInputPanel("x-velocity = ", 20);
    }

    private InputPanel createYVelocityInputPanel() {
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
                    game.shotVector = readShotVector(xInputPanel, yInputPanel);
                    frame.setVisible(false);
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

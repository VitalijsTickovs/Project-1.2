package gameengine;

import Data_storage.Vector2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ShotInput {
    private JFrame frame;
    public Game game;
    public boolean isOpen;

    public ShotInput() {
        isOpen = false;
    }

    public void openWindow() {
        isOpen = true;
        frame = new JFrame();
        frame.setTitle("Input shot velocity");
        frame.setSize(300, 170);
        frame.setLocationRelativeTo(game);
        frame.setLocation(frame.getX(), game.frame.getY()+game.frame.getHeight());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        JPanel row1 = new JPanel();
        row1.add(new JLabel("x-velocity = "), BorderLayout.WEST);
        JTextField xTextField = new JTextField(20);
        row1.add(xTextField, BorderLayout.EAST);

        JPanel row2 = new JPanel();
        row2.add(new JLabel("y-velocity = "), BorderLayout.WEST);
        JTextField yTextField = new JTextField(20);
        row2.add(yTextField, BorderLayout.EAST);

        JPanel row3 = new JPanel();
        JButton shoot = new JButton("SHOOT!");
        shoot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    double x = Double.parseDouble(xTextField.getText());
                    double y = Double.parseDouble(yTextField.getText());
                    game.shot = new Vector2(x, y);
                    frame.setVisible(false);
                    frame.dispose();
                    isOpen = false;
                } catch (Exception e) {
                    System.out.println("Velocities must be floating point values.");
                }
            }
        });
        row3.add(shoot);

        mainPanel.add(row1, BorderLayout.NORTH);
        mainPanel.add(row2, BorderLayout.CENTER);
        mainPanel.add(row3, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        ShotInput i = new ShotInput();
        i.openWindow();
    }
}

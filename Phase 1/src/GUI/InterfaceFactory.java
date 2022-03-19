package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import Data_storage.InputPanel;
import Data_storage.Vector2;

public class InterfaceFactory {
    /**
     * Creates a window positioned relative to another component
     */
    public static JFrame createFrame(String title, Vector2 size, boolean setResizable, Vector2 posRelativeTo,
            Component component) {
        JFrame frame = new JFrame();
        frame.setTitle(title);
        frame.setSize((int) size.x, (int) size.y);
        frame.setLocationRelativeTo(component);
        frame.setLocation((int) posRelativeTo.x, (int) posRelativeTo.y);
        frame.setResizable(setResizable);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        return frame;
    }

    /**
     * Creates a window positioned relative to some point in the window (the frame's
     * parent)
     */
    public static JFrame createFrame(String title, Vector2 size, boolean setResizable, Vector2 position) {
        JFrame frame = new JFrame();
        frame.setTitle(title);
        frame.setSize((int) size.x, (int) size.y);
        frame.setResizable(setResizable);
        frame.setLocation((int) position.x, (int) position.y);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        return frame;
    }

    /**
     * Creates
     */
    public static InputPanel createInputPanel(String title, int textColumns) {
        JPanel panel = new JPanel();
        panel.add(new JLabel(title), BorderLayout.EAST);
        JTextField textField = new JTextField(textColumns);
        panel.add(textField, BorderLayout.EAST);
        return new InputPanel(panel, textField);
    }

    /**
     * Creates a button with a text, adds a function to be called on-click and adds the button to a frame
     */
    public static JButton createButton(String title, ActionListener listener, JFrame frame){
        JButton button = new JButton(title);
        button.addActionListener(listener);

        return button;
    }
}

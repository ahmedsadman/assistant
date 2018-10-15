import ui.App;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Assistant");
        frame.setContentPane(new App().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        App.centerWindow(frame);
        frame.setVisible(true);
    }
}

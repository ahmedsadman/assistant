package ui;

import javax.swing.*;

public class App {
    private JPanel rootPanel;
    private JTable table1;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel centerPanel;
    private JTable table2;

    private void createUIComponents() {
        // TODO: place custom component creation code here
        String[] columnNames = {"First Name", "Last Name"};
        Object[][] data = {{"Kathy", "Smith"},{"John", "Doe"}};
        this.table1 = new JTable(data, columnNames);
        this.table2 = new JTable(data, columnNames);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new App().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


}

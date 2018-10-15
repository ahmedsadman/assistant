package ui;

import core.Database;

import javax.swing.*;
import java.awt.event.*;

public class AddEventDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField eventDesc;
    private JTextField eventDate;
    private Database db;

    public AddEventDialog() {
        db = Database.getdb();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        String event_date = eventDate.getText();
        String event_desc = eventDesc.getText();

        if (event_desc.equals("")) {
            JOptionPane.showMessageDialog(null, "Please fill up the description field",
                    "Date format error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (! event_date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(null, "Invalid date format. " +
                            "Please use YYYY-MM-DD format. For example, 2018-01-11", "Date format error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (db.insertEvent(event_desc, event_date)) {
            JOptionPane.showMessageDialog(null, "Event created successfully",
                    "Confirmation", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "The event could not be created",
                    "Error", JOptionPane.INFORMATION_MESSAGE);
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        AddEventDialog dialog = new AddEventDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}

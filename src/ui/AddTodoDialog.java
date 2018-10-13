package ui;

import core.Database;

import javax.swing.*;
import java.awt.event.*;

public class AddTodoDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textTodo;
    private Database db;

    public AddTodoDialog() {
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
        String desc = textTodo.getText();
        if (desc.equals("")) {
            JOptionPane.showMessageDialog(null, "Please fill up the field", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (db.insertTodo(desc)) {
            JOptionPane.showMessageDialog(null, "Task added", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Task could not be added", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        AddTodoDialog dialog = new AddTodoDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}

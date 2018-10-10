package ui;

import core.Database;
import core.ProthomAloScrapper;
import core.WeatherData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.awt.Desktop;

public class App {
    private JPanel rootPanel;
    private JTable table1;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel centerPanel;
    private JButton markAsDoneButton;
    private JTable table2;
    private JTable newsTable;
    private JButton visitButton;
    private JLabel weatherLabel;
    private JButton addTodoButton;
    private JButton addButton1;
    private JButton removeButton;
    private JTextField updateField;
    private JButton updateButton;
    private JLabel tempValue;
    private JLabel locName;
    private JLabel weatherCondition;
    private JLabel humidity;
    private JLabel pressureValue;
    DefaultTableModel newsModel;

    private ProthomAloScrapper sc;
    private WeatherData wd;
    private Database db;

    public App() {
        this.sc = new ProthomAloScrapper();
        this.wd = new WeatherData();
        this.db = new Database();
        this.updateNewsTable();


        markAsDoneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                weatherLabel.setText("Done");
            }
        });

        visitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = newsTable.getSelectedRow();
                String headline = newsTable.getModel().getValueAt(row, 0).toString();
                HashMap<String, String> newsIndex = sc.getHeadlineTags();

                // open in a web browser
                try {
                    Desktop.getDesktop().browse(new URI(newsIndex.get(headline)));
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("update button clicked");
                String loc = updateField.getText();
                db.updateWeatherLocation(loc);
                wd.updateData(loc);
                locName.setText(wd.getCity_name());
                String temp = WeatherData.roundValue(wd.getTemperature());
                String pressure = WeatherData.roundValue(wd.getPressure());
                tempValue.setText(temp + " C");
                weatherCondition.setText(wd.getWeather_type());
                humidity.setText(String.valueOf(wd.getHumidity()) + "%");
                pressureValue.setText(pressure + " Pa");
            }
        });

        addTodoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddTodoDialog dialog = new AddTodoDialog();
                dialog.setTitle("Create new task");
                dialog.pack();
                App.centerWindow(dialog);
                dialog.setVisible(true);
            }
        });

        // get the weather data, depends on updateButton event so should not be moved from here
        this.getWeatherData();
    }

    private void updateNewsTable() {

        // fetch and update the news table
        HashMap<String, String> headlineTags = sc.getHeadlineTags();
        Iterator it = headlineTags.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            newsModel.addRow(new Object[] {pair.getKey(), pair.getValue()});
        }
        this.setColumnWidth(newsTable);
    }

    private void getWeatherData() {
        String loc = this.db.getWeatherLocation();
        updateField.setText(loc);
        updateButton.doClick();
    }

    // Dynamically resizes a table columns based on max-width
    private void setColumnWidth(JTable table) {

        for (int column = 0; column < table.getColumnCount(); column++)
        {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            int preferredWidth = tableColumn.getMinWidth();
            int maxWidth = tableColumn.getMaxWidth();

            for (int row = 0; row < table.getRowCount(); row++)
            {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                Component c = table.prepareRenderer(cellRenderer, row, column);
                int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
                preferredWidth = Math.max(preferredWidth, width);

                //  We've exceeded the maximum width, no need to check other rows
                if (preferredWidth >= maxWidth)
                {
                    preferredWidth = maxWidth;
                    break;
                }
            }

            tableColumn.setPreferredWidth( preferredWidth );
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

        // setup the news table
        this.newsTable = new JTable();
        this.newsTable.setRowHeight(20);
        this.newsModel = new DefaultTableModel(0, 0) {
            @Override
            // make the cells non editable
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String[] columnNames = {"Title"};
        newsModel.setColumnIdentifiers(columnNames);
        this.newsTable.setModel(newsModel);

        // other tables
        Object[][] data = {{"Kathy", "Smith"},{"John", "Doe"}};
        this.table1 = new JTable(data, columnNames);
        this.table2 = new JTable(data, columnNames);

    }

    private static void centerWindow(Window frame) {
        // start the app at the center of screen
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Assistant");
        frame.setContentPane(new App().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        App.centerWindow(frame);
        frame.setVisible(true);
    }
}

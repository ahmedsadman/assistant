package ui;

import core.Database;
import core.ProthomAloScrapper;
import core.WeatherData;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.awt.Desktop;

public class App {
    private JPanel rootPanel;
    private JTable todoTable;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel centerPanel;
    private JButton markAsDoneButton;
    private JTable eventsTable;
    private JTable newsTable;
    private JButton visitButton;
    private JLabel weatherLabel;
    private JButton addTodoButton;
    private JButton addEventButton;
    private JButton removeEventButton;
    private JTextField updateField;
    private JButton updateButton;
    private JLabel tempValue;
    private JLabel locName;
    private JLabel weatherCondition;
    private JLabel humidity;
    private JLabel pressureValue;
    private JLabel weatherImage;
    DefaultTableModel newsModel;
    DefaultTableModel todoModel;
    DefaultTableModel eventsModel;

    private ProthomAloScrapper sc;
    private WeatherData wd;
    private Database db;

    public App() {
        this.db = Database.getdb();
        registerActionListeners();
        this.updateTodoTable();
        this.updateEventsTable();
    }

    private void updateDataFeeds() {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    updateNewsTable();
                    getWeatherData();
                    // setcolumnwidth() is called outside updatenewstable, for better gui experience
                    // this helps to keep the table columns in full length even in the case fetch error
                    setColumnWidth(newsTable);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "The feeds could not be updated " +
                                    "due to internet connectivity error. Please check your internet connection.",
                            "Connection Error", JOptionPane.ERROR_MESSAGE);
                    System.out.println(e.getMessage());
                }
            }
        });
        t1.start();

    }

    private void onUpdate() {
        // updates the weather info based on given location
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
        weatherImage.setIcon(wd.getWeatherIcon());
    }

    private void registerActionListeners() {
        markAsDoneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = todoTable.getSelectedRow();
                String desc = (String) todoTable.getModel().getValueAt(row, 0);
                if (!db.deleteTodo(desc))
                    JOptionPane.showMessageDialog(null, "Could not delete", "Error",
                            JOptionPane.INFORMATION_MESSAGE);
                updateTodoTable();
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
                onUpdate();
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
                updateTodoTable();
            }
        });

        addEventButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddEventDialog dlg = new AddEventDialog();
                dlg.setTitle("Create new event");
                dlg.pack();
                App.centerWindow(dlg);
                dlg.setVisible(true);
                updateEventsTable();
            }
        });

        removeEventButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = eventsTable.getSelectedRow();
                String desc = (String) eventsTable.getModel().getValueAt(row, 0);
                String date = (String) eventsTable.getModel().getValueAt(row, 1);
                if (date.equals("Today"))
                    date = String.valueOf(LocalDate.now());
                db.deleteEvent(desc, date);
                updateEventsTable();
            }
        });
    }

    private void updateNewsTable() {
        // fetch and update the news table
        HashMap<String, String> headlineTags = sc.getHeadlineTags();
        Iterator it = headlineTags.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            newsModel.addRow(new Object[] {pair.getKey(), pair.getValue()});
        }

    }

    private void updateEventsTable() {
        eventsModel.setRowCount(0);
        ResultSet rs = db.getEventsList();
        String date;
        String today = String.valueOf(LocalDate.now());

        try {
            while(rs.next()) {
                String desc = rs.getString("description");
                String date_comp = rs.getString("event_date");
                date = (date_comp.equals(today)) ? "Today" : date_comp;
                eventsModel.addRow(new Object[] {desc, date});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.setColumnWidth(eventsTable);
    }

    private void updateTodoTable() {
        ResultSet rs = db.getTodoList();
        todoModel.setRowCount(0);
        try {
            while (rs.next()) {
                String task = rs.getString("description");
                todoModel.addRow(new Object[] {task});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.setColumnWidth(todoTable);
    }

    private void getWeatherData() {
        String loc = this.db.getWeatherLocation();
        updateField.setText(loc);
        onUpdate();
    }

    // Dynamically resizes a table columns based on max-width
    private void setColumnWidth(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
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

            // if table still doesn't fill it's parent JScrollPane, fill it using Auto Resize
            if (table.getScrollableTracksViewportWidth())
                table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        weatherImage = new JLabel();
        String[] newsColumns = {"Title"};
        String[] todoColumns = {"Description"};
        String[] eventColumns = {"Description", "Date"};

        // setup the news table
        this.newsTable = new JTable();
        this.newsTable.setRowHeight(25);
        this.newsModel = new DefaultTableModel(0, 0) {
            @Override
            // make the cells non editable
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        newsModel.setColumnIdentifiers(newsColumns);
        this.newsTable.setModel(newsModel);

        // setup the todo table
        this.todoTable = new JTable() {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return getPreferredSize().width < getParent().getWidth();
            }
        };

        this.todoModel = new DefaultTableModel(0, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.todoModel.setColumnIdentifiers(todoColumns);
        this.todoTable.setModel(todoModel);
        this.todoTable.setRowHeight(20);

        // setup the events table
        this.eventsTable = new JTable() {
            public boolean getScrollableTracksViewportWidth() {
                return getPreferredSize().width < getParent().getWidth();
            }
        };

        this.eventsModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.eventsModel.setColumnIdentifiers(eventColumns);
        this.eventsTable.setModel(eventsModel);
        this.eventsTable.setRowHeight(20);

    }

    public static void centerWindow(Window frame) {
        // start the app at the center of screen
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }

    public static void startApp() {
        JFrame frame = new JFrame("Assistant");
        App ap = new App();
        frame.setContentPane(ap.rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        App.centerWindow(frame);
        frame.setVisible(true);

        /* the following statements fetches data from the internet. It is run after frame.setVisible() so
        that the app can start faster, and data can be fetched later using threads (see method updateDataFeeds)
         */
        ap.sc = new ProthomAloScrapper();
        ap.wd = new WeatherData();
        ap.updateDataFeeds();
    }
}

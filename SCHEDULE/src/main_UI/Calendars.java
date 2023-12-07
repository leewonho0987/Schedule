package main_UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jdesktop.swingx.JXDatePicker;

public class Calendars extends JPanel {
    public static void main(String[] args) {
        JFrame frame = new JFrame("달력 예제");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        JPanel panel = new JPanel();
        frame.add(panel);

        final JXDatePicker datePicker = new JXDatePicker();
        datePicker.setDate(new Date());
        panel.add(datePicker);

        JButton showDateButton = new JButton("선택한 날짜 표시");
        panel.add(showDateButton);

        final JLabel selectedDateLabel = new JLabel("");
        panel.add(selectedDateLabel);

        showDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date selectedDate = datePicker.getDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = sdf.format(selectedDate);
                selectedDateLabel.setText("선택한 날짜: " + formattedDate);
            }
        });

        frame.setVisible(true);
    }
}

/*
private static void createCalendarFrame() {
    calendarFrame = new JFrame("개선된 달력");
    calendarFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    calendarFrame.setSize(400, 300);
    calendarFrame.setLocationRelativeTo(null);

    JPanel panel = new JPanel();
    calendarFrame.add(panel);
    panel.setLayout(new BorderLayout());

    JPanel topPanel = new JPanel();
    panel.add(topPanel, BorderLayout.NORTH);

    final JXDatePicker datePicker = new JXDatePicker();
    datePicker.setDate(new Date());
    topPanel.add(datePicker);

    JButton prevMonthButton = new JButton("◀");
    topPanel.add(prevMonthButton);

    JButton nextMonthButton = new JButton("▶");
    topPanel.add(nextMonthButton);

    JPanel calendarPanel = new JPanel(new BorderLayout());
    panel.add(calendarPanel, BorderLayout.CENTER);

    final JLabel selectedDateLabel = new JLabel("");
    panel.add(selectedDateLabel);

    final JXDatePicker monthDatePicker = new JXDatePicker();
    monthDatePicker.setDate(new Date());
    monthDatePicker.setFormats(new SimpleDateFormat("yyyy-MM"));
    topPanel.add(monthDatePicker);

    JButton showMonthButton = new JButton("선택한 월 표시");
    topPanel.add(showMonthButton);

    final JLabel selectedMonthLabel = new JLabel("");
    topPanel.add(selectedMonthLabel);

    showMonthButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Date selectedMonth = monthDatePicker.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            String formattedMonth = sdf.format(selectedMonth);
            selectedMonthLabel.setText("선택한 월: " + formattedMonth);
        }
    });

    prevMonthButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(datePicker.getDate());
            calendar.add(Calendar.MONTH, -1);
            datePicker.setDate(calendar.getTime());
        }
    });

    nextMonthButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(datePicker.getDate());
            calendar.add(Calendar.MONTH, 1);
            datePicker.setDate(calendar.getTime());
        }
    });

    JButton showDateButton = new JButton("선택한 날짜 표시");
    panel.add(showDateButton);

    showDateButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Date selectedDate = datePicker.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(selectedDate);
            selectedDateLabel.setText("선택한 날짜: " + formattedDate);
        }
    });

    calendarFrame.setVisible(true);
}
}
*/
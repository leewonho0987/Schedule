package main_UI;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.calendar.DateUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Calendarss extends JPanel {
    private static JFrame frame;

    public static void main(String[] args) {
        frame = new JFrame("달력 예제");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        JPanel panel = new JPanel();
        frame.add(panel);

        // 큰 달력 추가
        final JXDatePicker bigDatePicker = new JXDatePicker();
        bigDatePicker.setPreferredSize(new Dimension(200, 30));
        panel.add(bigDatePicker);

        JButton showDateButton = new JButton("선택한 날짜 표시");
        panel.add(showDateButton);

        final JLabel selectedDateLabel = new JLabel("");
        panel.add(selectedDateLabel);

        showDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date selectedDate = bigDatePicker.getDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = sdf.format(selectedDate);
                selectedDateLabel.setText("선택한 날짜: " + formattedDate);
            }
        });

        frame.setVisible(true);
    }
}

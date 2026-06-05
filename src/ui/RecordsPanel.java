package ui;

import util.RecordManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class RecordsPanel extends JPanel {
    public RecordsPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        JButton backBtn = new JButton("بازگشت");
        backBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        backBtn.setBackground(new Color(225, 29, 72));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> frame.showMainFrame());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(backBtn);
        topPanel.setBackground(new Color(15, 23, 42));
        topPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        add(topPanel, BorderLayout.NORTH);

        setBackground(new Color(15, 23, 42));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(15, 23, 42));
        centerPanel.setBorder(new EmptyBorder(20, 50, 50, 50));

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setHighlighter(null);
        textArea.setFont(new Font("Tahoma", Font.BOLD, 18));
        textArea.setBackground(new Color(30, 41, 59));
        textArea.setForeground(new Color(241, 245, 249));
        textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        textArea.setMargin(new Insets(30, 40, 30, 40));

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(56, 189, 248), 2));
        scroll.getVerticalScrollBar().setBackground(new Color(15, 23, 42));
        centerPanel.add(scroll, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        Map<String, Integer> wins = RecordManager.getWinCounts();
        StringBuilder sb = new StringBuilder();

        sb.append(" لیست برندگان بازی  \n");
        if (wins.isEmpty()) {
            sb.append("   هنوز هیچ بردی ثبت نشده!\n");
        } else {
            for (Map.Entry<String, Integer> entry : wins.entrySet()) {
                sb.append(entry.getKey()).append(" => ").append(entry.getValue()).append(" برد\n");
            }
        }

        sb.append("\n\n\n");

        Map<String, Integer> loss = RecordManager.getLosCounts();
        sb.append(" لیست بازنده‌ها\n");
        if (loss.isEmpty()) {
            sb.append("   هنوز هیچ باختی ثبت نشده!\n");
        } else {
            for (Map.Entry<String, Integer> entry : loss.entrySet()) {
                sb.append(entry.getKey()).append(" => ").append(entry.getValue()).append(" باخت\n");
            }
        }

        textArea.setText(sb.toString());
        textArea.setCaretPosition(0);
    }
}
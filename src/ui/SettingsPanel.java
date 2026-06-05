package ui;

import config.GameSettings;
import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    public SettingsPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(new Color(15, 23, 42));

        JPanel form = new JPanel(new GridLayout(5, 2, 20, 20));
        form.setBackground(new Color(30, 41, 59));
        form.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JTextField p1Field = addField(form, "نام بازیکن اول:", GameSettings.player1Name);
        JTextField p2Field = addField(form, "نام بازیکن دوم:", GameSettings.player2Name);

        form.add(createLabel("انتخاب چوب:"));
        JComboBox<String> cueCombo = new JComboBox<>(new String[]{"معمولی", "حرفه ای"});
        cueCombo.setSelectedItem(GameSettings.selectedCue);
        form.add(cueCombo);

        form.add(createLabel("میزان صدا:"));
        JSlider volSlider = new JSlider(0, 100, GameSettings.volume);
        volSlider.setBackground(new Color(30, 41, 59));
        form.add(volSlider);

        JButton saveBtn = new JButton("ذخیره تنظیمات");
        saveBtn.setBackground(new Color(16, 185, 129));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            GameSettings.player1Name = p1Field.getText();
            GameSettings.player2Name = p2Field.getText();
            GameSettings.selectedCue = (String) cueCombo.getSelectedItem();
            GameSettings.volume = volSlider.getValue();
            frame.showMainFrame();
        });

        JButton backBtn = new JButton("بازگشت");
        backBtn.addActionListener(e -> frame.showMainFrame());

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(new Color(15, 23, 42));
        btnPanel.add(saveBtn);
        btnPanel.add(backBtn);

        add(form, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Tahoma", Font.BOLD, 14));
        return lbl;
    }

    private JTextField addField(JPanel parent, String labelText, String defaultValue) {
        parent.add(createLabel(labelText));
        JTextField field = new JTextField(defaultValue);
        parent.add(field);
        return field;
    }
}
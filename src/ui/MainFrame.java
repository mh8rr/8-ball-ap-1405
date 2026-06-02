package ui;

import config.GameConstants;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame{
    private final JPanel panel = new JPanel();

    public MainFrame(){
        setTitle("8-Ball");
        setSize(GameConstants.FRAME_WIDTH, GameConstants.FRAME_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        panel.setBackground(new Color(0 , 82 , 64));
        panel.setLayout(new GridLayout(1 , 4 , 50 , 50));
        panel.setBorder(new javax.swing.border.EmptyBorder(300 , 100 , 300 , 100));

        JButton startBtn = new JButton("شزوع بازی");
        JButton settingsBtn  = new JButton("تنظیمات");
        JButton recordsBtn  = new JButton("رکوردها");
        JButton exitBtn  = new JButton("خروج از بازی");

        startBtn.addActionListener(e -> showGamePanel());
        settingsBtn.addActionListener(e -> System.out.println("2"));
        recordsBtn.addActionListener(e -> System.out.println("3"));
        exitBtn.addActionListener(e -> System.exit(0));

        panel.add(startBtn);
        panel.add(settingsBtn);
        panel.add(recordsBtn);
        panel.add(exitBtn);

        add(panel);
    }

    public void showGamePanel(){
        remove(panel);
        GamePanel gamePanel = new GamePanel(this);
        add(gamePanel);
        revalidate();
        repaint();
    }

    public void showMainFrame(){
        getContentPane().removeAll();
        add(panel);
        repaint();
        revalidate();
    }
}

package ui;

import config.GameConstants;
import model.Ball;
import model.BallType;
import model.Cue;
import model.CueType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class GamePanel extends JPanel implements MouseListener , MouseMotionListener {

    private Ball cueBall;
    private boolean isDragging = false;
    private int dragOffsetX, dragOffsetY;
    private Cue cue;
    private Timer gameTimer;
    private ArrayList<Ball> balls = new ArrayList<>();

    public GamePanel(MainFrame frame){
        //setBackground(new Color(64 , 15 , 10));
        setLayout(new BorderLayout());

        //JLabel label = new JLabel("GameTable" , SwingConstants.CENTER);
        //add(label , BorderLayout.CENTER);

        JButton backBtn = new JButton( "بازگشت");
        backBtn.addActionListener(e -> frame.showMainFrame());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backBtn);
        topPanel.setBackground(new Color(39, 51, 79));
        add(topPanel , BorderLayout.NORTH);

        int startX = GameConstants.TABLE_X + 300 - GameConstants.BALL_RADIUS;
        int startY = GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT / 2;
        cueBall = new Ball(startX, startY, BallType.CUE);
        balls.add(cueBall);
        addMouseListener(this);
        addMouseMotionListener(this);

        cue = new Cue(CueType.PROFESSIONAL);

        /*gameTimer = new Timer(16 , e->{
            for (Ball ball : balls) {();}
            revalidate();
            repaint();
        });*/


        /*setLayout(null);
        backBtn.setBounds(10 , 10 , 70 , 30);
        add(backBtn);*/
    }

    @Override
    protected void paintComponent (Graphics g) {
        super.paintComponent (g);

        g.setColor(new Color(61, 81, 125));
        g.fillRect(0, 0, getWidth(), getHeight());

        int borderSize = 40 , arc = 30;
        g.setColor(new Color(105, 45, 43));
        g.fillRoundRect(GameConstants.TABLE_X - borderSize , GameConstants.TABLE_Y - borderSize , GameConstants.TABLE_WIDTH + 2*borderSize , GameConstants.TABLE_HEIGHT + 2*borderSize , arc + borderSize , arc + borderSize);

        g.setColor(new Color(24 , 84 , 0));
        g.fillRect(GameConstants.TABLE_X , GameConstants.TABLE_Y , GameConstants.TABLE_WIDTH , GameConstants.TABLE_HEIGHT);

        g.setColor(Color.BLACK);

        int leftTopX = GameConstants.TABLE_X;
        int leftTopY = GameConstants.TABLE_Y;
        g.fillOval(leftTopX - GameConstants.POCKET_RADIUS , leftTopY - GameConstants.POCKET_RADIUS , 2*GameConstants.POCKET_RADIUS , 2*GameConstants.POCKET_RADIUS);

        int rightTopX = GameConstants.TABLE_X + GameConstants.TABLE_WIDTH;
        int rightTopY = GameConstants.TABLE_Y;
        g.fillOval(rightTopX - GameConstants.POCKET_RADIUS , rightTopY - GameConstants.POCKET_RADIUS , 2*GameConstants.POCKET_RADIUS , 2*GameConstants.POCKET_RADIUS);


        int leftBottomX = GameConstants.TABLE_X;
        int leftBottomY = GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT;
        g.fillOval(leftBottomX - GameConstants.POCKET_RADIUS , leftBottomY - GameConstants.POCKET_RADIUS , 2*GameConstants.POCKET_RADIUS , 2*GameConstants.POCKET_RADIUS);


        int rightBottomX = GameConstants.TABLE_X + GameConstants.TABLE_WIDTH;
        int rightBottomY = GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT;
        g.fillOval(rightBottomX - GameConstants.POCKET_RADIUS , rightBottomY - GameConstants.POCKET_RADIUS , 2*GameConstants.POCKET_RADIUS , 2*GameConstants.POCKET_RADIUS);


        int middleTopX = GameConstants.TABLE_X + GameConstants.TABLE_WIDTH/2;
        int middleTopY = GameConstants.TABLE_Y;
        g.fillOval(middleTopX - GameConstants.POCKET_RADIUS , middleTopY - GameConstants.POCKET_RADIUS , 2*GameConstants.POCKET_RADIUS , 2*GameConstants.POCKET_RADIUS);


        int middleBottomX = GameConstants.TABLE_X + GameConstants.TABLE_WIDTH/2;
        int middleBottomY = GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT;
        g.fillOval(middleBottomX - GameConstants.POCKET_RADIUS , middleBottomY - GameConstants.POCKET_RADIUS , 2*GameConstants.POCKET_RADIUS , 2*GameConstants.POCKET_RADIUS);

        int lineX = GameConstants.TABLE_X + GameConstants.TABLE_WIDTH / 4; // یک چهارم عرض میز
        g.setColor(Color.GRAY);
        g.drawLine(lineX, GameConstants.TABLE_Y, lineX, GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT);

        cueBall.draw(g);

        if (!gameTimer.isRunning()) {
            cue.draw((Graphics2D) g, (int)cueBall.getX(), (int)cueBall.getY(), GameConstants.BALL_RADIUS);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        //System.out.println(cueBall.getX() + " " + cueBall.getY());
        //System.out.println(((e.getX() - cueBall.getX())*(e.getX() - cueBall.getX()) + (e.getY() - cueBall.getY())*(e.getY() - cueBall.getY())));
        //System.out.println((GameConstants.BALL_RADIUS * GameConstants.BALL_RADIUS));
        if( (e.getX() - cueBall.getX())*(e.getX() - cueBall.getX()) + (e.getY() - cueBall.getY())*(e.getY() - cueBall.getY()) <= GameConstants.BALL_RADIUS * GameConstants.BALL_RADIUS){
            isDragging = true;
            dragOffsetX = (int)cueBall.getX() - e.getX();
            dragOffsetY = (int)cueBall.getY() - e.getY();
        }
        else {
            double distance = Math.sqrt(Math.pow(e.getX() - cueBall.getX(), 2) + Math.pow(e.getY() - cueBall.getY(), 2));
            if (distance <= GameConstants.BALL_RADIUS) {
                isDragging = true;
                dragOffsetX = (int)cueBall.getX() - e.getX();
                dragOffsetY = (int)cueBall.getY() - e.getY();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isDragging) {
            isDragging = false;
        } else {
            cue.applyTo(cueBall);
            cue.setPullDistance(0);
            startGameLoop();
            repaint();
            revalidate();
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        if(isDragging == true){
            int newX = e.getX() + dragOffsetX;
            int newY = e.getY() + dragOffsetY;

            int minX = GameConstants.TABLE_X + GameConstants.BALL_RADIUS;
            int maxX = GameConstants.TABLE_X + GameConstants.TABLE_WIDTH/4 - GameConstants.BALL_RADIUS;
            int minY = GameConstants.TABLE_Y + GameConstants.BALL_RADIUS;
            int maxY = GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT - GameConstants.BALL_RADIUS;
            newX = Math.min(maxX, Math.max(minX, newX));
            newY = Math.min(maxY, Math.max(minY, newY));

            cueBall.setX(newX);
            cueBall.setY(newY);
            repaint();
            revalidate();
        }
        else {
            double distance = Math.sqrt(Math.pow(e.getX() - cueBall.getX(), 2) + Math.pow(e.getY() - cueBall.getY(), 2));
            cue.setPullDistance(distance);
            cue.updateAngle(e.getX(), e.getY(), (int)cueBall.getX(), (int)cueBall.getY());
            repaint();
            revalidate();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {}


    private boolean isMoving() {
        return gameTimer != null && gameTimer.isRunning();
    }

    private void startGameLoop() {
        if (gameTimer != null && gameTimer.isRunning()) return;

        gameTimer = new Timer(16, e -> {
            updateBalls();
            repaint();

            if (allBallsStopped()) {
                gameTimer.stop();
                repaint();
            }
        });
        gameTimer.start();
    }

    private boolean allBallsStopped() {
        for (Ball ball : balls) {
            if (Math.abs(ball.getVx()) > 0.15 || Math.abs(ball.getVy()) > 0.15){
                return false;
            }
        }
        return true;
    }

    private void updateBalls() {
        for (Ball ball : balls) {
            ball.move();
        }
    }




}

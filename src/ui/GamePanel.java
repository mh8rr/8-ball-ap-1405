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

    private int[][] pockets = {
            {GameConstants.TABLE_X, GameConstants.TABLE_Y},
            {GameConstants.TABLE_X + GameConstants.TABLE_WIDTH / 2, GameConstants.TABLE_Y},
            {GameConstants.TABLE_X + GameConstants.TABLE_WIDTH, GameConstants.TABLE_Y},
            {GameConstants.TABLE_X, GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT},
            {GameConstants.TABLE_X + GameConstants.TABLE_WIDTH / 2, GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT},
            {GameConstants.TABLE_X + GameConstants.TABLE_WIDTH, GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT}
    };



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

        setupBalls();

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

        for (Ball ball : balls) ball.draw(g);

        if (gameTimer == null || !gameTimer.isRunning()) {
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
        double distance = Math.sqrt(Math.pow(e.getX() - cueBall.getX(), 2) + Math.pow(e.getY() - cueBall.getY(), 2));
        if (distance <= GameConstants.BALL_RADIUS && !isMoving()) {
            isDragging = true;
            dragOffsetX = (int)cueBall.getX() - e.getX();
            dragOffsetY = (int)cueBall.getY() - e.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isDragging) {
            isDragging = false;
        } else {
            if(!isMoving()){
            cue.applyTo(cueBall);
            cue.setPullDistance(0);
            startGameLoop();
            repaint();
            revalidate();
            }
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

        gameTimer = new Timer(10, e -> {
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

        for (int i = 0; i < balls.size(); i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                balls.get(i).resolveCollision(balls.get(j));
            }
        }

        checkBallPoketed();

    }


    private void setupBalls() {
        double startX = GameConstants.TABLE_X + 300 - GameConstants.BALL_RADIUS;
        double startY = GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT / 2;
        cueBall = new Ball(startX, startY, BallType.CUE);
        balls.add(cueBall);
        addMouseListener(this);
        addMouseMotionListener(this);

        startX = GameConstants.TABLE_X + GameConstants.TABLE_WIDTH * 0.75;
        startY = GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT / 2;
        double radius = GameConstants.BALL_RADIUS;
        double rowSpacing = radius * Math.sqrt(3);

        BallType[] types = {
                BallType.SOLID,
                BallType.STRIPE, BallType.SOLID,
                BallType.SOLID, BallType.EIGHT, BallType.STRIPE,
                BallType.STRIPE, BallType.SOLID, BallType.STRIPE, BallType.SOLID,
                BallType.SOLID, BallType.STRIPE, BallType.SOLID, BallType.STRIPE, BallType.STRIPE
        };

        int index = 0;
        for (int row = 0; row < 5; row++) {
            double rowStartY = startY - (row * radius);
            for (int col = 0; col <= row; col++) {
                double posX = startX + (row * rowSpacing);
                double posY = rowStartY + (col * 2 * radius);
                balls.add(new Ball(posX, posY, types[index++]));
            }
        }
    }

    private void checkBallPoketed(){
        for (int i = balls.size()-1 ; i >=0 ; i--){
            Ball ball = balls.get(i);
            ball.move();

            for (int[] pocket : pockets) {
                double dx = ball.getX() - pocket[0];
                double dy = ball.getY() - pocket[1];
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < GameConstants.POCKET_RADIUS) {
                    if (ball.getType() == BallType.CUE) {
                        ball.setX(GameConstants.TABLE_X + 300 - GameConstants.BALL_RADIUS);
                        ball.setY(GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT / 2);
                        ball.setVx(0);
                        ball.setVy(0);
                    } else {
                        balls.remove(ball);
                    }
                    break;
                }
            }
        }

        for (int i = 0; i < balls.size(); i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                balls.get(i).resolveCollision(balls.get(j));
            }
        }
    }

}

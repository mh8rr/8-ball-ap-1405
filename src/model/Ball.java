package model;

import config.GameConstants;

import java.awt.*;

public class Ball {
    private double x , y;
    private double vx , vy;
    private BallType type;
    private boolean pocketed;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public boolean isPocketed() {
        return pocketed;
    }

    public void setPocketed(boolean pocketed) {
        this.pocketed = pocketed;
    }

    public Ball(int x , int y , BallType type){
        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
        this.type = type;
        this.pocketed = false;
    }

    public void draw(Graphics g){
        if(pocketed) return;

        Color fillColor = null;
        switch(type){
            case CUE -> fillColor = Color.WHITE;
            case SOLID -> fillColor = new Color(66, 133, 244);
            case STRIPE -> fillColor = new Color(234, 67, 53);
            case EIGHT -> fillColor = Color.BLACK;
        }

        g.setColor(fillColor);
        g.fillOval((int) (x - GameConstants.BALL_RADIUS), (int) (y - GameConstants.BALL_RADIUS), 2*GameConstants.BALL_RADIUS , 2*GameConstants.BALL_RADIUS);
        g.setColor(Color.BLACK);
        g.drawOval((int) (x - GameConstants.BALL_RADIUS), (int) (y - GameConstants.BALL_RADIUS), 2*GameConstants.BALL_RADIUS , 2*GameConstants.BALL_RADIUS);

    }

    public void move() {
        x += vx;
        y += vy;

        if (x - GameConstants.BALL_RADIUS < GameConstants.TABLE_X) {
            x = GameConstants.TABLE_X + GameConstants.BALL_RADIUS;
            vx = -vx;
        } else if (x + GameConstants.BALL_RADIUS > GameConstants.TABLE_X + GameConstants.TABLE_WIDTH) {
            x = GameConstants.TABLE_X + GameConstants.TABLE_WIDTH - GameConstants.BALL_RADIUS;
            vx = -vx;
        }
        if (y - GameConstants.BALL_RADIUS < GameConstants.TABLE_Y) {
            y = GameConstants.TABLE_Y + GameConstants.BALL_RADIUS;
            vy = -vy;
        } else if (y + GameConstants.BALL_RADIUS > GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT) {
            y = GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT - GameConstants.BALL_RADIUS;
            vy = -vy;
        }

        vx *= 0.98;
        vy *= 0.98;

        if (Math.abs(vx) < 0.15) vx = 0;
        if (Math.abs(vy) < 0.15) vy = 0;
    }

}

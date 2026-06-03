package model;

import config.GameConstants;
import ui.GamePanel;

import java.awt.*;

public class Ball {
    private double x , y;
    private double vx , vy;
    private BallType type;
    private boolean pocketed;
    private boolean hitWall = false;

    public boolean isHitWall() {
        return hitWall;
    }

    public void setHitWall(boolean hitWall) {
        this.hitWall = hitWall;
    }

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

    public BallType getType() {
        return type;
    }

    public void setType(BallType type) {
        this.type = type;
    }

    public Ball(double x , double y , BallType type){
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
            this.hitWall = true;
            x = GameConstants.TABLE_X + GameConstants.BALL_RADIUS;
            vx = -vx;
        } else if (x + GameConstants.BALL_RADIUS > GameConstants.TABLE_X + GameConstants.TABLE_WIDTH) {
            this.hitWall = true;
            x = GameConstants.TABLE_X + GameConstants.TABLE_WIDTH - GameConstants.BALL_RADIUS;
            vx = -vx;
        }
        if (y - GameConstants.BALL_RADIUS < GameConstants.TABLE_Y) {
            this.hitWall = true;
            y = GameConstants.TABLE_Y + GameConstants.BALL_RADIUS;
            vy = -vy;
        } else if (y + GameConstants.BALL_RADIUS > GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT) {
            this.hitWall = true;
            y = GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT - GameConstants.BALL_RADIUS;
            vy = -vy;
        }

        vx *= 0.987;
        vy *= 0.987;

        if (Math.abs(vx) < 0.15) vx = 0;
        if (Math.abs(vy) < 0.15) vy = 0;
    }

    public void resolveCollision(Ball other) {
        double dx = other.getX() - this.getX();
        double dy = other.getY() - this.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance == 0 || distance >= GameConstants.BALL_RADIUS * 2) {
            return;
        }

        if (this.getType() == BallType.CUE && GamePanel.getFirstBallHitThisTurn() == null) {
            ui.GamePanel.setFirstBallHitThisTurn(other);
        }

        double overlap = 0.5 * (GameConstants.BALL_RADIUS * 2 - distance);
        double nx = dx / distance;
        double ny = dy / distance;

        this.setX(this.getX() - overlap * nx);
        this.setY(this.getY() - overlap * ny);
        other.setX(other.getX() + overlap * nx);
        other.setY(other.getY() + overlap * ny);

        double relativeVx = other.getVx() - this.getVx();
        double relativeVy = other.getVy() - this.getVy();
        double velocityAlongNormal = relativeVx * nx + relativeVy * ny;

        if (velocityAlongNormal > 0) return;

        double restitution = 1.0;
        double impulse = -(1 + restitution) * velocityAlongNormal / 2;

        double impulseX = impulse * nx;
        double impulseY = impulse * ny;

        this.setVx(this.getVx() - impulseX);
        this.setVy(this.getVy() - impulseY);
        other.setVx(other.getVx() + impulseX);
        other.setVy(other.getVy() + impulseY);
    }

}

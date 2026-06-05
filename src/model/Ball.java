package model;

import config.GameConstants;
import java.awt.*;

public class Ball {
    private double x, y, vx, vy;
    private double svx = 0, svy = 0;
    private BallType type;
    private boolean pocketed, hitWall = false, hasCollided = false;

    public Ball(double x, double y, BallType type) {
        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
        this.type = type;
    }

    public boolean isHitWall() { return hitWall; }
    public void setHitWall(boolean h) { this.hitWall = h; }
    public boolean hasCollided() { return hasCollided; }
    public void setHasCollided(boolean c) { this.hasCollided = c; }
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getVx() { return vx; }
    public void setVx(double vx) { this.vx = vx; }
    public double getVy() { return vy; }
    public void setVy(double vy) { this.vy = vy; }
    public void setSvx(double svx) { this.svx = svx; }
    public void setSvy(double svy) { this.svy = svy; }
    public double getSvx() { return svx; }
    public double getSvy() { return svy; }
    public boolean isPocketed() { return pocketed; }
    public void setPocketed(boolean p) { this.pocketed = p; }
    public BallType getType() { return type; }

    public void draw(Graphics g) {
        if (pocketed) return;
        Color c = switch (type) {
            case CUE -> Color.WHITE;
            case SOLID -> new Color(6, 182, 212);
            case STRIPE -> new Color(244, 63, 94);
            case EIGHT -> new Color(15, 23, 42);
        };
        g.setColor(c);
        g.fillOval((int)(x - GameConstants.BALL_RADIUS), (int)(y - GameConstants.BALL_RADIUS), 2 * GameConstants.BALL_RADIUS, 2 * GameConstants.BALL_RADIUS);
        g.setColor(new Color(0, 0, 0, 100));
        g.drawOval((int)(x - GameConstants.BALL_RADIUS), (int)(y - GameConstants.BALL_RADIUS), 2 * GameConstants.BALL_RADIUS, 2 * GameConstants.BALL_RADIUS);
    }

    public boolean move(int[][] pockets) {
        x += vx;
        y += vy;
        boolean bounced = false;
        boolean inPocket = false;

        for (int[] p : pockets) {
            if (Math.sqrt(Math.pow(x - p[0], 2) + Math.pow(y - p[1], 2)) < GameConstants.POCKET_RADIUS + 15) {
                inPocket = true;
                break;
            }
        }

        if (!inPocket) {
            if (x - GameConstants.BALL_RADIUS < GameConstants.TABLE_X) {
                x = GameConstants.TABLE_X + GameConstants.BALL_RADIUS;
                vx = -vx;
                svx = -svx;
                bounced = true;
                hitWall = true;
                hasCollided = true;
            } else if (x + GameConstants.BALL_RADIUS > GameConstants.TABLE_X + GameConstants.TABLE_WIDTH) {
                x = GameConstants.TABLE_X + GameConstants.TABLE_WIDTH - GameConstants.BALL_RADIUS;
                vx = -vx; svx = -svx;
                bounced = true;
                hitWall = true;
                hasCollided = true;
            }
            if (y - GameConstants.BALL_RADIUS < GameConstants.TABLE_Y) {
                y = GameConstants.TABLE_Y + GameConstants.BALL_RADIUS;
                vy = -vy;
                svy = -svy;
                bounced = true;
                hitWall = true;
                hasCollided = true;
            } else if (y + GameConstants.BALL_RADIUS > GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT) {
                y = GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT - GameConstants.BALL_RADIUS;
                vy = -vy;
                svy = -svy;
                bounced = true;
                hitWall = true;
                hasCollided = true;
            }
        }

        if (hasCollided) {
            vx += svx * 0.015;
            vy += svy * 0.015;
        }

        svx *= 0.97;
        svy *= 0.97;

        vx *= 0.987;
        vy *= 0.987;

        if (Math.abs(vx) < 0.15 && Math.abs(vy) < 0.15 && Math.abs(svx) < 0.15 && Math.abs(svy) < 0.15) {
            vx = 0; vy = 0; svx = 0; svy = 0;
        }

        return bounced;
    }

    public void resolveCollision(Ball o) {
        double dx = o.x - x;
        double dy = o.y - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist == 0 || dist >= GameConstants.BALL_RADIUS * 2) return;

        this.hasCollided = true;
        o.hasCollided = true;

        double overlap = 0.5 * (GameConstants.BALL_RADIUS * 2 - dist);
        double nx = dx / dist;
        double ny = dy / dist;

        x -= overlap * nx;
        y -= overlap * ny;
        o.x += overlap * nx;
        o.y += overlap * ny;

        double rv = (o.vx - vx) * nx + (o.vy - vy) * ny;
        if (rv > 0) return;

        double imp = -(1.95) * rv / 2;
        vx -= imp * nx;
        vy -= imp * ny;
        o.vx += imp * nx;
        o.vy += imp * ny;
    }

}
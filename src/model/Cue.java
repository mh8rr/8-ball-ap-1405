package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class Cue {

    private double angle;
    private double pullDistance;
    private CueType type;

    public Cue(CueType type) {
        this.angle = 0;
        this.pullDistance = 0;
        this.type = type;
    }

    public void updateAngle(int mouseX, int mouseY, int cueBallX, int cueBallY) {
        double dx = mouseX - cueBallX;
        double dy = mouseY - cueBallY;
        this.angle = Math.atan2(dy, dx);
    }

    public void setPullDistance(double distance) {
        this.pullDistance = Math.min(distance * 0.5, 260);
    }

    public void applyTo(Ball cueBall) {
        double power = pullDistance * 0.25;

        double vx = -Math.cos(angle) * power;
        double vy = -Math.sin(angle) * power;

        cueBall.setVx(vx);
        cueBall.setVy(vy);
    }

    public void draw(Graphics2D g2d, int cueBallX, int cueBallY, int ballRadius) {
        AffineTransform oldTransform = g2d.getTransform();
        g2d.rotate(angle, cueBallX, cueBallY);

        int startX = (int) (cueBallX + ballRadius + 10 + pullDistance);
        int cueThickness = 8;
        int startY = cueBallY - (cueThickness / 2);
        int cueLength = 400;

        if (this.type == CueType.BEGINNER) {
            g2d.setColor(new Color(205, 133, 63));
            g2d.fillRect(startX, startY, cueLength, cueThickness);

            g2d.setColor(Color.BLUE);
            g2d.fillRect(startX, startY, 5, cueThickness);

        } else if (this.type == CueType.PROFESSIONAL) {

            g2d.setColor(new Color(222, 184, 135));
            g2d.fillRect(startX, startY, 120, cueThickness);

            g2d.setColor(Color.BLACK);
            g2d.fillRect(startX + 120, startY, 130, cueThickness);

            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(startX + 115, startY, 5, cueThickness);

            g2d.setColor(Color.RED);
            g2d.fillRect(startX, startY, 5, cueThickness);
        }

        g2d.setTransform(oldTransform);
    }

    public void setType(CueType newType) {
        this.type = newType;
    }

}
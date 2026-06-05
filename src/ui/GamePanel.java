package ui;

import config.GameConstants;
import config.GameSettings;
import model.*;
import util.RecordManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener {

    private Ball cueBall;
    private Cue cue;
    private Timer timer;
    private ArrayList<Ball> balls = new ArrayList<>();

    private boolean drag = false;
    private boolean spinMode = false;
    private int offX, offY;
    private double spinX = 0, spinY = 0;

    private boolean breakShot = true;
    private boolean tableOpen = true;
    private boolean inHand = true;
    private BallType p1 = null;
    private BallType p2 = null;
    private Player turn = Player.Player_1;

    private boolean foul = false;
    private Ball firstHit = null;
    private int selPkt = -1;
    private boolean pktClick = false;
    private boolean validPkt = false;
    private boolean anyPkt = false;
    private boolean eightPkt = false;
    private int eightIdx = -1;
    private boolean hitWallLater = false;
    private boolean tClosed = false;

    private int mx = 0;
    private int my = 0;

    private int time = 30;
    private Timer tTimer;

    private String player1Name;
    private String player2Name;

    MainFrame frame;

    private int[][] pkts = {
            {GameConstants.TABLE_X, GameConstants.TABLE_Y},
            {GameConstants.TABLE_X + GameConstants.TABLE_WIDTH / 2, GameConstants.TABLE_Y},
            {GameConstants.TABLE_X + GameConstants.TABLE_WIDTH, GameConstants.TABLE_Y},
            {GameConstants.TABLE_X, GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT},
            {GameConstants.TABLE_X + GameConstants.TABLE_WIDTH / 2, GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT},
            {GameConstants.TABLE_X + GameConstants.TABLE_WIDTH, GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT}
    };

    private ArrayList<Msg> msgs = new ArrayList<>();

    public GamePanel(MainFrame frame , String player1Name, String player2Name) {
        this.frame = frame;
        this.player1Name = player1Name;
        this.player2Name = player2Name;

        setLayout(new BorderLayout());

        JButton backBtn = new JButton("بازگشت");
        backBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        backBtn.setBackground(new Color(225, 29, 72));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> frame.showMainFrame());

        JButton winp1 = new JButton("برد" + player1Name);
        winp1.setFont(new Font("Tahoma", Font.BOLD, 14));
        winp1.setBackground(new Color(225, 29, 72));
        winp1.setForeground(Color.WHITE);
        winp1.setFocusPainted(false);
        winp1.addActionListener(e ->{
            RecordManager.saveRecord(player1Name , player2Name);
            frame.showMainFrame();
        });


        JButton winp2 = new JButton("برد" +  player2Name);
        winp2.setFont(new Font("Tahoma", Font.BOLD, 14));
        winp2.setBackground(new Color(225, 29, 72));
        winp2.setForeground(Color.WHITE);
        winp2.setFocusPainted(false);
        winp2.addActionListener(e ->{
            RecordManager.saveRecord(player2Name , player1Name);
            frame.showMainFrame();
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backBtn);
        topPanel.add(winp1);
        topPanel.add(winp2);
        topPanel.setBackground(new Color(15, 23, 42));
        add(topPanel, BorderLayout.NORTH);

        addMouseListener(this);
        addMouseMotionListener(this);

        setupBalls();
        CueType cueType = (GameSettings.selectedCue.equals("حرفه ای")) ? CueType.PROFESSIONAL : CueType.BEGINNER;
        cue = new Cue(cueType);

        tTimer = new Timer(1000, e -> {
            if (timer == null || !timer.isRunning()) {
                time--;
                if (time <= 0) {
                    handleTimeOut();
                }
                repaint();
            }
        });
        tTimer.start();

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(30, 41, 59));
        g.fillRect(0, 0, getWidth(), getHeight());

        int bs = 40, arc = 30;
        g.setColor(new Color(62, 39, 35));
        g.fillRoundRect(GameConstants.TABLE_X - bs, GameConstants.TABLE_Y - bs, GameConstants.TABLE_WIDTH + 2 * bs, GameConstants.TABLE_HEIGHT + 2 * bs, arc + bs, arc + bs);

        g.setColor(new Color(6, 95, 70));
        g.fillRect(GameConstants.TABLE_X, GameConstants.TABLE_Y, GameConstants.TABLE_WIDTH, GameConstants.TABLE_HEIGHT);

        g.setColor(new Color(10, 10, 10));
        for (int i=0; i<pkts.length; i++) {
            g.fillOval(pkts[i][0] - GameConstants.POCKET_RADIUS, pkts[i][1] - GameConstants.POCKET_RADIUS, 2 * GameConstants.POCKET_RADIUS, 2 * GameConstants.POCKET_RADIUS);
        }

        if (selPkt != -1) {
            g.setColor(new Color(250, 204, 21, 200));
            int px = pkts[selPkt][0];
            int py = pkts[selPkt][1];
            g2.setStroke(new BasicStroke(3));
            g.drawOval(px - GameConstants.POCKET_RADIUS - 8, py - GameConstants.POCKET_RADIUS - 8, 2 * GameConstants.POCKET_RADIUS + 16, 2 * GameConstants.POCKET_RADIUS + 16);
            g2.setStroke(new BasicStroke(1));
        }

        int lx = GameConstants.TABLE_X + GameConstants.TABLE_WIDTH / 4;
        g.setColor(new Color(255, 255, 255, 60));
        g.drawLine(lx, GameConstants.TABLE_Y, lx, GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT);

        if (timer == null || !timer.isRunning()) {
            if (!pktClick && mx > 0 && !spinMode) {
                g2.setColor(new Color(255, 255, 255, 140));
                Stroke old = g2.getStroke();
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{8, 6}, 0));

                double dx = cueBall.getX() - mx;
                double dy = cueBall.getY() - my;
                double angle = Math.atan2(dy, dx);

                double s = 2.0;
                double sx = Math.cos(angle) * s;
                double sy = Math.sin(angle) * s;
                double rx = cueBall.getX();
                double ry = cueBall.getY();

                while (true) {
                    rx += sx;
                    ry += sy;

                    if (rx - GameConstants.BALL_RADIUS <= GameConstants.TABLE_X || rx + GameConstants.BALL_RADIUS >= GameConstants.TABLE_X + GameConstants.TABLE_WIDTH || ry - GameConstants.BALL_RADIUS <= GameConstants.TABLE_Y || ry + GameConstants.BALL_RADIUS >= GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT) {
                        break;
                    }

                    boolean h = false;
                    for(int i=0; i<balls.size(); i++){
                        Ball b = balls.get(i);
                        if (b == cueBall || b.isPocketed()) continue;
                        double bx = rx - b.getX();
                        double by = ry - b.getY();
                        if (bx * bx + by * by <= GameConstants.BALL_RADIUS * GameConstants.BALL_RADIUS * 4) {
                            h = true;
                            break;
                        }
                    }
                    if (h) break;
                }

                g2.drawLine((int) cueBall.getX(), (int) cueBall.getY(), (int) rx, (int) ry);
                g2.drawOval((int)(rx - GameConstants.BALL_RADIUS), (int)(ry - GameConstants.BALL_RADIUS), (GameConstants.BALL_RADIUS * 2), (GameConstants.BALL_RADIUS * 2));
                g2.setStroke(old);
            }
        }

        BallType target = null;
        if (!tableOpen) {
            target = (turn == Player.Player_1) ? p1 : p2;
            boolean has = false;
            for (Ball b : balls) {
                if (b.getType() == target) has = true;
            }
            if (!has) target = BallType.EIGHT;
        }

        for (int i=0; i<balls.size(); i++) {
            Ball b = balls.get(i);
            if (target != null && b.getType() == target && !b.isPocketed()) {
                g2.setColor(new Color(250, 204, 21, 140));
                g2.fillOval((int)(b.getX() - GameConstants.BALL_RADIUS - 6), (int)(b.getY() - GameConstants.BALL_RADIUS - 6), (GameConstants.BALL_RADIUS + 6) * 2, (GameConstants.BALL_RADIUS + 6) * 2);
            }
            b.draw(g);
        }

        if (timer == null || !timer.isRunning()) {
            cue.draw(g2, (int) cueBall.getX(), (int) cueBall.getY(), GameConstants.BALL_RADIUS);
        }

        int cx = 100, cy = getHeight() - 100, sr = 45;
        g2.setColor(new Color(255, 255, 255, 20));
        g2.fillOval(cx - sr, cy - sr, sr * 2, sr * 2);
        g2.setColor(new Color(255, 255, 255, 120));
        g2.drawOval(cx - sr, cy - sr, sr * 2, sr * 2);
        g2.drawLine(cx - sr, cy, cx + sr, cy);
        g2.drawLine(cx, cy - sr, cx, cy + sr);
        g2.setColor(new Color(244, 63, 94));
        g2.fillOval(cx + (int)(spinX * sr) - 6, cy + (int)(spinY * sr) - 6, 12, 12);

        g.setFont(new Font("Tahoma", Font.BOLD, 15));
        g.setColor(Color.WHITE);
        g.drawString("Spin", cx - 20, cy - sr - 15);

        g.setFont(new Font("Tahoma", Font.BOLD, 17));

        String p1t = "آزاد";
        if (p1 == BallType.SOLID) p1t = "آبی";
        else if (p1 == BallType.STRIPE) p1t = "قرمز";

        int p1c = 0;
        if (p1 != null) {
            for (Ball b : balls) { if (b.getType() == p1) p1c++; }
            p1c = 7 - p1c;
        }

        Color p1color = Color.LIGHT_GRAY;
        if (turn == Player.Player_1) p1color = new Color(6, 182, 212);

        g.setColor(Color.BLACK);
        g.drawString(player1Name + "(" + p1t + ") | پاکت: " + p1c, 40 + 2, 130 + 2);
        g.setColor(p1color);
        g.drawString(player1Name + "(" + p1t + ") | پاکت: " + p1c, 40, 130);

        String p2t = "آزاد";
        if (p2 == BallType.SOLID) p2t = "آبی";
        else if (p2 == BallType.STRIPE) p2t = "قرمز";

        int p2c = 0;
        if (p2 != null) {
            for (Ball b : balls) { if (b.getType() == p2) p2c++; }
            p2c = 7 - p2c;
        }

        Color p2color = Color.LIGHT_GRAY;
        if (turn == Player.Player_2) p2color = new Color(249, 115, 22);

        g.setColor(Color.BLACK);
        g.drawString(player2Name + "(" + p2t + ") | پاکت: " + p2c, getWidth() - 280 + 2, 130 + 2);
        g.setColor(p2color);
        g.drawString(player2Name + "(" + p2t + ") | پاکت: " + p2c, getWidth() - 280, 130);

        g.setFont(new Font("Tahoma", Font.BOLD, 20));
        String tInfo = "نوبت: ";
        if (turn == Player.Player_1) tInfo += player1Name;

        else tInfo += player2Name;

        if (inHand) tInfo += " (Ball in hand)";

        g.setColor(new Color(250, 204, 21));
        g.drawString(tInfo, (getWidth()/2) - 80, 130);

        g.setFont(new Font("Tahoma", Font.BOLD, 22));

        if (time <= 10) {
            g.setColor(new Color(244, 63, 94));
        } else {
            g.setColor(Color.WHITE);
        }
        g.drawString("زمان: " + time, (getWidth()/2) - 45, 165);

        long now = System.currentTimeMillis();
        for (int i=msgs.size()-1; i>=0; i--) {
            if (now > msgs.get(i).getTime()) msgs.remove(i);
        }

        if (msgs.size() > 0) {
            g.setFont(new Font("Tahoma", Font.BOLD, 22));
            int start = GameConstants.TABLE_Y - (msgs.size() * 32);
            if (start < 160) start = 160;

            for (int i = 0; i < msgs.size(); i++) {
                String txt = msgs.get(i).getT();
                int yy = start + (i * 32);
                g.setColor(new Color(244, 63, 94));
                g.drawString(txt, (getWidth()/2) - 100, yy);
            }
        }
    }

    private void setSpin(int x, int y, int cx, int cy, int r) {
        double dx = x - cx;
        double dy = y - cy;
        if (Math.abs(dx) < 15 && Math.abs(dy) < 15) {
            spinX = 0;
            spinY = 0;
        }
        else if (Math.abs(dx) > Math.abs(dy)) {
            spinX = (dx > 0) ? 1.0 : -1.0;
            spinY = 0;
        }
        else {
            spinX = 0;
            spinY = (dy > 0) ? 1.0 : -1.0;
        }

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if (timer != null && timer.isRunning()) return;

        mx = e.getX();
        my = e.getY();

        if (e.getButton() == MouseEvent.BUTTON3) {
            cancel();
            return;
        }

        int cx = 100, cy = getHeight() - 100, r = 45;
        if (Math.sqrt(Math.pow(mx-cx,2) + Math.pow(my-cy,2)) <= r + 15) {
            spinMode = true;
            setSpin(mx, my, cx, cy, r);
            return;
        }

        if (!breakShot && !tableOpen) {
            for (int i = 0; i < pkts.length; i++) {
                double dx = e.getX() - pkts[i][0];
                double dy = e.getY() - pkts[i][1];
                if (Math.sqrt(dx * dx + dy * dy) <= GameConstants.POCKET_RADIUS + 10) {
                    selPkt = i;
                    pktClick = true;
                    repaint();
                    return;
                }
            }
        }

        double dist = Math.sqrt(Math.pow(e.getX() - cueBall.getX(), 2) + Math.pow(e.getY() - cueBall.getY(), 2));
        if (dist <= GameConstants.BALL_RADIUS && inHand) {
            drag = true;
            offX = (int) cueBall.getX() - e.getX();
            offY = (int) cueBall.getY() - e.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (spinMode) {
            spinMode = false;
            return;
        }
        if (drag) {
            drag = false;
        } else {
            if (pktClick) {
                pktClick = false;
                return;
            }
            if (timer == null || !timer.isRunning()) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    cancel();
                    return;
                }

                double d = Math.sqrt(Math.pow(e.getX() - cueBall.getX(), 2) + Math.pow(e.getY() - cueBall.getY(), 2));
                if (d < 25) {
                    cancel();
                    return;
                }

                if (!breakShot && selPkt == -1 && !tableOpen) {
                    showMsg("خطا! میز بسته شده، باید روی یک سوراخ کلیک کنید.");
                    cue.setPullDistance(0);
                    repaint();
                    return;
                }

                cue.applyTo(cueBall);

                double spd = Math.sqrt(Math.pow(cueBall.getVx(),2) + Math.pow(cueBall.getVy(),2));

                if (spd < 0.5) {
                    cueBall.setVx(0);
                    cueBall.setVy(0);
                    cancel();
                    return;
                }

                if (spd > 0) {
                    double dx = cueBall.getVx() / spd;
                    double dy = cueBall.getVy() / spd;

                    double top = -spinY * spd * 0.95;
                    double side = spinX * spd * 0.65;

                    double svx = dx * top + (-dy) * side;
                    double svy = dy * top + (dx) * side;

                    cueBall.setSvx(svx);
                    cueBall.setSvy(svy);
                    cueBall.setHasCollided(false);
                }

                inHand = false;
                cue.setPullDistance(0);
                util.SoundManager.playSound("Hit.wav");
                startTimer();
                repaint();
            }
        }
    }

    private void cancel() {
        cue.setPullDistance(0);
        pktClick = false;
        showMsg("ضربه لغو شد.");
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        mx = e.getX();
        my = e.getY();

        if (spinMode) {
            setSpin(mx, my, 100, getHeight() - 100, 45);
            return;
        }

        if (drag && inHand) {
            int nx = e.getX() + offX;
            int ny = e.getY() + offY;
            int minX = GameConstants.TABLE_X + GameConstants.BALL_RADIUS;
            int maxX = GameConstants.TABLE_X + GameConstants.TABLE_WIDTH / 4 - GameConstants.BALL_RADIUS;
            int minY = GameConstants.TABLE_Y + GameConstants.BALL_RADIUS;
            int maxY = GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT - GameConstants.BALL_RADIUS;

            for (Ball b : balls) {
                if (b == cueBall || b.isPocketed()) continue;

                double dx = nx - b.getX();
                double dy = ny - b.getY();
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < GameConstants.BALL_RADIUS * 2) {
                    if (dist == 0) {
                        dx = 1;
                        dist = 1;
                    }
                    double overlap = (GameConstants.BALL_RADIUS * 2) - dist + 1;
                    nx += (int) ((dx / dist) * overlap);
                    ny += (int) ((dy / dist) * overlap);
                }
            }

            if (nx < minX) nx = minX;
            if (nx > maxX) nx = maxX;
            if (ny < minY) ny = minY;
            if (ny > maxY) ny = maxY;

            cueBall.setX(nx);
            cueBall.setY(ny);
            repaint();
        } else {
            if (!pktClick) {
                double dist = Math.sqrt(Math.pow(e.getX() - cueBall.getX(), 2) + Math.pow(e.getY() - cueBall.getY(), 2));
                cue.setPullDistance(dist);
                cue.updateAngle(e.getX(), e.getY(), (int) cueBall.getX(), (int) cueBall.getY());
                repaint();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e){}

    private void startTimer() {
        if (timer != null && timer.isRunning()) return;
        timer = new Timer(10, e -> {
            updateAll();
            repaint();

            boolean stop = true;
            for (int i=0; i<balls.size(); i++) {
                Ball b = balls.get(i);
                if (Math.abs(b.getVx()) > 0.15 || Math.abs(b.getVy()) > 0.15 || Math.abs(b.getSvx()) > 0.15 || Math.abs(b.getSvy()) > 0.15) {
                    stop = false;
                }
            }

            if (stop) {
                checkRules();
                timer.stop();
                repaint();
            }
        });
        timer.start();
    }

    private void updateAll() {
        for (int i=0; i<balls.size(); i++) {
            Ball b = balls.get(i);
            boolean hit = b.move(pkts);
            if (firstHit != null && hit) {
                hitWallLater = true;
            }
        }
        for (int i = 0; i < balls.size(); i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball b1 = balls.get(i);
                Ball b2 = balls.get(j);
                double dx = b1.getX() - b2.getX();
                double dy = b1.getY() - b2.getY();
                double d = Math.sqrt(dx * dx + dy * dy);

                if (d <= GameConstants.BALL_RADIUS * 2) {
                    if (b1.getType() == BallType.CUE && firstHit == null) {
                        firstHit = b2;
                    } else if (b2.getType() == BallType.CUE && firstHit == null) {
                        firstHit = b1;
                    }
                }
                b1.resolveCollision(b2);
            }
        }

        for (int i = balls.size() - 1; i >= 0; i--) {
            Ball b = balls.get(i);
            for (int p = 0; p < pkts.length; p++) {
                double dx = b.getX() - pkts[p][0];
                double dy = b.getY() - pkts[p][1];

                if (Math.sqrt(dx * dx + dy * dy) < GameConstants.POCKET_RADIUS + 5) {
                    util.SoundManager.playSound("pocket.wav");

                    if (b.getType() == BallType.CUE) {
                        foul = true;
                        inHand = true;
                        b.setX(GameConstants.TABLE_X + 250 - GameConstants.BALL_RADIUS);
                        b.setY(GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT / 2);
                        b.setVx(0);
                        b.setVy(0);
                        b.setSvx(0);
                        b.setSvy(0);
                    } else if (b.getType() == BallType.EIGHT) {
                        balls.remove(b);
                        b.setPocketed(true);
                        eightPkt = true;
                        eightIdx = p;
                    } else {
                        balls.remove(b);
                        b.setPocketed(true);
                        anyPkt = true;

                        if (tableOpen) {
                            tableOpen = false;
                            tClosed = true;
                            validPkt = true;

                            if (turn == Player.Player_1) {
                                p1 = b.getType();
                                p2 = (b.getType() == BallType.SOLID) ? BallType.STRIPE : BallType.SOLID;
                            } else {
                                p2 = b.getType();
                                p1 = (b.getType() == BallType.SOLID) ? BallType.STRIPE : BallType.SOLID;
                            }

                            String color = (b.getType() == BallType.STRIPE) ? "قرمز" : "آبی";
                            showMsg("تعیین گروه: شما " + color + " هستید!");
                        } else if (breakShot) {
                            validPkt = true;
                        } else {
                            if (p == selPkt) {
                                BallType myType = (turn == Player.Player_1) ? p1 : p2;

                                if (b.getType() == myType) {
                                    validPkt = true;
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private void setupBalls() {
        double sx = GameConstants.TABLE_X + 250 - GameConstants.BALL_RADIUS;
        double sy = GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT / 2;
        cueBall = new Ball(sx, sy, BallType.CUE);
        balls.add(cueBall);

        sx = GameConstants.TABLE_X + GameConstants.TABLE_WIDTH * 0.75;
        sy = GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT / 2;
        double rad = GameConstants.BALL_RADIUS;
        double space = rad * Math.sqrt(3);

        BallType[] t = {
                BallType.SOLID, BallType.STRIPE, BallType.SOLID,
                BallType.SOLID, BallType.EIGHT, BallType.STRIPE,
                BallType.STRIPE, BallType.SOLID, BallType.STRIPE, BallType.SOLID,
                BallType.SOLID, BallType.STRIPE, BallType.SOLID, BallType.STRIPE, BallType.STRIPE
        };

        int idx = 0;
        for (int r = 0; r < 5; r++) {
            double ry = sy - (r * rad);
            for (int c = 0; c <= r; c++) {
                double px = sx + (r * space);
                double py = ry + (c * 2 * rad);
                balls.add(new Ball(px, py, t[idx]));
                idx++;
            }
        }
    }

    private void checkRules() {
        if (breakShot) {
            int wh = 0;
            boolean c8 = false;
            for (int i = 0 ; i < balls.size() ; i++) {
                if (balls.get(i).isHitWall()) wh++;
                if (balls.get(i).getType() == BallType.EIGHT && balls.get(i).isPocketed()) c8 = true;
            }
            if (c8 == false && (wh >= 4 || anyPkt)) {
                breakShot = false;
                showMsg("بریک صحیح");
            } else {
                showMsg("بریک غلط");
                if (turn == Player.Player_1) turn = Player.Player_2; else turn = Player.Player_1;
                reset();
                return;
            }
        }

        if (!breakShot) {
            BallType myT = (turn == Player.Player_1) ? p1 : p2;

            if (firstHit == null) {
                foul = true;
                showMsg("foul");
            } else if (tableOpen == false) {
                boolean hasMy = false;
                for (Ball b : balls) {
                    if (b.getType() == myT) hasMy = true;
                }

                if (hasMy) {
                    if (firstHit.getType() != myT) {
                        foul = true;
                        showMsg("foul");
                    }
                } else {
                    if (firstHit.getType() != BallType.EIGHT) {
                        foul = true;
                        showMsg("foul");
                    }
                }
            }

            if (foul == false && firstHit != null) {
                if (anyPkt == false && hitWallLater == false) {
                    foul = true;
                    showMsg("foul");
                }
            }
        }

        if (foul && tClosed) {
            tableOpen = true;
            p1 = null;
            p2 = null;
            validPkt = false;
            showMsg("foul");
        }

        if (!breakShot && !tableOpen) {
            BallType myT = (turn == Player.Player_1) ? p1 : p2;

            if (eightPkt) {
                boolean hasMy = false;
                for (Ball b : balls) {
                    if (b.getType() == myT) hasMy = true;
                }

                if (hasMy) {
                    JOptionPane.showMessageDialog(this, "بازیکن " + turn + " باخت!");
                    String winner = (turn == Player.Player_2) ? player1Name : player2Name;
                    String loser = (turn == Player.Player_2) ? player2Name : player1Name;
                    RecordManager.saveRecord(winner, loser);
                    frame.showMainFrame();
                    return;
                }
                if (eightIdx != selPkt) {
                    JOptionPane.showMessageDialog(this, "بازیکن " + turn + " باخت!");
                    String winner = (turn == Player.Player_2) ? player1Name : player2Name;
                    String loser = (turn == Player.Player_2) ? player2Name : player1Name;
                    RecordManager.saveRecord(winner, loser);
                    frame.showMainFrame();
                    return;
                }
                if (foul) {
                    JOptionPane.showMessageDialog(this, "بازیکن " + turn + " باخت! ");
                    String winner = (turn == Player.Player_2) ? player1Name : player2Name;
                    String loser = (turn == Player.Player_2) ? player2Name : player1Name;
                    RecordManager.saveRecord(winner, loser);
                    frame.showMainFrame();
                    return;
                }
                JOptionPane.showMessageDialog(this, " بازیکن " + turn + " برنده شد! ");
                String winner = (turn == Player.Player_1) ? player1Name : player2Name;
                String loser = (turn == Player.Player_1) ? player2Name : player1Name;
                RecordManager.saveRecord(winner, loser);
                frame.showMainFrame();
                return;
            }
        }

        if (foul) {
            if (turn == Player.Player_1) turn = Player.Player_2; else turn = Player.Player_1;
            inHand = true;
            showMsg("نوبت حریف (Ball in hand)");
            cueBall.setX(GameConstants.TABLE_X + 250 - GameConstants.BALL_RADIUS);
            cueBall.setY(GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT / 2);
            cueBall.setVx(0); cueBall.setVy(0); cueBall.setSvx(0); cueBall.setSvy(0);
            cueBall.setHasCollided(false);
        } else if (!validPkt) {
            if (turn == Player.Player_1) turn = Player.Player_2; else turn = Player.Player_1;
            inHand = false;
        } else {
            showMsg("ضربه صحیح");
        }

        foul = false;
        validPkt = false;
        anyPkt = false;
        eightPkt = false;
        eightIdx = -1;
        firstHit = null;
        hitWallLater = false;
        selPkt = -1;
        tClosed = false;
        spinX = 0;
        spinY = 0;
        time = 30;
        for (Ball b : balls) b.setHitWall(false);
    }

    private void reset() {
        balls.clear();
        setupBalls();
        tableOpen = true;
        inHand = true;
        p1 = null; p2 = null;
        breakShot = true;
        turn = Player.Player_1;
        selPkt = -1;
        spinX = 0; spinY = 0;
    }

    private void showMsg(String m) {
        msgs.add(new Msg(m, System.currentTimeMillis() + 4000));
        repaint();
        Timer t = new Timer(4000, e -> repaint());
        t.setRepeats(false);
        t.start();
    }

    private void handleTimeOut() {
        showMsg("زمان تمام شد! خطای نوبت");
        turn = (turn == Player.Player_1) ? Player.Player_2 : Player.Player_1;
        inHand = true;
        cueBall.setX(GameConstants.TABLE_X + 250 - GameConstants.BALL_RADIUS);
        cueBall.setY(GameConstants.TABLE_Y + GameConstants.TABLE_HEIGHT / 2);
        cueBall.setVx(0); cueBall.setVy(0);
        time = 30;
        selPkt = -1;
        repaint();
    }

}
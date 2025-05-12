package robots.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class GameVisualizer extends JPanel {
    private final Timer gameTimer;
    final RobotState robot;
    final PredatorState predator;
    final Point target;
    public int m_robotPositionY;
    public int m_robotPositionX;
    boolean gameOver;
    boolean gameWon;
    boolean gameStarted;

    private static final double ROBOT_SPEED = 0.1;
    private static final double ROBOT_TURN_SPEED = 0.001;
    private static final double PREDATOR_SPEED = 0.15;
    private static final double PREDATOR_TURN_SPEED = 0.005;
    private static final int COLLISION_DISTANCE = 20;
    private static final int TARGET_RADIUS = 5;

    static class RobotState {
        double x, y, direction;
        RobotState(double x, double y) {
            this.x = x;
            this.y = y;
            this.direction = 0;
        }
    }

    static class PredatorState {
        double x, y, direction;
        boolean up, down, left, right;
        PredatorState(double x, double y) {
            this.x = x;
            this.y = y;
            this.direction = 0;
        }
    }

    public GameVisualizer() {
        robot = new RobotState(100, 100);
        predator = new PredatorState(300, 300);
        target = new Point(-100, -100);
        gameTimer = new Timer("GameTimer", true);
        gameStarted = false;

        setupMouseControls();
        setupKeyboardControls();
        startGameLoop();
        setDoubleBuffered(true);
    }

    private void setupMouseControls() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!gameOver && !gameWon) {
                    if (!gameStarted) {
                        gameStarted = true;
                    }
                    target.setLocation(e.getPoint());
                    repaint();
                }
            }
        });
    }

    private void setupKeyboardControls() {
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameStarted && !gameOver && !gameWon) {
                    handleKeyEvent(e, true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (gameStarted && !gameOver && !gameWon) {
                    handleKeyEvent(e, false);
                }
            }
        });
    }

    void handleKeyEvent(KeyEvent e, boolean pressed) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP: predator.up = pressed; break;
            case KeyEvent.VK_DOWN: predator.down = pressed; break;
            case KeyEvent.VK_LEFT: predator.left = pressed; break;
            case KeyEvent.VK_RIGHT: predator.right = pressed; break;
        }
    }

    private void startGameLoop() {

        gameTimer.schedule(new TimerTask() {
            @Override public void run() {
                EventQueue.invokeLater(() -> repaint());
            }
        }, 0, 50);

        gameTimer.schedule(new TimerTask() {
            @Override public void run() {
                EventQueue.invokeLater(() -> updateGame());
            }
        }, 0, 10);
    }

    void updateGame() {
        if (gameOver || gameWon || !gameStarted) return;

        updateRobot();
        checkWinCondition();

        if (!gameWon) {
            updatePredator();
            checkCollision();
        }
    }

    private void updateRobot() {
        if (target.x < 0 || target.y < 0) return;

        double distance = distance(robot.x, robot.y, target.x, target.y);
        if (distance < TARGET_RADIUS) return;

        double angleToTarget = angleTo(robot.x, robot.y, target.x, target.y);
        double turn = calculateTurn(angleToTarget, robot.direction, ROBOT_TURN_SPEED);

        moveRobot(ROBOT_SPEED, turn, 10);
    }

    void moveRobot(double speed, double turn, double time) {
        double newX = robot.x + speed * Math.cos(robot.direction) * time;
        double newY = robot.y + speed * Math.sin(robot.direction) * time;
        double newDir = normalizeAngle(robot.direction + turn * time);

        robot.x = newX;
        robot.y = newY;
        robot.direction = newDir;
    }

    private void checkWinCondition() {
        if (distance(robot.x, robot.y, target.x, target.y) < TARGET_RADIUS) {
            gameWon = true;
            gameTimer.cancel();
        }
    }

    private void updatePredator() {
        double speed = 0;
        double turn = 0;

        if (predator.up) speed = PREDATOR_SPEED;
        if (predator.down) speed = -PREDATOR_SPEED;
        if (predator.left) turn = -PREDATOR_TURN_SPEED;
        if (predator.right) turn = PREDATOR_TURN_SPEED;

        if (!gameWon && !predator.up && !predator.down && !predator.left && !predator.right) {
            double angleToRobot = angleTo(predator.x, predator.y, robot.x, robot.y);
            turn = calculateTurn(angleToRobot, predator.direction, PREDATOR_TURN_SPEED);
            speed = PREDATOR_SPEED;
        }

        movePredator(speed, turn, 10);
    }

    private void movePredator(double speed, double turn, double time) {
        double newX = predator.x + speed * Math.cos(predator.direction) * time;
        double newY = predator.y + speed * Math.sin(predator.direction) * time;
        double newDir = normalizeAngle(predator.direction + turn * time);

        predator.x = newX;
        predator.y = newY;
        predator.direction = newDir;
    }

    private void checkCollision() {
        if (distance(predator.x, predator.y, robot.x, robot.y) < COLLISION_DISTANCE) {
            gameOver = true;
            gameTimer.cancel();
            showGameMessage("Game Over", "Predator caught the robot!");
        }
    }

    private void showGameMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    double calculateTurn(double targetAngle, double currentAngle, double maxTurn) {
        double diff = normalizeAngle(targetAngle - currentAngle);
        return diff > Math.PI ? -maxTurn : maxTurn;
    }

    double distance(double x1, double y1, double x2, double y2) {
        return Math.hypot(x1 - x2, y1 - y2);
    }

    double angleTo(double fromX, double fromY, double toX, double toY) {
        return Math.atan2(toY - fromY, toX - fromX);
    }

    double normalizeAngle(double angle) {
        angle %= 2 * Math.PI;
        return angle < 0 ? angle + 2 * Math.PI : angle;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        drawRobot(g2d);
        drawPredator(g2d);

        if (target.x >= 0 && target.y >= 0) {
            drawTarget(g2d);
        }

        if (!gameStarted) {
            drawGameMessage(g2d, "Click to set target and start", Color.BLUE);
        } else if (gameOver) {
            drawGameMessage(g2d, "GAME OVER", Color.RED);
        }
    }

    private void drawGameMessage(Graphics2D g, String text, Color color) {
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(text, x, y);
    }

    private void drawRobot(Graphics2D g) {
        drawCharacter(g, robot.x, robot.y, robot.direction, Color.MAGENTA, Color.WHITE);
    }

    private void drawPredator(Graphics2D g) {
        drawCharacter(g, predator.x, predator.y, predator.direction, Color.RED, Color.YELLOW);
    }

    private void drawCharacter(Graphics2D g, double x, double y, double dir, Color body, Color eye) {
        AffineTransform old = g.getTransform();
        g.rotate(dir, x, y);

        g.setColor(body);
        g.fillOval((int)x - 15, (int)y - 5, 30, 10);
        g.setColor(Color.BLACK);
        g.drawOval((int)x - 15, (int)y - 5, 30, 10);

        g.setColor(eye);
        g.fillOval((int)x + 5, (int)y - 2, 5, 5);

        g.setTransform(old);
    }

    private void drawTarget(Graphics2D g) {
        g.setColor(gameWon ? Color.CYAN : Color.GREEN);
        g.fillOval(target.x - TARGET_RADIUS, target.y - TARGET_RADIUS,
                TARGET_RADIUS * 2, TARGET_RADIUS * 2);
        g.setColor(Color.BLACK);
        g.drawOval(target.x - TARGET_RADIUS, target.y - TARGET_RADIUS,
                TARGET_RADIUS * 2, TARGET_RADIUS * 2);
    }
}
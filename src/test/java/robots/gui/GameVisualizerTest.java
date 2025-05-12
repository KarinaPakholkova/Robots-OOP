package robots.gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.event.KeyEvent;
import java.awt.*;

class GameVisualizerTest {

    private GameVisualizer game;
    private GameVisualizer.RobotState robot;
    private GameVisualizer.PredatorState predator;

    @BeforeEach
    void setUp() {
        game = new GameVisualizer();
        robot = game.robot;
        predator = game.predator;
    }

    @Test
    void testInitialState() {
        assertFalse(game.gameStarted);
        assertFalse(game.gameOver);
        assertFalse(game.gameWon);
        assertEquals(100, robot.x, 0.001);
        assertEquals(100, robot.y, 0.001);
        assertEquals(300, predator.x, 0.001);
        assertEquals(300, predator.y, 0.001);
        assertEquals(-100, game.target.x, 0.001);
        assertEquals(-100, game.target.y, 0.001);
    }


    @Test
    void testRobotMovement() {
        // Устанавливаем цель для робота
        game.target.setLocation(200, 100);

        game.updateGame();

        // Проверяем, что робот начал движение к цели
        double initialDistance = distance(robot.x, robot.y, 200, 100);
        double newDistance = distance(robot.x, robot.y, 200, 100);

        assertTrue(newDistance <= initialDistance, "Робот должен приближаться к цели");
    }

    @Test
    void testPredatorKeyboardControls() {
        // Симулируем нажатие клавиш для управления хищником
        game.handleKeyEvent(new KeyEvent(new Button("Test"), 0, 0, 0, KeyEvent.VK_UP, ' '), true);
        assertTrue(predator.up);

        game.handleKeyEvent(new KeyEvent(new Button("Test"), 0, 0, 0, KeyEvent.VK_DOWN, ' '), true);
        assertTrue(predator.down);

        game.handleKeyEvent(new KeyEvent(new Button("Test"), 0, 0, 0, KeyEvent.VK_LEFT, ' '), true);
        assertTrue(predator.left);

        game.handleKeyEvent(new KeyEvent(new Button("Test"), 0, 0, 0, KeyEvent.VK_RIGHT, ' '), true);
        assertTrue(predator.right);

        // Симулируем отпускание клавиш
        game.handleKeyEvent(new KeyEvent(new Button("Test"), 0, 0, 0, KeyEvent.VK_UP, ' '), false);
        assertFalse(predator.up);
    }

    @Test
    void testPredatorMovement() {
        // Включаем автоматическое преследование (без нажатия клавиш)
        double initialDistance = distance(predator.x, predator.y, robot.x, robot.y);

        game.updateGame();

        double newDistance = distance(predator.x, predator.y, robot.x, robot.y);
        assertTrue(newDistance <= initialDistance, "Хищник должен приближаться к роботу");
    }


    @Test
    void testAngleCalculations() {
        // Проверяем расчет угла между точками
        double angle = game.angleTo(0, 0, 1, 1);
        assertEquals(Math.PI/4, angle, 0.001);

        angle = game.angleTo(0, 0, -1, -1);
        assertEquals(-3*Math.PI/4, angle, 0.001);

        // Проверяем нормализацию угла
        double normalized = game.normalizeAngle(3*Math.PI);
        assertEquals(Math.PI, normalized, 0.001);

        normalized = game.normalizeAngle(-Math.PI/2);
        assertEquals(3*Math.PI/2, normalized, 0.001);
    }

    @Test
    void testTurnCalculation() {
        // Проверяем расчет поворота
        double turn = game.calculateTurn(Math.PI/4, 0, 0.1);
        assertTrue(turn > 0, "Должен быть положительный поворот");

        turn = game.calculateTurn(7*Math.PI/4, 0, 0.1);
        assertTrue(turn < 0, "Должен быть отрицательный поворот");
    }

    private double distance(double x1, double y1, double x2, double y2) {
        return Math.hypot(x1 - x2, y1 - y2);
    }

    // Mock класс для тестирования событий
    static class Button extends Component {
        Button(String name) {
            setName(name);
        }
    }
    
}
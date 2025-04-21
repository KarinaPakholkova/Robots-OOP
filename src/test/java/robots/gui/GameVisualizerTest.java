package robots.gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;

class GameVisualizerTest {

    private GameVisualizer gameVisualizer;

    @BeforeEach
    void setUp() {
        gameVisualizer = new GameVisualizer();
        gameVisualizer.setSize(400, 400); // Устанавливаем размеры окна для тестов
    }

    @Test
    void testRobotDoesNotExceedRightBoundary() {
        // Устанавливаем робота у правой границы
        gameVisualizer.m_robotPositionX = gameVisualizer.getWidth() - 1;
        gameVisualizer.m_robotPositionY = 100;
        gameVisualizer.m_robotDirection = 0; // Направление вправо

        // Пытаемся двигаться вправо
        gameVisualizer.moveRobot(0.1, 0, 10);

        // Проверяем, что робот не вышел за границу
        assertTrue(gameVisualizer.m_robotPositionX <= gameVisualizer.getWidth());
    }

    @Test
    void testRobotDoesNotExceedLeftBoundary() {
        // Устанавливаем робота у левой границы
        gameVisualizer.m_robotPositionX = 1;
        gameVisualizer.m_robotPositionY = 100;
        gameVisualizer.m_robotDirection = Math.PI; // Направление влево

        // Пытаемся двигаться влево
        gameVisualizer.moveRobot(0.1, 0, 10);
        assertTrue(gameVisualizer.m_robotPositionX >= 0);
    }

    @Test
    void testRobotDoesNotExceedTopBoundary() {
        // Устанавливаем робота у верхней границы
        gameVisualizer.m_robotPositionX = 100;
        gameVisualizer.m_robotPositionY = 1;
        gameVisualizer.m_robotDirection = 3 * Math.PI / 2; // Направление вверх

        // Пытаемся двигаться вверх
        gameVisualizer.moveRobot(0.1, 0, 10);
        assertTrue(gameVisualizer.m_robotPositionY >= 0);
    }

    @Test
    void testRobotDoesNotExceedBottomBoundary() {
        // Устанавливаем робота у нижней границы
        gameVisualizer.m_robotPositionX = 100;
        gameVisualizer.m_robotPositionY = gameVisualizer.getHeight() - 1;
        gameVisualizer.m_robotDirection = Math.PI / 2; // Направление вниз

        // Пытаемся двигаться вниз
        gameVisualizer.moveRobot(0.1, 0, 10);
        assertTrue(gameVisualizer.m_robotPositionY <= gameVisualizer.getHeight());
    }

    @Test
    void testTargetPositionIsLimitedToWindowBounds() {
        // Устанавливаем размеры окна
        gameVisualizer.setSize(400, 400);

        // Пытаемся установить цель за пределами окна
        gameVisualizer.setTargetPosition(new Point(-100, -100));
        gameVisualizer.onModelUpdateEvent();

        assertEquals(0, gameVisualizer.m_targetPositionX);
        assertEquals(0, gameVisualizer.m_targetPositionY);

        // Пытаемся установить цель за пределами окна (справа и снизу)
        gameVisualizer.setTargetPosition(new Point(500, 500));
        gameVisualizer.onModelUpdateEvent();

        assertEquals(gameVisualizer.getWidth(), gameVisualizer.m_targetPositionX);
        assertEquals(gameVisualizer.getHeight(), gameVisualizer.m_targetPositionY);
    }

    @Test
    void testRobotPositionIsLimitedInMoveRobot() {
        // Пытаемся переместить робота за пределы окна
        gameVisualizer.m_robotPositionX = -100;
        gameVisualizer.m_robotPositionY = -100;
        gameVisualizer.moveRobot(0.1, 0, 10);

        assertTrue(gameVisualizer.m_robotPositionX >= 0);
        assertTrue(gameVisualizer.m_robotPositionY >= 0);

        gameVisualizer.m_robotPositionX = gameVisualizer.getWidth() + 100;
        gameVisualizer.m_robotPositionY = gameVisualizer.getHeight() + 100;
        gameVisualizer.moveRobot(0.1, 0, 10);

        assertTrue(gameVisualizer.m_robotPositionX <= gameVisualizer.getWidth());
        assertTrue(gameVisualizer.m_robotPositionY <= gameVisualizer.getHeight());
    }
}
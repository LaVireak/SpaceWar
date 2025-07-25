package gdd.sprite;

import static gdd.Global.*;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 540;
    private int width;
    private int currentSpeed = 2;
    private int health = 5; // Add health field
    private int multiShotLevel = 1; // Multi-shot level (1-4)
    private int speedLevel = 1; // Speed level (1-4)

    private Rectangle bounds = new Rectangle(175,135,17,32);

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        var ii = new ImageIcon(IMG_PLAYER);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);

        setX(START_X);
        setY(START_Y);
    }

    public int getSpeed() {
        return currentSpeed;
    }

    public int setSpeed(int speed) {
        if (speed < 1) {
            speed = 1; // Ensure speed is at least 1
        }
        this.currentSpeed = speed;
        return currentSpeed;
    }

    public boolean isSpeedUp() {
        return speedLevel > 1;
    }
    
    public int getSpeedLevel() {
        return speedLevel;
    }
    
    public void setSpeedLevel(int level) {
        this.speedLevel = Math.min(4, Math.max(1, level));
        // Update actual speed based on level
        this.currentSpeed = 2 + (speedLevel - 1); // Speed 2, 3, 4, 5
    }
    
    public int getMultiShotLevel() {
        return multiShotLevel;
    }
    
    public void setMultiShotLevel(int level) {
        this.multiShotLevel = Math.min(4, Math.max(1, level));
    }

    public void act() {
        x += dx;
        int playerWidth = getImage().getWidth(null);
        int AdjustedWidth = playerWidth / 4; 

        if (x <= 2) {
            x = 2;
        }

        if (x >= BOARD_WIDTH - AdjustedWidth - BORDER_RIGHT) {
            x = BOARD_WIDTH - AdjustedWidth - BORDER_RIGHT;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = -currentSpeed;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = currentSpeed;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 0;
        }
    }

    // Add getter and setter for health
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.max(0, health);
    }

    public void takeDamage(int amount) {
        setHealth(health - amount);
        if (health <= 0) {
            setDying(true);
        }
    }
}

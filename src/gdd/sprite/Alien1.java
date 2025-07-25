package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Alien1 extends Enemy {

    private Bomb bomb;
    private int health = 10;
    private String movementPattern = "Straight";
    private String attackPattern = "SingleShot";
    private int frameCounter = 0;
    private double angle = 0; // For sine wave movement
    private int originalX; // Store original X for sine wave
    private boolean diveBombTriggered = false;

    public Alien1(int x, int y) {
        super(x, y);
        this.originalX = x;
        initEnemy(x, y);
    }
    
    public Alien1(int x, int y, String movementPattern, String attackPattern) {
        super(x, y);
        this.originalX = x;
        this.movementPattern = movementPattern;
        this.attackPattern = attackPattern;
        initEnemy(x, y);
    }

    private void initEnemy(int x, int y) {

        this.x = x;
        this.y = y;

        bomb = new Bomb(x, y);

        var ii = new ImageIcon(IMG_ENEMY);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    @Override
    public void act(int direction) {
        frameCounter++;
        
        // Move downward
        y += 1;
        
        // Apply movement pattern
        switch (movementPattern) {
            case "Straight":
                // Just move straight down
                break;
                
            case "SineWave":
                // Sine wave movement
                angle += 0.1;
                x = originalX + (int)(Math.sin(angle) * 30);
                break;
                
            case "DiveBomb":
                // Dive bomb - accelerate downward after certain time
                if (frameCounter > 60 && !diveBombTriggered) {
                    diveBombTriggered = true;
                }
                if (diveBombTriggered) {
                    y += 3; // Extra speed when dive bombing
                }
                break;
        }
        
        // Remove if off screen
        if (y > BOARD_HEIGHT + 50) {
            setVisible(false);
        }
        
        // Boundary checking for X movement
        if (x < 0) {
            x = 0;
        } else if (x > BOARD_WIDTH - getImage().getWidth(null)) {
            x = BOARD_WIDTH - getImage().getWidth(null);
        }
    }
    
    public int getHealth() {
        return health;
    }
    
    public void setHealth(int health) {
        this.health = health;
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            setDying(true);
        }
    }
    
    public String getMovementPattern() {
        return movementPattern;
    }
    
    public String getAttackPattern() {
        return attackPattern;
    }

    public Bomb getBomb() {

        return bomb;
    }

    public class Bomb extends Sprite {

        private boolean destroyed;

        public Bomb(int x, int y) {

            initBomb(x, y);
        }

        private void initBomb(int x, int y) {

            setDestroyed(true);

            this.x = x;
            this.y = y;

            var bombImg = "src/images/bomb.png";
            var ii = new ImageIcon(bombImg);
            setImage(ii.getImage());
        }

        public void setDestroyed(boolean destroyed) {

            this.destroyed = destroyed;
        }

        public boolean isDestroyed() {

            return destroyed;
        }
    }
}

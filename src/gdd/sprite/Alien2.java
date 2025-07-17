package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Alien2 extends Enemy {
    
    private int health = 15;
    private String movementPattern = "Straight";
    private String attackPattern = "SpreadShot";
    private int frameCounter = 0;
    private double angle = 0; // For sine wave movement
    private int originalX; // Store original X for sine wave
    private boolean diveBombTriggered = false;
    
    public Alien2(int x, int y) {
        super(x, y);
        this.originalX = x;
        initAlien2();
    }
    
    public Alien2(int x, int y, String movementPattern, String attackPattern) {
        super(x, y);
        this.originalX = x;
        this.movementPattern = movementPattern;
        this.attackPattern = attackPattern;
        initAlien2();
    }
    
    private void initAlien2() {
        // Use a different sprite for Alien2 - we'll use the explosion sprite for now
        // In a real game, you'd have a different alien sprite
        var ii = new ImageIcon("src/images/alien.png"); // Using same sprite but different behavior
        
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
        y += 2;
        
        // Apply movement pattern
        switch (movementPattern) {
            case "Straight":
                // Just move straight down
                break;
                
            case "SineWave":
                // Sine wave movement
                angle += 0.1;
                x = originalX + (int)(Math.sin(angle) * 50);
                break;
                
            case "DiveBomb":
                // Dive bomb - accelerate downward after certain time
                if (frameCounter > 60 && !diveBombTriggered) {
                    diveBombTriggered = true;
                }
                if (diveBombTriggered) {
                    y += 4; // Extra speed when dive bombing
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
}


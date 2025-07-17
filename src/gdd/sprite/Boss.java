package gdd.sprite;

import static gdd.Global.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class Boss extends Enemy {
    
    private int health = 100;
    private int maxHealth = 100;
    private String movementPattern = "BossPattern";
    private String attackPattern = "BossAttack";
    private int frameCounter = 0;
    private int phase = 1; // Boss has multiple phases
    private double angle = 0;
    private int originalX;
    private int attackCooldown = 0;
    private List<BossShot> bossShots = new ArrayList<>();
    
    public Boss(int x, int y) {
        super(x, y);
        this.originalX = x;
        initBoss();
    }
    
    private void initBoss() {
        // Use a larger sprite for the boss - we'll scale the alien sprite larger
        var ii = new ImageIcon(IMG_ENEMY);
        
        // Scale the boss to be much larger than regular enemies
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR * 2,
                ii.getIconHeight() * SCALE_FACTOR * 2,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }
    
    @Override
    public void act(int direction) {
        frameCounter++;
        attackCooldown--;
        
        // Determine current phase based on health
        if (health > 66) {
            phase = 1;
        } else if (health > 33) {
            phase = 2;
        } else {
            phase = 3;
        }
        
        // Boss movement pattern
        switch (phase) {
            case 1:
                // Phase 1: Slow horizontal movement
                angle += 0.05;
                x = originalX + (int)(Math.sin(angle) * 100);
                break;
                
            case 2:
                // Phase 2: Faster movement
                angle += 0.08;
                x = originalX + (int)(Math.sin(angle) * 150);
                break;
                
            case 3:
                // Phase 3: Erratic movement
                angle += 0.12;
                x = originalX + (int)(Math.sin(angle) * 200);
                y += (int)(Math.sin(frameCounter * 0.1) * 2);
                break;
        }
        
        // Boundary checking
        if (x < 0) {
            x = 0;
        } else if (x > BOARD_WIDTH - getImage().getWidth(null)) {
            x = BOARD_WIDTH - getImage().getWidth(null);
        }
        
        if (y < 50) {
            y = 50;
        } else if (y > 200) {
            y = 200;
        }
        
        // Boss attack patterns
        if (attackCooldown <= 0) {
            attack();
            // Set cooldown based on phase
            switch (phase) {
                case 1:
                    attackCooldown = 60; // Attack every second at 60 FPS
                    break;
                case 2:
                    attackCooldown = 40; // Faster attacks
                    break;
                case 3:
                    attackCooldown = 20; // Very fast attacks
                    break;
            }
        }
        
        // Update boss shots
        updateBossShots();
    }
    
    private void attack() {
        int centerX = x + getImage().getWidth(null) / 2;
        int bottomY = y + getImage().getHeight(null);
        
        switch (phase) {
            case 1:
                // Single shot downward
                bossShots.add(new BossShot(centerX, bottomY, 0, 5));
                break;
                
            case 2:
                // Three-way shot
                bossShots.add(new BossShot(centerX - 20, bottomY, -2, 5));
                bossShots.add(new BossShot(centerX, bottomY, 0, 5));
                bossShots.add(new BossShot(centerX + 20, bottomY, 2, 5));
                break;
                
            case 3:
                // Five-way spread shot
                for (int i = -2; i <= 2; i++) {
                    bossShots.add(new BossShot(centerX + i * 15, bottomY, i * 2, 5));
                }
                break;
        }
    }
    
    private void updateBossShots() {
        List<BossShot> shotsToRemove = new ArrayList<>();
        
        for (BossShot shot : bossShots) {
            shot.update();
            if (shot.y > BOARD_HEIGHT + 50) {
                shotsToRemove.add(shot);
            }
        }
        
        bossShots.removeAll(shotsToRemove);
    }
    
    public List<Shot> getBossShots() {
        // Convert BossShot to Shot for compatibility
        List<Shot> shots = new ArrayList<>();
        for (BossShot bossShot : bossShots) {
            Shot shot = new Shot(bossShot.x, bossShot.y);
            shots.add(shot);
        }
        return shots;
    }
    
    public void clearBossShots() {
        bossShots.clear();
    }
    
    public int getHealth() {
        return health;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public void setHealth(int health) {
        this.health = Math.max(0, health);
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            setDying(true);
        }
    }
    
    public int getPhase() {
        return phase;
    }
    
    // Inner class for boss projectiles
    private class BossShot {
        public int x, y;
        public int dx, dy;
        
        public BossShot(int x, int y, int dx, int dy) {
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
        }
        
        public void update() {
            x += dx;
            y += dy;
        }
    }
}


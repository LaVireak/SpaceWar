package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;
import javax.swing.ImageIcon;

public class MultiShot extends PowerUp {
    
    public MultiShot(int x, int y) {
        super(x, y);
        initMultiShot();
    }
    
    private void initMultiShot() {
        // Use multishot spell image
        var ii = new ImageIcon("src/images/ability/multishot_spell.png");
        
        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }
    
    @Override
    public void act() {
        // MultiShot specific behavior - move down the screen
        this.y += 2; // Move down by 2 pixels each frame
    }
    
    @Override
    public void upgrade(Player player) {
        // Increase multi-shot level (max 4 steps)
        int currentLevel = player.getMultiShotLevel();
        if (currentLevel < 4) {
            player.setMultiShotLevel(currentLevel + 1);
            System.out.println("Multi-shot upgraded to level " + (currentLevel + 1));
            
        } else {
            int newHealth = Math.min(player.getHealth() + 1, 5);
            player.setHealth(newHealth);
            // Give score bonus if already at max level
            // This would require adding score to player or passing it differently
            System.out.println("Multi-shot already at max level and filled one health!");
        }
        
        // Remove the power-up
        setVisible(false);
    }
}


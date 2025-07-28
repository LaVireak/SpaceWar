package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class ExplosionBoss extends Explosion {

    public ExplosionBoss(int x, int y, String imgPath) {
        super(x, y);

        var ii = new ImageIcon(imgPath);
        var scaledImage = ii.getImage().getScaledInstance(
                ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH
        );
        setImage(scaledImage);
    }
    
    // --- FIX #2: PREVENT THE EXPLOSION FROM FADING ---
    @Override
    public void visibleCountDown() {

    }

    // These methods can be left empty if not needed.
    public void act(int direction) {}
    
    @Override
    public void act() {}
}
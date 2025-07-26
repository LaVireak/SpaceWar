package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Explosion extends Sprite {
    // New constructor for custom explosion image (boss multi-frame)
    public Explosion(int x, int y, String imgPath) {
        initExplosionCustom(x, y, imgPath);
    }

    private void initExplosionCustom(int x, int y, String imgPath) {
        this.x = x;
        this.y = y;
        var ii = new ImageIcon(imgPath);
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }


    public Explosion(int x, int y) {

        initExplosion(x, y);
    }

    private void initExplosion(int x, int y) {

        this.x = x;
        this.y = y;

        var ii = new ImageIcon(IMG_EXPLOSION);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    public void act(int direction) {
        // Explosions don't move
    }
    
    @Override
    public void act() {
        // Explosions don't move
    }


}

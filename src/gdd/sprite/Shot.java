package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Shot extends Sprite {

    private static final int H_SPACE = 20;
    private static final int V_SPACE = 1;

    public Shot() {
    }

    public Shot(int x, int y) {

        initShot(x, y);
    }

    private void initShot(int x, int y) {
    var ii = new ImageIcon(IMG_SHOT);
    var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
            ii.getIconHeight() * SCALE_FACTOR, 
            java.awt.Image.SCALE_SMOOTH);
    setImage(scaledImage);

    setX(x); // No + H_SPACE
    setY(y - V_SPACE);
}
    
    @Override
    public void act() {
        // Shots move upward
        y -= 20;
        if (y < 0) {
            setVisible(false);
        }
    }
}

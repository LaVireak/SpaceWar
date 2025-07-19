package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Asteroid extends Sprite {
    public Asteroid(int x, int y) {
        var ii = new ImageIcon("src/images/Asteroid.png"); 
        var scaledImage = ii.getImage().getScaledInstance(
            ii.getIconWidth() * SCALE_FACTOR,
            ii.getIconHeight() * SCALE_FACTOR,
            java.awt.Image.SCALE_SMOOTH
        );
        setImage(scaledImage);
        setX(x);
        setY(y);
    }
    

    @Override
    public void act() {
        y += 4;
        if (y > BOARD_HEIGHT) {
            setVisible(false);
        }
    }
}
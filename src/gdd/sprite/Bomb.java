package gdd.sprite;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import gdd.Global;

public class Bomb {
    private int x, y;
    private int speed = 5;
    private boolean visible = true;
    private final Image img;
    

    public Bomb(int x, int y) {
        this.x = x;
        this.y = y;
        this.img = java.awt.Toolkit.getDefaultToolkit().getImage(Global.IMG_BOMB);
    }

    public void move() {
        y += speed;
        if (y > Global.BOARD_HEIGHT) visible = false;
    }

    public void draw(Graphics g) {
        if (visible) {
            g.drawImage(img, x, y, null);
        }
    }

    public boolean isVisible() { return visible; }
    public Rectangle getBounds() { return new Rectangle(x, y, img.getWidth(null), img.getHeight(null)); }
    public int getX() { return x; }
    public int getY() { return y; }
}
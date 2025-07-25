package gdd;

import java.util.Arrays;
import java.util.ArrayList;

public class Global {
    private Global() {
        // Prevent instantiation
    }

    public static final int SCALE_FACTOR = 3; // Scaling factor for sprites

    public static final int BOARD_WIDTH = 716; // Doubled from 358
    public static final int BOARD_HEIGHT = 700; // Doubled from 350
    public static final int BORDER_RIGHT = 60; // Doubled from 30
    public static final int BORDER_LEFT = 10; // Doubled from 5

    public static final int GROUND = 580; // Doubled from 290
    public static final int BOMB_HEIGHT = 10; // Doubled from 5

    public static final int ALIEN_HEIGHT = 24; // Doubled from 12
    public static final int ALIEN_WIDTH = 24; // Doubled from 12
    public static final int ALIEN_INIT_X = 300; // Doubled from 150
    public static final int ALIEN_INIT_Y = 10; // Doubled from 5
    public static final int ALIEN_GAP = 30; // Gap between aliens

    public static final int GO_DOWN = 30; // Doubled from 15
    public static final int NUMBER_OF_ALIENS_TO_DESTROY = 24;
    public static final int CHANCE = 5;
    public static final int DELAY = 17;
    public static final int PLAYER_WIDTH = 30; // Doubled from 15
    public static final int PLAYER_HEIGHT = 30; // Doubled from 10 

    // Images
    public static final String IMG_ENEMY = "src/images/alien.png";
    public static final String IMG_PLAYER = "src/images/player.png";
    public static final String IMG_SHOT = "src/images/shot.png";
    public static final String IMG_EXPLOSION = "src/images/explosion.png";
    public static final String IMG_TITLE = "src/images/title.png";
    public static final String IMG_POWERUP_SPEEDUP = "src/images/ability/Speedup_spell.png";
    public static final ArrayList<String> IMG_FINAL_EXPLOSIONS = new ArrayList<>(Arrays.asList(
        "src/images/Explosion1/explosion2_1.png",
        "src/images/Explosion1/explosion2_2.png",
        "src/images/Explosion1/explosion2_3.png",
        "src/images/Explosion1/explosion2_4.png",
        "src/images/Explosion1/explosion2_5.png",
        "src/images/Explosion1/explosion2_6.png",
        "src/images/Explosion1/explosion2_7.png",
        "src/images/Explosion1/explosion2_8.png",
        "src/images/Explosion1/explosion2_9.png",
        "src/images/Explosion1/explosion2_10.png",
        "src/images/Explosion1/explosion2_11.png"

    ));
    public static final String IMG_BOSS = "src/images/boss.png";
    public static final String IMG_BOMB = "src/images/Charge_2.png"; 
}

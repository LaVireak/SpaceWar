package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SpawnDetails;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.Alien1;
import gdd.sprite.Enemy;
import gdd.sprite.Explosion;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import gdd.sprite.Bomb;

public class Scene1 extends JPanel {

    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Bomb> bombs = new ArrayList<>();
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private Player player;
    // private Shot shot;

    final int BLOCKHEIGHT = 50;
    final int BLOCKWIDTH = 50;

    final int BLOCKS_TO_DRAW = BOARD_HEIGHT / BLOCKHEIGHT;

    private int direction = -1;
    private int deaths = 0;

    private boolean inGame = true;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private final Game game;

    private int currentRow = -1;
    // TODO load this map from a file
    private int mapOffset = 0;
    private final int[][] MAP = {
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0},
        {0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1},
        {0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1},
        {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}
    };

    private HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();
    private AudioPlayer audioPlayer;
    private int lastRowToShow;
    private int firstRowToShow;
    private AudioPlayer gameOverAudioPlayer; // Add this field to manage Game Over sound

    private int score = 0; // Add this field

    public Scene1(Game game) {
        this.game = game;
        // initBoard();
        // gameInit();
        loadSpawnDetails();
    }
    public Player getPlayer() {
    return player;
}

    private void initAudio() {
        try {
            String filePath = "src/audio/scene1.wav";
            audioPlayer = new AudioPlayer(filePath);
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error initializing audio player: " + e.getMessage());
        }
    }

    private void loadSpawnDetails() {
        try {
            Map<Integer, SpawnDetails> loadedMap = gdd.CSVLoader.loadSpawnDetailsFromCSV("src/data/scene1_spawns.csv");
            spawnMap = new HashMap<>(loadedMap);
            System.out.println("Loaded " + spawnMap.size() + " spawn details from CSV");
        } catch (Exception e) {
            System.err.println("Error loading spawn details from CSV: " + e.getMessage());
            // Fallback to hardcoded spawn details
            loadHardcodedSpawnDetails();
        }
    }
    
    private void loadHardcodedSpawnDetails() {
        // Fallback spawn details if CSV loading fails
        spawnMap.put(50, new SpawnDetails("PowerUp-SpeedUp", 100, 0));
        spawnMap.put(200, new SpawnDetails("Alien1", 200, 0));
        spawnMap.put(300, new SpawnDetails("Alien1", 300, 0));

        spawnMap.put(400, new SpawnDetails("Alien1", 400, 0));
        spawnMap.put(401, new SpawnDetails("Alien1", 450, 0));
        spawnMap.put(402, new SpawnDetails("Alien1", 500, 0));
        spawnMap.put(403, new SpawnDetails("Alien1", 550, 0));

        spawnMap.put(500, new SpawnDetails("Alien1", 100, 0));
        spawnMap.put(501, new SpawnDetails("Alien1", 150, 0));
        spawnMap.put(502, new SpawnDetails("Alien1", 200, 0));
        spawnMap.put(503, new SpawnDetails("Alien1", 350, 0));
    }

    private void initBoard() {

    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        gameInit();
        initAudio();
    }

    public void stop() {
        timer.stop();
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void gameInit() {

        enemies = new ArrayList<>();
        powerups = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();

        // for (int i = 0; i < 4; i++) {
        // for (int j = 0; j < 6; j++) {
        // var enemy = new Enemy(ALIEN_INIT_X + (ALIEN_WIDTH + ALIEN_GAP) * j,
        // ALIEN_INIT_Y + (ALIEN_HEIGHT + ALIEN_GAP) * i);
        // enemies.add(enemy);
        // }
        // }
        player = new Player();
        // shot = new Shot();
        
    }

    private void drawMap(Graphics g) {
        // Draw scrolling starfield background

        // Calculate smooth scrolling offset (1 pixel per frame)
        int scrollOffset = (frame) % BLOCKHEIGHT;

        // Calculate which rows to draw based on screen position
        int baseRow = (frame) / BLOCKHEIGHT;
        int rowsNeeded = (BOARD_HEIGHT / BLOCKHEIGHT) + 2; // +2 for smooth scrolling

        // Loop through rows that should be visible on screen
        for (int screenRow = 0; screenRow < rowsNeeded; screenRow++) {
            // Calculate which MAP row to use (with wrapping)
            int mapRow = (baseRow + screenRow) % MAP.length;

            // Calculate Y position for this row
            // int y = (screenRow * BLOCKHEIGHT) - scrollOffset;
            int y = BOARD_HEIGHT - ( (screenRow * BLOCKHEIGHT) - scrollOffset );

            // Skip if row is completely off-screen
            if (y > BOARD_HEIGHT || y < -BLOCKHEIGHT) {
                continue;
            }

            // Draw each column in this row
            for (int col = 0; col < MAP[mapRow].length; col++) {
                if (MAP[mapRow][col] == 1) {
                    // Calculate X position
                    int x = col * BLOCKWIDTH;

                    // Draw a cluster of stars
                    drawStarCluster(g, x, y, BLOCKWIDTH, BLOCKHEIGHT);
                }
            }
        }

    }

    private void drawStarCluster(Graphics g, int x, int y, int width, int height) {
        // Set star color to white
        g.setColor(Color.WHITE);

        // Draw multiple stars in a cluster pattern
        // Main star (larger)
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        g.fillOval(centerX - 2, centerY - 2, 4, 4);

        // Smaller surrounding stars
        g.fillOval(centerX - 15, centerY - 10, 2, 2);
        g.fillOval(centerX + 12, centerY - 8, 2, 2);
        g.fillOval(centerX - 8, centerY + 12, 2, 2);
        g.fillOval(centerX + 10, centerY + 15, 2, 2);

        // Tiny stars for more detail
        g.fillOval(centerX - 20, centerY + 5, 1, 1);
        g.fillOval(centerX + 18, centerY - 15, 1, 1);
        g.fillOval(centerX - 5, centerY - 18, 1, 1);
        g.fillOval(centerX + 8, centerY + 20, 1, 1);
    }

    private void drawAliens(Graphics g) {

        for (Enemy enemy : enemies) {

            if (enemy.isVisible()) {

                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
            }

            if (enemy.isDying()) {

                enemy.die();
            }
        }
    }

    private void drawPowreUps(Graphics g) {

        for (PowerUp p : powerups) {

            if (p.isVisible()) {

                g.drawImage(p.getImage(), p.getX(), p.getY(), this);
            }

            if (p.isDying()) {

                p.die();
            }
        }
    }

    private void drawPlayer(Graphics g) {

        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
            drawPlayerHealthBar(g); // Draw health bar above player
        }

        if (player.isDying()) {
            player.die();
            inGame = false;
        }
    }

    // Add this method to draw the health bar
    private void drawPlayerHealthBar(Graphics g) {
        int barWidth = 60;
        int barHeight = 8;
        int x = player.getX() + player.getImage().getWidth(null) / 2 - barWidth / 2;
        int y = player.getY() - 16;

        // Background
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, barWidth, barHeight);

        // Health
        int health = player.getHealth();
        int maxHealth = 5;
        int healthWidth = (int) ((barWidth - 2) * (health / (double) maxHealth));
        g.setColor(Color.GREEN);
        if (health <= 2) g.setColor(Color.RED);
        else if (health <= 3) g.setColor(Color.ORANGE);
        g.fillRect(x + 1, y + 1, healthWidth, barHeight - 2);

        // Border
        g.setColor(Color.WHITE);
        g.drawRect(x, y, barWidth, barHeight);
    }

    private void drawShot(Graphics g) {

        for (Shot shot : shots) {

            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawBombing(Graphics g) {

        // for (Enemy e : enemies) {
        //     Enemy.Bomb b = e.getBomb();
        //     if (!b.isDestroyed()) {
        //         g.drawImage(b.getImage(), b.getX(), b.getY(), this);
        //     }
        // }
    }

    private void drawExplosions(Graphics g) {

        List<Explosion> toRemove = new ArrayList<>();

        for (Explosion explosion : explosions) {

            if (explosion.isVisible()) {
                g.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
                explosion.visibleCountDown();
                if (!explosion.isVisible()) {
                    toRemove.add(explosion);
                }
            }
        }

        explosions.removeAll(toRemove);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        // Draw dashboard (now includes FRAME)
        drawDashboard(g);

        g.setColor(Color.green);

        if (inGame) {
            drawMap(g);  // Draw background stars first
            drawExplosions(g);
            drawPowreUps(g);
            drawAliens(g);
            drawPlayer(g);
            drawShot(g);
            // Draw bombs
            for (Bomb bomb : bombs) {
                bomb.draw(g);
            }
        } else {
            if (timer.isRunning()) {
                timer.stop();
            }
            gameOver(g);
        }
        Toolkit.getDefaultToolkit().sync();
    }

    private void drawDashboard(Graphics g) {
        int marginTop = 10; // Add margin from the top

        // Draw drop shadow
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRoundRect(6, marginTop + 6, BOARD_WIDTH - 12, 68, 20, 20);

        // Draw gradient background
        java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
        java.awt.GradientPaint gp = new java.awt.GradientPaint(
            0, marginTop, new Color(40, 40, 40, 230),
            0, marginTop + 70, new Color(80, 80, 80, 200)
        );
        g2d.setPaint(gp);
        g2d.fillRoundRect(0, marginTop, BOARD_WIDTH, 64, 20, 20);

        // Draw border
        g2d.setColor(new Color(200, 200, 200, 180));
        g2d.setStroke(new java.awt.BasicStroke(2f));
        g2d.drawRoundRect(0, marginTop, BOARD_WIDTH - 1, 64, 20, 20);

        // Draw text
        g.setColor(Color.white);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 20f));
        g.drawString("Score: " + score, 30, marginTop + 30);

        // Speed status
        String speedStatus = "Speed: " + player.getSpeedLevel() + "/4";
        g.drawString(speedStatus, 250, marginTop + 30);

        // Multi-shot status
        String shotStatus = "Shots: " + player.getMultiShotLevel() + "/4";
        g.drawString(shotStatus, 450, marginTop + 30);

        // Draw FRAME inside dashboard
        g.setFont(g.getFont().deriveFont(Font.PLAIN, 14f));
        g.drawString("FRAME: " + frame, 30, marginTop + 15);
    }

    private void gameOver(Graphics g) {
        if (gameOverAudioPlayer == null) {
            try {
                gameOverAudioPlayer = new AudioPlayer("src/audio/Game_over.wav");
                gameOverAudioPlayer.play();
            } catch (Exception e) {
                System.err.println("Error playing Game Over sound: " + e.getMessage());
            }
        }

        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);

        var small = new Font("Helvetica", Font.BOLD, 14);
        var fontMetrics = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,
                BOARD_WIDTH / 2);
    }

    private void update() {
        // --- Bomb spawn logic ---
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                if (frame % 120 == 0) {
                    if (randomizer.nextInt(100) < 30) {
                        bombs.add(new Bomb(
                            enemy.getX() + enemy.getImage().getWidth(null)/2,
                            enemy.getY() + enemy.getImage().getHeight(null)
                        ));
                    }
                }
            }
        }

        // --- Bomb update and collision ---
        List<Bomb> bombsToRemove = new ArrayList<>();
        for (Bomb bomb : bombs) {
            bomb.move();
            if (!bomb.isVisible()) {
                bombsToRemove.add(bomb);
                continue;
            }
            // Collision with player
            if (player.isVisible() && !player.isDying() && bomb.getBounds().intersects(
                    new java.awt.Rectangle(player.getX(), player.getY(), player.getImage().getWidth(null), player.getImage().getHeight(null)))) {
                player.takeDamage(1);
                bombsToRemove.add(bomb);
                explosions.add(new Explosion(bomb.getX(), bomb.getY()));
            }
        }
        bombs.removeAll(bombsToRemove);

        // Win if played for 1 minute (3600 frames at 60 FPS)
        if (frame >= 3600) {
            inGame = false;
            timer.stop();
            message = "Scene 1 Complete! Loading Scene 2...";
            
            // Add a small delay before transitioning to Scene2
            javax.swing.Timer transitionTimer = new javax.swing.Timer(2000, e -> {
                game.loadScene2();
            });
            transitionTimer.setRepeats(false);
            transitionTimer.start();
            return;
        }

        // ...existing code...
        // Check enemy spawn
        SpawnDetails sd = spawnMap.get(frame);
        if (sd != null) {
            // Create a new enemy based on the spawn details
            switch (sd.type) {
                case "Alien1":
                    Enemy enemy = new Alien1(sd.x, sd.y, sd.getMovementPattern(), sd.getAttackPattern());
                    enemies.add(enemy);
                    break;
                case "Alien2":
                    Enemy enemy2 = new gdd.sprite.Alien2(sd.x, sd.y, sd.getMovementPattern(), sd.getAttackPattern());
                    enemies.add(enemy2);
                    break;
                case "PowerUp-SpeedUp":
                    // Handle speed up item spawn
                    PowerUp speedUp = new SpeedUp(sd.x, sd.y);
                    powerups.add(speedUp);
                    break;
                case "PowerUp-MultiShot":
                    // Handle multi-shot item spawn
                    PowerUp multiShot = new gdd.powerup.MultiShot(sd.x, sd.y);
                    powerups.add(multiShot);
                    break;
                default:
                    System.out.println("Unknown enemy type: " + sd.type);
                    break;
            }
        }

        // ...existing code... (stage only ends at frame 3600)

        // player
        player.act();

        // Power-ups
        for (PowerUp powerup : powerups) {
            if (powerup.isVisible()) {
                powerup.act();
                if (powerup.collidesWith(player)) {
                    powerup.upgrade(player);
                }
            }
        }

        // Enemies
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                enemy.act(direction);

                // --- Add this block for player-enemy collision ---
                // Only check if player is alive and visible
                if (player.isVisible() && !player.isDying()) {
                    // Use bounding box collision
                    int enemyX = enemy.getX();
                    int enemyY = enemy.getY();
                    int enemyW = enemy.getImage().getWidth(null);
                    int enemyH = enemy.getImage().getHeight(null);

                    int playerX = player.getX();
                    int playerY = player.getY();
                    int playerW = player.getImage().getWidth(null);
                    int playerH = player.getImage().getHeight(null);

                    boolean collision = playerX < enemyX + enemyW &&
                                        playerX + playerW > enemyX &&
                                        playerY < enemyY + enemyH &&
                                        playerY + playerH > enemyY;

                    if (collision) {
                        player.takeDamage(1);
                        // Optionally, kill the enemy on collision
                        enemy.setDying(true);
                        // Optionally, add an explosion effect
                        explosions.add(new Explosion(enemyX, enemyY));
                    }
                }
                // --- End of added block ---
            }
        }

        // shot
        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {

            if (shot.isVisible()) {
                int shotX = shot.getX();
                int shotY = shot.getY();

                for (Enemy enemy : enemies) {
                    // Collision detection: shot and enemy
                    int enemyX = enemy.getX();
                    int enemyY = enemy.getY();

                    if (enemy.isVisible() && shot.isVisible()
                            && shotX >= (enemyX)
                            && shotX <= (enemyX + ALIEN_WIDTH)
                            && shotY >= (enemyY)
                            && shotY <= (enemyY + ALIEN_HEIGHT)) {
                        var ii = new ImageIcon(IMG_EXPLOSION);
                        enemy.setImage(ii.getImage());
                        enemy.setDying(true);
                        explosions.add(new Explosion(enemyX, enemyY));
                        // Play explosion sound
                        try {
                            AudioPlayer explosionPlayer = new AudioPlayer("src/audio/explosion.wav");
                            explosionPlayer.play();
                        } catch (Exception ex) {
                            System.err.println("Error playing explosion sound: " + ex.getMessage());
                        }
                        deaths++;
                        score++; // Increment score when enemy is killed
                        shot.die();
                        shotsToRemove.add(shot);
                    }
                }

                int y = shot.getY();
                // y -= 4;
                y -= 20;

                if (y < 0) {
                    shot.die();
                    shotsToRemove.add(shot);
                } else {
                    shot.setY(y);
                }
            }
        }
        shots.removeAll(shotsToRemove);

        // enemies
        // for (Enemy enemy : enemies) {
        //     int x = enemy.getX();
        //     if (x >= BOARD_WIDTH - BORDER_RIGHT && direction != -1) {
        //         direction = -1;
        //         for (Enemy e2 : enemies) {
        //             e2.setY(e2.getY() + GO_DOWN);
        //         }
        //     }
        //     if (x <= BORDER_LEFT && direction != 1) {
        //         direction = 1;
        //         for (Enemy e : enemies) {
        //             e.setY(e.getY() + GO_DOWN);
        //         }
        //     }
        // }
        // for (Enemy enemy : enemies) {
        //     if (enemy.isVisible()) {
        //         int y = enemy.getY();
        //         if (y > GROUND - ALIEN_HEIGHT) {
        //             inGame = false;
        //             message = "Invasion!";
        //         }
        //         enemy.act(direction);
        //     }
        // }
        // bombs - collision detection
        // Bomb is with enemy, so it loops over enemies
        /*
        for (Enemy enemy : enemies) {

            int chance = randomizer.nextInt(15);
            Enemy.Bomb bomb = enemy.getBomb();

            if (chance == CHANCE && enemy.isVisible() && bomb.isDestroyed()) {

                bomb.setDestroyed(false);
                bomb.setX(enemy.getX());
                bomb.setY(enemy.getY());
            }

            int bombX = bomb.getX();
            int bombY = bomb.getY();
            int playerX = player.getX();
            int playerY = player.getY();

            if (player.isVisible() && !bomb.isDestroyed()
                    && bombX >= (playerX)
                    && bombX <= (playerX + PLAYER_WIDTH)
                    && bombY >= (playerY)
                    && bombY <= (playerY + PLAYER_HEIGHT)) {

                var ii = new ImageIcon(IMG_EXPLOSION);
                player.setImage(ii.getImage());
                player.setDying(true);
                bomb.setDestroyed(true);
            }

            if (!bomb.isDestroyed()) {
                bomb.setY(bomb.getY() + 1);
                if (bomb.getY() >= GROUND - BOMB_HEIGHT) {
                    bomb.setDestroyed(true);
                }
            }
        }
         */
    }

    private void doGameCycle() {
        frame++;
        update();
        repaint();
    }

    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("Scene2.keyPressed: " + e.getKeyCode());

            int key = e.getKeyCode();

            // Only play shot sound and create shots when SPACE is pressed (not on movement)
            if (key == KeyEvent.VK_SPACE && inGame) {
                System.out.println("Shots: " + shots.size());
                if (shots.size() < 8) { // Increased limit for multi-shot
                    int playerCenterX = player.getX() + player.getImage().getWidth(null) / 2;
                    int playerY = player.getY();
                    int multiShotLevel = player.getMultiShotLevel();
                    switch (multiShotLevel) {
                        case 1:
                            shots.add(new Shot(playerCenterX, playerY));
                            try {
                                AudioPlayer shotPlayer = new AudioPlayer("src/audio/shot.wav");
                                shotPlayer.play();
                            } catch (Exception ex) {
                                System.err.println("Error playing shot sound: " + ex.getMessage());
                            }
                            break;
                        case 2:
                            shots.add(new Shot(playerCenterX - 10, playerY));
                            try {
                                AudioPlayer shotPlayer = new AudioPlayer("src/audio/shot.wav");
                                shotPlayer.play();
                            } catch (Exception ex) {
                                System.err.println("Error playing shot sound: " + ex.getMessage());
                            }
                            shots.add(new Shot(playerCenterX + 10, playerY));
                            try {
                                AudioPlayer shotPlayer = new AudioPlayer("src/audio/shot.wav");
                                shotPlayer.play();
                            } catch (Exception ex) {
                                System.err.println("Error playing shot sound: " + ex.getMessage());
                            }
                            break;
                        case 3:
                            shots.add(new Shot(playerCenterX - 15, playerY));
                            try {
                                AudioPlayer shotPlayer = new AudioPlayer("src/audio/shot.wav");
                                shotPlayer.play();
                            } catch (Exception ex) {
                                System.err.println("Error playing shot sound: " + ex.getMessage());
                            }
                            shots.add(new Shot(playerCenterX, playerY));
                            try {
                                AudioPlayer shotPlayer = new AudioPlayer("src/audio/shot.wav");
                                shotPlayer.play();
                            } catch (Exception ex) {
                                System.err.println("Error playing shot sound: " + ex.getMessage());
                            }
                            shots.add(new Shot(playerCenterX + 15, playerY));
                            try {
                                AudioPlayer shotPlayer = new AudioPlayer("src/audio/shot.wav");
                                shotPlayer.play();
                            } catch (Exception ex) {
                                System.err.println("Error playing shot sound: " + ex.getMessage());
                            }
                            break;
                        case 4:
                            shots.add(new Shot(playerCenterX - 20, playerY));
                            try {
                                AudioPlayer shotPlayer = new AudioPlayer("src/audio/shot.wav");
                                shotPlayer.play();
                            } catch (Exception ex) {
                                System.err.println("Error playing shot sound: " + ex.getMessage());
                            }
                            shots.add(new Shot(playerCenterX - 7, playerY));
                            try {
                                AudioPlayer shotPlayer = new AudioPlayer("src/audio/shot.wav");
                                shotPlayer.play();
                            } catch (Exception ex) {
                                System.err.println("Error playing shot sound: " + ex.getMessage());
                            }
                            shots.add(new Shot(playerCenterX + 7, playerY));
                            try {
                                AudioPlayer shotPlayer = new AudioPlayer("src/audio/shot.wav");
                                shotPlayer.play();
                            } catch (Exception ex) {
                                System.err.println("Error playing shot sound: " + ex.getMessage());
                            }
                            shots.add(new Shot(playerCenterX + 20, playerY));
                            try {
                                AudioPlayer shotPlayer = new AudioPlayer("src/audio/shot.wav");
                                shotPlayer.play();
                            } catch (Exception ex) {
                                System.err.println("Error playing shot sound: " + ex.getMessage());
                            }
                            break;
                    }
                }
            }

            // Now handle movement and other keys
            player.keyPressed(e);

        }
    }
}

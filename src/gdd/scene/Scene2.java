package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SpawnDetails;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.Alien1;
import gdd.sprite.Alien2;
import gdd.sprite.Asteroid;
import gdd.sprite.Boss;
import gdd.sprite.Enemy;
import gdd.sprite.Explosion;
import gdd.sprite.ExplosionBoss;
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
import javax.swing.JPanel;
import javax.swing.Timer;
import gdd.sprite.Bomb;

public class Scene2 extends JPanel {
    // For slow boss explosion animation
    private int bossExplosionIndex = 0;
    private int bossExplosionLastFrame = 0;
    // Add a counter to delay victory message after boss defeat


    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private List<Shot> bossShots; // Boss projectiles
    private Player player;
    private Boss boss;
    private boolean bossSpawned = false;
    private boolean bossDefeated = false;

    private int direction = -1;
    private int deaths = 0;
    private boolean inGame = true;
    private String message = "Game Over";
    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();
    private Timer timer;
    private final Game game;
    private HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();
    private AudioPlayer audioPlayer;
    private int score = 0;
    private List<Asteroid> asteroids;
    private static final int BG_ROWS = 40;
    private static final int BG_COLS = 24;
    private final int[][] backgroundMap = new int[BG_ROWS][BG_COLS];
    private int bgOffset = 0;
    private int initialPlayerHealth = 5;
    private List<Bomb> bombs = new ArrayList<>();
    private static final int BOSS_EXPLOSION_FRAME_DELAY = 8;

    public Scene2(Game game,int initialPlayerHealth) {
        this.game = game;
        loadSpawnDetails();
        this.initialPlayerHealth = initialPlayerHealth;
    }

    private void loadSpawnDetails() {
        try {
            Map<Integer, SpawnDetails> loadedMap = gdd.CSVLoader.loadSpawnDetailsFromCSV("src/data/scene2_spawns.csv");
            spawnMap = new HashMap<>(loadedMap);
            System.out.println("Loaded " + spawnMap.size() + " spawn details for Scene2 from CSV");
        } catch (Exception e) {
            System.err.println("Error loading Scene2 spawn details from CSV: " + e.getMessage());
            // Fallback to hardcoded spawn details
        }
    }
    

    private void initAudio() {
        try {
            String filePath = "src/audio/scene2.wav";
            audioPlayer = new AudioPlayer(filePath);
            audioPlayer.loop();

        } catch (Exception e) {
            System.err.println("Error initializing audio player: " + e.getMessage());
        }
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
                try {
                    audioPlayer.stop();
                } catch (javax.sound.sampled.UnsupportedAudioFileException | java.io.IOException | javax.sound.sampled.LineUnavailableException ex) {
                    System.err.println("Error stopping audio player: " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }
    private void initBackground() {
    for (int row = 0; row < BG_ROWS; row++) {
        for (int col = 0; col < BG_COLS; col++) {
            int r = randomizer.nextInt(100);
            if (r < 5) backgroundMap[row][col] = 2; // big star
            else if (r < 20) backgroundMap[row][col] = 1; // small star
            else if (r < 22) backgroundMap[row][col] = 3; // nebula
            else backgroundMap[row][col] = 0; // empty
        }
    }
}

    private void gameInit() {
        enemies = new ArrayList<>();
        powerups = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();
        bossShots = new ArrayList<>();
        player = new Player();
        player.setHealth(initialPlayerHealth);
        asteroids = new ArrayList<>();
        boss = null;
        bossSpawned = false;
        bossDefeated = false;
        bossExplosionIndex = 0;
        bossExplosionLastFrame = 0;
        initBackground();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
    

    private void doDrawing(Graphics g) {
        
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);
        drawBackground(g);
        drawDashboard(g);
        for (Bomb bomb : bombs) {

            bomb.draw(g);
        }

        if (inGame) {
            // Draw game elements
            drawAsteroids(g);
            drawEnemies(g);
            drawBoss(g);
            drawPowerUps(g);
            drawPlayer(g);
            drawShots(g);
            drawBossShots(g);
            drawExplosions(g);
        } else {
            if (timer.isRunning()) {
                timer.stop();
            }
            gameOver(g);
        }
        }
private void drawBackground(Graphics g) {
    int tileW = BOARD_WIDTH / BG_COLS;
    int tileH = BOARD_HEIGHT / (BG_ROWS - 8); 

    for (int row = 0; row < BG_ROWS; row++) {
        for (int col = 0; col < BG_COLS; col++) {
            int type = backgroundMap[row][col];
            int y = ((row * tileH) + bgOffset) % BOARD_HEIGHT;
            int x = col * tileW;

            switch (type) {
                case 1: // small star
                    g.setColor(Color.WHITE);
                    g.fillRect(x + tileW/2, y + tileH/2, 2, 2);
                    break;
                case 2: // big star
                    g.setColor(Color.YELLOW);
                    g.fillOval(x + tileW/2 - 2, y + tileH/2 - 2, 5, 5);
                    break;
                case 3: // nebula
                    g.setColor(new Color(100, 100, 255, 60));
                    g.fillOval(x, y, tileW, tileH);
                    break;
                default:
                    // empty
            }
        }
    }

        Toolkit.getDefaultToolkit().sync();
    }
    

    private void drawDashboard(Graphics g) {
        int marginTop = 10;

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

        // Draw boss health bar if boss is active
        if (bossSpawned && boss != null && boss.isVisible()) {
            g.setFont(g.getFont().deriveFont(Font.BOLD, 16f));
            g.drawString("BOSS HEALTH:", 30, marginTop + 55);
            
            // Boss health bar
            int barWidth = 200;
            int barHeight = 12;
            int barX = 150;
            int barY = marginTop + 45;
            
            // Background
            g.setColor(Color.DARK_GRAY);
            g.fillRect(barX, barY, barWidth, barHeight);
            
            // Health
            double healthPercent = (double)boss.getHealth() / boss.getMaxHealth();
            int healthWidth = (int)(barWidth * healthPercent);
            
            if (healthPercent > 0.6) g.setColor(Color.GREEN);
            else if (healthPercent > 0.3) g.setColor(Color.ORANGE);
            else g.setColor(Color.RED);
            
            g.fillRect(barX, barY, healthWidth, barHeight);
            
            // Border
            g.setColor(Color.WHITE);
            g.drawRect(barX, barY, barWidth, barHeight);
            
            // Phase indicator
            g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
            g.drawString("Phase " + boss.getPhase(), barX + barWidth + 10, barY + 10);
        }

        // Draw FRAME inside dashboard
        g.setFont(g.getFont().deriveFont(Font.PLAIN, 14f));
        g.drawString("FRAME: " + frame, 30, marginTop + 15);
    }

    private void gameOver(Graphics g) {
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
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
            if (bossDefeated){
                audioPlayer = new AudioPlayer("src/audio/win.wav");
            } else {
                audioPlayer = new AudioPlayer("src/audio/Game_over.wav");
            }
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error stopping audio player: " + e.getMessage());
        }
    }

    private void drawEnemies(Graphics g) {
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
            }
        }
    }
    
    private void drawBoss(Graphics g) {
        if (boss != null && boss.isVisible()) {
            g.drawImage(boss.getImage(), boss.getX(), boss.getY(), this);
        }
    }

    private void drawPowerUps(Graphics g) {
        for (PowerUp powerUp : powerups) {
            if (powerUp.isVisible()) {
                g.drawImage(powerUp.getImage(), powerUp.getX(), powerUp.getY(), this);
            }
        }
    }

    private void drawPlayer(Graphics g) {
        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
            drawPlayerHealthBar(g);
        }
        
        if (player.isDying()) {
            player.die();
            inGame = false;
        }
    }
    
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

    private void drawShots(Graphics g) {
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }
    
    private void drawBossShots(Graphics g) {
        for (Shot shot : bossShots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }
    private void drawAsteroids(Graphics g) {
        for (Asteroid asteroid : asteroids) {
            if (asteroid.isVisible()) {
                g.drawImage(asteroid.getImage(), asteroid.getX(), asteroid.getY(), this);
            }
        }
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

    private void update() {
        // Delay victory message and game end for boss explosion animation
        if (bossDefeated) {
        
        // 1. Is the multi-frame explosion animation still playing?
            if (bossExplosionIndex < IMG_FINAL_EXPLOSIONS.size()) {
            
                if ((frame - bossExplosionLastFrame) >= BOSS_EXPLOSION_FRAME_DELAY) {
                    String imgPath = IMG_FINAL_EXPLOSIONS.get(bossExplosionIndex);
                    ExplosionBoss tempExplosion = new ExplosionBoss(0, 0, imgPath);
                    int explosionWidth = tempExplosion.getImage().getWidth(null);
                    int explosionHeight = tempExplosion.getImage().getHeight(null);


                    int bossCenterX = boss.getX() - boss.getImage().getWidth(null) / 2;
                    int bossCenterY = boss.getY() - boss.getImage().getHeight(null) / 2;
                    int explosionX = bossCenterX - (explosionWidth / 2);
                    int explosionY = bossCenterY - (explosionHeight / 2);
                    explosions.add(new ExplosionBoss(explosionX, explosionY, imgPath));
                
                    bossExplosionIndex++;
                    bossExplosionLastFrame = frame; 
                }
            
            } else {
                if (inGame) {
                    inGame = false;
                    message = "Victory! You defeated the boss!";
                
                // Play final victory sound
                    try {
                        if (audioPlayer != null) audioPlayer.stop();
                        audioPlayer = new AudioPlayer("src/audio/win.wav");
                        audioPlayer.play();
                    } catch (Exception e) {
                        System.err.println("Error playing victory sound: " + e.getMessage());
                    }
                }
            }
        return;
    }
    // Increment frame count}
        if (frame % 60 == 0) {
    int asteroidX = randomizer.nextInt(BOARD_WIDTH - 40) + 20;
    asteroids.add(new Asteroid(asteroidX, -40));
    int bombInterval = 120;
    if (frame >= 2400) {
        bombInterval = 30;
    } else if (frame >= 3600) {
        bombInterval = 20;
    }
    else if (frame >= 1200) {
        bombInterval = 60;
    } 

            for (Enemy enemy : enemies) {
                if (!(enemy instanceof Boss) && enemy.isVisible()) {
                    if (frame % bombInterval == 0) {
                    if (randomizer.nextInt(100) < 60) {
                        bombs.add(new Bomb(
                            enemy.getX() + enemy.getImage().getWidth(null)/2,
                            enemy.getY() + enemy.getImage().getHeight(null)
                        ));
                    }
                }
            }
        }
    } 
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

    // Update asteroids
    List<Asteroid> asteroidsToRemove = new ArrayList<>();
    for (Asteroid asteroid : asteroids) {
        if (asteroid.isVisible()) {
            asteroid.act();
            // Collision with player
            if (player.isVisible() && !player.isDying() && asteroid.collidesWith(player)) {
                player.takeDamage(1);
                asteroid.setVisible(false);
                explosions.add(new Explosion(asteroid.getX(), asteroid.getY()));
            }
        }
        if (!asteroid.isVisible()) {
            asteroidsToRemove.add(asteroid);
        }
    }
    asteroids.removeAll(asteroidsToRemove);

        // Player movement
        player.act();

        // Handle spawning based on spawnMap
        SpawnDetails sd = spawnMap.get(frame);
        if (sd != null) {
            switch (sd.type) {
                case "Alien1":
                    Enemy enemy = new Alien1(sd.x, sd.y, sd.getMovementPattern(), sd.getAttackPattern());
                    enemies.add(enemy);
                    break;
                case "Alien2":
                    Enemy enemy2 = new Alien2(sd.x, sd.y, sd.getMovementPattern(), sd.getAttackPattern());
                    enemies.add(enemy2);
                    break;
                case "Boss":
                    if (!bossSpawned) {
                        boss = new Boss(sd.x, sd.y);
                        bossSpawned = true;
                        System.out.println("Boss spawned!");
                        // Switch to boss fight music
                        try {
                            if (audioPlayer != null) {
                                audioPlayer.stop();
                            }
                            audioPlayer = new AudioPlayer("src/audio/boss_fight.wav");
                            audioPlayer.play();
                        } catch (Exception e) {
                            System.err.println("Error switching to boss fight audio: " + e.getMessage());
                        }
                    }
                    break;
                case "PowerUp-SpeedUp":
                    powerups.add(new SpeedUp(sd.x, sd.y));
                    break;
                case "PowerUp-MultiShot":
                    powerups.add(new gdd.powerup.MultiShot(sd.x, sd.y));
                    break;
            }
        }

        // Update enemies
        List<Enemy> enemiesToRemove = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                enemy.act(1);
                
                // Check collision with player
                if (player.isVisible() && !player.isDying() && enemy.collidesWith(player)) {
                    player.takeDamage(1);
                    enemy.setDying(true);
                    explosions.add(new Explosion(enemy.getX(), enemy.getY()));
                }
                
                // Remove if off screen
                if (enemy.getY() > BOARD_HEIGHT + 50) {
                    enemy.die();
                }
            }
            
            if (enemy.isDying() || !enemy.isVisible()) {
                enemiesToRemove.add(enemy);
            }
        }
        enemies.removeAll(enemiesToRemove);


        if (boss != null && boss.isVisible()) {
            boss.act(1);

            if (player.isVisible() && !player.isDying() && boss.collidesWith(player)) {
                player.takeDamage(2);
                explosions.add(new Explosion(player.getX(), player.getY()));
            }

            bossShots.addAll(boss.getBossShots());
            boss.clearBossShots();
        }
        
        if (boss != null && boss.isDying()) {
            bossDefeated = true;
            if (boss.isVisible()) {
                boss.setVisible(false);
                score += 100; // Give score bonus once.
            }
            try{
                if (audioPlayer != null) {
                    audioPlayer.stop();
                }
                audioPlayer = new AudioPlayer("src/audio/explosion.wav");
                audioPlayer.play();
            } catch (Exception e) {
                System.err.println("Error playing boss defeated sound: " + e.getMessage());
            }
            

            int bossX = boss.getX();
            int bossY = boss.getY();
            int explosionDelay = 30; // 30 frames per explosion image
            // Show all explosion images in sequence, keep boss visible until done
            if (bossExplosionIndex < IMG_FINAL_EXPLOSIONS.size()) {
                if ((frame - bossExplosionLastFrame) >= explosionDelay) {
                    String imgPath = IMG_FINAL_EXPLOSIONS.get(bossExplosionIndex);
                    explosions.add(new ExplosionBoss(bossX, bossY, imgPath));
                    bossExplosionIndex++;
                    bossExplosionLastFrame = frame;
                }
                // Keep boss visible until all explosions are shown
                boss.setVisible(true);
            } else if (bossExplosionIndex == IMG_FINAL_EXPLOSIONS.size()) {
                bossExplosionIndex++; // Prevent repeat
            }
        }

        // Update shots
        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                shot.setY(shot.getY() - 20);
                
                // Check collision with enemies
                for (Enemy enemy : enemies) {
                    if (enemy.isVisible() && shot.collidesWith(enemy)) {
                        enemy.setDying(true);
                        shot.die();
                        explosions.add(new Explosion(enemy.getX(), enemy.getY()));
                        score++;
                        break;
                    }
                }
                
                // Check collision with boss
                if (boss != null && boss.isVisible() && shot.collidesWith(boss)) {
                    boss.takeDamage(1);
                    shot.die();
                    explosions.add(new Explosion(shot.getX(), shot.getY()));
                    score++;
                }
                
                if (shot.getY() < 0) {
                    shot.die();
                }
            }
            
            if (!shot.isVisible()) {
                shotsToRemove.add(shot);
            }
        }
        shots.removeAll(shotsToRemove);
        
        // Update boss shots
        List<Shot> bossShotsToRemove = new ArrayList<>();
        for (Shot bossShot : bossShots) {
            if (bossShot.isVisible()) {
                bossShot.setY(bossShot.getY() + 8);
                
                // Check collision with player
                if (player.isVisible() && !player.isDying() && bossShot.collidesWith(player)) {
                    player.takeDamage(1);
                    bossShot.die();
                    explosions.add(new Explosion(player.getX(), player.getY()));
                }
                
                if (bossShot.getY() > BOARD_HEIGHT + 50) {
                    bossShot.die();
                }
            }
            
            if (!bossShot.isVisible()) {
                bossShotsToRemove.add(bossShot);
            }
        }
        bossShots.removeAll(bossShotsToRemove);

        // Update power-ups
        for (PowerUp powerUp : powerups) {
            if (powerUp.isVisible()) {
                powerUp.act();
                if (powerUp.collidesWith(player)) {
                    powerUp.upgrade(player);
                }
            }
        }
    }

    private void doGameCycle() {
        frame++;
        update();
        repaint();
        bgOffset += 1; // scroll speed
        if (bgOffset >= BOARD_HEIGHT) bgOffset = 0;
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
            player.keyPressed(e);
            int x = player.getX();
            int y = player.getY();
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_SPACE && inGame) {
                if (shots.size() < 8) { // Increased limit for multi-shot
                    int playerCenterX = player.getX() + player.getImage().getWidth(null) / 2;
                    int playerY = player.getY();
                    
                    // Create shots based on multi-shot level
                    int multiShotLevel = player.getMultiShotLevel();
                    
                    switch (multiShotLevel) {
                        case 1:
                            // Single shot
                            shots.add(new Shot(playerCenterX, playerY));
                            break;
                        case 2:
                            // Double shot
                            shots.add(new Shot(playerCenterX - 10, playerY));
                            shots.add(new Shot(playerCenterX + 10, playerY));
                            break;
                        case 3:
                            // Triple shot
                            shots.add(new Shot(playerCenterX - 15, playerY));
                            shots.add(new Shot(playerCenterX, playerY));
                            shots.add(new Shot(playerCenterX + 15, playerY));
                            break;
                        case 4:
                            // Quad shot
                            shots.add(new Shot(playerCenterX - 20, playerY));
                            shots.add(new Shot(playerCenterX - 7, playerY));
                            shots.add(new Shot(playerCenterX + 7, playerY));
                            shots.add(new Shot(playerCenterX + 20, playerY));
                            break;
                    }
                }
            }
        }
    }
}

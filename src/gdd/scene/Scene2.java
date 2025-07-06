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
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Scene2 extends JPanel {

    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private Player player;

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

    public Scene2(Game game) {
        this.game = game;
        loadSpawnDetails();
    }

    private void loadSpawnDetails() {
        // TODO: Add spawn details for Scene2
        spawnMap.put(50, new SpawnDetails("PowerUp-SpeedUp", 100, 0));
        spawnMap.put(200, new SpawnDetails("Alien1", 200, 0));
        // Add more as needed
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
        player = new Player();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        drawDashboard(g);

        if (inGame) {
            // Draw game elements
            drawEnemies(g);
            drawPowerUps(g);
            drawPlayer(g);
            drawShots(g);
            drawExplosions(g);
        } else {
            if (timer.isRunning()) {
                timer.stop();
            }
            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void drawDashboard(Graphics g) {
        g.setColor(Color.white);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 20f));
        g.drawString("Score: " + score, 30, 30);
        // Add more dashboard info as needed
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
    }

    private void drawEnemies(Graphics g) {
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
            }
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
        }
    }

    private void drawShots(Graphics g) {
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawExplosions(Graphics g) {
        for (Explosion explosion : explosions) {
            if (explosion.isVisible()) {
                g.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
                explosion.visibleCountDown();
            }
        }
    }

    private void update() {
        // Implement Scene2 specific update logic
        // Similar to Scene1 but with Scene2 specific behavior

        // Player movement
        player.act();

        // Update shots
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                shot.setY(shot.getY() - 20);
                if (shot.getY() < 0) {
                    shot.die();
                }
            }
        }

        // Remove dead shots
        shots.removeIf(shot -> !shot.isVisible());

        // Update enemies
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                enemy.act(1); // Move enemies
            }
        }

        // Update power-ups
        for (PowerUp powerUp : powerups) {
            if (powerUp.isVisible()) {
                powerUp.act();
                if (powerUp.collidesWith(player)) {
                    powerUp.upgrade(player);
                }
            }
        }

        // Handle spawning based on spawnMap
        SpawnDetails sd = spawnMap.get(frame);
        if (sd != null) {
            switch (sd.type) {
                case "Alien1":
                    enemies.add(new Alien1(sd.x, sd.y));
                    break;
                case "PowerUp-SpeedUp":
                    powerups.add(new SpeedUp(sd.x, sd.y));
                    break;
            }
        }
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
            player.keyPressed(e);
            int x = player.getX();
            int y = player.getY();
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_SPACE && inGame) {
                if (shots.size() < 4) {
                    Shot shot = new Shot(x, y);
                    shots.add(shot);
                }
            }
        }
    }
}

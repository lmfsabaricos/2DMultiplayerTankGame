package tankgamepack.game;

import tankgamepack.GameConstants;
import tankgamepack.Launcher;
import tankgamepack.Resources.ResourceManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

/**
 * @author anthony-pc
 */
public class GameWorld extends JPanel implements Runnable {

    private BufferedImage world;
    private Tank t1;
    private Tank t2;
    private final Launcher lf;
    public int worldSize;

    List<GameObject> gobjs = new ArrayList<>();
    // private long tick = 0; // for tick logic, not necessary to be used.
    List<Animation> anims = new ArrayList<>();

    private boolean isRunning = true;

    public GameWorld(Launcher lf) {
        this.lf = lf;
    }

    @Override
    public void run() {
        Sound bgMusic = ResourceManager.getSound("bgmusic");
        bgMusic.loopCont();
        bgMusic.setVolume(.3f);
        bgMusic.playSound();

        try {
            while (isRunning) {
                updateObjs();
                checkCollision();
                checkGameOver();
                this.repaint();   // redraw game, never call paint component directly; repaint happens on different thread
                Thread.sleep(1000 / 144); // artificially slow game down to prevent it from updating too fast
            }
        } catch (InterruptedException ignored) {
            System.out.println(ignored);
        }
        this.lf.killGame();
    }

    public synchronized void updateObjs() {
        Iterator<GameObject> objItr = gobjs.iterator();
        GameObject currentObj;
        Queue<GameObject> toAdd = new LinkedList<>();
        Queue<GameObject> toRemove = new LinkedList<>();

        while (objItr.hasNext()) {
            currentObj = objItr.next();
            if (currentObj instanceof MovableObjects) {
                ((MovableObjects) currentObj).update();

                if (currentObj instanceof Bullet) {
                    if (((MovableObjects) currentObj).expired()) {
                        toAdd.add(((Bullet) currentObj).playExplode());
                        toRemove.add(currentObj);
                    }
                }

                if (currentObj instanceof Tank) {
                    GameObject temp = ((Tank) currentObj).addBulletToGameObjs();
                    if (temp != null) {
                        toAdd.add(temp);
                    }
                    if (((Tank) currentObj).expired()) {
                        gameOver(((Tank) currentObj).getId() == 1 ? 2 : 1); // Pass the winning tank's ID
                    }
                }
            }
            if (currentObj instanceof Animation) {
                if (!((Animation) currentObj).update()) {
                    toRemove.add(currentObj);
                }
            }

            if (currentObj instanceof PowerUp) {
                if (((PowerUp) currentObj).expired()) {
                    toRemove.add(currentObj);
                }
            }
        }

        while (!toAdd.isEmpty()) {
            gobjs.add(toAdd.remove());
        }
        while (!toRemove.isEmpty()) {
            gobjs.remove(toRemove.remove());
        }
    }

    private void gameOver(int winningTankId) {
        this.isRunning = false;
        lf.setWinner(winningTankId);
    }

    /**
     * Reset game to its initial state.
     */
    public void resetGame() {
        this.t1.setX(300);
        this.t1.setY(300);
    }

    /**
     * Load all resources for Tank Wars Game. Set all Game Objects to their
     * initial state as well.
     */
    public void InitializeGame() {
        this.world = new BufferedImage(
                GameConstants.GAME_WORLD_WIDTH,
                GameConstants.GAME_WORLD_HEIGHT,
                BufferedImage.TYPE_INT_RGB); // floor image

        InputStreamReader isr = new InputStreamReader(Objects.requireNonNull(ResourceManager.class.getClassLoader().getResourceAsStream("maps/TankMapConverted.csv")));

        // 0 = empty space
        // 9 = unbreakable barrier
        // 3 = unbreakable wall, collidable
        // 4-7 = power up
        // 8 = breakable wall

        // assume csv file follows the proper format (included in txt file)
        try (BufferedReader mapReader = new BufferedReader(isr)) {
            int row = 0;
            String[] gameItems;

            while (mapReader.ready()) {
                gameItems = mapReader.readLine().strip().split(",");

                for (int column = 0; column < gameItems.length; column++) {
                    String gameObj = gameItems[column];
                    if ("0".equals(gameObj)) continue; // skip over 0s
                    this.gobjs.add(GameObject.newInstance(gameObj, column * 30, row * 30));
                }
                row++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Use predefined spawn points for tanks
        int[][] spawnPoints = {
                {500, 500},
                {1500, 1500}
        };

        // Randomly choose spawn points for tanks
        Random rand = new Random();
        int[] spawnPoint1 = spawnPoints[rand.nextInt(spawnPoints.length)];
        int[] spawnPoint2 = spawnPoints[rand.nextInt(spawnPoints.length)];

        t1 = new Tank(spawnPoint1[0], spawnPoint1[1], 0, ResourceManager.getSprite("tank1"));
        t2 = new Tank(spawnPoint2[0], spawnPoint2[1], 0, ResourceManager.getSprite("tank2"));
        TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER);

        this.lf.getJf().addKeyListener(tc1);
        this.lf.getJf().addKeyListener(tc2);
        this.gobjs.add(t1);
        this.gobjs.add(t2);
    }

    static double miniMapScaleFactor = 0.1;

    public void renderMiniMap(Graphics2D g) {
        BufferedImage mm = this.world.getSubimage(0, 0, GameConstants.GAME_WORLD_WIDTH, GameConstants.GAME_WORLD_HEIGHT);
        g.scale(miniMapScaleFactor, miniMapScaleFactor);
        g.drawImage(mm, 0, 0, null);
        g.scale(1 / miniMapScaleFactor, 1 / miniMapScaleFactor);
    }

    public void renderSplitScreen(Graphics2D g) {
        g.drawImage(t1.cameraPosition(world), 0, 0, null);
        g.drawImage(t2.cameraPosition(world), GameConstants.GAME_SCREEN_WIDTH / 2 + 4, 0, null);
    }

    private void drawFloor(Graphics g) {
        for (int i = 0; i < GameConstants.GAME_WORLD_WIDTH; i += 320) {
            for (int j = 0; j < GameConstants.GAME_WORLD_HEIGHT; j += 240) {
                g.drawImage(ResourceManager.getSprite("floor"), i, j, null);
            }
        }
    }

    private void checkCollision() {
        for (int i = 0; i < gobjs.size(); i++) {
            GameObject obj1 = gobjs.get(i);
            if (!(obj1 instanceof MovableObjects) && !(obj1 instanceof PowerUp)) continue;
            for (int j = 0; j < gobjs.size(); j++) {
                if (i == j) continue;
                GameObject obj2 = gobjs.get(j);

                if (obj1.getHitbox().intersects(obj2.getHitbox())) {
                    obj1.collides(obj2);
                }
            }
        }
    }

    private void checkGameOver() {
        if (t1.expired()) {
            gameOver(2);  // Blue Tank wins
        } else if (t2.expired()) {
            gameOver(1);  // Red Tank wins
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Graphics2D buffer = world.createGraphics();

        this.drawFloor(buffer);

        this.gobjs.forEach(gameObject -> gameObject.drawImage(buffer));
        renderSplitScreen(g2);
        renderMiniMap(g2);
    }

    /**
     * TODO:
     * - make GameWorld aware of bullets
     *      - Cant have bulletpool here because it would need to know when tank is shooting.
     *      - Check bullet collision with tank inside the tank class? Would allow for removing
     *          bullets from the ammo array and deleting them.
     * - Add Wall Collision Detection
     * - Remove Tank class from being directly instanciated in GameWorld
     *      - put it in the gameOBJ list through the map?
     *      - Make spawn points on map, randomly choose one before spawning tanks
     *
     */
}

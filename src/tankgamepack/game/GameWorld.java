package tankgamepack.game;

import tankgamepack.GameConstants;
import tankgamepack.Launcher;
import tankgamepack.Resources.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

public class GameWorld extends JPanel implements Runnable {

    private BufferedImage world;
    private Tank t1;
    private Tank t2;
    private final Launcher lf;
    public int worldSize;
    private boolean isRunning = true;
    private boolean resetRequested = false;

    List<GameObject> gobjs = new ArrayList<>();

    public GameWorld(Launcher lf) {
        this.lf = lf;
    }

    @Override
    public void run() {
        Sound bgMusic = ResourceManager.getSound("bgmusic");
        bgMusic.setVolume(.3f);
        bgMusic.loopCont(); // Set the background music to loop continuously
        bgMusic.playSound();

        try {
            while (isRunning) {
                if (resetRequested) {
                    resetRequested = false;
                    resetGame();
                }
                updateObjs();
                checkCollision();
                checkGameOver();
                this.repaint();
                Thread.sleep(1000 / 144);
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
                        gameOver(((Tank) currentObj).getId() == 1 ? 2 : 1);
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

    public void resetGame() {
        gobjs.clear();
        InitializeGame();
    }

    public void InitializeGame() {
        this.world = new BufferedImage(
                GameConstants.GAME_WORLD_WIDTH,
                GameConstants.GAME_WORLD_HEIGHT,
                BufferedImage.TYPE_INT_RGB);

        InputStreamReader isr = new InputStreamReader(Objects.requireNonNull(ResourceManager.class.getClassLoader().getResourceAsStream("maps/TankMapConverted.csv")));

        try (BufferedReader mapReader = new BufferedReader(isr)) {
            int row = 0;
            String[] gameItems;

            while (mapReader.ready()) {
                gameItems = mapReader.readLine().strip().split(",");

                for (int column = 0; column < gameItems.length; column++) {
                    String gameObj = gameItems[column];
                    if ("0".equals(gameObj)) continue;
                    this.gobjs.add(GameObject.newInstance(gameObj, column * 30, row * 30));
                }
                row++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int[][] spawnPoints = {
                {500, 500},
                {1500, 1500}
        };

        t1 = new Tank(spawnPoints[0][0], spawnPoints[0][1], 0, ResourceManager.getSprite("tank1"));
        t2 = new Tank(spawnPoints[1][0], spawnPoints[1][1], 0, ResourceManager.getSprite("tank2"));
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
            gameOver(2);
        } else if (t2.expired()) {
            gameOver(1);
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

    public void requestReset() {
        resetRequested = true;
        this.isRunning = true;
    }
}


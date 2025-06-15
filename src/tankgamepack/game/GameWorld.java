package tankgamepack.game;


import tankgamepack.GameConstants;
import tankgamepack.Launcher;
import tankgamepack.Resources.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
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
    private Tank[] enemies;
    private final Launcher lf;
    public int worldSize;
    public static final int BLOCK_WIDTH = 30, BLOCK_HEIGHT = 30;
    public static final int MAP_WIDTH = 64, MAP_HEIGHT = 48;

    private PriorityQueue<Point> queue;

    private int[][] distance;
    private int[][] distance2;
    private int[][] updateDistance;

    private boolean[][] walls;

    List<GameObject> gobjs = new ArrayList<>();
    //private long tick = 0; // for tick logic, not necessary to be used.
    List<Animation> anims = new ArrayList<>();

    private boolean isRunning = true;

    /**
     *
     */
    public GameWorld(Launcher lf) {
        this.lf = lf;
    }
    //Animation an = new Animation(300, 300, ResourceManager.getAnimation("bullethit"));

    @Override
    public void run() { // check collisions here
        Sound bgMusic = ResourceManager.getSound("bgmusic");
        bgMusic.loopCont();
        bgMusic.setVolume(.3f);
        bgMusic.playSound();

        try {
            while (isRunning) {
                distance = updateDistance((int) (t1.getY() / BLOCK_HEIGHT), (int) (t1.getX() / BLOCK_WIDTH));
                t1.setDistance(distance);
                distance2 = updateDistance((int) (t2.getY() / BLOCK_HEIGHT), (int) (t2.getX() / BLOCK_WIDTH));
                updateObjs();
                checkCollision();
                this.repaint();   // redraw game, never call paint component directly; repaint happens on different thread
                /*
                 * Sleep for 1000/144 ms (~6.9ms). This is done to have our 
                 * loop run at a fixed rate per/sec. 
                */


                Thread.sleep(1000 / 144); // artificially slow game down to prevent it from updating too fast
            }
        } catch (InterruptedException ignored) {
            System.out.println(ignored);
        }
        this.lf.killGame();
        this.lf.setFrame("end");
    }

    public synchronized void updateObjs() {
        Iterator<GameObject> objItr = gobjs.iterator();
        GameObject currentObj;
        LinkedList<GameObject> toAdd = new LinkedList<>();
        LinkedList<GameObject> toRemove = new LinkedList<>();

        for (Tank enemy : enemies) {
            if (enemy != null) {
                Tank closest = enemy.closest(t1, t2);
                enemy.logic(closest, enemies);
            }
        }

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
                    Tank tank = (Tank) currentObj;
                    GameObject temp = tank.addBulletToGameObjs();
                    if (temp != null) {
                        toAdd.add(temp);
                    }
                    if (tank.expired() && tank.player()) {
                        gameOver();
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

        List<GameObject> objects = new ArrayList<>(gobjs.size() + toAdd.size());
        objects.addAll(gobjs);

        for (GameObject toDespawn : toRemove) {
            objects.remove(toDespawn);
            //objects.remove(toRemove.remove());
        }
        for (GameObject toSpawn : toAdd) {
            objects.add(toSpawn);
            //objects.add(toAdd.remove());
        }

        gobjs = objects;

    }

    private void gameOver() {
        this.isRunning = false;
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
        distance = new int[MAP_HEIGHT][MAP_WIDTH];
        walls = new boolean[MAP_HEIGHT][MAP_WIDTH];
        queue = new PriorityQueue<>(MAP_HEIGHT * MAP_WIDTH);
        enemies = new Tank[GameConstants.NUMBER_TANKS];

        InputStreamReader isr = new InputStreamReader(Objects.requireNonNull(ResourceManager.class.getClassLoader().getResourceAsStream("maps/TankMapConverted.csv")));
        //InputStreamReader isr = new InputStreamReader(Objects.requireNonNull(ResourceManager.class.getClassLoader().getResourceAsStream("maps/TankMapSpawns.csv"))); // random spawns map

        // 0 = empty space
        // 9 = unbreakable barrier
        // 3 = unbreakable wall, collidable
        // 4-7 = power up
        // 8 = breakable wall

        // assume csv file follows the proper format (included in txt file)
        try (BufferedReader mapReader = new BufferedReader(isr)) { // cycle through rows and columns to import map objs
            int row = 0;
            String[] gameItems;
            int enemy = 0;

            while (mapReader.ready()) {
                gameItems = mapReader.readLine().strip().split(",");

                for (int column = 0; column < gameItems.length; column++) {
//                    System.out.println(gameItems[column]); // debugging
                    String gameObj = gameItems[column];
                    if ("0".equals(gameObj)) continue; // skip over 0s
                    GameObject object = GameObject.newInstance(gameObj, column*BLOCK_WIDTH, row*BLOCK_HEIGHT);
                    if ((object instanceof Wall) || (object instanceof BreakableWall)) {
                        walls[row][column] = true;
                    }
                    if (object instanceof Tank) {
                        enemies[enemy++] = (Tank) object;
                    }
                    this.gobjs.add(object);
                    // position objects using the row and column values
                }
                row++;
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        t1 = new Tank(500, 500, 0, ResourceManager.getSprite("tank1"), false);
        t2 = new Tank(1500, 500, 0, ResourceManager.getSprite("tank3"), false);
        TankControl tc1 = new TankControl(t1, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER);
        //TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);
        //TankControl tc2 = new TankControl(t2, KeyEvent.VK_I, KeyEvent.VK_K, KeyEvent.VK_J, KeyEvent.VK_L, KeyEvent.VK_N);
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);
        this.lf.getJf().addKeyListener(tc1);
        this.lf.getJf().addKeyListener(tc2);
        this.gobjs.add(t1);
        this.gobjs.add(t2);


//        anim.add(new Animation(ResourceManager.getAnimation()))

    }

    private boolean isValid(int row, int col) {
        if (row < 0 || col < 0) return false;
        if (row >= MAP_HEIGHT || col >= MAP_WIDTH) return false;
        if (walls[row][col]) {
            updateDistance[row][col] = Integer.MAX_VALUE;
            return false;
        }
        if (updateDistance[row][col] != 0) return false;
        return true;
    }
    void floodFill(int row, int col) {
        if (row < 0 || col < 0) return;
        if (row >= MAP_HEIGHT || col >= MAP_WIDTH) return;
        if (walls[row][col]) {
            updateDistance[row][col] = Integer.MAX_VALUE;
            return;
        }

        if (!isValid(row, col)) return;

        int distance = 1;

        updateDistance[row][col] = distance;
        Point point = new Point(row, col, distance);
        //if (seen.contains(point)) return;
        //seen.add(point);
        queue.add(point);

        while (!queue.isEmpty()) {
            Point p = queue.remove();
            row = p.row;
            col = p.col;
            distance = p.distance + 1;

            if (isValid(row-1, col)) {
                updateDistance[row-1][col] = distance;
                queue.add(new Point(row-1, col, distance));
            }

            if (isValid(row+1, col)) {
                updateDistance[row+1][col] = distance;
                queue.add(new Point(row+1, col, distance));
            }

            if (isValid(row, col-1)) {
                updateDistance[row][col-1] = distance;
                queue.add(new Point(row, col-1, distance));
            }

            if (isValid(row, col+1)) {
                updateDistance[row][col+1] = distance;
                queue.add(new Point(row, col+1, distance));
            }
        }
    /*
        if (updateDistance[row][col] != 0) return;
        updateDistance[row][col] = distance;
        floodFill(row - 1, col, distance + 1);
        floodFill(row + 1, col, distance + 1);
        floodFill(row, col - 1, distance + 1);
        floodFill(row, col + 1, distance + 1);
     */
    }

    int[][] updateDistance(int row, int col) {
        updateDistance = new int[MAP_HEIGHT][MAP_WIDTH];
        queue.clear();
        floodFill(row, col);
        return updateDistance;
    }

    /*
    game object doesn't need to know about the objects it doesnt support, only those it does support
    in the try catch, 0 should be checked for instead of doing it in the game objects.
        Distinguished what is related to worlloader and game object. Because 0 isn't a game object,
        it shouldn't be in the GameObject class

     resource pool: prevent constant object creation in java
     synchronized list: ?
     */

    static double miniMapScaleFactor = 0.2;
    public void renderMiniMap(Graphics2D g) {
        /*
        Goal:
        - draw square
        - position square
        -
         */
        BufferedImage mm = this.world.getSubimage(0, 0, GameConstants.GAME_WORLD_WIDTH, GameConstants.GAME_WORLD_HEIGHT);
        //g.drawImage(mm, 0, 0, null);
        g.scale(.2,.2);
        g.drawImage(mm,
                (GameConstants.GAME_SCREEN_WIDTH*5)/2 - (GameConstants.GAME_WORLD_WIDTH/2),
                (GameConstants.GAME_SCREEN_HEIGHT*5) - (GameConstants.GAME_WORLD_HEIGHT) - 140, null);

    }


    public void renderSplitScreen(Graphics2D g) {
        g.drawImage(t1.cameraPosition(world), 0, 0, null);
        g.drawImage(t2.cameraPosition(world), GameConstants.GAME_SCREEN_WIDTH/2+4, 0, null);
    }

    private void drawFloor(Graphics g) {
        for (int i = 0; i < GameConstants.GAME_WORLD_WIDTH; i+= 320) {
            for (int j = 0; j < GameConstants.GAME_WORLD_HEIGHT; j+= 240) {
                g.drawImage(ResourceManager.getSprite("floor"), i, j, null);
            }
        }
        for (int row = 0; row < MAP_HEIGHT; row++) {
            for (int col = 0; col < MAP_WIDTH; col++) {
                int dist = distance[row][col];
                if (dist < Integer.MAX_VALUE) dist *= 6;
                if (dist > 255) dist = 255;
                g.setColor(new Color(255-dist, 255-dist, 255-dist, 127));
                g.fillRect(col * BLOCK_WIDTH, row * BLOCK_HEIGHT, BLOCK_WIDTH, BLOCK_HEIGHT);
            }
        }
    }

    private void checkCollision() {
        //Queue<GameObject> toRemove = new LinkedList<GameObject>();

        for (int i = 0; i < gobjs.size(); i++) { // moving object collision
            GameObject obj1 = gobjs.get(i);
            //if (obj1 instanceof Wall || obj1 instanceof PowerUp) continue; // for now continue
            if (!(obj1 instanceof MovableObjects) && !(obj1 instanceof PowerUp)) continue;
            for (int j = 0; j < gobjs.size(); j++) { // other objects in world
                if (i == j) continue; // prevents checking an object from collision with itself
                GameObject obj2 = gobjs.get(j);

                if (obj1.getHitbox().intersects(obj2.getHitbox())) {
                    obj1.collides(obj2);
                }
            }
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Graphics2D buffer = world.createGraphics(); // all game objects draw to this buffer

        this.drawFloor(buffer);

        try {
            this.gobjs.forEach(gameObject -> gameObject.drawImage(buffer));
        } catch (Exception e) {

        }
        //g.drawImage(t1.cameraPosition(world), 0, 0, null);
        renderSplitScreen(g2);
        renderMiniMap(g2); // renders it to the screen instead of the gameScreen. Drawing to game screen draws map inside map.


//        renderSplitScreen(g2);
        //g2.drawImage(world, 0, 0, null);
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

    class Point implements Comparable<Point> {
        public int row, col, distance;

        Point(int row, int col, int distance) {
            this.row = row;
            this.col = col;
            this.distance = distance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return row == point.row && col == point.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }

        @Override
        public int compareTo(Point o) {
            if (this == o) return 0;
            if (o == null || getClass() != o.getClass()) return 0;
            return distance - o.distance;
        }
    }
}

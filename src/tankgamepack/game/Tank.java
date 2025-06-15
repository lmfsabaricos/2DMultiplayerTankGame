package tankgamepack.game;

import tankgamepack.GameConstants;
import tankgamepack.Resources.ResourceManager;
import tankgamepack.Resources.ResourcePool;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author anthony-pc
 */
public class Tank extends GameObject implements MovableObjects{
    private final boolean ai; // normally player and tank are separated
    public static final double CLOSE_RANGE = 9000;
    public static final double STOP_RANGE = 25000;
    //public static final double FIRE_RANGE = 225000;
    public static final double FIRE_RANGE = 125000;
    //private float cameraX, cameraY;
    private List<PowerUp> activeBuffs = new ArrayList<>(); //power ups
    private static ResourcePool<Bullet> bulletPool = new ResourcePool<>("bullet", 300);
    static {
        bulletPool.fillPool(Bullet.class, 300);
    }
    private int lives = 3;
    static int count = 0;
    int id;
    private float x;
    private float y;
    private float vx = 0; // change in x
    private float vy = 0; // change in y
    private float angle; // way tank is facing

    private float R = 5; // movement speed
    private float ROTATIONSPEED = 3.0f;

    private BufferedImage img;
    private boolean upPressed;
    private boolean downPressed;
    private boolean rightPressed;
    private boolean leftPressed;
    private boolean shootPressed;
    private long timeSinceLastShot = 0L;
    private long cooldown = 2000;
    Rectangle hitbox;
    private boolean canShoot;
    private int[][] distance;

    Tank(float x, float y, float angle, BufferedImage img, boolean ai) {
        this.x = x;
        this.y = y;
        this.img = img;
        this.angle = angle;
        count++;
        this.id = count;
        this.hitbox = new Rectangle((int) x, (int) y, this.img.getWidth(), this.img.getHeight());
        this.ai = ai;

    }

    void setX(float x){ this.x = x; }

    void setY(float y) { this. y = y;}

    void setUpPressed() {
        this.upPressed = true;
    }

    void setDownPressed() {
        this.downPressed = true;
    }

    void setRightPressed() {
        this.rightPressed = true;
    }

    void setLeftPressed() {
        this.leftPressed = true;
    }

    void clearUpPressed() {
        this.upPressed = false;
    }

    void clearDownPressed() {
        this.downPressed = false;
    }

    void clearRightPressed() {
        this.rightPressed = false;
    }

    void clearLeftPressed() {
        this.leftPressed = false;
    }

    public void update() {
        updateBuffs(); // updates status before moving tank
        if (this.upPressed) {
            this.moveForwards();
        }

        if (this.downPressed) {
            this.moveBackwards();
        }

        if (this.leftPressed) {
            this.rotateLeft();
        }

        if (this.rightPressed) {
            this.rotateRight();
        }

        if (this.shootPressed && ((this.timeSinceLastShot + this.cooldown) < System.currentTimeMillis())) {
            canShoot = true;
            (ResourceManager.getSound("firing")).playSound();
            this.timeSinceLastShot = System.currentTimeMillis();
        } else {
            canShoot = false;
        }
        //this.ammo.forEach(Bullet::update);

        this.hitbox.setLocation((int)this.x, (int)this.y);

    }

    private void rotateLeft() {
        angle -= ROTATIONSPEED;
        if (angle < 0) angle += 360;
    }

    private void rotateRight() {
        angle += ROTATIONSPEED;
        if (angle > 360) angle -= 360;
    }

    private void setVelocity() {
        vx =  Math.round(R * Math.cos(Math.toRadians(angle)));
        vy =  Math.round(R * Math.sin(Math.toRadians(angle)));
    }
    private void moveBackwards() {
        setVelocity();
        x -= vx;
        y -= vy;
       checkBorder();
    }

    private void moveForwards() {
        setVelocity();
        x += vx;
        y += vy;
        checkBorder();
    }


    private void checkBorder() { // game screen measurements should be changed to game world measurements
        if (x < 30) {
            x = 30;
        }
        if (x >= GameConstants.GAME_WORLD_WIDTH - 88) {
            x = GameConstants.GAME_WORLD_WIDTH - 88;
        }
        if (y < 40) {
            y = 40;
        }
        if (y >= GameConstants.GAME_WORLD_HEIGHT - 80) {
            y = GameConstants.GAME_WORLD_HEIGHT - 80;
        }
    }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y + ", angle=" + angle;
    }

    @Override
    public void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.img, rotation, null);
        g2d.setColor(Color.WHITE);

        g2d.drawRect((int)x-30,(int)y-20,100, 15); // place cooldown bar above tank
        long currentWidth = 100 - ((this.timeSinceLastShot + this.cooldown) - System.currentTimeMillis())/20;
        if (currentWidth > 100) {
            currentWidth = 100;
        }
        g2d.setColor(Color.GREEN);
        g2d.fillRect((int)x-30, (int)y-20, (int)currentWidth, 15);


        for (int i = this.lives; i > 0; i--) {
            g2d.setColor(Color.RED);
            g2d.fillRect((int)x+(15*i), (int)y+70, 25, 15);
            g2d.setColor(Color.BLACK);
            g2d.drawRect((int)x+(15*i),(int)y+70,25, 15); // place cooldown bar above tank
        }

    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public BufferedImage cameraPosition(BufferedImage world) {
        int cameraX, cameraY;
        BufferedImage screenSide;

        //check for x axis border
        if ((int) this.x <= GameConstants.GAME_SCREEN_WIDTH/4) { // checks if too far left
            cameraX = GameConstants.GAME_SCREEN_WIDTH/4;
        } else if ((int) this.x >= (3*(GameConstants.GAME_SCREEN_WIDTH/4))+((GameConstants.GAME_SCREEN_WIDTH/4)*2)) { // checks for right edge
            cameraX = (3*(GameConstants.GAME_SCREEN_WIDTH/4))+((GameConstants.GAME_SCREEN_WIDTH/4)*2);
        } else { // default behavior (follow tank)
            cameraX = (int) this.x;
        }

        //check for y axis border
        if ((int) this.y <= GameConstants.GAME_SCREEN_HEIGHT/2) { // checks if too high up
            cameraY = GameConstants.GAME_SCREEN_HEIGHT/2;
        } else if (this.y >= GameConstants.GAME_SCREEN_HEIGHT) {
            cameraY = GameConstants.GAME_SCREEN_HEIGHT;
        } else {
            cameraY = (int) this.y;
        }

        screenSide = world.getSubimage(
                cameraX - GameConstants.GAME_SCREEN_WIDTH/4,
                cameraY - GameConstants.GAME_SCREEN_HEIGHT/2,
                GameConstants.GAME_SCREEN_WIDTH/2, GameConstants.GAME_SCREEN_HEIGHT);

        return screenSide;
    }

    public BufferedImage NonSplitCameraPosition(BufferedImage world) {
        int cameraX, cameraY;
        BufferedImage screenSide;

        cameraX = (int)(this.x - (GameConstants.GAME_SCREEN_WIDTH / 2));
        cameraY = (int)(this.y - (GameConstants.GAME_SCREEN_HEIGHT / 2));

        if (cameraX < 0) cameraX = 0;
        if (cameraY < 0) cameraY = 0;

        int maxX = GameConstants.GAME_WORLD_WIDTH - GameConstants.GAME_SCREEN_WIDTH;
        int maxY = GameConstants.GAME_WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT;
        if (cameraX > maxX) {
            cameraX = maxX;
        }
        if (cameraY > maxY) {
            cameraY = maxY;
        }

        screenSide = world.getSubimage(
                cameraX,
                cameraY,
                GameConstants.GAME_SCREEN_WIDTH, GameConstants.GAME_SCREEN_HEIGHT);

        return screenSide;
    }

    public void setShootPressed() {
        shootPressed = true;
    }

    public void clearShootPressed() {
        shootPressed = false;
    }

    @Override
    public Rectangle getHitbox() {
        return hitbox.getBounds();
    }

    @Override
    public void collides(GameObject with) {

        if (with instanceof Bullet) {
            if (((Bullet) with).getOwner() != this.id) {
                //if (!ai) this.lives--;
            }
            //.println("TANK " + this.id + "   " + this.lives);
        } else if (with instanceof Wall) {
            if (with instanceof BreakableWall && ((BreakableWall) with).getIsBroken()) { // if wall broken
                return;
            } else {
                wallCollision();
            }
        } else if (with instanceof PowerUp) {
            //((PowerUp) with).activatePowerUp(this);
            if (with instanceof SpeedPowerUp) {
                ((SpeedPowerUp) with).setActivationHealth(this.lives);
                this.R += 2;
            } else if (with instanceof HealthPowerUp) {
                ((HealthPowerUp) with).setActivationHealth(this.lives);
                this.lives++;
            }
            (ResourceManager.getSound("powerup")).playSound();

            activeBuffs.add((PowerUp) with);
        }

    }

    public Bullet addBulletToGameObjs() { // this could be the shoot action
        if (canShoot) {
            Bullet temp = bulletPool.getResource();
            temp.spawnBullet(this.x, this.y, this.angle, this.id); // do NOT understand why this did not correct for the bullet being at 0,0 when the bullet was spawned.
            return temp;
        }
        return null;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public boolean expired() { // tank expires when lives is 0
        return this.lives <= 0;
    }

    private void updateBuffs() {
        Queue<PowerUp> toRemove = new LinkedList<>();

        for (PowerUp powerUp : this.activeBuffs) {
            if (!powerUp.isActive(this.lives)) {
                toRemove.add(powerUp);
                if (powerUp instanceof SpeedPowerUp) {
                    this.R -= 2;
                }
            }
        }

        for (PowerUp powerUp : toRemove) {
            this.activeBuffs.remove(powerUp);
        }
    }

    private void wallCollision() { // tank behavior towards walls
        this.vx = 0;
        this.vy = 0;
        if (this.upPressed) {
            clearUpPressed();
            this.x = (float) (this.x - 10*Math.cos(Math.toRadians(angle)));
            this.y = (float) (this.y - 10*Math.sin(Math.toRadians(angle)));
            setUpPressed();
        } else if (this.downPressed) {
            clearDownPressed();
            this.x = (float) (this.x + 10 * Math.cos(Math.toRadians(angle)));
            this.y = (float) (this.y + 10 * Math.sin(Math.toRadians(angle)));
            setDownPressed();
        }
    }

    public boolean player() {
        return !ai;
    }

    private double distance(Tank other) {
        return (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y);
    }
    public Tank closest(Tank t1, Tank t2) {
        if (distance((t1)) < distance(t2)) return t1;
        return t2;
    }

    public void logic(Tank target, Tank[] enemies) {
        double radians = Math.atan2(target.y - y,target.x - x);
        float desired = (float) Math.toDegrees(radians);
        float difference = desired - angle;
        double range = distance(target);
        if (difference > 180) difference -= 360;
        if (difference < -180) difference += 360;
        if (difference > ROTATIONSPEED) angle += ROTATIONSPEED;
        if (difference < -ROTATIONSPEED) angle -= ROTATIONSPEED;

        clearDownPressed();
        clearUpPressed();

        boolean stop = false;
        for (Tank check : enemies) {
            if (check == null || check == this) continue;
            if (distance(check) < STOP_RANGE) {
                stop = true;
                break;
            }
        }

        if (!stop) {
            if (range < CLOSE_RANGE) {
                setDownPressed();
            }
            if (range > STOP_RANGE) {
                setUpPressed();
            }
        }
        clearShootPressed();

        if (distance(target) < FIRE_RANGE) {
            if (Math.abs(difference) < ROTATIONSPEED) setShootPressed();
        }
    }

    public void setDistance(int[][] distance) {
        this.distance = distance;
    }
}

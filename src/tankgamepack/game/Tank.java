package tankgamepack.game;

import tankgamepack.GameConstants;
import tankgamepack.Resources.ResourcePool;
import tankgamepack.Resources.ResourceManager;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Tank extends GameObject implements MovableObjects {
    private List<PowerUp> activeBuffs = new ArrayList<>();
    private static ResourcePool<Bullet> bulletPool = new ResourcePool<>("bullet", 300);
    private int lives = 3;
    private static int count = 0;
    private int id;
    private float x;
    private float y;
    private float vx = 0;
    private float vy = 0;
    private float angle;
    private float R = 5;
    private float ROTATIONSPEED = 3.0f;
    private BufferedImage img;
    private boolean upPressed;
    private boolean downPressed;
    private boolean rightPressed;
    private boolean leftPressed;
    private boolean shootPressed;
    private long timeSinceLastShot = 0L;
    private long cooldown = 2000;
    private Rectangle hitbox;
    private boolean canShoot;
    private long lastDamageTime = 0;
    private static final long DAMAGE_COOLDOWN = 500;
    private boolean hasShield = false;
    private int shieldCount = 0;

    static {
        bulletPool.fillPool(Bullet.class, 300);
    }

    public Tank(float x, float y, float angle, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.img = img;
        this.angle = angle;
        count++;
        this.id = count;
        this.hitbox = new Rectangle((int) x, (int) y, this.img.getWidth(), this.img.getHeight());
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void toggleUpPressed() {
        this.upPressed = true;
    }

    public void toggleDownPressed() {
        this.downPressed = true;
    }

    public void toggleRightPressed() {
        this.rightPressed = true;
    }

    public void toggleLeftPressed() {
        this.leftPressed = true;
    }

    public void unToggleUpPressed() {
        this.upPressed = false;
    }

    public void unToggleDownPressed() {
        this.downPressed = false;
    }

    public void unToggleRightPressed() {
        this.rightPressed = false;
    }

    public void unToggleLeftPressed() {
        this.leftPressed = false;
    }

    public void toggleShootPressed() {
        shootPressed = true;
    }

    public void untoggleShootPressed() {
        shootPressed = false;
    }

    public void update() {
        updateBuffs();
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
        this.hitbox.setLocation((int) this.x, (int) this.y);
    }

    private void rotateLeft() {
        this.angle -= this.ROTATIONSPEED;
    }

    private void rotateRight() {
        this.angle += this.ROTATIONSPEED;
    }

    private void moveBackwards() {
        vx = Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = Math.round(R * Math.sin(Math.toRadians(angle)));
        x -= vx;
        y -= vy;
        checkBorder();
    }

    private void moveForwards() {
        vx = Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = Math.round(R * Math.sin(Math.toRadians(angle)));
        x += vx;
        y += vy;
        checkBorder();
    }

    private void checkBorder() {
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

        // Draw cooldown bar above the tank
        g2d.drawRect((int) x - 30, (int) y - 20, 100, 15);
        long currentWidth = 100 - ((this.timeSinceLastShot + this.cooldown) - System.currentTimeMillis()) / 20;
        if (currentWidth > 100) {
            currentWidth = 100;
        }
        g2d.setColor(Color.GREEN);
        g2d.fillRect((int) x - 30, (int) y - 20, (int) currentWidth, 15);

        // Draw lives as small rectangles
        for (int i = this.lives; i > 0; i--) {
            g2d.setColor(Color.RED);
            g2d.fillRect((int) x + (15 * i), (int) y + 70, 25, 15);
            g2d.setColor(Color.BLACK);
            g2d.drawRect((int) x + (15 * i), (int) y + 70, 25, 15);
        }

        // Draw lives count as text
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Lives: " + lives, (int) x, (int) y - 10);

        // Draw shield count as text
        g2d.drawString("Shields: " + shieldCount, (int) x, (int) y - 30);
    }

    public void takeDamage() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDamageTime >= DAMAGE_COOLDOWN) {
            if (hasShield && shieldCount > 0) {
                shieldCount--;  // Decrease shield count
                if (shieldCount == 0) {
                    hasShield = false;  // Deactivate shield if no shields are left
                }
            } else {
                this.lives--;
            }
            lastDamageTime = currentTime;
        }
    }

    public Bullet addBulletToGameObjs() {
        if (canShoot) {
            Bullet temp = bulletPool.getResource();
            temp.spawnBullet(this.x, this.y, this.angle, this.id);
            return temp;
        }
        return null;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public boolean expired() {
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

        while (!toRemove.isEmpty()) {
            this.activeBuffs.remove(toRemove.remove());
        }
    }

    @Override
    public Rectangle getHitbox() {
        return hitbox.getBounds();
    }

    @Override
    public void collides(GameObject with) {
        if (with instanceof Bullet) {
            if (((Bullet) with).getOwner() != this.id) {
                this.takeDamage();
            }
        } else if (with instanceof Wall) {
            if (with instanceof BreakableWall && ((BreakableWall) with).getIsBroken()) {
                return;
            } else {
                wallCollision();
            }
        } else if (with instanceof PowerUp) {
            if (with instanceof SpeedPowerUp) {
                ((SpeedPowerUp) with).setActivationHealth(this.lives);
                this.R += 2;
            } else if (with instanceof HealthPowerUp) {
                ((HealthPowerUp) with).setActivationHealth(this.lives);
                this.lives++;
            } else if (with instanceof ShieldPowerUp) {
                this.hasShield = true;  // Activate shield when picking up ShieldPowerUp
                shieldCount++;  // Increase shield count
            }
            (ResourceManager.getSound("powerup")).playSound();
            activeBuffs.add((PowerUp) with);
        }
    }

    private void wallCollision() {
        this.vx = 0;
        this.vy = 0;
        if (this.upPressed) {
            unToggleUpPressed();
            this.x = (float) (this.x - 10 * Math.cos(Math.toRadians(angle)));
            this.y = (float) (this.y - 10 * Math.sin(Math.toRadians(angle)));
            toggleUpPressed();
        } else if (this.downPressed) {
            unToggleDownPressed();
            this.x = (float) (this.x + 10 * Math.cos(Math.toRadians(angle)));
            this.y = (float) (this.y + 10 * Math.sin(Math.toRadians(angle)));
            toggleDownPressed();
        }
    }

    public BufferedImage cameraPosition(BufferedImage world) {
        int cameraX, cameraY;
        BufferedImage screenSide;

        if ((int) this.x <= GameConstants.GAME_SCREEN_WIDTH / 4) {
            cameraX = GameConstants.GAME_SCREEN_WIDTH / 4;
        } else if ((int) this.x >= (3 * (GameConstants.GAME_SCREEN_WIDTH / 4)) + ((GameConstants.GAME_SCREEN_WIDTH / 4) * 2)) {
            cameraX = (3 * (GameConstants.GAME_SCREEN_WIDTH / 4)) + ((GameConstants.GAME_SCREEN_WIDTH / 4) * 2);
        } else {
            cameraX = (int) this.x;
        }

        if ((int) this.y <= GameConstants.GAME_SCREEN_HEIGHT / 2) {
            cameraY = GameConstants.GAME_SCREEN_HEIGHT / 2;
        } else if (this.y >= GameConstants.GAME_SCREEN_HEIGHT) {
            cameraY = GameConstants.GAME_SCREEN_HEIGHT;
        } else {
            cameraY = (int) this.y;
        }

        screenSide = world.getSubimage(
                cameraX - GameConstants.GAME_SCREEN_WIDTH / 4,
                cameraY - GameConstants.GAME_SCREEN_HEIGHT / 2,
                GameConstants.GAME_SCREEN_WIDTH / 2, GameConstants.GAME_SCREEN_HEIGHT);

        return screenSide;
    }
}

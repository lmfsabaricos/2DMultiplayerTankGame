package tankgamepack.game;

import tankgamepack.GameConstants;
import tankgamepack.Resources.ResourceManager;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullet extends GameObject implements MovableObjects {
    private float x;
    private float y;
    private float vx;
    private float vy;
    private BufferedImage img;
    private float angle;
    private float R = 5;
    private int owner;
    private Rectangle hitbox;
    private boolean hasCollided = false;

    public Bullet(float x, float y, BufferedImage sprite) {
        this.x = x;
        this.y = y;
        this.img = sprite;
        this.vx = 2;
        this.vy = 2;
        this.hitbox = new Rectangle((int) x, (int) y, this.img.getWidth() * 2, this.img.getHeight() * 2);
    }

    public void update() {
        this.vx = Math.round(this.R * Math.cos(Math.toRadians(angle)));
        this.vy = Math.round(this.R * Math.sin(Math.toRadians(angle)));
        this.x += this.vx;
        this.y += this.vy;
        this.hitbox.setLocation((int) this.x, (int) this.y);
        checkBorder();
    }

    private void checkBorder() {
        if (x < 30 || x >= GameConstants.GAME_WORLD_WIDTH - 46 || y < 30 || y >= GameConstants.GAME_WORLD_HEIGHT - 46) {
            this.hasCollided = true;
        }
    }

    public void spawnBullet(float x, float y, float angle, int owner) {
        this.x = x + 17;
        this.y = y + 15;
        this.angle = angle;
        this.owner = owner;
    }

    public int getOwner() {
        return this.owner;
    }

    @Override
    public void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.scale(2, 2);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.img, rotation, null);
    }

    @Override
    public Rectangle getHitbox() {
        return hitbox.getBounds();
    }

    @Override
    public void collides(GameObject with) {
        if (with instanceof Tank) {
            if (((Tank) with).getId() != this.owner) {
                ((Tank) with).takeDamage();
                this.hasCollided = true;
            }
        } else if (with instanceof Wall) {
            hasCollided = true;
            if (with instanceof BreakableWall) {
                ((BreakableWall) with).updateImage();
            }
        }
    }

    public Animation playExplode() {
        (ResourceManager.getSound("explosion")).playSound();
        return new Animation(this.x, this.y, ResourceManager.getAnimation("bullethit"));
    }

    @Override
    public boolean expired() {
        return hasCollided;
    }
}

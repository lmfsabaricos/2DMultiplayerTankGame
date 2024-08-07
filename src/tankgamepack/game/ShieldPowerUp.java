package tankgamepack.game;

import tankgamepack.Resources.ResourceManager;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ShieldPowerUp extends GameObject implements PowerUp {
    private float x;
    private float y;
    private BufferedImage img;
    private Rectangle hitbox;
    private boolean isExpired = false;

    public ShieldPowerUp(float x, float y, BufferedImage sprite) {
        this.x = x;
        this.y = y;
        this.img = sprite;
        this.hitbox = new Rectangle((int) x, (int) y, this.img.getWidth(), this.img.getHeight());
    }

    @Override
    public void drawImage(Graphics g) {
        g.drawImage(this.img, (int) x, (int) y, null);
    }

    @Override
    public Rectangle getHitbox() {
        return this.hitbox;
    }

    @Override
    public void collides(GameObject with) {
        if (with instanceof Tank) {
            this.isExpired = true;
        }
    }

    @Override
    public boolean expired() {
        return this.isExpired;
    }

    @Override
    public boolean isActive(int currentHealth) {
        return !this.isExpired;
    }

    @Override
    public void setActivationHealth(int activationHealth) {
        // Not used for ShieldPowerUp
    }
}

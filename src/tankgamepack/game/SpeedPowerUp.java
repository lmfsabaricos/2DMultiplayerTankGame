package tankgamepack.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SpeedPowerUp extends GameObject implements PowerUp {
    private BufferedImage img;
    private float x;
    private float y;
    private int activationHealth;
    Rectangle hitbox;
    boolean isExpired = false;


    SpeedPowerUp(float x, float y, BufferedImage img) {
        this.img = img;
        this.x = x;
        this.y = y;
        this.hitbox = new Rectangle((int) x, (int) y, this.img.getWidth(), this.img.getHeight());
    }

    public void setActivationHealth(int activationHealth) {
        this.activationHealth = activationHealth;
    }

    @Override
    public void drawImage(Graphics buffer) {
        buffer.drawImage(this.img, (int)x, (int)y, null);
        buffer.drawRect((int)this.x,(int)this.y, this.img.getWidth(), this.img.getHeight());
    }

    @Override
    public Rectangle getHitbox() {
        return this.hitbox.getBounds();
    }

    @Override
    public void collides(GameObject with) {
        if (with instanceof Tank) {
            this.isExpired = true;
        }
    }

    public boolean isActive(int currentLives) {
        return currentLives >= activationHealth;
    }

    @Override
    public boolean expired() {
        return isExpired;
    }
}

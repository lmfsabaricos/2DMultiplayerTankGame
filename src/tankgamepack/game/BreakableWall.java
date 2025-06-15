package tankgamepack.game;

import tankgamepack.Resources.ResourceManager;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BreakableWall extends Wall/*extends Wall*/ {
    private float x;
    private float y;
    private BufferedImage img;
    private boolean isBroken = false;
    public BreakableWall(float x, float y, BufferedImage sprite) {
        super(x, y, sprite);
        this.x = x;
        this.y = y;
        this.img = sprite;
    }

    @Override
    public void drawImage(Graphics buffer) {
        buffer.drawImage(this.img, (int)x, (int)y, null);
        buffer.drawRect((int)this.x,(int)this.y, this.img.getWidth(), this.img.getHeight());

    }
    @Override
    public Rectangle getHitbox() {
        return hitbox.getBounds();
    }

    public void updateImage() { // called in BulletClass
        this.isBroken = true;
        this.img = ResourceManager.getSprite("broken");
    }

    public boolean getIsBroken() {
        return this.isBroken;
    }

    @Override
    public void collides(GameObject with) {
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }
}

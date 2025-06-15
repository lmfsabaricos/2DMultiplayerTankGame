package tankgamepack.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Wall extends GameObject {
    private BufferedImage img;
    private float x;
    private float y;
    Rectangle hitbox;

    public Wall(float x, float y, BufferedImage sprite) {
        this.img = sprite;
        this.x = x;
        this.y = y;
        this.hitbox = new Rectangle((int) x, (int) y, this.img.getWidth(), this.img.getHeight());

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

    @Override
    public void collides(GameObject with) {
        //stop?
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }
}

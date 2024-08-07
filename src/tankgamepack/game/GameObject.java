package tankgamepack.game;

import tankgamepack.Resources.ResourceManager;

import java.awt.*;
import java.awt.geom.AffineTransform;

public abstract class GameObject {

    public static GameObject newInstance(String type, float x, float y) throws UnsupportedOperationException {
        // add spawn points to map
        // add method to tank that randomly chooses spawn point when it dies
        return switch (type) {
            case "9" -> new Wall(x, y, ResourceManager.getSprite("unbreak"));
            case "3" -> new Wall(x, y, ResourceManager.getSprite("unbreak"));
            case "4" -> new HealthPowerUp(x, y, ResourceManager.getSprite("health"));
            case "5" -> new SpeedPowerUp(x, y, ResourceManager.getSprite("speed"));
            case "6" -> new ShieldPowerUp(x, y, ResourceManager.getSprite("shield")); // Ensure this is correct
            case "8" -> new BreakableWall(x, y, ResourceManager.getSprite("breakable"), ResourceManager.getSprite("broken"));

            default -> throw new UnsupportedOperationException();
        };
    }

    public abstract void drawImage(Graphics buffer);

    public abstract Rectangle getHitbox();

    public abstract void collides(GameObject with);
}

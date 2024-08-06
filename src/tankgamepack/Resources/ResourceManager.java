package tankgamepack.Resources;

import tankgamepack.game.Bullet;
import tankgamepack.game.Sound;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class ResourceManager {
    private final static Map<String, BufferedImage> sprites = new HashMap<>();
    private final static Map<String, List<BufferedImage>> animations = new HashMap<>();
    private final static Map<String, Sound> sounds = new HashMap<>();
    private static final Map<String, Integer> animationInfo = new HashMap<String, Integer>() {{
        put("bullethit", 24);
        put("bulletshoot", 24);
        put("powerpick", 32);
        put("puffsmoke", 32);
        put("rocketflame", 16);
        put("rockethit", 32);
    }};

    private static void initSprites() {
        try {
            sprites.put("tank1", loadSprite("tank1.png"));
            sprites.put("tank2", loadSprite("tank2.png"));
            sprites.put("menu", loadSprite("title.png"));
            sprites.put("bullet", loadSprite("bullet/bullet.jpg"));
            sprites.put("rocket1", loadSprite("bullet/rocket1.png"));
            sprites.put("rocket2", loadSprite("bullet/rocket2.png"));
            sprites.put("floor", loadSprite("floor/bg.bmp"));
            sprites.put("unbreak", loadSprite("walls/unbreak.jpg"));
            sprites.put("breakable", loadSprite("walls/break1.jpg"));
            sprites.put("broken", loadSprite("walls/break2.jpg"));
            sprites.put("health", loadSprite("powerups/health.png"));
            sprites.put("shield", loadSprite("powerups/shield.png"));
            sprites.put("speed", loadSprite("powerups/speed.png"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sprites", e);
        }
    }

    private static void initAnimations() {
        String baseName = "animations/%s/%s_%04d.png";
        animationInfo.forEach((animationName, frameCount) -> {
            List<BufferedImage> frames = new ArrayList<>();
            try {
                for (int i = 0; i < frameCount; i++) {
                    String spritePath = baseName.formatted(animationName, animationName, i);
                    frames.add(loadSprite(spritePath));
                    System.out.println("Loaded frame: " + spritePath); // Debug statement
                }
                animations.put(animationName, frames);
                System.out.println("Loaded animation: " + animationName); // Debug statement
            } catch (IOException e) {
                throw new RuntimeException("Failed to load animation: " + animationName, e);
            }
        });
    }

    public static void loadResources() {
        initSprites();
        initAnimations();
        initSounds();
    }

    public static BufferedImage getSprite(String type) {
        BufferedImage sprite = sprites.get(type);
        if (sprite == null) {
            throw new RuntimeException(type + " is missing from the sprite resources");
        }
        return sprite;
    }

    public static List<BufferedImage> getAnimation(String type) {
        List<BufferedImage> animation = animations.get(type);
        if (animation == null) {
            throw new RuntimeException(type + " animation is missing from the resources");
        }
        return animation;
    }

    private static BufferedImage loadSprite(String path) throws IOException {
        return ImageIO.read(Objects.requireNonNull(ResourceManager.class.getClassLoader().getResource(path),
                "Unable to find image at path: " + path));
    }

    public static Sound getSound(String type) {
        Sound sound = sounds.get(type);
        if (sound == null) {
            throw new RuntimeException(type + " sound resource is missing");
        }
        return sound;
    }

    private static Sound loadSound(String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream ais = AudioSystem.getAudioInputStream(Objects.requireNonNull(ResourceManager.class.getClassLoader().getResource(path)));
        Clip c = AudioSystem.getClip();
        c.open(ais);
        Sound s = new Sound(c);
        s.setVolume(1f);
        return s;
    }

    public static void initSounds() {
        try {
            sounds.put("bullet", loadSound("sounds/bullet.wav"));
            sounds.put("explosion", loadSound("sounds/shotexplosion.wav"));
            sounds.put("firing", loadSound("sounds/shotfiring.wav"));
            sounds.put("bgmusic", loadSound("sounds/Music.mid"));
            sounds.put("powerup", loadSound("sounds/pickup.wav"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load sounds", e);
        }
    }
}

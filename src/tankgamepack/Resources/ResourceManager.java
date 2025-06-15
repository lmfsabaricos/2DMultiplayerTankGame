package tankgamepack.Resources;

import tankgamepack.game.Bullet;
import tankgamepack.game.Sound;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class ResourceManager {
    private final static Map<String, BufferedImage> sprites = new HashMap<String, BufferedImage>();
    private final static Map<String, List<BufferedImage>> animations = new HashMap<String, List<BufferedImage>>();;
    private final static Map<String, Sound> sounds = new HashMap<String, Sound>();
    private static final Map<String, Integer> animationInfo = new HashMap<String, Integer>() {{ // animation name, num of frames
        put("bullethit", 24);
        put("bulletshoot", 24);
        put("powerpick", 32);
        put("puffsmoke", 32);
        put("rocketflame", 16);
        put("rockethit", 32);
    }};

    private static void initSprites() {

        try {
            ResourceManager.sprites.put("tank1", loadSprite("tank1.png"));
            ResourceManager.sprites.put("tank2", loadSprite("tank2.png"));
            ResourceManager.sprites.put("tank3", loadSprite("tank3.png"));
            ResourceManager.sprites.put("menu", loadSprite("title.png"));
            ResourceManager.sprites.put("bullet", loadSprite("bullet/bullet.jpg"));
            ResourceManager.sprites.put("rocket1", loadSprite("bullet/rocket1.png"));
            ResourceManager.sprites.put("rocket2", loadSprite("bullet/rocket2.png"));
            ResourceManager.sprites.put("floor", loadSprite("floor/bg.bmp"));
            ResourceManager.sprites.put("unbreak", loadSprite("walls/unbreak.jpg"));
            ResourceManager.sprites.put("breakable", loadSprite("walls/break1.jpg"));
            ResourceManager.sprites.put("broken", loadSprite("walls/break2.jpg"));
            ResourceManager.sprites.put("health", loadSprite("powerups/health.png"));
            ResourceManager.sprites.put("shield", loadSprite("powerups/shield.png"));
            ResourceManager.sprites.put("speed", loadSprite("powerups/speed.png"));


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //BufferedImage t = ImageIO.read(ResourceManager.class.getClassLoader().getResource("tank1.png"));
    }

    private static void initAnimations() {

            String baseName = "animations/%s/%s_%04d.png";
            animationInfo.forEach((animationName, frameCount) -> {
                List<BufferedImage> frames = new ArrayList<BufferedImage>();
                try {
                    for (int i = 0; i < frameCount; i++) {
                        String spritePath = baseName.formatted(animationName, animationName, i);
                        frames.add(loadSprite(spritePath));
                    }

                    ResourceManager.animations.put(animationName, frames);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            });


    }

    public static void loadResources() {
        ResourceManager.initSprites();
        ResourceManager.initAnimations();
        ResourceManager.initSounds();
    }

    public static void main(String[] args) { // test assets loading
        //ResourceManager.initSprites();
        ResourceManager.loadResources();
        //ResourceManager.initAnimations();
//        ResourceManager.initSounds();
        Sound bg = ResourceManager.getSound("bgmusic");
        bg.loopCont();
        bg.setVolume(.2f);
        bg.playSound();
        //bg.setLooping(3);

        // create pool of bullets, reminder to find where it belongs
        ResourcePool<Bullet> bulletPool = new ResourcePool<>("bullet", 300);
        bulletPool.fillPool(Bullet.class, 300);
    }

    public static BufferedImage getSprite(String type) {
        if (!ResourceManager.sprites.containsKey(type)) {
            throw new RuntimeException("%s is missing from the sprite resources".formatted(type));
        }

        return ResourceManager.sprites.get(type);
    }

    public static List<BufferedImage> getAnimation(String type) {
        return ResourceManager.animations.get(type);
    }

    private static BufferedImage loadSprite(String path) throws IOException {
        return ImageIO.read(Objects.requireNonNull(ResourceManager.class.getClassLoader().getResource(path), "Unable to find image at path: %s".formatted(path)));
    }

    private static void loadAnimations() {
        String baseName = "anmations/%s/%s-%04d.png";
        ResourceManager.animationInfo.forEach((animationName, frameCount) -> {
            List<BufferedImage> frames = new ArrayList<>();
            try {
                for (int i = 0; i < frameCount; i++) {

                    loadSprite(baseName.formatted(animationName, animationName, i));

                }
                ResourceManager.animations.put(animationName, frames);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Sound getSound(String type) {
        if (!ResourceManager.sounds.containsKey(type)) {
            throw new RuntimeException("%s resource is missing".formatted(type));

        }
        return ResourceManager.sounds.get(type);
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
            ResourceManager.sounds.put("bullet",loadSound("sounds/bullet.wav"));
            ResourceManager.sounds.put("explosion",loadSound("sounds/shotexplosion.wav"));
            ResourceManager.sounds.put("firing",loadSound("sounds/shotfiring.wav"));
            ResourceManager.sounds.put("bgmusic",loadSound("sounds/Music.mid"));
            ResourceManager.sounds.put("powerup",loadSound("sounds/pickup.wav"));

        } catch (Exception e) {
            System.out.println("e");
        }
    }
}

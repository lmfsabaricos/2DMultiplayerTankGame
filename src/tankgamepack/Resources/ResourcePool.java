package tankgamepack.Resources;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResourcePool<T> {
    private final List<T> pool;
    private final String resourceType;


    public ResourcePool(String name, int size) {
        this.pool = Collections.synchronizedList(new ArrayList<>(size)); // Thread safe list
        this.resourceType = name;
    }

    public boolean fillPool(Class<T> type, int size) {
        for (int i = 0; i < size; i++) {
            try {
                //System.out.println(this.resourceType.equals("bullet")); debug bullet sprite loading
                // I do not undertand why setting it to 0,0 was causing a bug that caused the bullet to collide with the wall at 0,0. The position of the bullet
                // was made the same as the tanks in the spawnBullet method. Unsure why the default coordinates of 0,0 caused a bug.
                this.pool.add(type.getDeclaredConstructor(Float.TYPE, Float.TYPE, BufferedImage.class).newInstance(100,100,ResourceManager.getSprite(this.resourceType)));
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }


        }
        return true;
    }
    public T getResource() { // get last resource in list
        return this.pool.remove(this.pool.size() - 1);
    }

    public boolean returnResource(T obj) {
        return this.pool.add(obj);
    }

    public int getSize() {
        return this.pool.size();
    }

}

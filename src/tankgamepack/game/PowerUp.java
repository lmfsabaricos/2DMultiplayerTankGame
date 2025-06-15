package tankgamepack.game;

public interface PowerUp { // powerup is lost depending on the amount of health the player got it with
    //public void activatePowerUp(Tank tank); // powerup activation will be handled by tank collision method
    boolean expired(); // used in GameWorld
    boolean isActive(int currentLives); // used in tank
    void setActivationHealth(int activationHealth);
}

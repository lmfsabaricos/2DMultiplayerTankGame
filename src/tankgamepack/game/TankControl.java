package tankgamepack.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author anthony-pc
 */
public class TankControl implements KeyListener {
    // these variables may or may not be final
    // if a controls screen is created, these values should not be final
    // do not make these final if powerups are going to affect them

    // finals can be initialized in the declaration and constructor

    /*
    Todo:
    - add shoot key

     */
    private final Tank tank; // t1 should just be tank
    private final int up;
    private final int down;
    private final int right;
    private final int left;
    private final int shoot;
    
    public TankControl(Tank t1, int up, int down, int left, int right, int shoot) {
        this.tank = t1;
        this.up = up;
        this.down = down;
        this.right = right;
        this.left = left;
        this.shoot = shoot;
    }

    @Override
    public void keyTyped(KeyEvent ke) { // key pressed and release

    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int keyPressed = ke.getKeyCode();
        if (keyPressed == up) {
            this.tank.setUpPressed();
        }
        if (keyPressed == down) {
            this.tank.setDownPressed();
        }
        if (keyPressed == left) {
            this.tank.setLeftPressed();
        }
        if (keyPressed == right) {
            this.tank.setRightPressed();
        }

        if (keyPressed == shoot) {
            this.tank.setShootPressed();
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        int keyReleased = ke.getKeyCode();
        if (keyReleased  == up) {
            this.tank.clearUpPressed();
        }
        if (keyReleased == down) {
            this.tank.clearDownPressed();
        }
        if (keyReleased  == left) {
            this.tank.clearLeftPressed();
        }
        if (keyReleased  == right) {
            this.tank.clearRightPressed();
        }
        if (keyReleased == shoot) {
            this.tank.clearShootPressed();
        }
    }
}

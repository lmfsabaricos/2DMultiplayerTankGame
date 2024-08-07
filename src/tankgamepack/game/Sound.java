package tankgamepack.game;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {
    private Clip sound;

    public Sound(Clip c) {
        this.sound = c;
    }

    public void stopSound() {
        if (this.sound != null && this.sound.isRunning()) {
            this.sound.stop();
        }
    }

    public void playSound() {
        if (this.sound != null) {
            if (this.sound.isRunning()) {
                this.sound.stop();
            }
            this.sound.setFramePosition(0);
            this.sound.start();
        }
    }

    public void setLooping(int loopCount) {
        if (this.sound != null) {
            this.sound.loop(loopCount);
        }
    }

    public void loopCont() {
        if (this.sound != null) {
            this.sound.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void setVolume(float level) {
        if (this.sound != null) {
            FloatControl volume = (FloatControl) this.sound.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(20.0f * (float) Math.log10(level));
        }
    }
}

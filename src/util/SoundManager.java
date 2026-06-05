package util;

import config.GameSettings;
import javax.sound.sampled.*;
import java.io.File;

public class SoundManager {
    public static void playSound(String soundFileName) {
        if (GameSettings.isMuted) return;

        try {
            File soundFile = new File("src/audio/" + soundFileName);
            if (!soundFile.exists()) return;

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * (GameSettings.volume / 100.0f)) + gainControl.getMinimum();
            gainControl.setValue(gain);

            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
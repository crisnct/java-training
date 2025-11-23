package com.example.training;

import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.*;
import javax.sound.midi.*;

public class WavPlayerDemo {

    public static void main(String[] args) {
        // 1) Redare WAV (ArnlocuieETte calea cu un .wav PCM 8/16-bit, 22/44.1kHz)
        playWav("test.wav");

        // 2) Reda o nota MIDI (pian, 500 ms)
        playMidiNote(0 /* canal */, 60 /* C4 */, 90 /* velocity */, 500 /* ms */);
    }

    // --- Sampled audio (WAV) ---
    public static void playWav(String path) {
        AudioInputStream in = null;
        Clip clip = null;
        InputStream fs = null;
        BufferedInputStream buffered = null;
        try {
            fs = WavPlayerDemo.class.getClassLoader().getResourceAsStream(path);
            if (fs == null) {
                System.err.println("Nu am gasit resursa audio: " + path);
                return;
            }

            // InputStream din JAR nu are mark/reset -> il impachetam in BufferedInputStream
            buffered = new BufferedInputStream(fs);
            in = AudioSystem.getAudioInputStream(buffered);      // necesita WAV PCM suportat
            AudioFormat fmt = in.getFormat();

            DataLine.Info info = new DataLine.Info(Clip.class, fmt);
            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("Format audio nesuportat: " + fmt);
                return;
            }
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(in);
            clip.start();

            Thread.sleep(1500);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (clip != null) { try { clip.close(); } catch (Exception ignored) {} }
            if (in != null)   { try { in.close(); }   catch (Exception ignored) {} }
            if (buffered != null) { try { buffered.close(); } catch (Exception ignored) {} }
            if (fs != null) { try { fs.close(); } catch (Exception ignored) {} }
        }
    }

    // --- MIDI (note simple prin sintetizatorul software) ---
    public static void playMidiNote(int channel, int note, int velocity, int millis) {
        Synthesizer synth = null;
        try {
            synth = MidiSystem.getSynthesizer();
            synth.open();

            MidiChannel[] chs = synth.getChannels();
            if (chs == null || chs.length == 0) {
                return;
            }

            MidiChannel ch = chs[Math.max(0, Math.min(channel, chs.length - 1))];
            ch.programChange(0);        // preset 0 (pian)
            ch.noteOn(note, velocity);  // porneste nota
            try { Thread.sleep(millis); } catch (InterruptedException ignored) {}
            ch.noteOff(note);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (synth != null) { try { synth.close(); } catch (Exception ignored) {} }
        }
    }
}

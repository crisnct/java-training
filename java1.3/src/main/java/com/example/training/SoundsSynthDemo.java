package com.example.training;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class SoundsSynthDemo {

  // Config audio
  private static final float SAMPLE_RATE = 44100f;
  private static final int BYTES_PER_SAMPLE = 2; // 16-bit PCM
  private static final int CHANNELS = 1;         // mono

  public static void main(String[] args) {
    SourceDataLine line = null;
    try {
      AudioFormat fmt = new AudioFormat(
          AudioFormat.Encoding.PCM_SIGNED,
          SAMPLE_RATE,               // sample rate
          16,                        // bits
          CHANNELS,                  // channels
          CHANNELS * BYTES_PER_SAMPLE, // frame size
          SAMPLE_RATE,               // frame rate
          false                      // little-endian
      );

      line = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, fmt));
      line.open(fmt, 4096);
      line.start();

      // 1) Sine sweep: 220 Hz -> 880 Hz, 2 sec, cu fade in/out
      playSineSweep(line, 220.0, 880.0, 2000, 0.5);

      // 2) „Acord”: 440 Hz + 550 Hz, 1.5 sec, envelope ADSR simplu
      playTwoOscChord(line, 440.0, 550.0, 1500, 0.45);

      // 3) „Percuție”: zgomot alb cu decay rapid (hi-hat like), 400 ms
      playNoisePerc(line, 400, 0.6);

      line.drain();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (line != null) {
        try {
          line.stop();
        } catch (Exception ignore) {
        }
        try {
          line.close();
        } catch (Exception ignore) {
        }
      }
    }
  }

  // ------------------- Synthesis blocks -------------------

  private static void playSineSweep(SourceDataLine line, double fStart, double fEnd, int durationMs, double amp) {
    int totalSamples = (int) (SAMPLE_RATE * durationMs / 1000.0);
    byte[] buffer = new byte[totalSamples * BYTES_PER_SAMPLE];

    double phase = 0.0;
    double twoPi = 2.0 * Math.PI;
    double fRange = fEnd - fStart;

    for (int i = 0; i < totalSamples; i++) {
      double t = i / SAMPLE_RATE;
      double frac = (double) i / (double) totalSamples;
      double f = fStart + fRange * frac;      // frecvența curentă
      double inc = twoPi * f / SAMPLE_RATE;

      // Fade in/out ușor
      double env = smoothStep(0.0, 0.1, frac) * (1.0 - smoothStep(0.9, 1.0, frac));

      phase += inc;
      double sample = Math.sin(phase) * amp * env;
      writeSample16(buffer, i, sample);
    }
    line.write(buffer, 0, buffer.length);
  }

  private static void playTwoOscChord(SourceDataLine line, double f1, double f2, int durationMs, double amp) {
    int totalSamples = (int) (SAMPLE_RATE * durationMs / 1000.0);
    byte[] buffer = new byte[totalSamples * BYTES_PER_SAMPLE];

    // Oscilatoare
    double phase1 = 0.0, phase2 = 0.0;
    double twoPi = 2.0 * Math.PI;
    double inc1 = twoPi * f1 / SAMPLE_RATE;
    double inc2 = twoPi * f2 / SAMPLE_RATE;

    // Envelope ADSR simplu (timpi în ms)
    int attackMs = 20, decayMs = 120, releaseMs = 150;
    double sustainLevel = 0.65;

    for (int i = 0; i < totalSamples; i++) {
      phase1 += inc1;
      phase2 += inc2;

      double s1 = Math.sin(phase1);
      double s2 = Math.sin(phase2);

      // mix simplu (evităm clipping scăzând amplitudinea)
      double mixed = (s1 + s2) * 0.5;

      double env = adsrEnvelope(i, totalSamples, attackMs, decayMs, sustainLevel, releaseMs);
      double sample = mixed * amp * env;

      writeSample16(buffer, i, sample);
    }
    line.write(buffer, 0, buffer.length);
  }

  private static void playNoisePerc(SourceDataLine line, int durationMs, double amp) {
    int totalSamples = (int) (SAMPLE_RATE * durationMs / 1000.0);
    byte[] buffer = new byte[totalSamples * BYTES_PER_SAMPLE];

    long seed = 123456789L; // PRNG simplu (Linear Congruential)
    for (int i = 0; i < totalSamples; i++) {
      seed = (seed * 1103515245L + 12345L) & 0x7fffffffL;
      double white = ((seed / (double) 0x7fffffff) * 2.0) - 1.0; // [-1, 1]

      // decay exponential rapid
      double frac = (double) i / (double) totalSamples;
      double env = Math.pow(1.0 - frac, 3.0);

      double sample = white * amp * env;
      writeSample16(buffer, i, sample);
    }
    line.write(buffer, 0, buffer.length);
  }

  // ------------------- Helpers -------------------

  // ADSR foarte simplu; timpi în ms, sustain constant
  private static double adsrEnvelope(int index, int totalSamples, int attackMs, int decayMs, double sustain, int releaseMs) {
    double attackSamples = SAMPLE_RATE * attackMs / 1000.0;
    double decaySamples = SAMPLE_RATE * decayMs / 1000.0;
    double releaseSamples = SAMPLE_RATE * releaseMs / 1000.0;

    int relStart = totalSamples - (int) releaseSamples;
    if (relStart < 0) {
      relStart = 0;
    }

    if (index < attackSamples) {
      return index / attackSamples; // 0 -> 1
    } else if (index < attackSamples + decaySamples) {
      double dpos = (index - attackSamples) / decaySamples;
      return 1.0 + (sustain - 1.0) * dpos; // 1 -> sustain
    } else if (index < relStart) {
      return sustain; // platou
    } else {
      double rpos = (index - relStart) / releaseSamples;
      if (rpos > 1.0) {
        rpos = 1.0;
      }
      return sustain * (1.0 - rpos); // sustain -> 0
    }
  }

  // curbă de easing lin/soft pentru fade
  private static double smoothStep(double edge0, double edge1, double x) {
    if (x <= edge0) {
      return 0.0;
    }
    if (x >= edge1) {
      return 1.0;
    }
    double t = (x - edge0) / (edge1 - edge0);
    return t * t * (3.0 - 2.0 * t);
  }

  private static void writeSample16(byte[] buffer, int sampleIndex, double value) {
    // clamp [-1,1]
    if (value > 1.0) {
      value = 1.0;
    }
    if (value < -1.0) {
      value = -1.0;
    }

    int s = (int) (value * 32767.0);
    int byteIndex = sampleIndex * BYTES_PER_SAMPLE;

    // little-endian
    buffer[byteIndex] = (byte) (s & 0xFF);
    buffer[byteIndex + 1] = (byte) ((s >>> 8) & 0xFF);
  }
}

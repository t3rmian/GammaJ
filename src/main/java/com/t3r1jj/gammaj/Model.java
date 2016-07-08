package com.t3r1jj.gammaj;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.WORD;

/*
 Copyright (C) 2016 Damian Terlecki

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
public class Model {

    public static final int[][] MY_DEFAULT_GAMMA_RAMP = new int[][]{{0, 256, 512, 768, 1024, 1280, 1536, 1792, 2048, 2304, 2560, 2816, 3072, 3328, 3584, 3840, 4096, 4352, 4608, 4864, 5120, 5376, 5632, 5888, 6144, 6400, 6656, 6912, 7168, 7424, 7680, 7936, 8192, 8448, 8704, 8960, 9216, 9472, 9728, 9984, 10240, 10496, 10752, 11008, 11264, 11520, 11776, 12032, 12288, 12544, 12800, 13056, 13312, 13568, 13824, 14080, 14336, 14592, 14848, 15104, 15360, 15616, 15872, 16128, 16384, 16640, 16896, 17152, 17408, 17664, 17920, 18176, 18432, 18688, 18944, 19200, 19456, 19712, 19968, 20224, 20480, 20736, 20992, 21248, 21504, 21760, 22016, 22272, 22528, 22784, 23040, 23296, 23552, 23808, 24064, 24320, 24576, 24832, 25088, 25344, 25600, 25856, 26112, 26368, 26624, 26880, 27136, 27392, 27648, 27904, 28160, 28416, 28672, 28928, 29184, 29440, 29696, 29952, 30208, 30464, 30720, 30976, 31232, 31488, 31744, 32000, 32256, 32512, -32768, -32512, -32256, -32000, -31744, -31488, -31232, -30976, -30720, -30464, -30208, -29952, -29696, -29440, -29184, -28928, -28672, -28416, -28160, -27904, -27648, -27392, -27136, -26880, -26624, -26368, -26112, -25856, -25600, -25344, -25088, -24832, -24576, -24320, -24064, -23808, -23552, -23296, -23040, -22784, -22528, -22272, -22016, -21760, -21504, -21248, -20992, -20736, -20480, -20224, -19968, -19712, -19456, -19200, -18944, -18688, -18432, -18176, -17920, -17664, -17408, -17152, -16896, -16640, -16384, -16128, -15872, -15616, -15360, -15104, -14848, -14592, -14336, -14080, -13824, -13568, -13312, -13056, -12800, -12544, -12288, -12032, -11776, -11520, -11264, -11008, -10752, -10496, -10240, -9984, -9728, -9472, -9216, -8960, -8704, -8448, -8192, -7936, -7680, -7424, -7168, -6912, -6656, -6400, -6144, -5888, -5632, -5376, -5120, -4864, -4608, -4352, -4096, -3840, -3584, -3328, -3072, -2816, -2560, -2304, -2048, -1792, -1536, -1280, -1024, -768, -512, -256}, {0, 256, 512, 768, 1024, 1280, 1536, 1792, 2048, 2304, 2560, 2816, 3072, 3328, 3584, 3840, 4096, 4352, 4608, 4864, 5120, 5376, 5632, 5888, 6144, 6400, 6656, 6912, 7168, 7424, 7680, 7936, 8192, 8448, 8704, 8960, 9216, 9472, 9728, 9984, 10240, 10496, 10752, 11008, 11264, 11520, 11776, 12032, 12288, 12544, 12800, 13056, 13312, 13568, 13824, 14080, 14336, 14592, 14848, 15104, 15360, 15616, 15872, 16128, 16384, 16640, 16896, 17152, 17408, 17664, 17920, 18176, 18432, 18688, 18944, 19200, 19456, 19712, 19968, 20224, 20480, 20736, 20992, 21248, 21504, 21760, 22016, 22272, 22528, 22784, 23040, 23296, 23552, 23808, 24064, 24320, 24576, 24832, 25088, 25344, 25600, 25856, 26112, 26368, 26624, 26880, 27136, 27392, 27648, 27904, 28160, 28416, 28672, 28928, 29184, 29440, 29696, 29952, 30208, 30464, 30720, 30976, 31232, 31488, 31744, 32000, 32256, 32512, -32768, -32512, -32256, -32000, -31744, -31488, -31232, -30976, -30720, -30464, -30208, -29952, -29696, -29440, -29184, -28928, -28672, -28416, -28160, -27904, -27648, -27392, -27136, -26880, -26624, -26368, -26112, -25856, -25600, -25344, -25088, -24832, -24576, -24320, -24064, -23808, -23552, -23296, -23040, -22784, -22528, -22272, -22016, -21760, -21504, -21248, -20992, -20736, -20480, -20224, -19968, -19712, -19456, -19200, -18944, -18688, -18432, -18176, -17920, -17664, -17408, -17152, -16896, -16640, -16384, -16128, -15872, -15616, -15360, -15104, -14848, -14592, -14336, -14080, -13824, -13568, -13312, -13056, -12800, -12544, -12288, -12032, -11776, -11520, -11264, -11008, -10752, -10496, -10240, -9984, -9728, -9472, -9216, -8960, -8704, -8448, -8192, -7936, -7680, -7424, -7168, -6912, -6656, -6400, -6144, -5888, -5632, -5376, -5120, -4864, -4608, -4352, -4096, -3840, -3584, -3328, -3072, -2816, -2560, -2304, -2048, -1792, -1536, -1280, -1024, -768, -512, -256}, {0, 256, 512, 768, 1024, 1280, 1536, 1792, 2048, 2304, 2560, 2816, 3072, 3328, 3584, 3840, 4096, 4352, 4608, 4864, 5120, 5376, 5632, 5888, 6144, 6400, 6656, 6912, 7168, 7424, 7680, 7936, 8192, 8448, 8704, 8960, 9216, 9472, 9728, 9984, 10240, 10496, 10752, 11008, 11264, 11520, 11776, 12032, 12288, 12544, 12800, 13056, 13312, 13568, 13824, 14080, 14336, 14592, 14848, 15104, 15360, 15616, 15872, 16128, 16384, 16640, 16896, 17152, 17408, 17664, 17920, 18176, 18432, 18688, 18944, 19200, 19456, 19712, 19968, 20224, 20480, 20736, 20992, 21248, 21504, 21760, 22016, 22272, 22528, 22784, 23040, 23296, 23552, 23808, 24064, 24320, 24576, 24832, 25088, 25344, 25600, 25856, 26112, 26368, 26624, 26880, 27136, 27392, 27648, 27904, 28160, 28416, 28672, 28928, 29184, 29440, 29696, 29952, 30208, 30464, 30720, 30976, 31232, 31488, 31744, 32000, 32256, 32512, -32768, -32512, -32256, -32000, -31744, -31488, -31232, -30976, -30720, -30464, -30208, -29952, -29696, -29440, -29184, -28928, -28672, -28416, -28160, -27904, -27648, -27392, -27136, -26880, -26624, -26368, -26112, -25856, -25600, -25344, -25088, -24832, -24576, -24320, -24064, -23808, -23552, -23296, -23040, -22784, -22528, -22272, -22016, -21760, -21504, -21248, -20992, -20736, -20480, -20224, -19968, -19712, -19456, -19200, -18944, -18688, -18432, -18176, -17920, -17664, -17408, -17152, -16896, -16640, -16384, -16128, -15872, -15616, -15360, -15104, -14848, -14592, -14336, -14080, -13824, -13568, -13312, -13056, -12800, -12544, -12288, -12032, -11776, -11520, -11264, -11008, -10752, -10496, -10240, -9984, -9728, -9472, -9216, -8960, -8704, -8448, -8192, -7936, -7680, -7424, -7168, -6912, -6656, -6400, -6144, -5888, -5632, -5376, -5120, -4864, -4608, -4352, -4096, -3840, -3584, -3328, -3072, -2816, -2560, -2304, -2048, -1792, -1536, -1280, -1024, -768, -512, -256}};
    private static final int TOTAL_RAMP_VALUES_COUNT = 768;
    private static final int SINGLE_RAMP_COLOR_VALUES_COUNT = 256;
    private static final int TOTAL_COLORS_COUNT = 3;
    private static final int MAX_WORD = 65535;
    private static final int MAX_HALF_WORD = 32768;

    private static final float DEFAULT_GAMMA = 1f;
    private static final float DEFAULT_BRIGHTNESS = 0.5f;
    private static final float DEFAULT_CONTRAST = 0f;

    float gamma = DEFAULT_GAMMA;
    float brightness = DEFAULT_BRIGHTNESS;
    float contrast = DEFAULT_CONTRAST;

    WORD[] gammaRamp = new WORD[TOTAL_RAMP_VALUES_COUNT];

    public Model() {
    }

    private void getDeviceGammaRamp() {
        WinDef.HDC deviceContext = User32.INSTANCE.GetDC(null);
        BOOL success = MyGDI32.INSTANCE.GetDeviceGammaRamp(deviceContext, gammaRamp);
        if (!success.booleanValue()) {
//            throw new GammaCallException();
        }
    }

    private void setDeviceGammaRamp() {
        WinDef.HDC deviceContext = User32.INSTANCE.GetDC(null);
        BOOL success = MyGDI32.INSTANCE.SetDeviceGammaRamp(deviceContext, gammaRamp);
        if (!success.booleanValue()) {
//            throw new GammaCallException(); //"If this function fails, the return value is FALSE." - It succeeds on fail return too
        }
    }

    public void resetModel() {
        gamma = DEFAULT_GAMMA;
        brightness = DEFAULT_BRIGHTNESS;
        contrast = DEFAULT_CONTRAST;
    }

    public void resetGammaRamp() {
        setGammaRamp(MY_DEFAULT_GAMMA_RAMP);
        setDeviceGammaRamp();
    }

    /**
     *
     * @param brightness value from 0f...1f range, 0.5 = no change
     */
    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
    }

    /**
     *
     * @param contrast value from -1f...1f range, 0 = no change
     */
    public void setContrast(float contrast) {
        this.contrast = contrast;
    }

    public void calculateGammaRamp() {
        int brightnessIncrement = (int) ((brightness * 2 - 1f) * MAX_WORD);
        float contrastLevel = contrast * 255;
        float contrastFactor = 259 * (contrastLevel + 255) / (255 * (259 - contrastLevel));
        int[][] newGammaRamp = new int[TOTAL_COLORS_COUNT][SINGLE_RAMP_COLOR_VALUES_COUNT];
        for (int y = 0; y < newGammaRamp.length; y++) {
            for (int x = 0; x < newGammaRamp[0].length; x++) {
                newGammaRamp[y][x] = (int) (Math.pow((float) x / SINGLE_RAMP_COLOR_VALUES_COUNT, gamma) * MAX_WORD);
                newGammaRamp[y][x] += brightnessIncrement;
                newGammaRamp[y][x] = (int) (contrastFactor * (newGammaRamp[y][x] - MAX_HALF_WORD) + MAX_HALF_WORD);

                if (newGammaRamp[y][x] > MAX_WORD) {
                    newGammaRamp[y][x] = MAX_WORD;
                } else if (newGammaRamp[y][x] < 0) {
                    newGammaRamp[y][x] = 0;
                }
                System.out.println(newGammaRamp[y][x]);
            }
        }
        setGammaRamp(newGammaRamp);
        setDeviceGammaRamp();
    }

    public int[][] getCurrentGammaRamp() {
        getDeviceGammaRamp();
        int[][] rampValues = new int[TOTAL_COLORS_COUNT][SINGLE_RAMP_COLOR_VALUES_COUNT];
        for (int i = 0; i < TOTAL_RAMP_VALUES_COUNT; i++) {
            rampValues[i / SINGLE_RAMP_COLOR_VALUES_COUNT][i % SINGLE_RAMP_COLOR_VALUES_COUNT]
                    = (gammaRamp[i].shortValue()) < 0 ? gammaRamp[i].shortValue() + MAX_WORD : gammaRamp[i].shortValue();
        }
        return rampValues;
    }

    public int[][] getCachedGammaRamp() {
        int[][] rampValues = new int[TOTAL_COLORS_COUNT][SINGLE_RAMP_COLOR_VALUES_COUNT];
        for (int i = 0; i < TOTAL_RAMP_VALUES_COUNT; i++) {
            rampValues[i / SINGLE_RAMP_COLOR_VALUES_COUNT][i % SINGLE_RAMP_COLOR_VALUES_COUNT] = (gammaRamp[i].shortValue()) < 0 ? gammaRamp[i].shortValue() + MAX_WORD : gammaRamp[i].shortValue();

        }
        return rampValues;
    }

    public void setGammaRamp(int[][] rampValues) {
        int i = 0;
        for (int y = 0; y < rampValues.length; y++) {
            for (int x = 0; x < rampValues[0].length; x++) {
                gammaRamp[i++] = new WORD(rampValues[y][x]);
            }
        }
    }

    public void printGammaRamp() {
        int[][] rampValues = getCurrentGammaRamp();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{{");
        for (int y = 0; y < rampValues.length; y++) {
            for (int x = 0; x < rampValues[0].length; x++) {
                stringBuilder.append(rampValues[y][x]).append(", ");
            }
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
            stringBuilder.append("},{");
        }
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        stringBuilder.append("}");
        System.out.println(stringBuilder.toString());
    }

    public float[][] getGammaZZZZZ() {
        float[][] gammaZZZZZ = new float[TOTAL_COLORS_COUNT][SINGLE_RAMP_COLOR_VALUES_COUNT];
        int[][] gammaRamp = getCurrentGammaRamp();
        for (int y = 0; y < TOTAL_COLORS_COUNT; y++) {
            for (int x = 0; x < SINGLE_RAMP_COLOR_VALUES_COUNT; x++) {
                gammaZZZZZ[y][x] = (float) gammaRamp[y][x] / MAX_WORD;
            }
        }
        return gammaZZZZZ;
    }

    private class GammaCallException extends RuntimeException {

    }
}

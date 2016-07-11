/* 
 * Copyright 2016 Damian Terlecki.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.t3r1jj.gammaj.model;

import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.WORD;
import io.github.t3r1jj.gammaj.MyGDI32;

public class Gamma {

    private static final int TOTAL_RAMP_VALUES_COUNT = 768;
    private static final int SINGLE_RAMP_COLOR_VALUES_COUNT = 256;
    private static final int TOTAL_COLORS_COUNT = 3;
    private static final int MAX_WORD = 65535;
    private static final int MAX_HALF_WORD = 32768;

    static final int[][] DEFAULT_GAMMA_RAMP = new int[][]{{0, 256, 512, 768, 1024, 1280, 1536, 1792, 2048, 2304, 2560, 2816, 3072, 3328, 3584, 3840, 4096, 4352, 4608, 4864, 5120, 5376, 5632, 5888, 6144, 6400, 6656, 6912, 7168, 7424, 7680, 7936, 8192, 8448, 8704, 8960, 9216, 9472, 9728, 9984, 10240, 10496, 10752, 11008, 11264, 11520, 11776, 12032, 12288, 12544, 12800, 13056, 13312, 13568, 13824, 14080, 14336, 14592, 14848, 15104, 15360, 15616, 15872, 16128, 16384, 16640, 16896, 17152, 17408, 17664, 17920, 18176, 18432, 18688, 18944, 19200, 19456, 19712, 19968, 20224, 20480, 20736, 20992, 21248, 21504, 21760, 22016, 22272, 22528, 22784, 23040, 23296, 23552, 23808, 24064, 24320, 24576, 24832, 25088, 25344, 25600, 25856, 26112, 26368, 26624, 26880, 27136, 27392, 27648, 27904, 28160, 28416, 28672, 28928, 29184, 29440, 29696, 29952, 30208, 30464, 30720, 30976, 31232, 31488, 31744, 32000, 32256, 32512, 32767, 33023, 33279, 33535, 33791, 34047, 34303, 34559, 34815, 35071, 35327, 35583, 35839, 36095, 36351, 36607, 36863, 37119, 37375, 37631, 37887, 38143, 38399, 38655, 38911, 39167, 39423, 39679, 39935, 40191, 40447, 40703, 40959, 41215, 41471, 41727, 41983, 42239, 42495, 42751, 43007, 43263, 43519, 43775, 44031, 44287, 44543, 44799, 45055, 45311, 45567, 45823, 46079, 46335, 46591, 46847, 47103, 47359, 47615, 47871, 48127, 48383, 48639, 48895, 49151, 49407, 49663, 49919, 50175, 50431, 50687, 50943, 51199, 51455, 51711, 51967, 52223, 52479, 52735, 52991, 53247, 53503, 53759, 54015, 54271, 54527, 54783, 55039, 55295, 55551, 55807, 56063, 56319, 56575, 56831, 57087, 57343, 57599, 57855, 58111, 58367, 58623, 58879, 59135, 59391, 59647, 59903, 60159, 60415, 60671, 60927, 61183, 61439, 61695, 61951, 62207, 62463, 62719, 62975, 63231, 63487, 63743, 63999, 64255, 64511, 64767, 65023, 65279}, {0, 256, 512, 768, 1024, 1280, 1536, 1792, 2048, 2304, 2560, 2816, 3072, 3328, 3584, 3840, 4096, 4352, 4608, 4864, 5120, 5376, 5632, 5888, 6144, 6400, 6656, 6912, 7168, 7424, 7680, 7936, 8192, 8448, 8704, 8960, 9216, 9472, 9728, 9984, 10240, 10496, 10752, 11008, 11264, 11520, 11776, 12032, 12288, 12544, 12800, 13056, 13312, 13568, 13824, 14080, 14336, 14592, 14848, 15104, 15360, 15616, 15872, 16128, 16384, 16640, 16896, 17152, 17408, 17664, 17920, 18176, 18432, 18688, 18944, 19200, 19456, 19712, 19968, 20224, 20480, 20736, 20992, 21248, 21504, 21760, 22016, 22272, 22528, 22784, 23040, 23296, 23552, 23808, 24064, 24320, 24576, 24832, 25088, 25344, 25600, 25856, 26112, 26368, 26624, 26880, 27136, 27392, 27648, 27904, 28160, 28416, 28672, 28928, 29184, 29440, 29696, 29952, 30208, 30464, 30720, 30976, 31232, 31488, 31744, 32000, 32256, 32512, 32767, 33023, 33279, 33535, 33791, 34047, 34303, 34559, 34815, 35071, 35327, 35583, 35839, 36095, 36351, 36607, 36863, 37119, 37375, 37631, 37887, 38143, 38399, 38655, 38911, 39167, 39423, 39679, 39935, 40191, 40447, 40703, 40959, 41215, 41471, 41727, 41983, 42239, 42495, 42751, 43007, 43263, 43519, 43775, 44031, 44287, 44543, 44799, 45055, 45311, 45567, 45823, 46079, 46335, 46591, 46847, 47103, 47359, 47615, 47871, 48127, 48383, 48639, 48895, 49151, 49407, 49663, 49919, 50175, 50431, 50687, 50943, 51199, 51455, 51711, 51967, 52223, 52479, 52735, 52991, 53247, 53503, 53759, 54015, 54271, 54527, 54783, 55039, 55295, 55551, 55807, 56063, 56319, 56575, 56831, 57087, 57343, 57599, 57855, 58111, 58367, 58623, 58879, 59135, 59391, 59647, 59903, 60159, 60415, 60671, 60927, 61183, 61439, 61695, 61951, 62207, 62463, 62719, 62975, 63231, 63487, 63743, 63999, 64255, 64511, 64767, 65023, 65279}, {0, 256, 512, 768, 1024, 1280, 1536, 1792, 2048, 2304, 2560, 2816, 3072, 3328, 3584, 3840, 4096, 4352, 4608, 4864, 5120, 5376, 5632, 5888, 6144, 6400, 6656, 6912, 7168, 7424, 7680, 7936, 8192, 8448, 8704, 8960, 9216, 9472, 9728, 9984, 10240, 10496, 10752, 11008, 11264, 11520, 11776, 12032, 12288, 12544, 12800, 13056, 13312, 13568, 13824, 14080, 14336, 14592, 14848, 15104, 15360, 15616, 15872, 16128, 16384, 16640, 16896, 17152, 17408, 17664, 17920, 18176, 18432, 18688, 18944, 19200, 19456, 19712, 19968, 20224, 20480, 20736, 20992, 21248, 21504, 21760, 22016, 22272, 22528, 22784, 23040, 23296, 23552, 23808, 24064, 24320, 24576, 24832, 25088, 25344, 25600, 25856, 26112, 26368, 26624, 26880, 27136, 27392, 27648, 27904, 28160, 28416, 28672, 28928, 29184, 29440, 29696, 29952, 30208, 30464, 30720, 30976, 31232, 31488, 31744, 32000, 32256, 32512, 32767, 33023, 33279, 33535, 33791, 34047, 34303, 34559, 34815, 35071, 35327, 35583, 35839, 36095, 36351, 36607, 36863, 37119, 37375, 37631, 37887, 38143, 38399, 38655, 38911, 39167, 39423, 39679, 39935, 40191, 40447, 40703, 40959, 41215, 41471, 41727, 41983, 42239, 42495, 42751, 43007, 43263, 43519, 43775, 44031, 44287, 44543, 44799, 45055, 45311, 45567, 45823, 46079, 46335, 46591, 46847, 47103, 47359, 47615, 47871, 48127, 48383, 48639, 48895, 49151, 49407, 49663, 49919, 50175, 50431, 50687, 50943, 51199, 51455, 51711, 51967, 52223, 52479, 52735, 52991, 53247, 53503, 53759, 54015, 54271, 54527, 54783, 55039, 55295, 55551, 55807, 56063, 56319, 56575, 56831, 57087, 57343, 57599, 57855, 58111, 58367, 58623, 58879, 59135, 59391, 59647, 59903, 60159, 60415, 60671, 60927, 61183, 61439, 61695, 61951, 62207, 62463, 62719, 62975, 63231, 63487, 63743, 63999, 64255, 64511, 64767, 65023, 65279}};
    static final double DEFAULT_GAMMA = 1f;
    static final double DEFAULT_BRIGHTNESS = 0.5f;
    static final double DEFAULT_CONTRAST = 0f;
    static final double DEFAULT_CONTRAST_GAIN = 50f;
    static final double DEFAULT_TEMPERATURE = 1f;

    private final HDC hdc;

    double[] gamma = new double[]{DEFAULT_GAMMA, DEFAULT_GAMMA, DEFAULT_GAMMA};
    double[] brightness = new double[]{DEFAULT_BRIGHTNESS, DEFAULT_BRIGHTNESS, DEFAULT_BRIGHTNESS};
    double[] contrast = new double[]{DEFAULT_CONTRAST, DEFAULT_CONTRAST, DEFAULT_CONTRAST};
    double[] contrastGain = new double[]{DEFAULT_CONTRAST_GAIN, DEFAULT_CONTRAST_GAIN, DEFAULT_CONTRAST_GAIN};
    double[] temperature = new double[]{DEFAULT_TEMPERATURE, DEFAULT_TEMPERATURE, DEFAULT_TEMPERATURE};

    WORD[] gammaRamp = new WORD[TOTAL_RAMP_VALUES_COUNT];

    public Gamma(HDC hdc) {
        this.hdc = hdc;
    }

    private void getDeviceGammaRamp() {
        BOOL success = MyGDI32.INSTANCE.GetDeviceGammaRamp(hdc, gammaRamp);
        if (!success.booleanValue()) {
//            throw new GammaCallException();
        }
    }

    /**
     * Sets device gamma ramp based on current model
     */
    private void setDeviceGammaRamp() {
        BOOL success = MyGDI32.INSTANCE.SetDeviceGammaRamp(hdc, gammaRamp);
        if (!success.booleanValue()) {
//            throw new GammaCallException(); //"If this function fails, the return value is FALSE." - It succeeds on fail return too
        }
    }

    void resetGammaRamp() {
        for (int i = 0; i < TOTAL_COLORS_COUNT; i++) {
            gamma[i] = Gamma.DEFAULT_GAMMA;
            brightness[i] = Gamma.DEFAULT_BRIGHTNESS;
            contrast[i] = Gamma.DEFAULT_CONTRAST;
            contrastGain[i] = Gamma.DEFAULT_CONTRAST_GAIN;
            temperature[i] = Gamma.DEFAULT_TEMPERATURE;
        }
        setGammaRamp(DEFAULT_GAMMA_RAMP);
        setDeviceGammaRamp();
    }

    /**
     * Recalculates model and sets device gamma ramp based on it
     */
    void reinitializeGammaRamp() {
        int[][] newGammaRamp = new int[TOTAL_COLORS_COUNT][SINGLE_RAMP_COLOR_VALUES_COUNT];
        for (int y = 0; y < newGammaRamp.length; y++) {
            int brightnessIncrement = (int) ((brightness[y] * 2 - 1f) * MAX_WORD);
            double contrastLevel = contrast[y] * 255;
            double contrastFactor = 259 * (contrastLevel + 255) / (255 * (259 - contrastLevel));

            // TODO: Change this, also change gamma range
            double contrastGainFactor = 1;
            if (contrastGain[y] > 50) {
                contrastGainFactor = Math.pow(32, (contrastGain[y] - 50) * 2 / 50);
            } else {
                contrastGainFactor = contrastGain[y] / 50;
            }
            System.out.println(contrastGainFactor);
            
            for (int x = 0; x < newGammaRamp[0].length; x++) {
                newGammaRamp[y][x] = (int) (Math.pow((double) x / SINGLE_RAMP_COLOR_VALUES_COUNT, gamma[y]) * MAX_WORD);
                newGammaRamp[y][x] += brightnessIncrement;
                newGammaRamp[y][x] = (int) (contrastFactor * (newGammaRamp[y][x] - MAX_HALF_WORD) + MAX_HALF_WORD);
                newGammaRamp[y][x] *= contrastGainFactor;
                newGammaRamp[y][x] *= temperature[y];

                if (newGammaRamp[y][x] > MAX_WORD) {
                    newGammaRamp[y][x] = MAX_WORD;
                } else if (newGammaRamp[y][x] < 0) {
                    newGammaRamp[y][x] = 0;
                }
            }
        }
        setGammaRamp(newGammaRamp);
        setDeviceGammaRamp();
    }

    int[][] getGammaRamp() {
        getDeviceGammaRamp();
        int[][] rampValues = new int[TOTAL_COLORS_COUNT][SINGLE_RAMP_COLOR_VALUES_COUNT];
        for (int i = 0; i < TOTAL_RAMP_VALUES_COUNT; i++) {
            rampValues[i / SINGLE_RAMP_COLOR_VALUES_COUNT][i % SINGLE_RAMP_COLOR_VALUES_COUNT] = gammaRamp[i].intValue();
        }
        return rampValues;
    }

    void setGammaRamp(int[][] rampValues) {
        int i = 0;
        for (int y = 0; y < rampValues.length; y++) {
            for (int x = 0; x < rampValues[y].length; x++, i++) {
                gammaRamp[i] = new WORD(rampValues[y][x]);
            }
        }
    }

    void printGammaRamp() {
        int[][] rampValues = getGammaRamp();
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

    double[][] getGammaScaledRamp() {
        getDeviceGammaRamp();
        double[][] scaledGammaRamp = new double[TOTAL_COLORS_COUNT][SINGLE_RAMP_COLOR_VALUES_COUNT];
        int[][] gammaRamp = getGammaRamp();
        for (int y = 0; y < TOTAL_COLORS_COUNT; y++) {
            for (int x = 0; x < SINGLE_RAMP_COLOR_VALUES_COUNT; x++) {
                scaledGammaRamp[y][x] = (double) gammaRamp[y][x] / MAX_WORD;
            }
        }
        return scaledGammaRamp;
    }

    public enum Channel {

        RED(0), GREEN(1), BLUE(2);

        private final int index;

        private Channel(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    private class GammaCallException extends RuntimeException {

    }
}

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

public interface Display {

    /**
     * 
     * @return name of display
     */
    String getName();

    /**
     * 
     * @param channel RGB
     * @param gamma value from 0d...infinity range
     */
    void setGamma(Gamma.Channel channel, double gamma);

    /**
     *
     * @param channel RGB
     * @param brightness value from 0d...100d range, 50 = no change
     */
    void setBrightness(Gamma.Channel channel, double brightness);

    /**
     *
     * @param channel RGB
     * @param contrastBilateral value from 0d...100d range, 50 = no change
     */
    void setContrastBilateral(Gamma.Channel channel, double contrastBilateral);

    /**
     * 
     * @param channel RGB
     * @param contrastUnilateral value from 0d...100d range, 50 = no change
     */
    void setContrastUnilateral(Gamma.Channel channel, double contrastUnilateral);

    /**
     *
     * @param temperature in Kelvin, value from 1000...25000 range
     */
    void setTemperature(ColorTemperature temperature);

    /**
     * Resets gamma ramp to default, linear values for all channels
     */
    void resetGammaRamp();

    /**
     * @param channel RGB
     * Inverts gamma ramp for selected channel
     */
    void invertGammaRamp(Gamma.Channel channel);

    /**
     * Recalculates and sets gamma ramp based on current model
     */
    void reinitialize();

    /**
     * 
     * @return normalized (0d...1d) gamma ramp for all RGB channels (256 values each)
     */
    double[][] getNormalizedGammaRamp();
    
    /**
     * 
     * @return gamma ramp for all RGB channels (256 values each from the range of 0...65535)
     */
    int[][] getGammaRamp();
    
    /**
     * 
     * @return RGB array of booleans isChannelInverted
     */
    boolean[] getInvertedChannels();

}

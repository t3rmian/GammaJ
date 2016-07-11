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

import com.sun.jna.platform.win32.WinDef;

public interface Display {

    String getName();

    WinDef.HDC getHdc();

    void setGamma(Gamma.Channel channel, double gamma);

    /**
     *
     * @param channel RGB
     * @param brightness value from 0d...1d range, 0.5 = no change
     */
    void setBrightness(Gamma.Channel channel, double brightness);

    /**
     *
     * @param channel RGB
     * @param contrast value from -1d...1d range, 0 = no change
     */
    void setContrast(Gamma.Channel channel, double contrast);

    /**
     * 
     * @param channel RGB
     * @param contrastGain value from 0d...100d range, 50 = no change
     */
    void setContrastGain(Gamma.Channel channel, double contrastGain);

    /**
     *
     * @param temperature Kelvin
     */
    void setTemperature(ColorTemperature temperature);

    void resetGammaRamp();

    void reinitialize();

    double[][] getGammaRamp();

}

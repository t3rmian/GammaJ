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
import io.github.t3r1jj.gammaj.model.Gamma.Channel;

public abstract class AbstractDisplay implements Display {

    protected String name;
    protected WinDef.HDC hdc;
    protected Gamma gammaModel;

    protected AbstractDisplay() {
    }

    public AbstractDisplay(WinDef.HDC hdc) {
        this.hdc = hdc;
        this.gammaModel = new Gamma(hdc);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setGamma(Channel channel, double gamma) {
        gammaModel.setGamma(channel, gamma);;
    }

    @Override
    public void setBrightness(Channel channel, double brightness) {
        gammaModel.setBrightness(channel, brightness);
    }

    @Override
    public void setContrastBilateral(Channel channel, double contrast) {
        gammaModel.setContrastBilateral(channel, contrast);
    }

    @Override
    public void setContrastUnilateral(Channel channel, double contrast) {
        gammaModel.setContrastUnilateral(channel, contrast);
    }

    @Override
    public void setTemperature(ColorTemperature colorTemperature) {
        gammaModel.setTemperature(colorTemperature.getRgb());
    }

    @Override
    public void resetGammaRamp() {
        gammaModel.resetGammaRamp();
    }

    @Override
    public void invertGammaRamp(Channel channel) {
        gammaModel.invertGammaRamp(channel);
    }

    @Override
    public void reinitialize() {
        gammaModel.reinitializeGammaRamp();
    }

    @Override
    public double[][] getNormalizedGammaRamp() {
        return gammaModel.getNormalizedGammaRamp();
    }

    @Override
    public int[][] getGammaRamp() {
        return gammaModel.getGammaRamp();
    }
    
    @Override
    public boolean[] getInvertedChannels() {
        return gammaModel.getInvertedChannels();
    }

    @Override
    public String toString() {
        return name;
    }
}

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
    public WinDef.HDC getHdc() {
        return hdc;
    }

    @Override
    public void setGamma(Channel channel, double gamma) {
        gammaModel.gamma[channel.getIndex()] = gamma;
    }

    @Override
    public void setBrightness(Channel channel, double brightness) {
        gammaModel.brightness[channel.getIndex()] = brightness;
    }

    @Override
    public void setContrast(Channel channel, double contrast) {
        gammaModel.contrast[channel.getIndex()] = contrast;
    }

    @Override
    public void setContrastGain(Channel channel, double contrast) {
        gammaModel.contrastGain[channel.getIndex()] = contrast;
    }

    @Override
    public void setTemperature(ColorTemperature colorTemperature) {
        gammaModel.temperature = colorTemperature.getRgb();
    }

    @Override
    public void resetGammaRamp() {
        gammaModel.resetGammaRamp();
    }

    @Override
    public void reinitialize() {
        gammaModel.reinitializeGammaRamp();
    }

    @Override
    public double[][] getGammaRamp() {
        return gammaModel.getGammaScaledRamp();
    }

    @Override
    public String toString() {
        return name;
    }
}

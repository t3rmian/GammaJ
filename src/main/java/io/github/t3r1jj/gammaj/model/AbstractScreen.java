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

public abstract class AbstractScreen implements Screen {

    protected String name;
    protected WinDef.HDC hdc;
    protected Gamma gammaModel;

    protected AbstractScreen() {
    }

    public AbstractScreen(WinDef.HDC hdc) {
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
    public void setGamma(float gamma) {
        gammaModel.gamma = gamma;
    }

    @Override
    public void setBrightness(float brightness) {
        gammaModel.brightness = brightness;
    }

    @Override
    public void setContrast(float contrast) {
        gammaModel.contrast = contrast;
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
    public float[][] getGammaRamp() {
        return gammaModel.getGammaScaledRamp();
    }

    @Override
    public String toString() {
        return name;
    }
}

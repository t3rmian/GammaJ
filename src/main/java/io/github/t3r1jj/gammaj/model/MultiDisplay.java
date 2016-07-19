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

import io.github.t3r1jj.gammaj.model.temperature.RgbTemperature;
import com.sun.jna.platform.win32.WinDef.HDC;
import java.util.ArrayList;
import java.util.List;

public class MultiDisplay extends AbstractDisplay {

    private final List<Display> displays = new ArrayList<>();
    private final List<Display> detachedDisplays = new ArrayList<>();

    public MultiDisplay(HDC hdc) {
        super(hdc);
        name = "Whole screen";
    }

    public void addScreen(Display display) {
        displays.add(display);
    }

    public List<Display> getDisplays() {
        return displays;
    }

    public void detachDisplays() {
        detachedDisplays.addAll(displays);
        displays.clear();
    }

    public void attachDisplays() {
        displays.addAll(detachedDisplays);
        detachedDisplays.clear();
    }

    @Override
    public void resetGammaRamp() {
        super.resetGammaRamp();
        for (Display display : displays) {
            display.resetGammaRamp();
        }
    }

    @Override
    public void setContrastBilateral(Gamma.Channel channel, double contrast) {
        super.setContrastBilateral(channel, contrast);
        for (Display display : displays) {
            display.setContrastBilateral(channel, contrast);
        }
    }

    @Override
    public void setContrastUnilateral(Gamma.Channel channel, double contrastGain) {
        super.setContrastUnilateral(channel, contrastGain);
        for (Display display : displays) {
            display.setContrastUnilateral(channel, contrastGain);
        }
    }

    @Override
    public void setBrightness(Gamma.Channel channel, double brightness) {
        super.setBrightness(channel, brightness);
        for (Display display : displays) {
            display.setBrightness(channel, brightness);
        }
    }

    @Override
    public void setGamma(Gamma.Channel channel, double gamma) {
        super.setGamma(channel, gamma);
        for (Display display : displays) {
            display.setGamma(channel, gamma);
        }
    }

    @Override
    public void setTemperature(RgbTemperature temperature) {
        super.setTemperature(temperature);
        for (Display display : displays) {
            display.setTemperature(temperature);
        }
    }

    @Override
    public void setColorProfile(ColorProfile colorProfile) {
        super.setColorProfile(colorProfile);
        for (Display display : displays) {
            display.setColorProfile(colorProfile.clone(colorProfile.getName()));
        }
    }

    @Override
    public void loadModelFromProfile(boolean useRamp) {
        super.loadModelFromProfile(useRamp);
        for (Display display : displays) {
            display.setColorProfile(colorProfile.clone(colorProfile.getName()));
            display.loadModelFromProfile(useRamp);
        }
    }

    @Override
    public void reinitialize() {
        for (Display display : displays) {
            display.reinitialize();
        }
        super.reinitialize();
    }

    @Override
    public void setGammaRampValue(Gamma.Channel channel, int x, int value) {
        super.setGammaRampValue(channel, x, value);
        for (Display display : displays) {
            display.setGammaRampValue(channel, x, value);
        }
    }
    
    
    @Override
    public void setDeviceGammaRamp() {
        for (Display display : displays) {
            display.setDeviceGammaRamp();
        }
        super.setDeviceGammaRamp();
    }

}

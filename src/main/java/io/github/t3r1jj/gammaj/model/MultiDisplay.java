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

import com.sun.jna.platform.win32.WinDef.HDC;
import java.util.ArrayList;
import java.util.List;

public class MultiDisplay extends AbstractDisplay {

    private List<Display> screens = new ArrayList<>();

    public MultiDisplay(HDC hdc) {
        super(hdc);
        name = "Whole screen";
    }

    public void addScreen(Display screen) {
        screens.add(screen);
    }

    public List<Display> getDisplays() {
        return screens;
    }

    @Override
    public void resetGammaRamp() {
        super.resetGammaRamp();
        for (Display screen : screens) {
            screen.resetGammaRamp();
        }
    }

    @Override
    public void setContrastBilateral(Gamma.Channel channel, double contrast) {
        super.setContrastBilateral(channel, contrast);
        for (Display screen : screens) {
            screen.setContrastBilateral(channel, contrast);
        }
    }

    @Override
    public void setContrastUnilateral(Gamma.Channel channel, double contrastGain) {
        super.setContrastUnilateral(channel, contrastGain);
        for (Display screen : screens) {
            screen.setContrastUnilateral(channel, contrastGain);
        }
    }

    @Override
    public void setBrightness(Gamma.Channel channel, double brightness) {
        super.setBrightness(channel, brightness);
        for (Display screen : screens) {
            screen.setBrightness(channel, brightness);
        }
    }

    @Override
    public void setGamma(Gamma.Channel channel, double gamma) {
        super.setGamma(channel, gamma);
        for (Display screen : screens) {
            screen.setGamma(channel, gamma);
        }
    }

    @Override
    public void setTemperature(ColorTemperature temperature) {
        super.setTemperature(temperature);
        for (Display screen : screens) {
            screen.setTemperature(temperature);
        }
    }

}

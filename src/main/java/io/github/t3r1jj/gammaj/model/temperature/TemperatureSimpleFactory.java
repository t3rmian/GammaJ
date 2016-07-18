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
package io.github.t3r1jj.gammaj.model.temperature;

public class TemperatureSimpleFactory {

    private String type;
    private boolean isSrgb;
    
    public TemperatureSimpleFactory() {
    }
    
    public TemperatureSimpleFactory(String type) {
        this.type = type;
    }

    public TemperatureSimpleFactory(boolean isSrgb) {
        this.isSrgb = isSrgb;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIsSrgb(boolean isSrgb) {
        this.isSrgb = isSrgb;
        type = null;
    }

    public boolean isIsSrgb() {
        return isSrgb || "srgb".equals(type);
    }
    
    public RgbTemperature createTemperature(double temperature) {
        if (type != null) {
            switch (type) {
                case "rgb": return new RgbTemperature(temperature);
                case "srgb": return new SrgbTemperature(temperature);
                    default: return new RgbTemperature(temperature);
            }
        } else {
            if (isSrgb) {
                return new SrgbTemperature(temperature);
            } else {
                return new RgbTemperature(temperature);
            }
        }
    }
}

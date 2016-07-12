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

public class ColorTemperatureSrgb extends ColorTemperature {

    private static final double A = 0.055;
    private static final double GAMMA = 2.4;

    public ColorTemperatureSrgb(double temperature) {
        super(temperature);
        calculateSrgb();
    }

    private void calculateSrgb() {
        for (int i = 0; i < 3; i++) {
            rgb[i] = (rgb[i] <= 0.0031308) ? rgb[i] * 12.92 : 1.055 * Math.pow(rgb[i], 1d / GAMMA) - A;
        }
    }
}

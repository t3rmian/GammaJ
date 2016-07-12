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

public class ColorTemperature {

    protected final double rgb[] = new double[3];
    private final double temperature;
    private double xc;
    private double yc;
    private double y;
    private double x;
    private double z;
    private double u;
    private double v;

    public double[] getRgb() {
        return rgb;
    }
    
    public ColorTemperatureSrgb toSrgb() {
        return new ColorTemperatureSrgb(temperature);
    }

    /**
     *
     * @param temperature in Kelvin
     */
    public ColorTemperature(double temperature) {
        this.temperature = temperature;
        temperatureToXyy();
        XyyToXyz();
        XyzToRgb();
    }

    private void temperatureToXyy() {
        calculateXc();
        calculateYc();
    }

    private void calculateXc() {
        if (temperature < 1667) {
            temperatureToCie1960Ucs();
        } else if (temperature < 4000) {
            xc = -0.2661239 * (Math.pow(10, 9) / Math.pow(temperature, 3))
                    - 0.2343580 * (Math.pow(10, 6) / Math.pow(temperature, 2))
                    + 0.8776956 * (Math.pow(10, 3) / temperature)
                    + 0.179910;
        } else if (temperature <= 25000) {
            xc = -3.0258469 * (Math.pow(10, 9) / Math.pow(temperature, 3))
                    + 2.1070379 * (Math.pow(10, 6) / Math.pow(temperature, 2))
                    + 0.2226347 * (Math.pow(10, 3) / temperature)
                    + 0.240390;
        }
    }

    private void calculateYc() {
        if (temperature < 1667) {
            temperatureToCie1960Ucs();
        } else if (temperature < 2222) {
            yc = -1.1063814 * Math.pow(xc, 3)
                    - 1.34811020 * Math.pow(xc, 2)
                    + 2.18555832 * xc
                    - 0.20219683;
        } else if (temperature < 4000) {
            yc = -0.9549476 * Math.pow(xc, 3)
                    - 1.37418593 * Math.pow(xc, 2)
                    + 2.09137015 * xc
                    - 0.16748867;
        } else if (temperature <= 25000) {
            yc = 3.0817580 * Math.pow(xc, 3)
                    - 5.87338670 * Math.pow(xc, 2)
                    + 3.75112997 * xc
                    - 0.37001483;
        }
    }

    private void XyyToXyz() {
        y = (yc == 0) ? 0 : 1;
        x = (yc == 0) ? 0 : xc * y / yc;
        z = (yc == 0) ? 0 : ((1 - xc - yc) * y) / yc;
    }

    private void XyzToRgb() {
        calculateLinearRgb();
        scale();
    }

    private void calculateLinearRgb() {
        rgb[0] = 3.2406 * x - 1.5372 * y - 0.4986 * z;
        rgb[1] = -0.9689 * x + 1.8758 * y + 0.0415 * z;
        rgb[2] = 0.0557 * x - 0.2040 * y + 1.0570 * z;
    }

    private void scale() {
        double max = Math.max(rgb[0], Math.max(rgb[1], rgb[2]));
        if (max == 0) {
            for (int i = 0; i < rgb.length; i++) {
                rgb[i] = 0;
            }
        }
        for (int i = 0; i < rgb.length; i++) {
            rgb[i] /= max;
        }
    }

    private void temperatureToCie1960Ucs() {
        u = (0.860117757 + 1.54118254 * Math.pow(10, -4) * temperature + 1.28641212 * Math.pow(10, -7) * temperature * temperature)
                / (1 + 8.42420235 * Math.pow(10, -4) * temperature + 7.08145163 * Math.pow(10, -7) * temperature * temperature);
        v = (0.317398726 + 4.22806245 * Math.pow(10, -5) * temperature + 4.20481691 * Math.pow(10, -8) * temperature * temperature)
                / (1 - 2.89741816 * Math.pow(10, -5) * temperature + 1.61456053 * Math.pow(10, -7) * temperature * temperature);
        xc = 3d * u / (2d * u - 8d * v + 4d);
        yc = 2d * v / (2d * u - 8d * v + 4d);
    }

}

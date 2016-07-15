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

import io.github.t3r1jj.gammaj.jna.HotkeyPollerThread;
import io.github.t3r1jj.gammaj.model.Gamma.Channel;
import static io.github.t3r1jj.gammaj.model.Gamma.DEFAULT_BRIGHTNESS;
import static io.github.t3r1jj.gammaj.model.Gamma.DEFAULT_CONTRAST_BILATERAL;
import static io.github.t3r1jj.gammaj.model.Gamma.DEFAULT_CONTRAST_UNILATERAL;
import static io.github.t3r1jj.gammaj.model.Gamma.DEFAULT_GAMMA;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

    // ?? should I make default properties for app start?
public class ColorProfile {

    private final Properties properties = new Properties();
    private String name;
    private File file;

    public ColorProfile(String name) {
        this.name = name;

        for (Channel channel : Channel.values()) {
            setGamma(channel, DEFAULT_GAMMA);
            setBrightness(channel, DEFAULT_BRIGHTNESS);
            setContrastBilateral(channel, DEFAULT_CONTRAST_BILATERAL);
            setContrastUnilateral(channel, DEFAULT_CONTRAST_UNILATERAL);
            setTemperature(6500);
            setInvertedChannels(new boolean[]{false, false, false});
        }
    }

    /**
     *
     * @param file call loadProfile to initialize this object with data
     */
    public ColorProfile(File file) {
        this.file = file;
        this.name = file.getName().replaceFirst("[.][^.]+$", "");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getGamma(Channel channel) {
        return Double.parseDouble(properties.getProperty("gamma_" + channel.toString().toLowerCase()));
    }

    public void setGamma(Channel channel, double gamma) {
        properties.setProperty("gamma_" + channel.toString().toLowerCase(), String.valueOf(gamma));
    }

    public double getContrastUnilateral(Channel channel) {
        return Double.parseDouble(properties.getProperty("contrast_unilateral_" + channel.toString().toLowerCase()));
    }

    public void setContrastUnilateral(Channel channel, double contrastUnilateral) {
        properties.setProperty("contrast_unilateral_" + channel.toString().toLowerCase(), String.valueOf(contrastUnilateral));
    }

    public double getContrastBilateral(Channel channel) {
        return Double.parseDouble(properties.getProperty("contrast_bilateral_" + channel.toString().toLowerCase()));
    }

    public void setContrastBilateral(Channel channel, double contrastBilateral) {
        properties.setProperty("contrast_bilateral_" + channel.toString().toLowerCase(), String.valueOf(contrastBilateral));
    }

    public double getBrightness(Channel channel) {
        return Double.parseDouble(properties.getProperty("brightness_" + channel.toString().toLowerCase()));
    }

    public void setBrightness(Channel channel, double brightness) {
        properties.setProperty("brightness_" + channel.toString().toLowerCase(), String.valueOf(brightness));
    }

    public double getTemperature() {
        return Double.parseDouble(properties.getProperty("temperature"));
    }

    public void setTemperature(double temperature) {
        properties.setProperty("temperature", String.valueOf(temperature));
    }

    public boolean[] getInvertedChannels() {
        boolean[] invertedChannels = new boolean[Gamma.Channel.values().length];
        for (Gamma.Channel channel : Gamma.Channel.values()) {
            invertedChannels[channel.getIndex()] = Boolean.parseBoolean(properties.getProperty(channel.toString().toLowerCase() + "_inverted"));
        }
        return invertedChannels;
    }

    public void setInvertedChannels(boolean[] invertedChannels) {
        for (Gamma.Channel channel : Gamma.Channel.values()) {
            properties.setProperty(channel.toString().toLowerCase() + "_inverted", String.valueOf(invertedChannels[channel.getIndex()]));
        }
    }

    public int[][] getGammaRamp() {
        int[][] gammaRamp = new int[Gamma.TOTAL_COLORS_COUNT][Gamma.SINGLE_RAMP_CHANNEL_VALUES_COUNT];
        for (int y = 0; y < Gamma.TOTAL_COLORS_COUNT; y++) {
            int x = 0;
            for (String gammaRampValueString : properties.getProperty(Gamma.Channel.getChannel(y).toString().toLowerCase() + "_ramp")
                    .replace(" ", "").split(",")) {
                try {
                    gammaRamp[y][x++] = Integer.parseInt(gammaRampValueString);
                } catch (NumberFormatException ex) {
                    throw new GammaRampParsingException("Error while parsing gamma ramp value for"
                            + Gamma.Channel.getChannel(y).toString().toLowerCase()
                            + " channel, position: " + x + " (counting from 1), cannot parse string: \"" + gammaRampValueString + "\".");
                }
            }
        }
        return gammaRamp;
    }

    public void setGammaRamp(int[][] gammaRamp) {
        for (int y = 0; y < gammaRamp.length; y++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int x = 0; x < gammaRamp[y].length - 1; x++) {
                stringBuilder.append(gammaRamp[y][x]).append(",");
            }
            stringBuilder.append(gammaRamp[y][gammaRamp[y].length - 1]);
            properties.setProperty(Gamma.Channel.getChannel(y).toString().toLowerCase() + "_ramp", stringBuilder.toString());
        }
    }

    /**
     *
     * @return returns unregistered key or null if no key has been specified in
     * profile
     */
    public HotkeyPollerThread getHotkey() {
        String keyPrefix = "hotkey_";
        String keyCode = properties.getProperty(keyPrefix + "key_code");
        if (keyCode == null) {
            return null;
        }
        keyCode = keyCode.toUpperCase();
        boolean isShiftDown = Boolean.parseBoolean(properties.getProperty(keyPrefix + "is_shift_down", "false"));
        boolean isAltDown = Boolean.parseBoolean(properties.getProperty(keyPrefix + "is_alt_down", "false"));
        boolean isMetaDown = Boolean.parseBoolean(properties.getProperty(keyPrefix + "is_win_down", "false"));
        boolean isControlDown = Boolean.parseBoolean(properties.getProperty(keyPrefix + "is_ctrl_down", "false"));
        KeyEvent keyEvent = new KeyEvent(null, null, null, null, null, KeyCode.valueOf(keyCode), isShiftDown, isControlDown, isAltDown, isMetaDown);
        HotkeyPollerThread hotkey = new HotkeyPollerThread(keyEvent);
        return hotkey;
    }

    public void setHotkey(HotkeyPollerThread hotkey) {
        String keyPrefix = "hotkey_";
        KeyEvent keyEvent = hotkey.getKeyEvent();
        properties.setProperty(keyPrefix + "key_code", keyEvent.getCode().toString());
        properties.setProperty(keyPrefix + "is_alt_down", String.valueOf(keyEvent.isAltDown()));
        properties.setProperty(keyPrefix + "is_ctrl_down", String.valueOf(keyEvent.isControlDown()));
        properties.setProperty(keyPrefix + "is_win_down", String.valueOf(keyEvent.isMetaDown()));
        properties.setProperty(keyPrefix + "is_shift_down", String.valueOf(keyEvent.isShiftDown()));
    }

    public boolean getModeIsAssissted() {
        return Boolean.parseBoolean(properties.getProperty("mode"));
    }

    public void setModeIsAssissted(boolean isAssisted) {
        properties.setProperty("mode", String.valueOf(isAssisted));
    }

    public void saveProfile(String path) throws IOException {
        file = new File(path);
        properties.store(new FileOutputStream(file), name);
    }

    public void deleteProfile() {
        file.delete();
    }

    public void loadProfile() throws FileNotFoundException, IOException {
        properties.load(new FileInputStream(file));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ColorProfile other = (ColorProfile) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

    public static class GammaRampParsingException extends NumberFormatException {

        public GammaRampParsingException(String message) {
            super(message);
        }

    }
}

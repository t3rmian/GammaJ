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
package io.github.t3r1jj.gammaj;

import io.github.t3r1jj.gammaj.hotkeys.HotkeyPollerThread;
import io.github.t3r1jj.gammaj.info.ProjectInfo;
import io.github.t3r1jj.gammaj.model.Display;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Configuration {

    private static final String path = new ProjectInfo().getProjectName() + ".ini";
    private final Properties properties = new Properties();

    public boolean isTrayEnabled() {
        return Boolean.parseBoolean(properties.getProperty("tray_enabled", "false"));
    }

    public void setIsTrayEnabled(boolean isTrayEnabled) {
        properties.setProperty("tray_enabled", String.valueOf(isTrayEnabled));
    }

    public boolean isDisplaysDetached() {
        return Boolean.parseBoolean(properties.getProperty("displays_detached", "false"));
    }

    public void setIsDisplaysDetached(boolean isDisplaysDetached) {
        properties.setProperty("displays_detached", String.valueOf(isDisplaysDetached));
    }

    public boolean isColorResetOnExit() {
        return Boolean.parseBoolean(properties.getProperty("color_reset_on_exit", "true"));
    }

    public void setIsColorResetOnExit(boolean resetOnExit) {
        properties.setProperty("color_reset_on_exit", String.valueOf(resetOnExit));
    }

    public HotkeyPollerThread getHotkey() {
        String keyPrefix = "reset_hotkey_";
        boolean isHotkeySet = Boolean.parseBoolean(properties.getProperty(keyPrefix + "on"));
        if (!isHotkeySet) {
            return getDefaultResetHotkey();
        }
        String keyCode = properties.getProperty(keyPrefix + "key_code").toUpperCase();
        boolean isShiftDown = Boolean.parseBoolean(properties.getProperty(keyPrefix + "is_shift_down", "false"));
        boolean isAltDown = Boolean.parseBoolean(properties.getProperty(keyPrefix + "is_alt_down", "false"));
        boolean isMetaDown = Boolean.parseBoolean(properties.getProperty(keyPrefix + "is_win_down", "false"));
        boolean isControlDown = Boolean.parseBoolean(properties.getProperty(keyPrefix + "is_ctrl_down", "false"));
        KeyEvent keyEvent = new KeyEvent(null, null, null, null, null, KeyCode.valueOf(keyCode), isShiftDown, isControlDown, isAltDown, isMetaDown);
        try {
            return new HotkeyPollerThread(keyEvent);
        } catch (IllegalArgumentException ex) {
            return getDefaultResetHotkey();
        }
    }

    private HotkeyPollerThread getDefaultResetHotkey() {
        KeyEvent ctrlShiftR = new KeyEvent(null, null, null, null, null, KeyCode.R, true, true, false, false);
        return new HotkeyPollerThread(ctrlShiftR);
    }

    public void setHotkey(HotkeyPollerThread hotkey) {
        String keyPrefix = "reset_hotkey_";
        boolean isHotkeySet = hotkey != null;
        properties.setProperty(keyPrefix + "on", String.valueOf(hotkey != null));
        if (!isHotkeySet) {
            return;
        }
        KeyEvent keyEvent = hotkey.getKeyEvent();
        properties.setProperty(keyPrefix + "key_code", keyEvent.getCode().toString());
        properties.setProperty(keyPrefix + "is_alt_down", String.valueOf(keyEvent.isAltDown()));
        properties.setProperty(keyPrefix + "is_ctrl_down", String.valueOf(keyEvent.isControlDown()));
        properties.setProperty(keyPrefix + "is_win_down", String.valueOf(keyEvent.isMetaDown()));
        properties.setProperty(keyPrefix + "is_shift_down", String.valueOf(keyEvent.isShiftDown()));
    }

    public void setLoadCorrespondingProfiles(boolean load) {
        properties.setProperty("load_selected_profiles_on_start", String.valueOf(load));
    }

    public boolean getLoadCorrespondingProfiles() {
        return Boolean.parseBoolean(properties.getProperty("load_selected_profiles_on_start", "true"));
    }

    public void setCorrespondingProfiles(List<Display> displays) {
        for (Display display : displays) {
            properties.setProperty(display.getName().replace(" ", "_"), display.getColorProfile().getName());
        }
    }

    public Map<Display, String> getCorrespondingProfiles(List<Display> displays) {
        Map<Display, String> correspondingProfiles = new HashMap<>();
        for (Display display : displays) {
            correspondingProfiles.put(display, properties.getProperty(display.getName().replace(" ", "_"), ""));
        }
        return correspondingProfiles;
    }

    public void load() {
        try {
            properties.load(new FileInputStream(path));
        } catch (IOException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void save() {
        try {
            properties.store(new FileOutputStream(path), "GammaJ application configuration settings");
        } catch (IOException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

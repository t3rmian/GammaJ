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
package io.github.t3r1jj.gammaj.hotkeys;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class HotkeysRunner {

    private static final HotkeysRunner instance = new HotkeysRunner();
    private final List<HotkeyPollerThread> registeredHotkeys = new ArrayList<>();
    private final ObjectProperty<HotkeyPollerThread> applicationHotkey = new SimpleObjectProperty<>();

    private HotkeysRunner() {
    }

    public static HotkeysRunner getInstance() {
        return instance;
    }

    public HotkeyPollerThread getApplicationHotkey() {
        return applicationHotkey.get();
    }

    public void deregisterHotkey(HotkeyPollerThread hotkeyToDelete) {
        for (Iterator<HotkeyPollerThread> hotkeyIterator = registeredHotkeys.iterator(); hotkeyIterator.hasNext();) {
            HotkeyPollerThread hotkey = hotkeyIterator.next();
            if (hotkey.equals(hotkeyToDelete)) {
                hotkey.interrupt();
                hotkeyIterator.remove();
                return;
            }
        }
    }

    public boolean isRegisteredOnProfile(HotkeyPollerThread hotkey) {
        return registeredHotkeys.contains(hotkey);
    }

    public boolean isRegistered(HotkeyPollerThread hotkey) {
        return registeredHotkeys.contains(hotkey) || applicationHotkey.get().equals(hotkey);
    }

    public String registeredProfileInfo(HotkeyPollerThread hotkey) {
        for (HotkeyPollerThread registeredHotkey : registeredHotkeys) {
            if (registeredHotkey.equals(hotkey)) {
                return hotkey.getHotkeyListener().toString();
            }
        }
        return "";
    }

    /**
     * Starts hotkey poller. Same hotkey registration without previous
     * deregistration is ignored
     *
     * @param hotkey
     */
    public void registerHotkey(HotkeyPollerThread hotkey) {
        if (!isRegistered(hotkey)) {
            hotkey.start();
            registeredHotkeys.add(hotkey);
        }
    }

    public void reregisterApplicationHotkey(HotkeyPollerThread newHotkey) {
        if (applicationHotkey.get() != null) {
            applicationHotkey.get().interrupt();
        }
        newHotkey.start();
        applicationHotkey.set(newHotkey);
    }

    public ObjectProperty<HotkeyPollerThread> applicationHotkeyProperty() {
        return applicationHotkey;
    }

}

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

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class HotkeyInputEventHandler implements EventHandler<KeyEvent> {
    
    private final TextField source;
    private HotkeyPollerThread hotkey;
    
    public HotkeyInputEventHandler(TextField source) {
        this.source = source;
    }
    
    public HotkeyPollerThread getHotkey() {
        return hotkey;
    }
    
    public void setHotkey(HotkeyPollerThread hotkey) {
        this.hotkey = hotkey;
        if (hotkey != null) {
            source.setText(hotkey.getDisplayText());
        } else {
            source.setText("");
        }
    }
    
    public boolean isEmpty() {
        return hotkey == null;
    }
    
    public void clear() {
        hotkey = null;
    }
    
    @Override
    public void handle(KeyEvent event) {
        try {
            hotkey = new HotkeyPollerThread(event);
            source.setText("");
            source.setText(hotkey.getDisplayText());
        } catch (java.lang.IllegalArgumentException ex) {
            System.out.println("ILLEGAL ARGUMENT");
            hotkey = null;
        }
    }
    
}

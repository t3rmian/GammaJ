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
package io.github.t3r1jj.gammaj.jna;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import static com.sun.jna.platform.win32.WinUser.WM_HOTKEY;
import javafx.application.Platform;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.ModifierValue;
import javafx.scene.input.KeyEvent;

public class HotkeyPollerThread extends Thread {

    private final int id = IdGenerator.nextId();
    private final KeyEvent keyEvent;
    private final KeyCombination keyCombination;
    private int modifiers;
    private HotkeyListener hotkeyListener;

    /**
     * 
     * @param keyEvent 
     * Start thread to register global hotkey in windows, call stop() to unregister hotkey
     */
    public HotkeyPollerThread(KeyEvent keyEvent) {
        this.keyEvent = keyEvent;
        ModifierValue[] modifierValues = new ModifierValue[5];
        modifierValues[0] = (keyEvent.isShiftDown()) ? ModifierValue.DOWN : ModifierValue.ANY;
        modifierValues[1] = (keyEvent.isControlDown()) ? ModifierValue.DOWN : ModifierValue.ANY;
        modifierValues[2] = (keyEvent.isAltDown()) ? ModifierValue.DOWN : ModifierValue.ANY;
        modifierValues[3] = (keyEvent.isMetaDown()) ? ModifierValue.DOWN : ModifierValue.ANY;
        modifierValues[4] = (keyEvent.isShortcutDown()) ? ModifierValue.DOWN : ModifierValue.ANY;
        keyCombination = new KeyCodeCombination(keyEvent.getCode(), modifierValues[0], modifierValues[1], modifierValues[2], modifierValues[3], modifierValues[4]);
        if (keyEvent.isAltDown()) {
            modifiers |= WinUser.MOD_ALT;
        }
        if (keyEvent.isControlDown()) {
            modifiers |= WinUser.MOD_CONTROL;
        }
        if (keyEvent.isShiftDown()) {
            modifiers |= WinUser.MOD_SHIFT;
        }
        if (keyEvent.isMetaDown()) {
            modifiers |= WinUser.MOD_WIN;
        }
        modifiers |= WinUser.MOD_NOREPEAT;
    }

    public void setHotkeyListener(HotkeyListener hotkeyListener) {
        this.hotkeyListener = hotkeyListener;
    }

    public String getDisplayText() {
        return keyCombination.getDisplayText();
    }

    public int getVkCode() {
        return keyEvent.getCode().impl_getCode();
    }

    public int getModifiers() {
        return modifiers;
    }
    
    public KeyEvent getKeyEvent() {
        return keyEvent;
    }

    @Override
    public void run() {
        System.out.println("Starting run()");
        registerHotkey();
        WinUser.MSG msg = new WinUser.MSG();
        while (User32.INSTANCE.GetMessage(msg, null, 0, 0) != 0) {
            if (msg.message == WM_HOTKEY && msg.wParam.intValue() == id) {
                System.out.println("Global listener caught: " + this);
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        hotkeyListener.hotkeyPressed();
                    }
                });
            }
        }
    }

    private void registerHotkey() {
        System.out.println("REGISTERING HOTKEY: " + this);
        User32.INSTANCE.RegisterHotKey(null, id, getModifiers(), getVkCode());
    }

    public void unregisterHotkay() {
        User32.INSTANCE.UnregisterHotKey(null, id);
    }

    private static class IdGenerator {

        private static int id = 0;

        public static int nextId() {
            return ++id;
        }
    }

    @Override
    public String toString() {
        return "Hotkey{" + getDisplayText() + '}';
    }

}

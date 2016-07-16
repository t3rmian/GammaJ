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

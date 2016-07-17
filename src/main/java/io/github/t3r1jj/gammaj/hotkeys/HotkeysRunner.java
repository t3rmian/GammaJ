package io.github.t3r1jj.gammaj.hotkeys;

import io.github.t3r1jj.gammaj.hotkeys.HotkeyPollerThread;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class HotkeysRunner {

    private static final HotkeysRunner instance = new HotkeysRunner();
    private final List<HotkeyPollerThread> registeredHotkeys = new ArrayList<>();

    private HotkeysRunner() {
    }

    public static HotkeysRunner getInstance() {
        return instance;
    }

    public HotkeyPollerThread getApplicationHotkey() {
        return registeredHotkeys.get(0);
    }

    public void deregisterHotkey(HotkeyPollerThread hotkeyToDelete) {
        for (Iterator<HotkeyPollerThread> hotkeyIterator = registeredHotkeys.iterator(); hotkeyIterator.hasNext();) {
            HotkeyPollerThread hotkey = hotkeyIterator.next();
            if (hotkey.equals(hotkeyToDelete)) {
                System.out.println("REMOVED " + hotkey);
                hotkey.interrupt();
                hotkeyIterator.remove();
                return;
            }
        }
    }

    public boolean isRegisteredOnProfile(HotkeyPollerThread hotkey) {
        return registeredHotkeys.indexOf(hotkey) > 0;
    }

    public boolean isRegistered(HotkeyPollerThread hotkey) {
        return registeredHotkeys.contains(hotkey);
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
            System.out.println("ADDED: " + hotkey.getDisplayText());
        }
    }

    public void reregisterApplicationHotkey(HotkeyPollerThread newHotkey) {
        deregisterHotkey(registeredHotkeys.get(0));
        newHotkey.start();
        registeredHotkeys.add(0, newHotkey);
        System.out.println("READDED: " + newHotkey);
    }

}

package io.github.t3r1jj.gammaj.hotkeys;

import io.github.t3r1jj.gammaj.model.ColorProfile;
import javafx.beans.property.ObjectProperty;

public class ProfileHotkeyListener implements HotkeyListener{
    
    private final ObjectProperty<ColorProfile> currentProfile;
    private final ColorProfile colorProfile;

    public ProfileHotkeyListener(ObjectProperty<ColorProfile> currentProfile, ColorProfile colorProfile) {
        this.currentProfile = currentProfile;
        this.colorProfile = colorProfile;
    }

    @Override
    public void hotkeyPressed() {
        currentProfile.set(colorProfile);
    }

    @Override
    public String toString() {
        return colorProfile.toString();
    }
    
}

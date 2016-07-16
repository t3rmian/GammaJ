package io.github.t3r1jj.gammaj.hotkeys;

import io.github.t3r1jj.gammaj.hotkeys.HotkeyListener;
import io.github.t3r1jj.gammaj.model.ColorProfile;
import javafx.scene.control.ComboBox;

public class ProfileHotkeyListener implements HotkeyListener{
    
    private final ComboBox profilesComboBox;
    private final ColorProfile colorProfile;

    public ProfileHotkeyListener(ComboBox profilesComboBox, ColorProfile colorProfile) {
        this.profilesComboBox = profilesComboBox;
        this.colorProfile = colorProfile;
    }

    @Override
    public void hotkeyPressed() {
        profilesComboBox.getSelectionModel().select(colorProfile);
    }

    @Override
    public String toString() {
        return colorProfile.toString();
    }
    
}

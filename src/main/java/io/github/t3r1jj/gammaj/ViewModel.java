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

import com.sun.javafx.collections.ObservableListWrapper;
import io.github.t3r1jj.gammaj.Configuration;
import io.github.t3r1jj.gammaj.controllers.SceneController;
import io.github.t3r1jj.gammaj.hotkeys.HotkeyListener;
import io.github.t3r1jj.gammaj.hotkeys.HotkeyPollerThread;
import io.github.t3r1jj.gammaj.hotkeys.HotkeysRunner;
import io.github.t3r1jj.gammaj.hotkeys.ProfileHotkeyListener;
import io.github.t3r1jj.gammaj.model.ColorProfile;
import io.github.t3r1jj.gammaj.model.Display;
import io.github.t3r1jj.gammaj.model.DisplayUtil;
import io.github.t3r1jj.gammaj.model.Gamma;
import io.github.t3r1jj.gammaj.model.MultiDisplay;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.control.Alert;
import javafx.stage.StageStyle;

public class ViewModel {

    private static final ViewModel instance = new ViewModel();
    private final ListProperty<ColorProfile> loadedProfiles = new SimpleListProperty<>(new ObservableListWrapper<>(new ArrayList<ColorProfile>()));
    private final List<Display> displays = new ArrayList<>();
    private final SetProperty<Gamma.Channel> selectedChannels = new SimpleSetProperty();
    private final HotkeysRunner hotkeysRunner = HotkeysRunner.getInstance();
    private final ObjectProperty<Display> currentDisplay = new SimpleObjectProperty<>();
    private final ObjectProperty<ColorProfile> currentProfile = new SimpleObjectProperty<>();
    private final BooleanProperty assistedAdjustment = new SimpleBooleanProperty(true);
    private final BooleanProperty detachDisplay = new SimpleBooleanProperty(false);
    private final BooleanProperty reset = new SimpleBooleanProperty(true);
    private final BooleanProperty isSrgb = new SimpleBooleanProperty(false);
    private final Configuration configuration = new Configuration();

    private ViewModel() {
        configuration.load();
        selectedChannels.set(FXCollections.observableSet(Gamma.Channel.values()));
        loadFileProfiles();
        registerHotkeys();

        DisplayUtil screenUtil = new DisplayUtil();
        final MultiDisplay multiDisplay = screenUtil.getMultiDisplay();
        List<Display> singleDisplays = multiDisplay.getDisplays();
        if (singleDisplays.size() == 1) {
            displays.addAll(singleDisplays);
        } else {
            displays.addAll(singleDisplays);
            displays.add(multiDisplay);
        }
        detachDisplay.addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean nowDetach) {
                if (nowDetach) {
                    multiDisplay.detachDisplays();
                } else {
                    multiDisplay.attachDisplays();
                }
                configuration.setIsDisplaysDetached(nowDetach);
                configuration.save();
            }
        });
        currentDisplay.set(singleDisplays.get(0));
        if (configuration.isDisplaysDetached()) {
            detachDisplay.set(true);
        }
        if (configuration.getLoadCorrespongingProfiles()) {
            for (HashMap.Entry<Display, String> displayProfileEntry : configuration.getCorrespondingProfiles(displays).entrySet()) {
                String profileName = displayProfileEntry.getValue();
                if ("".equals(profileName)) {
                    continue;
                }
                Display display = displayProfileEntry.getKey();
                for (ColorProfile profile : loadedProfiles) {
                    if (profile.getName().equals(profileName)) {
                        display.setColorProfile(profile.clone(profileName));
                    }
                }
            }
        }
        assistedAdjustment.set(currentDisplay.get().getColorProfile().getModeIsAssissted());
    }

    public static ViewModel getInstance() {
        return instance;
    }

    private void loadFileProfiles() {
        File[] colorProfileProperties = new File(".").listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.getAbsolutePath().endsWith(".properties");
            }
        });

        StringBuilder errorBuilder = new StringBuilder();
        for (File file : colorProfileProperties) {
            ColorProfile colorProfile = new ColorProfile(file);
            try {
                colorProfile.loadProfile();
                loadedProfiles.getValue().add(colorProfile);
            } catch (IOException ex) {
                Logger.getLogger(SceneController.class.getName()).log(Level.SEVERE, null, ex);
                errorBuilder.append("\"").append(colorProfile).append("\", ");
            }
        }
        currentProfile.set(null);
        if (errorBuilder.length() > 0) {
            errorBuilder.delete(errorBuilder.length() - 2, errorBuilder.length());
            errorBuilder.append(".");
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.initStyle(StageStyle.UTILITY);
            errorAlert.setTitle("Profile loading error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Could not load color profiles: " + errorBuilder.toString());
            errorAlert.showAndWait();
        }
    }

    private void registerHotkeys() {
        registerResetHotkey();
        registerProfileHotkeys();
    }

    private void registerResetHotkey() {
        HotkeyPollerThread resetHotkey = configuration.getHotkey();
        resetHotkey.setHotkeyListener(new HotkeyListener() {

            @Override
            public void hotkeyPressed() {
                reset.set(!reset.getValue());
            }

            @Override
            public String toString() {
                return "Reset (Application hotkey)";
            }

        });
        hotkeysRunner.reregisterApplicationHotkey(resetHotkey);
    }

    private void registerProfileHotkeys() {
        StringBuilder errorBuilder = new StringBuilder();
        for (ColorProfile colorProfile : loadedProfiles) {
            HotkeyPollerThread loadedHotkey = null;
            try {
                loadedHotkey = colorProfile.getHotkey();
                if (loadedHotkey != null && !hotkeysRunner.isRegistered(loadedHotkey)) {
                    loadedHotkey.setHotkeyListener(new ProfileHotkeyListener(currentProfile, colorProfile));
                    hotkeysRunner.registerHotkey(loadedHotkey);
                } else {
                    colorProfile.setHotkey(null);
                }
            } catch (Exception ex) {
                Logger.getLogger(SceneController.class.getName()).log(Level.SEVERE, null, ex);
                errorBuilder.append("\"").append(colorProfile).append("\", ");
            }
        }
        if (errorBuilder.length() > 0) {
            errorBuilder.delete(errorBuilder.length() - 2, errorBuilder.length());
            errorBuilder.append(".");
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.initStyle(StageStyle.UTILITY);
            errorAlert.setTitle("Hotkey registration error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Could not register hotkey for profiles: " + errorBuilder.toString());
            errorAlert.showAndWait();
        }
    }

    public void saveAndReset() {
        configuration.setCorrespondingProfiles(displays);
        configuration.save();
        if (configuration.isColorResetOnExit()) {
            for (Display display : displays) {
                display.resetGammaRamp();
            }
        }
    }

    public ListProperty<ColorProfile> loadedProfilesProperty() {
        return loadedProfiles;
    }

    public SetProperty<Gamma.Channel> selectedChannelsProperty() {
        return selectedChannels;
    }

    public HotkeysRunner getHotkeysRunner() {
        return hotkeysRunner;
    }

    public List<Display> getDisplays() {
        return displays;
    }

    public ObjectProperty<Display> currentDisplayProperty() {
        return currentDisplay;
    }

    public Display getCurrentDisplay() {
        return currentDisplay.get();
    }

    public void setCurrentDisplay(Display display) {
        this.currentDisplay.set(display);
    }

    public ObjectProperty<ColorProfile> currentProfileProperty() {
        return currentProfile;
    }

    public ColorProfile getCurrentProfile() {
        return currentProfile.get();
    }

    public void setCurrentProfile(ColorProfile colorProfile) {
        this.currentProfile.set(colorProfile);
    }

    public BooleanProperty assistedAdjustmentProperty() {
        return assistedAdjustment;
    }

    public boolean isAssistedAdjustment() {
        return assistedAdjustment.get();
    }

    public void setAssistedAdjustment(boolean isAssistedAdjustment) {
        this.assistedAdjustment.set(isAssistedAdjustment);
    }

    public BooleanProperty detachDisplayProperty() {
        return detachDisplay;
    }

    public boolean isDetachDisplay() {
        return detachDisplay.get();
    }

    public void setDetachDisplay(boolean isDetachDisplay) {
        this.detachDisplay.set(isDetachDisplay);
    }

    public BooleanProperty resetProperty() {
        return reset;
    }

    public boolean isReset() {
        return reset.get();
    }

    public void setReset(boolean isReset) {
        this.reset.set(isReset);
    }

    public BooleanProperty isSrgbProperty() {
        return isSrgb;
    }

    public boolean isSrgb() {
        return isSrgb.get();
    }

    public void setSrgb(boolean isSrgb) {
        this.isSrgb.set(isSrgb);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public ObservableList<ColorProfile> getLoadedProfiles() {
        return loadedProfiles.get();
    }

    public ObservableSet<Gamma.Channel> getSelectedChannels() {
        return selectedChannels.get();
    }

}

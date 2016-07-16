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
package io.github.t3r1jj.gammaj.controllers;

import io.github.t3r1jj.gammaj.hotkeys.HotkeyPollerThread;
import io.github.t3r1jj.gammaj.model.ColorProfile;
import io.github.t3r1jj.gammaj.model.Display;
import io.github.t3r1jj.gammaj.model.Gamma;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

public class ManualTabController implements Initializable {

    @FXML
    private Canvas canvas;
    @FXML
    private ComboBox<Display> screenComboBox;
    @FXML
    private ComboBox<ColorProfile> profilesComboBox;
    @FXML
    private TextField hotkeyTextField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

        @FXML
    private void handleResetButtonAction(ActionEvent event) {
        System.out.println("Reset button clicked!");
//        resetProfile();
//        resetSliders();
//        currentDisplay.resetGammaRamp();
//        drawGammaLine();
    }

    @FXML
    private void handleInvertButtonAction(ActionEvent event) {
//        resetProfile();
//        for (Gamma.Channel channel : selectedChannels) {
//            currentDisplay.invertGammaRamp(channel);
//        }
//        currentDisplay.reinitialize();
//        drawGammaLine();
    }

    @FXML
    private void handleSaveProfileAsButtonAction(ActionEvent event) throws IOException, InterruptedException {
//        TextInputDialog nameInputDialog = new TextInputDialog();
//        nameInputDialog.setTitle("Save as");
//        nameInputDialog.setHeaderText("Color profile");
//        nameInputDialog.setContentText("File name");
//        Optional<String> nameWrapper = nameInputDialog.showAndWait();
//        if (nameWrapper.isPresent()) {
//            String fullName = nameWrapper.get() + ".properties";
//            if (new File(fullName).exists() && !userWantsProfileOverwrite(nameWrapper.get())) {
//                return;
//            }
//            ColorProfile newColorProfile = currentDisplay.getColorProfile().cloneOrSame(nameWrapper.get());
//            System.out.println(newColorProfile.getGamma(Gamma.Channel.RED));
//            newColorProfile.setModeIsAssissted(true);
//            newColorProfile.setGammaRamp(currentDisplay.getGammaRamp());
//            if (newColorProfile.equals(currentDisplay.getColorProfile())) {
//                HotkeyPollerThread oldHotkey = currentDisplay.getColorProfile().getHotkey();
//                if (hotkeyInput.isEmpty() || !hotkeyInput.getHotkey().equals(oldHotkey)) {
//                    hotkeysRunner.deregisterHotkey(oldHotkey);
//                }
//                registerInputHotkey(newColorProfile);
//            } else {
//                registerInputHotkey(newColorProfile);
//                loadedProfiles.add(newColorProfile);
//                profilesComboBox.getItems().add(newColorProfile);
//                profilesComboBox.getSelectionModel().select(profilesComboBox.getItems().size() - 1);
//            }
//            newColorProfile.saveProfile(fullName);
//        }
    }
    
        @FXML
    public void handleRedSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        if (isNowSelected) {
//            selectedChannels.clear();
//            selectedChannels.add(Gamma.Channel.RED);
//            loadLocalProfile();
        }
    }

    @FXML
    public void handleGreenSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        if (isNowSelected) {
//            selectedChannels.clear();
//            selectedChannels.add(Gamma.Channel.GREEN);
//            loadLocalProfile();
        }
    }

    @FXML
    public void handleBlueSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        if (isNowSelected) {
//            selectedChannels.clear();
//            selectedChannels.add(Gamma.Channel.BLUE);
//            loadLocalProfile();
        }
    }

    @FXML
    public void handleRgbSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        if (isNowSelected) {
//            selectedChannels.clear();
//            selectedChannels.add(Gamma.Channel.RED);
//            selectedChannels.add(Gamma.Channel.GREEN);
//            selectedChannels.add(Gamma.Channel.BLUE);
//            loadLocalProfile();
        }
    }
    
      @FXML
    private void handleDeleteProfileButtonAction(ActionEvent event) {
//        ChoiceDialog choiceDialog = new ChoiceDialog(profilesComboBox.getSelectionModel().getSelectedItem(), loadedProfiles);
//        choiceDialog.setTitle("Delete color profile");
//        choiceDialog.setHeaderText(null);
//        choiceDialog.setContentText("Select color profile to delete");
//        Optional<ColorProfile> selectedProfile = choiceDialog.showAndWait();
//        if (selectedProfile.isPresent()) {
//            ColorProfile profileToDelete = selectedProfile.get();
//            hotkeysRunner.deregisterHotkey(profileToDelete.getHotkey());
//            removeProfileFromApp(profileToDelete);
//            profileToDelete.deleteProfile();
//        }
    }
    
}

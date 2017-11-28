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

import io.github.t3r1jj.gammaj.ViewModel;
import io.github.t3r1jj.gammaj.hotkeys.HotkeyInputEventHandler;
import io.github.t3r1jj.gammaj.hotkeys.HotkeyPollerThread;
import io.github.t3r1jj.gammaj.hotkeys.ProfileHotkeyListener;
import io.github.t3r1jj.gammaj.model.ColorProfile;
import io.github.t3r1jj.gammaj.model.Display;
import io.github.t3r1jj.gammaj.model.Gamma;
import io.github.t3r1jj.gammaj.model.temperature.TemperatureSimpleFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public abstract class AbstractTabController implements Initializable {

    public static final Paint GAMMA_CANVAS_BACKGROUND_COLOR = Color.WHITE;
    public static final Paint[] GAMMA_CANVAS_LINE_COLOR = new Paint[]{Color.RED, Color.GREEN, Color.BLUE};

    protected ViewModel viewModel;
    protected HotkeyInputEventHandler hotkeyInput;
    protected final TemperatureSimpleFactory temperatureFactory = new TemperatureSimpleFactory();
    protected boolean loadingProfile;
    protected ChangeListener<Boolean> resetListener;

    @FXML
    protected Canvas canvas;
    @FXML
    protected Button resetButton;
    @FXML
    protected ComboBox<Display> displaysComboBox;
    protected ChangeListener<Display> displayChangeListener;
    @FXML
    protected ComboBox<ColorProfile> profilesComboBox;
    protected ChangeListener<ColorProfile> profileChangeListener;
    @FXML
    protected TextField hotkeyTextField;
    @FXML
    protected RadioButton redRadioButton;
    @FXML
    protected RadioButton greenRadioButton;
    @FXML
    protected RadioButton blueRadioButton;
    @FXML
    protected RadioButton rgbRadioButton;
    protected ResourceBundle resources;


    AbstractTabController(ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        hotkeyInput = new HotkeyInputEventHandler(hotkeyTextField);
        hotkeyTextField.setOnKeyPressed(hotkeyInput);

        profilesComboBox.itemsProperty().set(viewModel.loadedProfilesProperty());
        profilesComboBox.valueProperty().bindBidirectional(viewModel.currentProfileProperty());

        displaysComboBox.getItems().setAll(viewModel.getDisplays());
        displaysComboBox.valueProperty().bindBidirectional(viewModel.currentDisplayProperty());
        initializeTabListeners();
        bindTabListeners();
        resetButton.setText("Reset (" + viewModel.getHotkeysRunner().applicationHotkeyProperty().get().getDisplayText() + ")");
        viewModel.getHotkeysRunner().applicationHotkeyProperty().addListener((observable, oldValue, newHotkey) ->
                resetButton.setText("Reset (" + newHotkey.getDisplayText() + ")"));
    }

    private void initializeTabListeners() {
        profileChangeListener = (observable, oldValue, selectedColorProfile) -> {
            if (selectedColorProfile == null) {
                hotkeyTextField.setText("");
                return;
            }
            if (!viewModel.loadedProfilesProperty().contains(selectedColorProfile)) {
                profilesComboBox.getSelectionModel().select(null);
                handleLoadLocalProfile();
                return;
            }
            viewModel.getCurrentDisplay().setColorProfile(selectedColorProfile);
            handleLoadLocalProfile();
        };

        displayChangeListener = (observable, oldValue, selectedDisplay) -> profilesComboBox.getSelectionModel().select(selectedDisplay.getColorProfile());
        resetListener = (observable, oldValue, newValue) -> handleResetButtonAction(null);
    }

    protected void addTabListeners() {
        profilesComboBox.getSelectionModel().selectedItemProperty().addListener(profileChangeListener);
        displaysComboBox.getSelectionModel().selectedItemProperty().addListener(displayChangeListener);
        viewModel.resetProperty().addListener(resetListener);
    }

    protected void removeTabListeners() {
        profilesComboBox.getSelectionModel().selectedItemProperty().removeListener(profileChangeListener);
        displaysComboBox.getSelectionModel().selectedItemProperty().removeListener(displayChangeListener);
        viewModel.resetProperty().removeListener(resetListener);
    }

    @FXML
    protected void handleResetButtonAction(ActionEvent event) {
        resetProfile();
        viewModel.getCurrentDisplay().resetGammaRamp();
        resetColorAdjustment();
        drawGammaRamp();
    }

    @FXML
    protected void handleSaveProfileAsButtonAction(ActionEvent event) throws IOException, InterruptedException {
        TextInputDialog nameInputDialog = new TextInputDialog();
        nameInputDialog.initOwner(canvas.getScene().getWindow());
        nameInputDialog.setTitle(resources.getString("save_as"));
        nameInputDialog.setHeaderText(resources.getString("color_profile"));
        nameInputDialog.setContentText(resources.getString("file_name"));
        Optional<String> nameWrapper = nameInputDialog.showAndWait();
        if (nameWrapper.isPresent()) {
            String fullName = nameWrapper.get() + ".properties";
            if (new File(fullName).exists() && !userWantsProfileOverwrite()) {
                handleSaveProfileAsButtonAction(event);
                return;
            }
            Display currentDisplay = viewModel.getCurrentDisplay();
            ColorProfile newColorProfile = currentDisplay.getColorProfile().cloneOrSame(nameWrapper.get());
            saveModeSettings(newColorProfile);
            newColorProfile.setGammaRamp(currentDisplay.getGammaRamp());
            if (newColorProfile.equals(currentDisplay.getColorProfile())) {
                HotkeyPollerThread oldHotkey = currentDisplay.getColorProfile().getHotkey();
                if (hotkeyInput.isEmpty() || !hotkeyInput.getHotkey().equals(oldHotkey)) {
                    viewModel.getHotkeysRunner().deregisterHotkey(oldHotkey);
                }
                registerInputHotkey(newColorProfile);
            } else {
                registerInputHotkey(newColorProfile);
                viewModel.loadedProfilesProperty().add(newColorProfile);
                profilesComboBox.getSelectionModel().select(profilesComboBox.getItems().size() - 1);
            }
            newColorProfile.saveProfile(fullName);
        }
    }

    protected void registerInputHotkey(ColorProfile newColorProfile) {
        if (!hotkeyInput.isEmpty()) {
            HotkeyPollerThread hotkey = hotkeyInput.getHotkey();
            if (!viewModel.getHotkeysRunner().isRegistered(hotkey)) {
                newColorProfile.setHotkey(hotkey);
                hotkey.setHotkeyListener(new ProfileHotkeyListener(viewModel.currentProfileProperty(), newColorProfile));
                viewModel.getHotkeysRunner().registerHotkey(hotkey);
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.initOwner(canvas.getScene().getWindow());
                errorAlert.setTitle(resources.getString("hotkey_not_registered"));
                errorAlert.setHeaderText(null);
                errorAlert.setContentText(resources.getString("hotkey") + " \"" + hotkey.getDisplayText()
                        + "\" " + resources.getString("hotkey_not_registered_info") + " \""
                        + viewModel.getHotkeysRunner().registeredProfileInfo(hotkey) + "\"");
                errorAlert.showAndWait();
            }
        }
    }

    @FXML
    protected void handleRedSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        if (isNowSelected) {
            viewModel.selectedChannelsProperty().clear();
            viewModel.selectedChannelsProperty().add(Gamma.Channel.RED);
        }
    }

    @FXML
    protected void handleGreenSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        if (isNowSelected) {
            viewModel.selectedChannelsProperty().clear();
            viewModel.selectedChannelsProperty().add(Gamma.Channel.GREEN);
        }
    }

    @FXML
    protected void handleBlueSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        if (isNowSelected) {
            viewModel.selectedChannelsProperty().clear();
            viewModel.selectedChannelsProperty().add(Gamma.Channel.BLUE);
        }
    }

    @FXML
    protected void handleRgbSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        if (isNowSelected) {
            viewModel.selectedChannelsProperty().clear();
            viewModel.selectedChannelsProperty().add(Gamma.Channel.RED);
            viewModel.selectedChannelsProperty().add(Gamma.Channel.GREEN);
            viewModel.selectedChannelsProperty().add(Gamma.Channel.BLUE);
        }
    }

    protected void updateRgbRadioButtons() {
        if (viewModel.selectedChannelsProperty().get().size() == 3) {
            rgbRadioButton.setSelected(true);
        } else {
            Gamma.Channel channel = viewModel.selectedChannelsProperty().get().iterator().next();
            switch (channel) {
                case RED:
                    redRadioButton.setSelected(true);
                    break;
                case GREEN:
                    greenRadioButton.setSelected(true);
                    break;
                case BLUE:
                    blueRadioButton.setSelected(true);
                    break;
                default:
                    rgbRadioButton.setSelected(true);
            }
        }
    }

    protected boolean userWantsProfileOverwrite() throws IOException {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.initOwner(canvas.getScene().getWindow());
        confirmationAlert.setTitle(resources.getString("color_profile_exists"));
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText(resources.getString("color_profile_exists_remove"));
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    protected void removeProfileFromApp(ColorProfile profileToRemove) {
        viewModel.loadedProfilesProperty().remove(profileToRemove);
        profilesComboBox.getItems().remove(profileToRemove);
    }

    @FXML
    protected void handleDeleteProfileButtonAction(ActionEvent event) {
        ChoiceDialog choiceDialog = new ChoiceDialog(profilesComboBox.getSelectionModel().getSelectedItem(), viewModel.getLoadedProfiles());
        choiceDialog.initOwner(canvas.getScene().getWindow());
        choiceDialog.setTitle("Delete color profile");
        choiceDialog.setHeaderText(null);
        choiceDialog.setContentText("Select color profile to delete");
        Optional<ColorProfile> selectedProfile = choiceDialog.showAndWait();
        if (selectedProfile.isPresent()) {
            ColorProfile profileToDelete = selectedProfile.get();
            viewModel.getHotkeysRunner().deregisterHotkey(profileToDelete.getHotkey());
            removeProfileFromApp(profileToDelete);
            profileToDelete.deleteProfile();
        }
    }

    protected void resetProfile() {
        if (!loadingProfile && profilesComboBox.getSelectionModel().getSelectedItem() != null) {
            viewModel.getCurrentDisplay().setColorProfile(viewModel.getCurrentDisplay().getColorProfile().cloneOrSame(""));
            profilesComboBox.getSelectionModel().select(null);
            hotkeyInput.setHotkey(null);
        }
    }

    protected boolean isCurrentProfileDefault() {
        return "".equals(viewModel.getCurrentDisplay().getColorProfile().getName());
    }

    protected void drawGammaRamp() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(GAMMA_CANVAS_BACKGROUND_COLOR);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        graphicsContext.setLineWidth(1);
        double[][] gammaRamp = viewModel.getCurrentDisplay().getNormalizedGammaRamp();
        for (int i = 0; i < gammaRamp.length; i++) {
            graphicsContext.setStroke(GAMMA_CANVAS_LINE_COLOR[i]);
            graphicsContext.strokeLine(0, (1 - gammaRamp[i][0]) * canvas.getWidth(), 0, (1 - gammaRamp[i][0]) * canvas.getWidth());
            for (int x = 1; x < canvas.getWidth(); x++) {
                graphicsContext.strokeLine(x - 1, (1 - gammaRamp[i][x - 1]) * canvas.getWidth(), x, (1 - gammaRamp[i][x]) * canvas.getWidth());
            }
        }
    }

    @FXML
    protected abstract void handleInvertButtonAction(ActionEvent event);

    protected abstract void handleLoadLocalProfile();

    protected abstract void resetColorAdjustment();

    protected abstract void bindTabListeners();

    protected abstract void saveModeSettings(ColorProfile newColorProfile);

}

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

import static io.github.t3r1jj.gammaj.controllers.SceneController.GAMMA_CANVAS_BACKGROUND_COLOR;
import static io.github.t3r1jj.gammaj.controllers.SceneController.GAMMA_CANVAS_LINE_COLOR;
import io.github.t3r1jj.gammaj.hotkeys.HotkeyInputEventHandler;
import io.github.t3r1jj.gammaj.hotkeys.HotkeyPollerThread;
import io.github.t3r1jj.gammaj.hotkeys.HotkeysRunner;
import io.github.t3r1jj.gammaj.hotkeys.ProfileHotkeyListener;
import io.github.t3r1jj.gammaj.model.ColorProfile;
import io.github.t3r1jj.gammaj.model.Display;
import io.github.t3r1jj.gammaj.model.DisplayUtil;
import io.github.t3r1jj.gammaj.model.Gamma;
import io.github.t3r1jj.gammaj.model.MultiDisplay;
import io.github.t3r1jj.gammaj.model.ViewModel;
import io.github.t3r1jj.gammaj.model.temperature.RgbTemperature;
import io.github.t3r1jj.gammaj.model.temperature.TemperatureSimpleFactory;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.util.StringConverter;

public class AssistedTabController implements Initializable {

    ViewModel viewModel = ViewModel.getInstance();
    private HotkeyInputEventHandler hotkeyInput;
    private final TemperatureSimpleFactory temperatureFactory = new TemperatureSimpleFactory();
    private boolean loadingProfile;

    @FXML
    private Canvas canvas;
    @FXML
    private ComboBox<Display> screenComboBox;
    @FXML
    private Slider gammaSlider;
    @FXML
    private Slider brightnessSlider;
    @FXML
    private Slider contrastBilateralSlider;
    @FXML
    private Slider contrastUnilateralSlider;
    @FXML
    private Slider temperatureSlider;
    @FXML
    private Spinner gammaSpinner;
    @FXML
    private Spinner brightnessSpinner;
    @FXML
    private Spinner contrastBilateralSpinner;
    @FXML
    private Spinner contrastUnilateralSpinner;
    @FXML
    private Spinner temperatureSpinner;
    @FXML
    private ComboBox<ColorProfile> profilesComboBox;
    @FXML
    private TextField hotkeyTextField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hotkeyInput = new HotkeyInputEventHandler(hotkeyTextField);
        hotkeyTextField.setOnKeyPressed(hotkeyInput);

        profilesComboBox.itemsProperty().set(viewModel.getLoadedProfilesProperty());
        profilesComboBox.valueProperty().bindBidirectional(viewModel.getCurrentProfileProperty());
        profilesComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ColorProfile>() {

            @Override
            public void changed(ObservableValue<? extends ColorProfile> observable, ColorProfile oldValue, ColorProfile selectedColorProfile) {
                if (selectedColorProfile == null) {
                    System.out.println("Empty profile, NULL...");
                    hotkeyTextField.setText("");
                    return;
                }
                if (!viewModel.getLoadedProfilesProperty().contains(selectedColorProfile)) {
                    System.out.println("Empty profile, ignoring...");
                    profilesComboBox.getSelectionModel().select(null);
                    loadLocalProfile();
                    return;
                }
                viewModel.getCurrentDisplayProperty().get().setColorProfile(selectedColorProfile);
                System.out.println("Loading profile: " + selectedColorProfile);
                loadLocalProfile();
            }
        });

        screenComboBox.itemsProperty().set(viewModel.getDisplaysProperty());
        screenComboBox.valueProperty().bindBidirectional(viewModel.getCurrentDisplayProperty());
        screenComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Display>() {
            @Override
            public void changed(ObservableValue<? extends Display> observable, Display oldValue, Display selectedDisplay) {
                profilesComboBox.getSelectionModel().select(selectedDisplay.getColorProfile());
            }

        });

        viewModel.getResetProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                handleResetButtonAction(null);
            }
        });

        temperatureFactory.setIsSrgb(viewModel.getIsSrgbProperty().get());
        viewModel.getIsSrgbProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean wasSrgb, Boolean isSrgb) {
                if (!loadingProfile) {
                    resetProfile();
                    System.out.println("SETTING SRGB: " + isSrgb);
                    temperatureFactory.setIsSrgb(isSrgb);
                    viewModel.getCurrentDisplayProperty().get().setTemperature(temperatureFactory.createTemperature(temperatureSlider.valueProperty().getValue()));
                    viewModel.getCurrentDisplayProperty().get().reinitialize();
                    drawGammaLine();
                }
            }
        });

        gammaSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!loadingProfile) {
                    resetProfile();
                    for (Gamma.Channel channel : viewModel.getSelectedChannelsProperty()) {
                        viewModel.getCurrentDisplayProperty().get().setGamma(channel, newValue.doubleValue());
                    }
                    viewModel.getCurrentDisplayProperty().get().reinitialize();
                    drawGammaLine();
                }
            }

        });
        brightnessSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!loadingProfile) {
                    resetProfile();
                    for (Gamma.Channel channel : viewModel.getSelectedChannelsProperty()) {
                        viewModel.getCurrentDisplayProperty().get().setBrightness(channel, newValue.doubleValue());
                    }
                    viewModel.getCurrentDisplayProperty().get().reinitialize();
                    drawGammaLine();
                }
            }

        });
        contrastBilateralSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!loadingProfile) {
                    resetProfile();
                    for (Gamma.Channel channel : viewModel.getSelectedChannelsProperty()) {
                        viewModel.getCurrentDisplayProperty().get().setContrastBilateral(channel, newValue.doubleValue());
                    }
                    viewModel.getCurrentDisplayProperty().get().reinitialize();
                    drawGammaLine();
                }
            }

        });
        contrastUnilateralSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!loadingProfile) {
                    resetProfile();
                    for (Gamma.Channel channel : viewModel.getSelectedChannelsProperty()) {
                        viewModel.getCurrentDisplayProperty().get().setContrastUnilateral(channel, newValue.doubleValue());
                    }
                    viewModel.getCurrentDisplayProperty().get().reinitialize();
                    drawGammaLine();
                }
            }

        });

        temperatureSlider.setLabelFormatter(new StringConverter<Double>() {

            @Override
            public String toString(Double object) {
                return (int) (object / 1000) + "kK";
            }

            @Override
            public Double fromString(String string) {
                return Double.valueOf(string.substring(0, string.length() - 2));
            }
        });

        temperatureSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                resetProfile();
                if (!loadingProfile) {
                    viewModel.getCurrentDisplayProperty().get().setTemperature(temperatureFactory.createTemperature(newValue.doubleValue()));
                    viewModel.getCurrentDisplayProperty().get().reinitialize();
                }
                drawGammaLine();
            }

        });

        Bindings.bindBidirectional(gammaSlider.valueProperty(), gammaSpinner.getValueFactory().valueProperty());
        Bindings.bindBidirectional(contrastBilateralSlider.valueProperty(), contrastBilateralSpinner.getValueFactory().valueProperty());
        Bindings.bindBidirectional(contrastUnilateralSlider.valueProperty(), contrastUnilateralSpinner.getValueFactory().valueProperty());
        Bindings.bindBidirectional(brightnessSlider.valueProperty(), brightnessSpinner.getValueFactory().valueProperty());
        Bindings.bindBidirectional(temperatureSlider.valueProperty(), temperatureSpinner.getValueFactory().valueProperty());

        drawGammaLine();
    }

    @FXML
    private void handleResetButtonAction(ActionEvent event) {
        System.out.println("Reset button clicked!");
        resetProfile();
        resetSliders();
        viewModel.getCurrentDisplayProperty().get().resetGammaRamp();
        drawGammaLine();
    }

    @FXML
    private void handleInvertButtonAction(ActionEvent event) {
        if (!loadingProfile) {
            resetProfile();
            for (Gamma.Channel channel : viewModel.getSelectedChannelsProperty()) {
                viewModel.getCurrentDisplayProperty().get().invertGammaRamp(channel);
            }
            viewModel.getCurrentDisplayProperty().get().reinitialize();
            drawGammaLine();
        }
    }

    @FXML
    private void handleSaveProfileAsButtonAction(ActionEvent event) throws IOException, InterruptedException {
        TextInputDialog nameInputDialog = new TextInputDialog();
        nameInputDialog.setTitle("Save as");
        nameInputDialog.setHeaderText("Color profile");
        nameInputDialog.setContentText("File name");
        Optional<String> nameWrapper = nameInputDialog.showAndWait();
        if (nameWrapper.isPresent()) {
            String fullName = nameWrapper.get() + ".properties";
            if (new File(fullName).exists() && !userWantsProfileOverwrite()) {
                handleSaveProfileAsButtonAction(event);
                return;
            }
            ColorProfile newColorProfile = viewModel.getCurrentDisplayProperty().get().getColorProfile().cloneOrSame(nameWrapper.get());
            newColorProfile.setModeIsAssissted(true);
            newColorProfile.setGammaRamp(viewModel.getCurrentDisplayProperty().get().getGammaRamp());
            if (newColorProfile.equals(viewModel.getCurrentDisplayProperty().get().getColorProfile())) {
                HotkeyPollerThread oldHotkey = viewModel.getCurrentDisplayProperty().get().getColorProfile().getHotkey();
                if (hotkeyInput.isEmpty() || !hotkeyInput.getHotkey().equals(oldHotkey)) {
                    viewModel.getHotkeysRunner().deregisterHotkey(oldHotkey);
                }
                registerInputHotkey(newColorProfile);
            } else {
                registerInputHotkey(newColorProfile);
                viewModel.getLoadedProfilesProperty().add(newColorProfile);
                profilesComboBox.getSelectionModel().select(profilesComboBox.getItems().size() - 1);
            }
            newColorProfile.saveProfile(fullName);
        }
    }

    private void registerInputHotkey(ColorProfile newColorProfile) {
        if (!hotkeyInput.isEmpty()) {
            HotkeyPollerThread hotkey = hotkeyInput.getHotkey();
            if (!viewModel.getHotkeysRunner().isRegistered(hotkey)) {
                newColorProfile.setHotkey(hotkey);
                hotkey.setHotkeyListener(new ProfileHotkeyListener(viewModel.getCurrentProfileProperty(), newColorProfile));
                viewModel.getHotkeysRunner().registerHotkey(hotkey);
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Hotkey not registered");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Hotkey \"" + hotkey.getDisplayText()
                        + "\" has not been registered because it is already assigned to profile \""
                        + viewModel.getHotkeysRunner().registeredProfileInfo(hotkey) + "\"");
                errorAlert.showAndWait();
            }
        }
    }

    @FXML
    public void handleRedSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        if (isNowSelected) {
            viewModel.getSelectedChannelsProperty().clear();
            viewModel.getSelectedChannelsProperty().add(Gamma.Channel.RED);
            loadLocalProfile();
        }
    }

    @FXML
    public void handleGreenSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        if (isNowSelected) {
            viewModel.getSelectedChannelsProperty().clear();
            viewModel.getSelectedChannelsProperty().add(Gamma.Channel.GREEN);
            loadLocalProfile();
        }
    }

    @FXML
    public void handleBlueSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        if (isNowSelected) {
            viewModel.getSelectedChannelsProperty().clear();
            viewModel.getSelectedChannelsProperty().add(Gamma.Channel.BLUE);
            loadLocalProfile();
        }
    }

    @FXML
    public void handleRgbSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        if (isNowSelected) {
            viewModel.getSelectedChannelsProperty().clear();
            viewModel.getSelectedChannelsProperty().add(Gamma.Channel.RED);
            viewModel.getSelectedChannelsProperty().add(Gamma.Channel.GREEN);
            viewModel.getSelectedChannelsProperty().add(Gamma.Channel.BLUE);
            loadLocalProfile();
        }
    }

    private boolean userWantsProfileOverwrite() throws IOException {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Color profile already exists");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Color profile with that name already exists. Do you want to overwrite it?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.get() != ButtonType.OK) {
            return false;
        }
        return true;
    }

    private void removeProfileFromApp(ColorProfile profileToRemove) {
        viewModel.getLoadedProfilesProperty().remove(profileToRemove);
        profilesComboBox.getItems().remove(profileToRemove);
    }

    @FXML
    private void handleDeleteProfileButtonAction(ActionEvent event) {
        ChoiceDialog choiceDialog = new ChoiceDialog(profilesComboBox.getSelectionModel().getSelectedItem(), viewModel.getLoadedProfilesProperty());
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

    private void loadLocalProfile() {
        loadingProfile = true;
        ColorProfile colorProfile = viewModel.getCurrentDisplayProperty().get().getColorProfile();
        Gamma.Channel selectedChannel = viewModel.getSelectedChannelsProperty().iterator().next();
        System.out.println("Starting profile load: " + colorProfile + ", gamma: " + colorProfile.getGamma(selectedChannel));
        gammaSpinner.getValueFactory().setValue(colorProfile.getGamma(selectedChannel));
        contrastBilateralSpinner.getValueFactory().setValue(colorProfile.getContrastBilateral(selectedChannel));
        contrastUnilateralSpinner.getValueFactory().setValue(colorProfile.getContrastUnilateral(selectedChannel));
        brightnessSpinner.getValueFactory().setValue(colorProfile.getBrightness(selectedChannel));
        temperatureSpinner.getValueFactory().setValue(colorProfile.getTemperature().getTemperature());
        viewModel.getIsSrgbProperty().set(colorProfile.isTemperatureSrgb());
        HotkeyPollerThread hotkey = colorProfile.getHotkey();
        hotkeyInput.setHotkey(hotkey);
        viewModel.getCurrentDisplayProperty().get().loadModelFromProfile(false);
        viewModel.getCurrentDisplayProperty().get().reinitialize();
        drawGammaLine();
        loadingProfile = false;
    }

    private void drawGammaLine() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(GAMMA_CANVAS_BACKGROUND_COLOR);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        graphicsContext.setLineWidth(1);
        double[][] gammaRamp = viewModel.getCurrentDisplayProperty().get().getNormalizedGammaRamp();
        for (int i = 0; i < gammaRamp.length; i++) {
            graphicsContext.setStroke(GAMMA_CANVAS_LINE_COLOR[i]);
            graphicsContext.strokeLine(0, (1 - gammaRamp[i][0]) * canvas.getWidth(), 0, (1 - gammaRamp[i][0]) * canvas.getWidth());
            for (int x = 1; x < canvas.getWidth(); x++) {
                graphicsContext.strokeLine(x - 1, (1 - gammaRamp[i][x - 1]) * canvas.getWidth(), x, (1 - gammaRamp[i][x]) * canvas.getWidth());
            }
        }
    }

    private void resetSliders() {
        gammaSlider.setValue(Gamma.DEFAULT_GAMMA);
        brightnessSlider.setValue(Gamma.DEFAULT_BRIGHTNESS);
        contrastBilateralSlider.setValue(Gamma.DEFAULT_CONTRAST_BILATERAL);
        contrastUnilateralSlider.setValue(Gamma.DEFAULT_CONTRAST_UNILATERAL);
        temperatureSlider.setValue(Gamma.DEFAULT_TEMPERATURE);
    }

    private void resetProfile() {
        if (!loadingProfile && profilesComboBox.selectionModelProperty().get().getSelectedItem() != null) {
            System.out.println("Resetting profile " + (profilesComboBox.selectionModelProperty().get() != null) + " " + profilesComboBox.selectionModelProperty().get().getSelectedItem());
            viewModel.getCurrentDisplayProperty().get().setColorProfile(viewModel.getCurrentDisplayProperty().get().getColorProfile().cloneOrSame(""));
            profilesComboBox.getSelectionModel().select(null);
        }
    }

}

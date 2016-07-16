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

    private Display currentDisplay;
    private final Set<Gamma.Channel> selectedChannels = EnumSet.allOf(Gamma.Channel.class);
    private final List<ColorProfile> loadedProfiles = new ArrayList<>();
    private final HotkeysRunner hotkeysRunner;
    private HotkeyInputEventHandler hotkeyInput;
    private TemperatureSimpleFactory temperatureFactory = new TemperatureSimpleFactory("rgb");
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

    public AssistedTabController(HotkeysRunner hotkeysRunner) {
        this.hotkeysRunner = hotkeysRunner;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        loadFileProfiles();
        registerHotkeys();
        
        hotkeyInput = new HotkeyInputEventHandler(hotkeyTextField);
        hotkeyTextField.setOnKeyPressed(hotkeyInput);

        profilesComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ColorProfile>() {

            @Override
            public void changed(ObservableValue<? extends ColorProfile> observable, ColorProfile oldValue, ColorProfile selectedColorProfile) {
                if (selectedColorProfile == null || loadingProfile) {
//                    loadingProfile = false;
                    System.out.println("Empty profile, NULL...");
                    return;
                }
                if (!loadedProfiles.contains(selectedColorProfile)) {
                    System.out.println("Empty profile, ignoring...");
                    profilesComboBox.getSelectionModel().select(null);
                    hotkeyTextField.setText("");
                    return;
                }
                currentDisplay.setColorProfile(selectedColorProfile);
                System.out.println("Loading profile: " + selectedColorProfile);
                loadLocalProfile();
            }
        });

        DisplayUtil screenUtil = new DisplayUtil();
        MultiDisplay multiDisplay = screenUtil.getMultiDisplay();
        List<Display> displays = multiDisplay.getDisplays();
        screenComboBox.getItems().add(multiDisplay);
        screenComboBox.getItems().addAll(displays);
        currentDisplay = multiDisplay;
        screenComboBox.getSelectionModel().select(currentDisplay);

        screenComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Display>() {
            @Override
            public void changed(ObservableValue<? extends Display> observable, Display oldValue, Display newValue) {
                currentDisplay = newValue;
                ColorProfile colorProfile = currentDisplay.getColorProfile();
                if (!loadedProfiles.contains(colorProfile)) {
                    System.out.println("Changed to no profile - loading gamma from no profile aka previously set settings: " + colorProfile);
                    currentDisplay.reinitialize();
                    System.out.println("AFTER REINIT");
                    profilesComboBox.getSelectionModel().select(null);
                    loadLocalProfile();
                    drawGammaLine();
                } else {
                    profilesComboBox.getSelectionModel().select(currentDisplay.getColorProfile());
                }
            }

        });

        gammaSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                resetProfile();
                for (Gamma.Channel channel : selectedChannels) {
                    currentDisplay.setGamma(channel, newValue.doubleValue());
                }
                currentDisplay.reinitialize();
                drawGammaLine();
            }

        });
        brightnessSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                resetProfile();
                for (Gamma.Channel channel : selectedChannels) {
                    currentDisplay.setBrightness(channel, newValue.doubleValue());
                }
                currentDisplay.reinitialize();
                drawGammaLine();
            }

        });
        contrastBilateralSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                resetProfile();
                for (Gamma.Channel channel : selectedChannels) {
                    currentDisplay.setContrastBilateral(channel, newValue.doubleValue());
                }
                currentDisplay.reinitialize();
                drawGammaLine();
            }

        });
        contrastUnilateralSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                resetProfile();
                for (Gamma.Channel channel : selectedChannels) {
                    currentDisplay.setContrastUnilateral(channel, newValue.doubleValue());
                }
                currentDisplay.reinitialize();
                drawGammaLine();
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
                currentDisplay.setTemperature(temperatureFactory.createTemperature(newValue.doubleValue()));
                currentDisplay.reinitialize();
                drawGammaLine();
            }

        });

//        menuBarController.srgbSelectedProperty().addListener(new ChangeListener<Boolean>() {
//
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean wasSrgb, Boolean isSrgb) {
//                resetProfile();
//                System.out.println("SRGB=++++++++");
//                temperatureFactory.setIsSrgb(isSrgb);
//                currentDisplay.setTemperature(temperatureFactory.createTemperature(temperatureSlider.valueProperty().getValue()));
//                currentDisplay.reinitialize();
//                drawGammaLine();
//            }
//        });
//        
//        menuBarController.setOnResetEventHandler(new EventHandler<ActionEvent>() {
//
//            @Override
//            public void handle(ActionEvent event) {
//                handleResetButtonAction(event);
//            }
//        });
        
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
        currentDisplay.resetGammaRamp();
        drawGammaLine();
    }

    @FXML
    private void handleInvertButtonAction(ActionEvent event) {
        resetProfile();
        for (Gamma.Channel channel : selectedChannels) {
            currentDisplay.invertGammaRamp(channel);
        }
        currentDisplay.reinitialize();
        drawGammaLine();
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
            if (new File(fullName).exists() && !userWantsProfileOverwrite(nameWrapper.get())) {
                return;
            }
            ColorProfile newColorProfile = currentDisplay.getColorProfile().cloneOrSame(nameWrapper.get());
            System.out.println(newColorProfile.getGamma(Gamma.Channel.RED));
            newColorProfile.setModeIsAssissted(true);
            newColorProfile.setGammaRamp(currentDisplay.getGammaRamp());
            if (newColorProfile.equals(currentDisplay.getColorProfile())) {
                HotkeyPollerThread oldHotkey = currentDisplay.getColorProfile().getHotkey();
                if (hotkeyInput.isEmpty() || !hotkeyInput.getHotkey().equals(oldHotkey)) {
                    hotkeysRunner.deregisterHotkey(oldHotkey);
                }
                registerInputHotkey(newColorProfile);
            } else {
                registerInputHotkey(newColorProfile);
                loadedProfiles.add(newColorProfile);
                profilesComboBox.getItems().add(newColorProfile);
                profilesComboBox.getSelectionModel().select(profilesComboBox.getItems().size() - 1);
            }
            newColorProfile.saveProfile(fullName);
        }
    }

    private void registerInputHotkey(ColorProfile newColorProfile) {
        if (!hotkeyInput.isEmpty()) {
            HotkeyPollerThread hotkey = hotkeyInput.getHotkey();
            if (hotkeysRunner.isRegistered(hotkey)) {
                newColorProfile.setHotkey(hotkey);
                hotkey.setHotkeyListener(new ProfileHotkeyListener(profilesComboBox, newColorProfile));
                hotkeysRunner.registerHotkey(hotkey);
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Hotkey not registered");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Hotkey \"" + hotkey.getDisplayText()
                        + "\" has not been registered because it is already assigned to profile \""
                        + hotkeysRunner.registeredProfileInfo(hotkey) + "\"");
                errorAlert.showAndWait();
            }
        }
    }

    @FXML
    public void handleRedSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        if (isNowSelected) {
            selectedChannels.clear();
            selectedChannels.add(Gamma.Channel.RED);
            loadLocalProfile();
        }
    }

    @FXML
    public void handleGreenSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        if (isNowSelected) {
            selectedChannels.clear();
            selectedChannels.add(Gamma.Channel.GREEN);
            loadLocalProfile();
        }
    }

    @FXML
    public void handleBlueSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        if (isNowSelected) {
            selectedChannels.clear();
            selectedChannels.add(Gamma.Channel.BLUE);
            loadLocalProfile();
        }
    }

    @FXML
    public void handleRgbSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        if (isNowSelected) {
            selectedChannels.clear();
            selectedChannels.add(Gamma.Channel.RED);
            selectedChannels.add(Gamma.Channel.GREEN);
            selectedChannels.add(Gamma.Channel.BLUE);
            loadLocalProfile();
        }
    }

    private boolean userWantsProfileOverwrite(String name) throws IOException {
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
        loadedProfiles.remove(profileToRemove);
        profilesComboBox.getItems().remove(profileToRemove);
    }

    @FXML
    private void handleDeleteProfileButtonAction(ActionEvent event) {
        ChoiceDialog choiceDialog = new ChoiceDialog(profilesComboBox.getSelectionModel().getSelectedItem(), loadedProfiles);
        choiceDialog.setTitle("Delete color profile");
        choiceDialog.setHeaderText(null);
        choiceDialog.setContentText("Select color profile to delete");
        Optional<ColorProfile> selectedProfile = choiceDialog.showAndWait();
        if (selectedProfile.isPresent()) {
            ColorProfile profileToDelete = selectedProfile.get();
            hotkeysRunner.deregisterHotkey(profileToDelete.getHotkey());
            removeProfileFromApp(profileToDelete);
            profileToDelete.deleteProfile();
        }
    }
    
    
    private void loadLocalProfile() {
        loadingProfile = true;
        ColorProfile colorProfile = currentDisplay.getColorProfile();
        Gamma.Channel selectedChannel = selectedChannels.iterator().next();
        System.out.println("Starting profile load: " + colorProfile + ", gamma: " + colorProfile.getGamma(selectedChannel));
        gammaSpinner.getValueFactory().setValue(colorProfile.getGamma(selectedChannel));
        contrastBilateralSpinner.getValueFactory().setValue(colorProfile.getContrastBilateral(selectedChannel));
        contrastUnilateralSpinner.getValueFactory().setValue(colorProfile.getContrastUnilateral(selectedChannel));
        brightnessSpinner.getValueFactory().setValue(colorProfile.getBrightness(selectedChannel));
        temperatureSpinner.getValueFactory().setValue(colorProfile.getTemperature().getTemperature());
//        menuBarController.srgbSelectedProperty().set(colorProfile.isTemperatureSrgb());
        boolean[] invertedChannels = colorProfile.getInvertedChannels();
        for (Gamma.Channel channel : Gamma.Channel.values()) {
            if (invertedChannels[channel.getIndex()]) {
                currentDisplay.invertGammaRamp(channel);
            }
        }
        HotkeyPollerThread hotkey = colorProfile.getHotkey();
        hotkeyTextField.setText("");
        if (hotkey != null) {
            hotkeyTextField.setText(hotkey.getDisplayText());
            hotkeyInput.setHotkey(hotkey);
        }
        currentDisplay.reinitialize();
        drawGammaLine();
        loadingProfile = false;
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
                loadedProfiles.add(colorProfile);
            } catch (IOException ex) {
                Logger.getLogger(SceneController.class.getName()).log(Level.SEVERE, null, ex);
                errorBuilder.append("\"").append(colorProfile).append("\", ");
            }
        }
        profilesComboBox.getItems().setAll(loadedProfiles);
        profilesComboBox.getSelectionModel().select(null);
        if (errorBuilder.length() > 0) {
            errorBuilder.delete(errorBuilder.length() - 2, errorBuilder.length());
            errorBuilder.append(".");
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Profile loading error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Could not load color profiles: " + errorBuilder.toString());
            errorAlert.showAndWait();
        }
    }

    private void drawGammaLine() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(GAMMA_CANVAS_BACKGROUND_COLOR);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        graphicsContext.setLineWidth(1);
        double[][] gammaRamp = currentDisplay.getNormalizedGammaRamp();
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

    private void registerHotkeys() {
        // TODO: Register global reset hotkey
        StringBuilder errorBuilder = new StringBuilder();
        for (ColorProfile colorProfile : loadedProfiles) {
            HotkeyPollerThread loadedHotkey = null;
            try {
                loadedHotkey = colorProfile.getHotkey();
                if (loadedHotkey != null && !hotkeysRunner.isRegistered(loadedHotkey)) {
                    loadedHotkey.setHotkeyListener(new ProfileHotkeyListener(profilesComboBox, colorProfile));
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
            errorAlert.setTitle("Hotkey registration error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Could not register hotkey for profiles: " + errorBuilder.toString());
            errorAlert.showAndWait();
        }
    }

    private void resetProfile() {
        if (!loadingProfile && profilesComboBox.selectionModelProperty().get() != null) {
            profilesComboBox.getSelectionModel().select(null);
            currentDisplay.setColorProfile(new ColorProfile(""));
        }
    }

}

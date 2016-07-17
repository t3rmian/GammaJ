package io.github.t3r1jj.gammaj.controllers;

import io.github.t3r1jj.gammaj.GammaRampPainter;
import io.github.t3r1jj.gammaj.hotkeys.HotkeyInputEventHandler;
import io.github.t3r1jj.gammaj.hotkeys.HotkeyPollerThread;
import io.github.t3r1jj.gammaj.hotkeys.ProfileHotkeyListener;
import io.github.t3r1jj.gammaj.model.ColorProfile;
import io.github.t3r1jj.gammaj.model.Display;
import io.github.t3r1jj.gammaj.model.Gamma;
import io.github.t3r1jj.gammaj.model.ViewModel;
import io.github.t3r1jj.gammaj.model.temperature.TemperatureSimpleFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

public abstract class AbstractTabController implements Initializable {

    ViewModel viewModel = ViewModel.getInstance();
    protected HotkeyInputEventHandler hotkeyInput;
    protected final TemperatureSimpleFactory temperatureFactory = new TemperatureSimpleFactory();
    protected GammaRampPainter gammaRampPainter;
    protected boolean loadingProfile;

    @FXML
    protected Canvas canvas;
    @FXML
    protected ComboBox<Display> screenComboBox;
    @FXML
    protected ComboBox<ColorProfile> profilesComboBox;
    @FXML
    protected TextField hotkeyTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gammaRampPainter = viewModel.getGammaRampPainter();
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

    }

    @FXML
    protected void handleResetButtonAction(ActionEvent event) {
        System.out.println("Reset button clicked!");
        resetProfile();
        resetColorAdjustment();
        viewModel.getCurrentDisplayProperty().get().resetGammaRamp();
        gammaRampPainter.drawGammaRamp(canvas, viewModel.getCurrentDisplayProperty().get());
    }

    @FXML
    protected void handleSaveProfileAsButtonAction(ActionEvent event) throws IOException, InterruptedException {
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

    protected void registerInputHotkey(ColorProfile newColorProfile) {
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

    protected boolean userWantsProfileOverwrite() throws IOException {
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

    protected void removeProfileFromApp(ColorProfile profileToRemove) {
        viewModel.getLoadedProfilesProperty().remove(profileToRemove);
        profilesComboBox.getItems().remove(profileToRemove);
    }

    @FXML
    protected void handleDeleteProfileButtonAction(ActionEvent event) {
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

    protected void resetProfile() {
        if (!loadingProfile && profilesComboBox.selectionModelProperty().get().getSelectedItem() != null) {
            System.out.println("Resetting profile " + (profilesComboBox.selectionModelProperty().get() != null) + " " + profilesComboBox.selectionModelProperty().get().getSelectedItem());
            viewModel.getCurrentDisplayProperty().get().setColorProfile(viewModel.getCurrentDisplayProperty().get().getColorProfile().cloneOrSame(""));
            profilesComboBox.getSelectionModel().select(null);
        }
    }

    @FXML
    protected void handleInvertButtonAction(ActionEvent event) {
        if (!loadingProfile) {
            resetProfile();
            for (Gamma.Channel channel : viewModel.getSelectedChannelsProperty()) {
                viewModel.getCurrentDisplayProperty().get().invertGammaRamp(channel);
            }
            viewModel.getCurrentDisplayProperty().get().reinitialize();
            gammaRampPainter.drawGammaRamp(canvas, viewModel.getCurrentDisplayProperty().get());
        }
    }

    protected abstract void loadLocalProfile();

    protected abstract void resetColorAdjustment();

}

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

import io.github.t3r1jj.gammaj.info.HttpVersionUtility;
import io.github.t3r1jj.gammaj.tray.TrayManager;
import io.github.t3r1jj.gammaj.hotkeys.HotkeyInputEventHandler;
import io.github.t3r1jj.gammaj.hotkeys.HotkeyPollerThread;
import io.github.t3r1jj.gammaj.info.Library;
import io.github.t3r1jj.gammaj.info.ProjectInfo;
import io.github.t3r1jj.gammaj.ViewModel;
import java.awt.AWTException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

public class MenuBarController implements Initializable {

    private static final String colorPalettePath = "images/color_palette.png";

    private final ViewModel viewModel;
    private final HostServices hostServices;
    private final TrayManager trayManager;

    private HotkeyInputEventHandler hotkeyInput;

    @FXML
    private MenuBar menuBar;
    @FXML
    private CheckMenuItem srgbCheckMenuItem;

    public MenuBarController(HostServices hostServices, TrayManager trayManager, ViewModel viewModel) {
        this.hostServices = hostServices;
        this.trayManager = trayManager;
        this.viewModel = viewModel;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        srgbCheckMenuItem.selectedProperty().bindBidirectional(viewModel.isSrgbProperty());
        viewModel.assistedAdjustmentProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isNowAssisted) {
                srgbCheckMenuItem.setDisable(!isNowAssisted);
            }
        });
        if (viewModel.getConfiguration().isTrayEnabled()) {
            try {
                trayManager.enableTray(true);
            } catch (IOException | AWTException ex) {
                Logger.getLogger(MenuBarController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    public void handleResetAction(ActionEvent event) {
        viewModel.resetProperty().setValue(!viewModel.resetProperty().get());
    }

    @FXML
    private void handleSettingsAction(ActionEvent event) {
        Alert settingsAlert = new Alert(Alert.AlertType.CONFIRMATION);
        settingsAlert.initOwner(menuBar.getScene().getWindow());
        settingsAlert.setTitle("Settings");
        settingsAlert.setHeaderText(null);
        final TextField hotkeyTextField = new TextField();
        hotkeyTextField.setEditable(false);
        HotkeyPollerThread resetHotkey = viewModel.getHotkeysRunner().getApplicationHotkey();
        hotkeyInput = new HotkeyInputEventHandler(hotkeyTextField);
        hotkeyInput.setHotkey(resetHotkey);
        settingsAlert.getDialogPane().getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE) {
                    event.consume();
                    if (hotkeyTextField.focusedProperty().get()) {
                        hotkeyInput.setHotkey(null);
                    }
                }
            }
        });
        hotkeyTextField.setOnKeyPressed(hotkeyInput);
        Label hotkeyLabel = new Label("Reset global hotkey:  ");
        CheckBox detachDisplaysCheckBox = new CheckBox("Displays detached from whole screen");

        detachDisplaysCheckBox.setSelected(viewModel.detachDisplayProperty().get());
        CheckBox resetOnExitCheckbox = new CheckBox("Reset color on exit");
        resetOnExitCheckbox.setSelected(viewModel.getConfiguration().isColorResetOnExit());
        CheckBox loadOnStartCheckbox = new CheckBox("Load selected profiles on start");
        loadOnStartCheckbox.setSelected(viewModel.getConfiguration().getLoadCorrespondingProfiles());
        viewModel.detachDisplayProperty().bind(detachDisplaysCheckBox.selectedProperty());
        GridPane outPane = new GridPane();
        outPane.setMaxWidth(Double.MAX_VALUE);
        outPane.setMaxHeight(Double.MAX_VALUE);
        GridPane contentPane = new GridPane();
        contentPane.setMaxWidth(Double.MAX_VALUE);
        contentPane.setMaxHeight(Double.MAX_VALUE);
        contentPane.add(hotkeyLabel, 0, 0);
        contentPane.add(hotkeyTextField, 1, 0);
        outPane.add(contentPane, 0, 0);
        outPane.add(new Label(), 0, 1);
        outPane.add(detachDisplaysCheckBox, 0, 2);
        outPane.add(new Label(), 0, 3);
        outPane.add(resetOnExitCheckbox, 0, 4);
        outPane.add(new Label(), 0, 5);
        outPane.add(loadOnStartCheckbox, 0, 6);
        settingsAlert.getDialogPane().contentProperty().set(outPane);
        Optional<ButtonType> result = settingsAlert.showAndWait();
        if (result.get().equals(ButtonType.OK)) {
            viewModel.getConfiguration().setIsColorResetOnExit(resetOnExitCheckbox.isSelected());
            viewModel.getConfiguration().setLoadCorrespondingProfiles(loadOnStartCheckbox.isSelected());
            handleHotkeyChange(event, resetHotkey);
            viewModel.getConfiguration().save();
        }
        hotkeyInput = null;
    }

    private void handleHotkeyChange(ActionEvent event, HotkeyPollerThread resetHotkey) {
        HotkeyPollerThread newHotkey = hotkeyInput.getHotkey();
        if (newHotkey == null) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.initOwner(menuBar.getScene().getWindow());
            errorAlert.setTitle("Hotkey not changed");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Hotkey must not be empty in case of setting or loading entirely black/white profile you would not be able to reset it without system restart.");
            errorAlert.showAndWait();
            handleSettingsAction(event);
        } else if (viewModel.getHotkeysRunner().isRegisteredOnProfile(newHotkey)) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.initOwner(menuBar.getScene().getWindow());
            errorAlert.setTitle("Hotkey not changed");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Hotkey \"" + newHotkey.getDisplayText()
                    + "\" has not been registered because it is already assigned to profile \""
                    + viewModel.getHotkeysRunner().registeredProfileInfo(newHotkey) + "\"");
            errorAlert.showAndWait();
            handleSettingsAction(event);
        } else if (!resetHotkey.equals(newHotkey)) {
            newHotkey.setHotkeyListener(resetHotkey.getHotkeyListener());
            viewModel.getHotkeysRunner().reregisterApplicationHotkey(newHotkey);
            viewModel.getConfiguration().setHotkey(newHotkey);
        }
    }

    @FXML
    private void handleExitAction(ActionEvent event) {
        viewModel.saveAndReset();
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void handleColorPaletteAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Color palette");
        alert.setContentText(null);
        alert.setHeaderText(null);
        Image image = new Image(this.getClass().getClassLoader().getResourceAsStream(colorPalettePath));
        alert.setGraphic(new ImageView(image));
        alert.initModality(Modality.NONE);
        alert.getDialogPane().setMaxWidth(image.getWidth() - 100);
        alert.show();
    }

    @FXML
    private void handleUpdateAction(ActionEvent event) {
        final Scene scene = menuBar.getScene();
        scene.setCursor(Cursor.WAIT);
        new Thread(new Runnable() {

            @Override
            public void run() {
                HttpVersionUtility httpVerionUtility = new HttpVersionUtility();
                try {
                    final String version = httpVerionUtility.getVersion();
                    ProjectInfo projectInfo = new ProjectInfo();
                    if (projectInfo.isNewerVersion(version)) {
                        handleNewerVersion(httpVerionUtility);
                    } else {
                        handleUpToDate();
                    }
                } catch (Exception ex) {
                    handleVersionError(ex);
                }
            }

            private void handleVersionError(final Exception ex) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        scene.setCursor(Cursor.DEFAULT);
                        Logger.getLogger(MenuBarController.class.getName()).log(Level.SEVERE, null, ex);
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.initOwner(scene.getWindow());
                        errorAlert.setTitle("Version");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText("Could not connect with server to check version.");
                        errorAlert.showAndWait();
                    }
                });
            }

            private void handleUpToDate() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        scene.setCursor(Cursor.DEFAULT);
                        Alert upToDateAlert = new Alert(Alert.AlertType.INFORMATION);
                        upToDateAlert.initOwner(scene.getWindow());
                        upToDateAlert.setTitle("Version");
                        upToDateAlert.setHeaderText(null);
                        upToDateAlert.setContentText("Current version is up to date.");
                        upToDateAlert.showAndWait();
                    }
                });
            }

            private void handleNewerVersion(HttpVersionUtility httpVerionUtility) throws IOException {
                final String link = httpVerionUtility.getLink();
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        scene.setCursor(Cursor.DEFAULT);
                        Alert linkAlert = new Alert(Alert.AlertType.INFORMATION);
                        linkAlert.initOwner(scene.getWindow());
                        linkAlert.setTitle("Version");
                        linkAlert.setHeaderText(null);
                        linkAlert.setContentText("There is a new version of GammaJ.");
                        ButtonType linkButton = new ButtonType("Download");
                        linkAlert.getButtonTypes().add(linkButton);
                        Optional<ButtonType> showAndWait = linkAlert.showAndWait();
                        if (showAndWait.get() == linkButton) {
                            hostServices.showDocument(link);
                        }
                    }
                });
            }
        }).start();
    }

    @FXML
    private void handleTraySelectedChange(ActionEvent event) {
        CheckMenuItem trayCheckBox = (CheckMenuItem) event.getSource();
        boolean trayEnabled = trayCheckBox.isSelected();
        try {
            trayManager.enableTray(trayEnabled);
            viewModel.getConfiguration().setIsTrayEnabled(trayEnabled);
            viewModel.getConfiguration().save();
        } catch (IOException | AWTException ex) {
            Logger.getLogger(MenuBarController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleBugReportAction(ActionEvent event) {
        ProjectInfo projectInfo = new ProjectInfo();
        hostServices.showDocument(projectInfo.getProjectUrl() + "/issues");
    }

    @FXML
    private void handleLicenseAction(ActionEvent event) {
        ProjectInfo projectInfo = new ProjectInfo();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(menuBar.getScene().getWindow());
        List<Library> libraries = projectInfo.getLibrariesUsed();
        StringBuilder stringBuilder = new StringBuilder();
        List<ButtonType> buttons = new ArrayList<>();
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE);
        for (Library library : libraries) {
            stringBuilder.append("&#x2022; ")
                    .append(library.nameLong)
                    .append(" v")
                    .append(library.version)
                    .append(" - ")
                    .append(library.licenseShort)
                    .append("\n");
            ButtonType button = new ButtonType(library.nameShort);
            buttons.add(button);
        }
        alert.setTitle("Licenses");
        alert.setHeaderText("Libraries used");
        alert.setContentText(stringBuilder.toString());
        alert.getButtonTypes().setAll(buttons);
        alert.getButtonTypes().add(okButton);
        Optional<ButtonType> result = alert.showAndWait();
        if (buttons.contains(result.get())) {
            Library pressedLibrary = libraries.get(buttons.indexOf(result.get()));
            showLibraryLicense(pressedLibrary);
        }
    }

    private void showLibraryLicense(Library pressedLibrary) {
        Alert licenseAlert = new Alert(Alert.AlertType.INFORMATION);
        licenseAlert.initOwner(menuBar.getScene().getWindow());
        licenseAlert.setTitle(pressedLibrary.nameLong + " license");
        licenseAlert.setHeaderText(null);
        licenseAlert.setContentText(pressedLibrary.licenseLong);
        ButtonType urlButton = new ButtonType("Website");
        licenseAlert.getButtonTypes().setAll(urlButton);
        licenseAlert.getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE));
        Optional<ButtonType> licenseResult = licenseAlert.showAndWait();
        if (licenseResult.get().equals(urlButton)) {
            hostServices.showDocument(pressedLibrary.url);
        } else {
            handleLicenseAction(null);
        }
    }

    @FXML
    private void handleAboutAction(ActionEvent event) {
        ProjectInfo projectInfo = new ProjectInfo();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(menuBar.getScene().getWindow());
        alert.setTitle("About");
        alert.setHeaderText(projectInfo.getAboutHeader());
        alert.setContentText(projectInfo.getAboutContent());
        ButtonType donateButton = new ButtonType("Donate");
        alert.getButtonTypes().setAll(donateButton);
        alert.getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == donateButton) {
            final Scene scene = menuBar.getScene();
            scene.setCursor(Cursor.WAIT);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        final String donateLink = new HttpVersionUtility().getDonateLink();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                scene.setCursor(Cursor.DEFAULT);
                                hostServices.showDocument(donateLink);
                            }
                        });
                    } catch (final Exception ex) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                scene.setCursor(Cursor.DEFAULT);
                                Logger.getLogger(MenuBarController.class.getName()).log(Level.SEVERE, null, ex);
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.initOwner(scene.getWindow());
                                errorAlert.setTitle("Donate");
                                errorAlert.setHeaderText(null);
                                errorAlert.setContentText("Could not connect with server.");
                                errorAlert.showAndWait();
                            }
                        });
                    }
                }
            }).start();
        }
    }

}

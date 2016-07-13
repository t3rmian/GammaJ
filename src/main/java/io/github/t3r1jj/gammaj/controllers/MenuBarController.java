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

import io.github.t3r1jj.gammaj.TrayManager;
import io.github.t3r1jj.gammaj.info.Library;
import io.github.t3r1jj.gammaj.info.ProjectInfo;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;

public class MenuBarController implements Initializable {

    private final HostServices hostServices;
    private final TrayManager trayManager;

    public MenuBarController(HostServices hostServices, TrayManager trayManager) {
        this.hostServices = hostServices;
        this.trayManager = trayManager;
    }

    @FXML
    private void handleExitAction(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void handleTraySelectedChange(ActionEvent event) {
        CheckMenuItem trayCheckBox = (CheckMenuItem) event.getSource();
        boolean trayEnabled = trayCheckBox.isSelected();
        try {
            trayManager.enableTray(trayEnabled);
        } catch (IOException | AWTException ex) {
            Logger.getLogger(MenuBarController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleAboutAction(ActionEvent event) {
        ProjectInfo projectInfo = new ProjectInfo();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(projectInfo.getAboutHeader());
        alert.setContentText(projectInfo.getAboutContent());
        alert.showAndWait();
    }

    @FXML
    private void handleLicenseAction(ActionEvent event) {
        ProjectInfo projectInfo = new ProjectInfo();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        List<Library> libraries = projectInfo.getLibrariesUsed();
        StringBuilder stringBuilder = new StringBuilder();
        List<ButtonType> buttons = new ArrayList<>();
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        for (Library library : libraries) {
            stringBuilder.append(library.nameLong)
                    .append(" v")
                    .append(library.version)
                    .append(" - ")
                    .append(library.licenseShort);
            ButtonType button = new ButtonType(library.nameShort);
            buttons.add(button);
        }
        buttons.add(okButton);
        alert.setTitle("Licenses");
        alert.setHeaderText("Libraries used");
        alert.setContentText(stringBuilder.toString());
        alert.getButtonTypes().setAll(buttons);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() != okButton) {
            Library pressedLibrary = libraries.get(buttons.indexOf(result.get()));
            showLibraryLicense(pressedLibrary);
        }
    }

    private void showLibraryLicense(Library pressedLibrary) {
        Alert licenseAlert = new Alert(Alert.AlertType.INFORMATION);
        licenseAlert.setTitle(pressedLibrary.nameLong);
        licenseAlert.setHeaderText(null);
        licenseAlert.setContentText(pressedLibrary.licenseLong);
        ButtonType urlButton = new ButtonType("Website");
        licenseAlert.getButtonTypes().addAll(urlButton);
        Optional<ButtonType> licenseResult = licenseAlert.showAndWait();
        if (licenseResult.get().equals(urlButton)) {
            hostServices.showDocument(pressedLibrary.url);
        } else {
            handleLicenseAction(null);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

}

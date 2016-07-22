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

import io.github.t3r1jj.gammaj.tray.TrayManager;
import io.github.t3r1jj.gammaj.controllers.ApplicationControllerFactory;
import io.github.t3r1jj.gammaj.info.OperatingSystemUtility;
import io.github.t3r1jj.gammaj.jna.GammaRegistry;
import io.github.t3r1jj.gammaj.model.GammaWinapiCallException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.swing.JOptionPane;

public class GammaJ extends Application {

    private static final String appIconPath = "images/tray_icon.png";
    private final ViewModel viewModel = ViewModel.getInstance();
    private TrayManager trayManager;

    @Override
    public void start(Stage stage) throws Exception {
        stage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream(appIconPath)));
        trayManager = new TrayManager(stage, appIconPath, viewModel);
        FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("/fxml/Scene.fxml"));
        fXMLLoader.setControllerFactory(new ApplicationControllerFactory(getHostServices(), trayManager, viewModel));
        Parent root = null;
        try {
            root = fXMLLoader.load();
        } catch (GammaWinapiCallException exception) {
            JOptionPane.showConfirmDialog(null, "Could not initialize application. Winapi call exception.\n\nPossible causes:\n- missing GDI32.dll (unsupported operating system),\n- device with drivers that do not support downloadable gamma ramps in hardware.", "Initialization error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            Platform.exit();
            System.exit(1);
        }
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setTitle("GammaJ");
        stage.setScene(scene);

        stage.show();
        stage.setMaxWidth(stage.getWidth());
        stage.setMaxHeight(stage.getHeight());
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                viewModel.saveAndReset();
                Platform.exit();
                System.exit(0);
            }
        });

        if (OperatingSystemUtility.getOperatingSystemType() != OperatingSystemUtility.OperatingSystem.Windows) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.initOwner(stage);
            errorAlert.setTitle("Windows not detected");
            errorAlert.setHeaderText("Detected operating system other than Windows");
            errorAlert.setContentText("Current version does not support operating systems other than Windows family (GDI32.dll).");
            errorAlert.showAndWait();
        }

        GammaRegistry gammaRegistry = new GammaRegistry();
        if (!gammaRegistry.isGammaExtensionInstalled()) {
            try {
                gammaRegistry.installGammaExtension();
                Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                infoAlert.initOwner(stage);
                infoAlert.setTitle("Extended gamma installed");
                infoAlert.setHeaderText("System needs reboot");
                infoAlert.setContentText("Extended gamma has been installed and system reboot is needed. Entirely custom color adjustment will be then enabled.");
                infoAlert.showAndWait();
            } catch (Exception ex) {
                Logger.getLogger(GammaRegistry.class.getName()).log(Level.SEVERE, null, ex);
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.initOwner(stage);
                errorAlert.setTitle("Extended gamma not installed");
                errorAlert.setHeaderText("Cannot access registry");
                errorAlert.setContentText("Extended gamma has not been installed. This application needs to add registry entry. Only then entirely custom color adjustment will be enabled.");
                errorAlert.showAndWait();
            }
        }
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

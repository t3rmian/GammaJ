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

import io.github.t3r1jj.gammaj.hotkeys.HotkeysRunner;
import io.github.t3r1jj.gammaj.controllers.ApplicationControllerFactory;
import io.github.t3r1jj.gammaj.jna.GammaRegistry;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GammaJ extends Application {

    private TrayManager trayManager;

    @Override
    public void start(Stage stage) throws Exception {
        GammaRegistry gammaRegistry = new GammaRegistry();
        gammaRegistry.installGammaExtension();

        trayManager = new TrayManager(stage);
        FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("/fxml/Scene.fxml"));
        fXMLLoader.setControllerFactory(new ApplicationControllerFactory(getHostServices(), trayManager, HotkeysRunner.getInstance()));
        Parent root = fXMLLoader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setTitle("GammaJ");
        stage.setScene(scene);

        stage.show();
        stage.setMaxWidth(stage.getWidth());
        stage.setMaxHeight(stage.getHeight());
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

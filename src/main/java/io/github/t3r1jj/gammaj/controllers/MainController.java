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

import io.github.t3r1jj.gammaj.info.Library;
import io.github.t3r1jj.gammaj.info.ProjectInfo;
import io.github.t3r1jj.gammaj.model.ColorTemperature;
import io.github.t3r1jj.gammaj.model.Gamma.Channel;
import io.github.t3r1jj.gammaj.model.Display;
import io.github.t3r1jj.gammaj.model.DisplayUtil;
import io.github.t3r1jj.gammaj.model.MultiDisplay;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.StringConverter;

public class FXMLController implements Initializable {

    private Display currentScreen;
    private final HostServices hostServices;
    private final TrayRunnable trayRunnable;

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
    private Canvas canvas;
    private static final double GAMMA_SLIDER_DEFAULT_VALUE = 1;
    private static final double BRIGHTNESS_SLIDER_DEFAULT_VALUE = 50;
    private static final double CONTRAST_BILATERAL_SLIDER_DEFAULT_VALUE = 50;
    private static final double CONTRAST_UNILATERAL_SLIDER_DEFAULT_VALUE = 50;
    private static final Paint GAMMA_CANVAS_BACKGROUND_COLOR = Color.WHITE;
    private static final Paint[] GAMMA_CANVAS_LINE_COLOR = new Paint[]{Color.RED, Color.GREEN, Color.BLUE};
    private static final double TEMPERATURE_SLIDER_DEFAULT_VALUE = 6500;

    public FXMLController(HostServices hostServices, TrayRunnable trayRunnable) {
        this.hostServices = hostServices;
        this.trayRunnable = trayRunnable;
    }

    @FXML
    private void handleResetButtonAction(ActionEvent event) {
        System.out.println("Reset button clicked!");
        resetSliders();
        currentScreen.resetGammaRamp();
        drawGammaLine();
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
            trayRunnable.enableTray(trayEnabled);
        } catch (IOException | AWTException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleAboutAction(ActionEvent event) {
        ProjectInfo projectInfo = new ProjectInfo();
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(projectInfo.getAboutHeader());
        alert.setContentText(projectInfo.getAboutContent());
        alert.showAndWait();
    }

    @FXML
    private void handleLicenseAction(ActionEvent event) {
        ProjectInfo projectInfo = new ProjectInfo();
        Alert alert = new Alert(AlertType.INFORMATION);
        List<Library> libraries = projectInfo.getLibrariesUsed();
        StringBuilder stringBuilder = new StringBuilder();
        List<ButtonType> buttons = new ArrayList<>();
        ButtonType okButton = new ButtonType("OK", ButtonData.OK_DONE);
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
        Alert licenseAlert = new Alert(AlertType.INFORMATION);
        licenseAlert.setTitle(pressedLibrary.nameLong);
        licenseAlert.setHeaderText(null);
        licenseAlert.setContentText(pressedLibrary.licenseLong);
        ButtonType urlButton = new ButtonType("Website");
        licenseAlert.getButtonTypes().addAll(urlButton);
        Optional<ButtonType> licenseResult = licenseAlert.showAndWait();
        if (licenseResult.get().equals(urlButton)) {
            hostServices.showDocument(pressedLibrary.url);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DisplayUtil screenUtil = new DisplayUtil();
        MultiDisplay wholeScreen = screenUtil.getWholeScreen();
        List<Display> screens = wholeScreen.getScreens();
        screenComboBox.getItems().add(wholeScreen);
        screenComboBox.getItems().addAll(screens);
        currentScreen = wholeScreen;
        screenComboBox.getSelectionModel().select(currentScreen);

        screenComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Display>() {

            @Override
            public void changed(ObservableValue<? extends Display> observable, Display oldValue, Display newValue) {
                currentScreen = newValue;
                currentScreen.reinitialize();
                drawGammaLine();
            }

        });

        gammaSlider.setValue(GAMMA_SLIDER_DEFAULT_VALUE);
        gammaSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                currentScreen.setGamma(Channel.RED, newValue.doubleValue());
                currentScreen.reinitialize();
                drawGammaLine();
            }

        });
        brightnessSlider.setValue(BRIGHTNESS_SLIDER_DEFAULT_VALUE);
        brightnessSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                currentScreen.setBrightness(Channel.RED, newValue.doubleValue());
                currentScreen.reinitialize();
                drawGammaLine();
            }

        });
        contrastBilateralSlider.setValue(CONTRAST_BILATERAL_SLIDER_DEFAULT_VALUE);
        contrastBilateralSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                currentScreen.setContrastBilateral(Channel.RED, newValue.doubleValue());
                currentScreen.reinitialize();
                drawGammaLine();
            }

        });
        contrastUnilateralSlider.setValue(CONTRAST_UNILATERAL_SLIDER_DEFAULT_VALUE);
        contrastUnilateralSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                currentScreen.setContrastUnilateral(Channel.RED, newValue.doubleValue());
                currentScreen.reinitialize();
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

        temperatureSlider.setValue(TEMPERATURE_SLIDER_DEFAULT_VALUE);
        temperatureSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                currentScreen.setTemperature(new ColorTemperature(newValue.doubleValue()));
                currentScreen.reinitialize();
                drawGammaLine();
                System.out.println(newValue.doubleValue());
            }

        });

        drawGammaLine();

    }

    private void drawGammaLine() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(GAMMA_CANVAS_BACKGROUND_COLOR);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        graphicsContext.setLineWidth(1);
        double[][] gammaRamp = currentScreen.getGammaRamp();
        for (int i = 0; i < gammaRamp.length; i++) {
            graphicsContext.setStroke(GAMMA_CANVAS_LINE_COLOR[i]);
            graphicsContext.strokeLine(0, (1 - gammaRamp[i][0]) * canvas.getWidth(), 0, (1 - gammaRamp[i][0]) * canvas.getWidth());
            for (int x = 1; x < canvas.getWidth(); x++) {
                graphicsContext.strokeLine(x - 1, (1 - gammaRamp[i][x - 1]) * canvas.getWidth(), x, (1 - gammaRamp[i][x]) * canvas.getWidth());
            }
        }
    }

    private void resetSliders() {
        gammaSlider.setValue(GAMMA_SLIDER_DEFAULT_VALUE);
        brightnessSlider.setValue(BRIGHTNESS_SLIDER_DEFAULT_VALUE);
        contrastBilateralSlider.setValue(CONTRAST_BILATERAL_SLIDER_DEFAULT_VALUE);
        contrastUnilateralSlider.setValue(CONTRAST_UNILATERAL_SLIDER_DEFAULT_VALUE);
        temperatureSlider.setValue(TEMPERATURE_SLIDER_DEFAULT_VALUE);
    }

}

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

import io.github.t3r1jj.gammaj.model.ColorTemperature;
import io.github.t3r1jj.gammaj.model.Gamma.Channel;
import io.github.t3r1jj.gammaj.model.Screen;
import io.github.t3r1jj.gammaj.model.ScreenUtil;
import io.github.t3r1jj.gammaj.model.WholeScreen;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class FXMLController implements Initializable {

    private Screen currentScreen;

    @FXML
    private ComboBox<Screen> screenComboBox;
    @FXML
    private Slider gammaSlider;
    @FXML
    private Slider brightnessSlider;
    @FXML
    private Slider contrastSlider;
    @FXML
    private Slider temperatureSlider;

    @FXML
    private Canvas canvas;
    private static final double GAMMA_SLIDER_DEFAULT_VALUE = 1;
    private static final double BRIGHTNESS_SLIDER_DEFAULT_VALUE = 50;
    private static final double CONTRAST_SLIDER_DEFAULT_VALUE = 50;
    private static final Paint GAMMA_CANVAS_BACKGROUND_COLOR = Color.WHITE;
    private static final Paint[] GAMMA_CANVAS_LINE_COLOR = new Paint[]{Color.RED, Color.GREEN, Color.BLUE};
    private static final double TEMPERATURE_SLIDER_DEFAULT_VALUE = 6500;

    @FXML
    private void handleResetButtonAction(ActionEvent event) {
        System.out.println("Reset button clicked!");
        currentScreen.resetGammaRamp();
        resetSliders();
        drawGammaLine();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ScreenUtil screenUtil = new ScreenUtil();
        WholeScreen wholeScreen = screenUtil.getWholeScreen();
        List<Screen> screens = wholeScreen.getScreens();
        screenComboBox.getItems().add(wholeScreen);
        screenComboBox.getItems().addAll(screens);
        currentScreen = wholeScreen;
        screenComboBox.getSelectionModel().select(currentScreen);

        screenComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Screen>() {

            @Override
            public void changed(ObservableValue<? extends Screen> observable, Screen oldValue, Screen newValue) {
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
                currentScreen.setBrightness(Channel.RED, newValue.doubleValue() / 100f);
                currentScreen.reinitialize();
                drawGammaLine();
            }

        });
        contrastSlider.setValue(CONTRAST_SLIDER_DEFAULT_VALUE);
        contrastSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                currentScreen.setContrast(Channel.RED, (newValue.doubleValue() - 50) / 50f);
                currentScreen.reinitialize();
                drawGammaLine();
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
        contrastSlider.setValue(CONTRAST_SLIDER_DEFAULT_VALUE);
        temperatureSlider.setValue(TEMPERATURE_SLIDER_DEFAULT_VALUE);
    }

}

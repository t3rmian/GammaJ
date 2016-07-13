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

import io.github.t3r1jj.gammaj.model.ColorTemperature;
import io.github.t3r1jj.gammaj.model.Gamma.Channel;
import io.github.t3r1jj.gammaj.model.Display;
import io.github.t3r1jj.gammaj.model.DisplayUtil;
import io.github.t3r1jj.gammaj.model.MultiDisplay;
import java.net.URL;
import java.util.EnumSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.StringConverter;

public class MainController implements Initializable {

    private Display currentDisplay;
    private final Set<Channel> selectedChannels = EnumSet.allOf(Channel.class);

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
    private CheckBox redCheckBox;
    @FXML
    private CheckBox greenCheckBox;
    @FXML
    private CheckBox blueCheckBox;
    @FXML
    private Canvas canvas;

    private static final double GAMMA_SLIDER_DEFAULT_VALUE = 1;
    private static final double BRIGHTNESS_SLIDER_DEFAULT_VALUE = 50;
    private static final double CONTRAST_BILATERAL_SLIDER_DEFAULT_VALUE = 50;
    private static final double CONTRAST_UNILATERAL_SLIDER_DEFAULT_VALUE = 50;
    private static final Paint GAMMA_CANVAS_BACKGROUND_COLOR = Color.WHITE;
    private static final Paint[] GAMMA_CANVAS_LINE_COLOR = new Paint[]{Color.RED, Color.GREEN, Color.BLUE};
    private static final double TEMPERATURE_SLIDER_DEFAULT_VALUE = 6500;

    @FXML
    private void handleResetButtonAction(ActionEvent event) {
        System.out.println("Reset button clicked!");
        resetSliders();
        currentDisplay.resetGammaRamp();
        drawGammaLine();
    }

    @FXML
    private void handleInvertButtonAction(ActionEvent event) {
        for (Channel channel : selectedChannels) {
            currentDisplay.invertGammaRamp(channel);
        }
        currentDisplay.reinitialize();
        drawGammaLine();
    }

    @FXML
    private void handleSaveProfileAsButtonAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Not implemented");
        alert.setHeaderText("Not implemented yet");
        alert.showAndWait();
    }

    @FXML
    private void handleDeleteProfileButtonAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Not implemented");
        alert.setHeaderText("Not implemented yet");
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        redCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean nowSelected) {
                if (nowSelected) {
                    selectedChannels.add(Channel.RED);
                } else {
                    selectedChannels.remove(Channel.RED);
                }
            }
        });
        greenCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean nowSelected) {
                if (nowSelected) {
                    selectedChannels.add(Channel.RED);
                } else {
                    selectedChannels.remove(Channel.GREEN);
                }
            }
        });
        blueCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean nowSelected) {
                if (nowSelected) {
                    selectedChannels.add(Channel.RED);
                } else {
                    selectedChannels.remove(Channel.BLUE);
                }
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
                currentDisplay.reinitialize();
                drawGammaLine();
            }

        });

        gammaSlider.setValue(GAMMA_SLIDER_DEFAULT_VALUE);
        gammaSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                for (Channel channel : selectedChannels) {
                    currentDisplay.setGamma(channel, newValue.doubleValue());
                }
                currentDisplay.reinitialize();
                drawGammaLine();
            }

        });
        brightnessSlider.setValue(BRIGHTNESS_SLIDER_DEFAULT_VALUE);
        brightnessSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                for (Channel channel : selectedChannels) {
                    currentDisplay.setBrightness(channel, newValue.doubleValue());
                }
                currentDisplay.reinitialize();
                drawGammaLine();
            }

        });
        contrastBilateralSlider.setValue(CONTRAST_BILATERAL_SLIDER_DEFAULT_VALUE);
        contrastBilateralSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                for (Channel channel : selectedChannels) {
                    currentDisplay.setContrastBilateral(channel, newValue.doubleValue());
                }
                currentDisplay.reinitialize();
                drawGammaLine();
            }

        });
        contrastUnilateralSlider.setValue(CONTRAST_UNILATERAL_SLIDER_DEFAULT_VALUE);
        contrastUnilateralSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                for (Channel channel : selectedChannels) {
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

        temperatureSlider.setValue(TEMPERATURE_SLIDER_DEFAULT_VALUE);
        temperatureSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                currentDisplay.setTemperature(new ColorTemperature(newValue.doubleValue()));
                currentDisplay.reinitialize();
                drawGammaLine();
                System.out.println(newValue.doubleValue());
            }

        });

        Bindings.bindBidirectional(gammaSlider.valueProperty(), gammaSpinner.getValueFactory().valueProperty());
        Bindings.bindBidirectional(contrastBilateralSlider.valueProperty(), contrastBilateralSpinner.getValueFactory().valueProperty());
        Bindings.bindBidirectional(contrastUnilateralSlider.valueProperty(), contrastUnilateralSpinner.getValueFactory().valueProperty());
        Bindings.bindBidirectional(brightnessSlider.valueProperty(), brightnessSpinner.getValueFactory().valueProperty());
        Bindings.bindBidirectional(temperatureSlider.valueProperty(), temperatureSpinner.getValueFactory().valueProperty());

        drawGammaLine();

    }

    private void drawGammaLine() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(GAMMA_CANVAS_BACKGROUND_COLOR);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        graphicsContext.setLineWidth(1);
        double[][] gammaRamp = currentDisplay.getGammaRamp();
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

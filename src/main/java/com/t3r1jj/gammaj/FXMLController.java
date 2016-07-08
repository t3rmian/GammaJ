package com.t3r1jj.gammaj;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/*
 Copyright (C) 2016 Damian Terlecki

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
public class FXMLController implements Initializable {

    private static final Model model = new Model();

    float center = 1;

    @FXML
    private Label label;
    @FXML
    private Slider gammaSlider;
    @FXML
    private Slider brightnessSlider;
    @FXML
    private Slider contrastSlider;

    @FXML
    private Canvas canvas;
    private static final double GAMMA_SLIDER_DEFAULT_VALUE = 1;
    private static final double BRIGHTNESS_SLIDER_DEFAULT_VALUE = 50;
    private static final double CONTRAST_SLIDER_DEFAULT_VALUE = 50;
    private static final Paint GAMMA_CANVAS_BACKGROUND_COLOR = Color.WHITE;
    private static final Paint GAMMA_CANVAS_LINE_COLOR = Color.BLACK;

    @FXML
    private void handleResetButtonAction(ActionEvent event) {
        System.out.println("Reset button clicked!");
        model.resetModel();
        model.resetGammaRamp();
        resetSliders();
        drawGammaLine();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gammaSlider.setValue(GAMMA_SLIDER_DEFAULT_VALUE);
        gammaSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                model.setGamma(newValue.floatValue());
                model.calculateGammaRamp();
                drawGammaLine();
            }

        });
        brightnessSlider.setValue(BRIGHTNESS_SLIDER_DEFAULT_VALUE);
        brightnessSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                model.setBrightness(newValue.floatValue() / 100f);
                model.calculateGammaRamp();
                drawGammaLine();
            }

        });
        contrastSlider.setValue(CONTRAST_SLIDER_DEFAULT_VALUE);
        contrastSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                model.setContrast((newValue.floatValue() - 50) / 50f);
                model.calculateGammaRamp();
                drawGammaLine();
            }

        });

        drawGammaLine();

    }

    private void drawGammaLine() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(GAMMA_CANVAS_BACKGROUND_COLOR);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        graphicsContext.setStroke(GAMMA_CANVAS_LINE_COLOR);
        graphicsContext.setLineWidth(1);
        float[][] gammaZZZ = model.getGammaZZZZZ();
        graphicsContext.strokeLine(0, (1 - gammaZZZ[0][0]) * canvas.getWidth(), 0, (1 - gammaZZZ[0][0]) * canvas.getWidth());
        for (int x = 1; x < canvas.getWidth(); x++) {
            graphicsContext.strokeLine(x - 1, (1 - gammaZZZ[0][x-1]) * canvas.getWidth(), x, (1 - gammaZZZ[0][x]) * canvas.getWidth());
        }
    }

    private void resetSliders() {
        gammaSlider.setValue(GAMMA_SLIDER_DEFAULT_VALUE);
        brightnessSlider.setValue(BRIGHTNESS_SLIDER_DEFAULT_VALUE);
        contrastSlider.setValue(CONTRAST_SLIDER_DEFAULT_VALUE);
    }

}

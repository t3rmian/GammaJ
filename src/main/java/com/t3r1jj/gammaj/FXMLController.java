package com.t3r1jj.gammaj;

import java.net.URL;
import java.util.Arrays;
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

    float center = 3;

    @FXML
    private Label label;
    @FXML
    private Slider gammaSlider;

    @FXML
    private Canvas canvas;

    @FXML
    private void handleResetButtonAction(ActionEvent event) {
        System.out.println("Reset button clicked!");
        model.resetGamma();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gammaSlider.setValue(3);
        gammaSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                model.setGamma(newValue.floatValue() - center);
                drawGammaLine();
            }

        });

        drawGammaLine();

    }

    private void drawGammaLine() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.RED);
        graphicsContext.fillRect(0, 0, 256, 256);

        graphicsContext.setStroke(Color.BLUE);
        graphicsContext.setFill(Color.GREEN);
        graphicsContext.setLineWidth(2);
        float[][] gammaZZZ = model.getGammaZZZZZ();
        for (int x = 0; x < 256; x++) {
            graphicsContext.strokeLine(x, (1 - gammaZZZ[0][x]) * 256, x, (1 - gammaZZZ[0][x]) * 256);
        }
    }
}

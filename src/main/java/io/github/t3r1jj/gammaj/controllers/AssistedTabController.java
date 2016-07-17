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

import io.github.t3r1jj.gammaj.hotkeys.HotkeyPollerThread;
import io.github.t3r1jj.gammaj.model.ColorProfile;
import io.github.t3r1jj.gammaj.model.Gamma;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.util.StringConverter;

public class AssistedTabController extends AbstractTabController {
    
    @FXML
    private Canvas canvas;
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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        temperatureFactory.setIsSrgb(viewModel.getIsSrgbProperty().get());
        viewModel.getIsSrgbProperty().addListener(new ChangeListener<Boolean>() {
            
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean wasSrgb, Boolean isSrgb) {
                if (!loadingProfile) {
                    resetProfile();
                    System.out.println("SETTING SRGB: " + isSrgb);
                    temperatureFactory.setIsSrgb(isSrgb);
                    viewModel.getCurrentDisplayProperty().get().setTemperature(temperatureFactory.createTemperature(temperatureSlider.valueProperty().getValue()));
                    viewModel.getCurrentDisplayProperty().get().reinitialize();
                    gammaRampPainter.drawGammaRamp(canvas, viewModel.getCurrentDisplayProperty().get());
                }
            }
        });
        
        gammaSlider.valueProperty().addListener(new ChangeListener<Number>() {
            
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!loadingProfile) {
                    resetProfile();
                    for (Gamma.Channel channel : viewModel.getSelectedChannelsProperty()) {
                        viewModel.getCurrentDisplayProperty().get().setGamma(channel, newValue.doubleValue());
                    }
                    viewModel.getCurrentDisplayProperty().get().reinitialize();
                    gammaRampPainter.drawGammaRamp(canvas, viewModel.getCurrentDisplayProperty().get());
                }
            }
            
        });
        brightnessSlider.valueProperty().addListener(new ChangeListener<Number>() {
            
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!loadingProfile) {
                    resetProfile();
                    for (Gamma.Channel channel : viewModel.getSelectedChannelsProperty()) {
                        viewModel.getCurrentDisplayProperty().get().setBrightness(channel, newValue.doubleValue());
                    }
                    viewModel.getCurrentDisplayProperty().get().reinitialize();
                    gammaRampPainter.drawGammaRamp(canvas, viewModel.getCurrentDisplayProperty().get());
                }
            }
            
        });
        contrastBilateralSlider.valueProperty().addListener(new ChangeListener<Number>() {
            
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!loadingProfile) {
                    resetProfile();
                    for (Gamma.Channel channel : viewModel.getSelectedChannelsProperty()) {
                        viewModel.getCurrentDisplayProperty().get().setContrastBilateral(channel, newValue.doubleValue());
                    }
                    viewModel.getCurrentDisplayProperty().get().reinitialize();
                    gammaRampPainter.drawGammaRamp(canvas, viewModel.getCurrentDisplayProperty().get());
                }
            }
            
        });
        contrastUnilateralSlider.valueProperty().addListener(new ChangeListener<Number>() {
            
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!loadingProfile) {
                    resetProfile();
                    for (Gamma.Channel channel : viewModel.getSelectedChannelsProperty()) {
                        viewModel.getCurrentDisplayProperty().get().setContrastUnilateral(channel, newValue.doubleValue());
                    }
                    viewModel.getCurrentDisplayProperty().get().reinitialize();
                    gammaRampPainter.drawGammaRamp(canvas, viewModel.getCurrentDisplayProperty().get());
                }
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
        
        temperatureSlider.valueProperty().addListener(new ChangeListener<Number>() {
            
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                resetProfile();
                if (!loadingProfile) {
                    viewModel.getCurrentDisplayProperty().get().setTemperature(temperatureFactory.createTemperature(newValue.doubleValue()));
                    viewModel.getCurrentDisplayProperty().get().reinitialize();
                }
                gammaRampPainter.drawGammaRamp(canvas, viewModel.getCurrentDisplayProperty().get());
            }
            
        });
        
        
        Bindings.bindBidirectional(gammaSlider.valueProperty(), gammaSpinner.getValueFactory().valueProperty());
        Bindings.bindBidirectional(contrastBilateralSlider.valueProperty(), contrastBilateralSpinner.getValueFactory().valueProperty());
        Bindings.bindBidirectional(contrastUnilateralSlider.valueProperty(), contrastUnilateralSpinner.getValueFactory().valueProperty());
        Bindings.bindBidirectional(brightnessSlider.valueProperty(), brightnessSpinner.getValueFactory().valueProperty());
        Bindings.bindBidirectional(temperatureSlider.valueProperty(), temperatureSpinner.getValueFactory().valueProperty());

        gammaRampPainter.drawGammaRamp(canvas, viewModel.getCurrentDisplayProperty().get());
    }
    
    @Override
    protected void loadLocalProfile() {
        loadingProfile = true;
        ColorProfile colorProfile = viewModel.getCurrentDisplayProperty().get().getColorProfile();
        Gamma.Channel selectedChannel = viewModel.getSelectedChannelsProperty().iterator().next();
        System.out.println("Starting profile load: " + colorProfile + ", gamma: " + colorProfile.getGamma(selectedChannel));
        gammaSpinner.getValueFactory().setValue(colorProfile.getGamma(selectedChannel));
        contrastBilateralSpinner.getValueFactory().setValue(colorProfile.getContrastBilateral(selectedChannel));
        contrastUnilateralSpinner.getValueFactory().setValue(colorProfile.getContrastUnilateral(selectedChannel));
        brightnessSpinner.getValueFactory().setValue(colorProfile.getBrightness(selectedChannel));
        temperatureSpinner.getValueFactory().setValue(colorProfile.getTemperature().getTemperature());
        viewModel.getIsSrgbProperty().set(colorProfile.isTemperatureSrgb());
        HotkeyPollerThread hotkey = colorProfile.getHotkey();
        hotkeyInput.setHotkey(hotkey);
        viewModel.getCurrentDisplayProperty().get().loadModelFromProfile(false);
        viewModel.getCurrentDisplayProperty().get().reinitialize();
        gammaRampPainter.drawGammaRamp(canvas, viewModel.getCurrentDisplayProperty().get());
        loadingProfile = false;
    }
    
    @Override
    protected void resetColorAdjustment() {
        gammaSlider.setValue(Gamma.DEFAULT_GAMMA);
        brightnessSlider.setValue(Gamma.DEFAULT_BRIGHTNESS);
        contrastBilateralSlider.setValue(Gamma.DEFAULT_CONTRAST_BILATERAL);
        contrastUnilateralSlider.setValue(Gamma.DEFAULT_CONTRAST_UNILATERAL);
        temperatureSlider.setValue(Gamma.DEFAULT_TEMPERATURE);
    }
    
}

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

import io.github.t3r1jj.gammaj.StringTemperatureConverter;
import io.github.t3r1jj.gammaj.hotkeys.HotkeyPollerThread;
import io.github.t3r1jj.gammaj.model.ColorProfile;
import io.github.t3r1jj.gammaj.model.Gamma;
import io.github.t3r1jj.gammaj.ViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;

public class AssistedTabController extends AbstractTabController {

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

    public AssistedTabController(ViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        temperatureFactory.setIsSrgb(viewModel.isSrgbProperty().get());
        viewModel.isSrgbProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean wasSrgb, Boolean isSrgb) {
                if (!loadingProfile) {
                    resetProfile();
                    temperatureFactory.setIsSrgb(isSrgb);
                    viewModel.getCurrentDisplay().setTemperature(temperatureFactory.createTemperature(temperatureSlider.valueProperty().getValue()));
                    viewModel.getCurrentDisplay().reinitialize();
                    drawGammaRamp();
                }
            }
        });

        initializeSliders();
        bindSliders();

        if (viewModel.assistedAdjustmentProperty().get()) {
            viewModel.setCurrentProfile(viewModel.getCurrentDisplay().getColorProfile());
        }
        viewModel.assistedAdjustmentProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean nowAssisted) {
                if (nowAssisted) {
                    if (isCurrentDisplayProfileManual()) {
                        resetProfile();
                    }
                    loadLocalProfile();
                    updateRgbRadioButtons();
                    drawGammaRamp();
                    addTabListeners();
                } else {
                    removeTabListeners();
                }
            }
        });

    }

    private boolean isCurrentDisplayProfileManual() {
        return !isCurrentProfileDefault() && !viewModel.getCurrentDisplay().getColorProfile().getModeIsAssissted();
    }

    private void initializeSliders() {
        gammaSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!loadingProfile) {
                    resetProfile();
                    for (Gamma.Channel channel : viewModel.selectedChannelsProperty()) {
                        viewModel.getCurrentDisplay().setGamma(channel, newValue.doubleValue());
                    }
                    viewModel.getCurrentDisplay().reinitialize();
                    drawGammaRamp();
                }
            }

        });
        brightnessSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!loadingProfile) {
                    resetProfile();
                    for (Gamma.Channel channel : viewModel.selectedChannelsProperty()) {
                        viewModel.getCurrentDisplay().setBrightness(channel, newValue.doubleValue());
                    }
                    viewModel.getCurrentDisplay().reinitialize();
                    drawGammaRamp();
                }
            }

        });
        contrastBilateralSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!loadingProfile) {
                    resetProfile();
                    for (Gamma.Channel channel : viewModel.selectedChannelsProperty()) {
                        viewModel.getCurrentDisplay().setContrastBilateral(channel, newValue.doubleValue());
                    }
                    viewModel.getCurrentDisplay().reinitialize();
                    drawGammaRamp();
                }
            }

        });
        contrastUnilateralSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!loadingProfile) {
                    resetProfile();
                    for (Gamma.Channel channel : viewModel.selectedChannelsProperty()) {
                        viewModel.getCurrentDisplay().setContrastUnilateral(channel, newValue.doubleValue());
                    }
                    viewModel.getCurrentDisplay().reinitialize();
                    drawGammaRamp();
                }
            }

        });

        temperatureSlider.setLabelFormatter(new StringTemperatureConverter());

        temperatureSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                resetProfile();
                if (!loadingProfile) {
                    viewModel.getCurrentDisplay().setTemperature(temperatureFactory.createTemperature(newValue.doubleValue()));
                    viewModel.getCurrentDisplay().reinitialize();
                }
                drawGammaRamp();
            }

        });
    }

    private void bindSliders() {
        Bindings.bindBidirectional(gammaSlider.valueProperty(), gammaSpinner.getValueFactory().valueProperty());
        Bindings.bindBidirectional(contrastBilateralSlider.valueProperty(), contrastBilateralSpinner.getValueFactory().valueProperty());
        Bindings.bindBidirectional(contrastUnilateralSlider.valueProperty(), contrastUnilateralSpinner.getValueFactory().valueProperty());
        Bindings.bindBidirectional(brightnessSlider.valueProperty(), brightnessSpinner.getValueFactory().valueProperty());
        Bindings.bindBidirectional(temperatureSlider.valueProperty(), temperatureSpinner.getValueFactory().valueProperty());
    }

    @Override
    protected void handleLoadLocalProfile() {
        if (isCurrentDisplayProfileManual()) {
            viewModel.assistedAdjustmentProperty().set(false);
        } else {
            loadLocalProfile();
        }
    }

    private void loadLocalProfile() {
        loadingProfile = true;
        ColorProfile colorProfile = viewModel.getCurrentDisplay().getColorProfile();
        Gamma.Channel selectedChannel = viewModel.selectedChannelsProperty().iterator().next();
        gammaSpinner.getValueFactory().setValue(colorProfile.getGamma(selectedChannel));
        contrastBilateralSpinner.getValueFactory().setValue(colorProfile.getContrastBilateral(selectedChannel));
        contrastUnilateralSpinner.getValueFactory().setValue(colorProfile.getContrastUnilateral(selectedChannel));
        brightnessSpinner.getValueFactory().setValue(colorProfile.getBrightness(selectedChannel));
        temperatureSpinner.getValueFactory().setValue(colorProfile.getTemperature().getTemperature());
        viewModel.isSrgbProperty().set(colorProfile.isTemperatureSrgb());
        HotkeyPollerThread hotkey = colorProfile.getHotkey();
        hotkeyInput.setHotkey(hotkey);
        viewModel.getCurrentDisplay().loadModelFromProfile(false);
        viewModel.getCurrentDisplay().reinitialize();
        drawGammaRamp();
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

    @Override
    protected void bindTabListeners() {
        if (viewModel.assistedAdjustmentProperty().get()) {
            addTabListeners();
        }
    }

    @Override
    protected void handleInvertButtonAction(ActionEvent event) {
        if (!loadingProfile) {
            resetProfile();
            for (Gamma.Channel channel : viewModel.selectedChannelsProperty()) {
                viewModel.getCurrentDisplay().invertGammaRamp(channel);
            }
            viewModel.getCurrentDisplay().reinitialize();
            drawGammaRamp();
        }
    }

    @Override
    protected void saveModeSettings(ColorProfile newColorProfile) {
        newColorProfile.setModeIsAssissted(true);
    }

    @Override
    protected void handleRedSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        super.handleRedSelectionChange(obs, wasPreviouslySelected, isNowSelected);
        if (isNowSelected) {
            handleLoadLocalProfile();
        }
    }

    @Override
    protected void handleGreenSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        super.handleGreenSelectionChange(obs, wasPreviouslySelected, isNowSelected);
        if (isNowSelected) {
            handleLoadLocalProfile();
        }
    }

    @Override
    protected void handleBlueSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        super.handleBlueSelectionChange(obs, wasPreviouslySelected, isNowSelected);
        if (isNowSelected) {
            handleLoadLocalProfile();
        }
    }

    @Override
    protected void handleRgbSelectionChange(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
        super.handleRgbSelectionChange(obs, wasPreviouslySelected, isNowSelected);
        if (isNowSelected) {
            handleLoadLocalProfile();
        }
    }

}

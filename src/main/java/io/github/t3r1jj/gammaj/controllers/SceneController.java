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

import io.github.t3r1jj.gammaj.ViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class SceneController implements Initializable {

    ViewModel viewModel = ViewModel.getInstance();
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab assistedTab;
    @FXML
    private Tab manualTab;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (viewModel.assistedAdjustmentProperty().get()) {
            tabPane.getSelectionModel().select(assistedTab);
        } else {
            tabPane.getSelectionModel().select(manualTab);
        }
        
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab nowSelectedTab) {
                if (nowSelectedTab.equals(assistedTab)) {
                    viewModel.assistedAdjustmentProperty().set(true);
                } else {
                    viewModel.assistedAdjustmentProperty().set(false);
                }
            }
        });

        viewModel.assistedAdjustmentProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean nowAssisted) {
                if (nowAssisted) {
                    tabPane.getSelectionModel().select(assistedTab);
                } else {
                    tabPane.getSelectionModel().select(manualTab);
                }
            }
        });
    }

}

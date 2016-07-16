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

import io.github.t3r1jj.gammaj.hotkeys.HotkeysRunner;
import io.github.t3r1jj.gammaj.model.ColorProfile;
import io.github.t3r1jj.gammaj.hotkeys.HotkeyInputEventHandler;
import io.github.t3r1jj.gammaj.model.Gamma.Channel;
import io.github.t3r1jj.gammaj.model.Display;
import io.github.t3r1jj.gammaj.model.temperature.TemperatureSimpleFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class SceneController implements Initializable {

    private Display currentDisplay;
    private final Set<Channel> selectedChannels = EnumSet.allOf(Channel.class);
    private final List<ColorProfile> loadedProfiles = new ArrayList<>();
    private final HotkeysRunner hotkeysRunner;
    private HotkeyInputEventHandler hotkeyInput;
    private TemperatureSimpleFactory temperatureFactory = new TemperatureSimpleFactory("rgb");

    @FXML
    private MenuBarController menuBarController;
    @FXML
    private AssistedTabController assistedTabController;
    @FXML
    private ManualTabController manualTabController;
   

    public SceneController(HotkeysRunner hotkeysRunner) {
        this.hotkeysRunner = hotkeysRunner;
    }

    public static final Paint GAMMA_CANVAS_BACKGROUND_COLOR = Color.WHITE;
    public static final Paint[] GAMMA_CANVAS_LINE_COLOR = new Paint[]{Color.RED, Color.GREEN, Color.BLUE};

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }


}

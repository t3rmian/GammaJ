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
package io.github.t3r1jj.gammaj.tray;

import io.github.t3r1jj.gammaj.GammaJ;
import io.github.t3r1jj.gammaj.ViewModel;
import java.awt.AWTError;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;

public class TrayManager {

    private final Stage stage;
    private final String trayIconPath;
    private final ViewModel viewModel;
    private boolean trayEnabled = false;
    private TrayIcon trayIcon;
    private SystemTray systemTray;
    private ChangeListener changeListener;

    public TrayManager(Stage stage, String trayIconPath, ViewModel viewModel) {
        this.stage = stage;
        this.trayIconPath = trayIconPath;
        this.viewModel = viewModel;
        setExitAction();
    }

    public boolean isTrayEnabled() {
        return trayEnabled;
    }

    public void enableTray(boolean enable) throws IOException, AWTException {
        trayEnabled = enable;
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                setupTray();
            }
        });
    }

    private void setupTray() {
        try {
            Toolkit.getDefaultToolkit();
            if (!SystemTray.isSupported()) {
                return;
            }
            setupTrayIcon();
            setTrayOnIconify();
        } catch (AWTError | IOException | AWTException toolkitError) {
            Logger.getLogger(GammaJ.class.getName()).log(Level.SEVERE, null, toolkitError);
            stage.show();
        }
    }

    private void setupTrayIcon() throws IOException, AWTException {
        if (trayEnabled) {
            systemTray = SystemTray.getSystemTray();
            Image trayImageIcon = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(trayIconPath));
            Dimension trayIconSize = systemTray.getTrayIconSize();
            TrayIcon trayIcon = new TrayIcon(trayImageIcon.getScaledInstance(trayIconSize.width, trayIconSize.height, Image.SCALE_SMOOTH));
            trayIcon.addActionListener(new DeiconifyActionListener(this));
            trayIcon.setPopupMenu(new TrayPopupMenu(this, viewModel));
            systemTray.add(trayIcon);
        } else if (trayIcon != null) {
            systemTray.remove(trayIcon);
        }
    }

    void showStage() {
        stage.show();
        stage.toFront();
    }

    private void setTrayOnIconify() {
        if (trayEnabled) {
            changeListener = new ChangeListener<Boolean>() {

                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean wasIconified, Boolean nowIconified) {
                    if (nowIconified) {
                        stage.hide();
                        stage.setIconified(false);
                    }
                }
            };
            stage.iconifiedProperty().addListener(changeListener);
        } else if (changeListener != null) {
            stage.iconifiedProperty().removeListener(changeListener);
        }
    }

    private void setExitAction() {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                exit();
            }
        });
        Platform.setImplicitExit(false);
    }

    void exit() {
        viewModel.saveAndReset();
        Platform.exit();
        if (trayIcon != null) {
            systemTray.remove(trayIcon);
        }
        System.exit(0);
    }

}

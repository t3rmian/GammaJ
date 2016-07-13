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

import java.awt.AWTError;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class TrayRunnable implements Runnable {
    
    private static final String iconImagePath = "images/tray_icon.png";
    private final Stage stage;
    private boolean trayEnabled;
    private TrayIcon trayIcon;
    private SystemTray systemTray;
    private ChangeListener changeListener;
    
    public TrayRunnable(Stage stage, boolean trayEnabled) {
        this.stage = stage;
        this.trayEnabled = trayEnabled;
    }
    
    public boolean isTrayEnabled() {
        return trayEnabled;
    }
    
    @Override
    public void run() {
        try {
            Toolkit.getDefaultToolkit();
            if (!SystemTray.isSupported()) {
                return;
            }
            setupTrayIcon();
            setTrayOnIconify();
            setExitAction();
        } catch (AWTError | IOException | AWTException toolkitError) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, toolkitError);
            stage.show();
        }
    }
    
    private void setupTrayIcon() throws IOException, AWTException {
        if (trayEnabled) {
            Image imageIcon = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(iconImagePath));
            systemTray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(imageIcon);
            trayIcon.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    Platform.runLater(new Runnable() {
                        
                        @Override
                        public void run() {
                            showStage();
                        }
                    });
                }
            });
            systemTray.add(trayIcon);
        } else if (trayIcon != null) {
            systemTray.remove(trayIcon);
        }
    }
    
    private void showStage() {
        stage.show();
        stage.toFront();
    }
    
    public void enableTray(boolean enable) throws IOException, AWTException {
        trayEnabled = enable;
        setupTrayIcon();
        setTrayOnIconify();
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
                Platform.exit();
                systemTray.remove(trayIcon);
                System.exit(0);
            }
        });
        Platform.setImplicitExit(false);
    }
    
}

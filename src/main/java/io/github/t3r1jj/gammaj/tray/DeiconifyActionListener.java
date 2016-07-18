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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javafx.application.Platform;

class DeiconifyActionListener implements ActionListener {

    private final TrayManager trayManager;

    public DeiconifyActionListener(TrayManager trayManager) {
        this.trayManager = trayManager;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                trayManager.showStage();
            }
        });
    }
}

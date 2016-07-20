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

import io.github.t3r1jj.gammaj.model.ColorProfile;
import io.github.t3r1jj.gammaj.model.Display;
import io.github.t3r1jj.gammaj.ViewModel;
import java.awt.CheckboxMenuItem;
import java.awt.HeadlessException;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;

class TrayPopupMenu extends PopupMenu {

    private final ViewModel viewModel;
    private final Menu profilesMenu = new Menu("Profiles");
    private final Menu displaysMenu = new Menu("Displays");

    public TrayPopupMenu(final TrayManager trayManager, final ViewModel viewModel) throws HeadlessException {
        this.viewModel = viewModel;
        MenuItem deiconifyMenuItem = new MenuItem("GammaJ");
        deiconifyMenuItem.addActionListener(new DeiconifyActionListener(trayManager));
        add(deiconifyMenuItem);
        addSeparator();
        MenuItem resetMenuItem = new MenuItem("Reset");
        resetMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                viewModel.resetProperty().set(!viewModel.resetProperty().get());
            }
        });
        add(profilesMenu);
        reinitializeProfilesMenu();
        add(displaysMenu);
        reinitializeDisplaysMenu();
        addSeparator();
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                trayManager.exit();
            }
        });
        add(exitMenuItem);
        addModelListeners();
    }

    private void reinitializeProfilesMenu() {
        profilesMenu.removeAll();
        for (final ColorProfile colorProfile : viewModel.getLoadedProfiles()) {
            CheckboxMenuItem profileCheckbox = new CheckboxMenuItem(colorProfile.getName());
            if (colorProfile.equals(viewModel.getCurrentProfile())) {
                profileCheckbox.setState(true);
            }
            profileCheckbox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    viewModel.setCurrentProfile(colorProfile);
                }
            });
            profilesMenu.add(profileCheckbox);
        }
    }

    private void reinitializeDisplaysMenu() {
        for (final Display display : viewModel.getDisplays()) {
            CheckboxMenuItem displayCheckbox = new CheckboxMenuItem(display.getName());
            if (display.equals(viewModel.getCurrentDisplay())) {
                displayCheckbox.setState(true);
            }
            displayCheckbox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    viewModel.setCurrentDisplay(display);
                }
            });
            displaysMenu.add(displayCheckbox);
        }
    }

    private void addModelListeners() {
        viewModel.loadedProfilesProperty().addListener(new ListChangeListener<ColorProfile>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends ColorProfile> c) {
                reinitializeProfilesMenu();
            }
        });
        viewModel.currentProfileProperty().addListener(new ChangeListener<ColorProfile>() {

            @Override
            public void changed(ObservableValue<? extends ColorProfile> observable, ColorProfile oldValue, ColorProfile newValue) {
                reinitializeProfilesMenu();
            }
        });
        viewModel.currentDisplayProperty().addListener(new ChangeListener<Display>() {

            @Override
            public void changed(ObservableValue<? extends Display> observable, Display oldValue, Display newValue) {
                reinitializeDisplaysMenu();
            }
        });
    }

}

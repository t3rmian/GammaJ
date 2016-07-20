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
import io.github.t3r1jj.gammaj.ViewModel;
import io.github.t3r1jj.gammaj.tray.TrayManager;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import javafx.application.HostServices;
import javafx.util.Callback;

public class ApplicationControllerFactory implements Callback<Class<?>, Object> {

    private final HostServices hostServices;
    private final TrayManager trayManager;
    private final ViewModel viewModel;

    public ApplicationControllerFactory(HostServices hostServices, TrayManager trayRunnable, ViewModel viewModel) {
        this.hostServices = hostServices;
        this.trayManager = trayRunnable;
        this.viewModel = viewModel;
    }

    @Override
    public Object call(Class<?> param) {
        try {
            for (Constructor<?> constructor : param.getConstructors()) {
                if (constructor.getParameterCount() == 1) {
                    if (constructor.getParameterTypes()[0].equals(HostServices.class)) {
                        return constructor.newInstance(hostServices);
                    } else if (constructor.getParameterTypes()[0].equals(HotkeysRunner.class)) {
                        return constructor.newInstance(viewModel);
                    } else if (constructor.getParameterTypes()[0].equals(ViewModel.class)) {
                        return constructor.newInstance(viewModel);
                    }
                } else if (constructor.getParameterCount() == 2 && Arrays.equals(constructor.getParameterTypes(), new Class<?>[]{HostServices.class, TrayManager.class})) {
                    return constructor.newInstance(hostServices, trayManager);
                } else if (constructor.getParameterCount() == 3 && Arrays.equals(constructor.getParameterTypes(), new Class<?>[]{HostServices.class, TrayManager.class, ViewModel.class})) {
                    return constructor.newInstance(hostServices, trayManager, viewModel);
                }
            }
            return param.newInstance();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}

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

import java.net.URL;
import java.util.ResourceBundle;

public class ManualTabController extends AbstractTabController {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        gammaRampPainter.drawGammaRamp(canvas, viewModel.getCurrentDisplayProperty().get());
    }

    @Override
    protected void loadLocalProfile() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void resetColorAdjustment() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

}

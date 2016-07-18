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
package io.github.t3r1jj.gammaj.hotkeys;

import io.github.t3r1jj.gammaj.model.ColorProfile;
import javafx.beans.property.ObjectProperty;

public class ProfileHotkeyListener implements HotkeyListener{
    
    private final ObjectProperty<ColorProfile> currentProfile;
    private final ColorProfile colorProfile;

    public ProfileHotkeyListener(ObjectProperty<ColorProfile> currentProfile, ColorProfile colorProfile) {
        this.currentProfile = currentProfile;
        this.colorProfile = colorProfile;
    }

    @Override
    public void hotkeyPressed() {
        currentProfile.set(colorProfile);
    }

    @Override
    public String toString() {
        return colorProfile.toString();
    }
    
}

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
package io.github.t3r1jj.gammaj.jna;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

public class GammaRegistry {

    private static final WinReg.HKEY GAMMA_HKEY = WinReg.HKEY_LOCAL_MACHINE;
    private static final String GDI_ICM_GAMMA_RANGE = "Software\\Microsoft\\Windows NT\\CurrentVersion\\ICM";
    private static final String GAMMA_KEY_NAME = "GdiIcmGammaRange";
    private static final int GAMMA_EXTENSION_VALUE = 256;

    /**
     * System restart required for registry Gamma settings to get changed
     */
    public void installGammaExtension() {
        setGammaExtensionValue(GAMMA_EXTENSION_VALUE);
    }

    private void setGammaExtensionValue(int value) {
        Advapi32Util.registrySetIntValue(GAMMA_HKEY, GDI_ICM_GAMMA_RANGE, GAMMA_KEY_NAME, value);
    }
    
    public int getGammaExtensionValue() {
        return Advapi32Util.registryGetIntValue(GAMMA_HKEY, GDI_ICM_GAMMA_RANGE, GAMMA_KEY_NAME);
    }

}

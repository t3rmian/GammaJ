package com.t3r1jj.gammaj;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

/*
 Copyright (C) 2016 Damian Terlecki

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
public class GammaRegistry {

    private static final WinReg.HKEY GAMMA_HKEY = WinReg.HKEY_LOCAL_MACHINE;
    private static final String GDI_ICM_GAMMA_RANGE = "Software\\Microsoft\\Windows NT\\CurrentVersion\\ICM";
    private static final String GAMMA_KEY_NAME = "GdiIcmGammaRange";
    private static final int GAMMA_EXTENSION_VALUE = 256;

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

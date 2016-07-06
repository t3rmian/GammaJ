package com.t3r1jj.gammaj;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.win32.W32APIOptions;

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
public interface MyGDI32 extends GDI32{

    // GNU/Linux??
    MyGDI32 INSTANCE = (MyGDI32) Native.loadLibrary("GDI32", MyGDI32.class, W32APIOptions.DEFAULT_OPTIONS);

    BOOL SetDeviceGammaRamp(
            HDC hDC,
            WORD[] lpRamp
    );

    BOOL GetDeviceGammaRamp(
            HDC hDC,
            WORD[] lpRamp
    );
    
}

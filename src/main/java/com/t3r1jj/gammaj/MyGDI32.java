package com.t3r1jj.gammaj;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.win32.W32APIOptions;

/**
 *
 * @author Damian Terlecki
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

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

import com.sun.jna.Native;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.win32.W32APIOptions;

public interface MyGDI32 extends GDI32 {

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

    HDC CreateDC(
            String lpszDriver,
            String lpszDevice,
            String lpszOutput,
            Void lpInitData
    );

}

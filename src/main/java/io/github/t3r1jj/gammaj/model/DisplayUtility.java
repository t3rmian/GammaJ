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
package io.github.t3r1jj.gammaj.model;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.MONITORENUMPROC;
import io.github.t3r1jj.gammaj.jna.MyGDI32;

public class DisplayUtility {

    private int monitorsCount = 0;

    public int getMonitorsCount() {
        monitorsCount = 0;
        WinDef.HDC hdc = User32.INSTANCE.GetDC(null);
        User32.INSTANCE.EnumDisplayMonitors(hdc, null, new MONITORENUMPROC() {

            @Override
            public int apply(WinUser.HMONITOR arg0, WinDef.HDC arg1, WinDef.RECT arg2, WinDef.LPARAM arg3) {
                monitorsCount++;
                return 1;
            }
        }, null);
        return monitorsCount;
    }

    public Display getMonitor(int id) {
        String lpszDriver = "\\\\.\\DISPLAY" + id;
        String lpszDevice = lpszDriver;
        HDC hdc = MyGDI32.INSTANCE.CreateDC(lpszDriver, lpszDriver, null, null);
        SingleDisplay monitor = new SingleDisplay(id, hdc);
        return monitor;
    }

    public MultiDisplay getMultiDisplay() {
        String lpszDriver = "DISPLAY";
        HDC hdc = MyGDI32.INSTANCE.CreateDC(lpszDriver, null, null, null);
        MultiDisplay screen = new MultiDisplay(hdc);
        int count = getMonitorsCount();
        for (int i = 1; i <= count; i++) {
            screen.addScreen(getMonitor(i));
        }
        return screen;
    }

}

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
package io.github.t3r1jj.gammaj.info;

import java.util.Locale;

public class OperatingSystemUtility {

    public static OperatingSystem getOperatingSystemType() {
        String operatingSystem = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if (operatingSystem.contains("win")) {
            return OperatingSystem.Windows;
        } else if (operatingSystem.contains("nux")) {
            return OperatingSystem.Linux;
        } else if (operatingSystem.contains("mac") || operatingSystem.contains("darwin")) {
            return OperatingSystem.MacOS;
        } else {
            return OperatingSystem.Other;
        }
    }

    public enum OperatingSystem {

        Windows, MacOS, Linux, Other
    }
}

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
package com.t3r1jj.gammaj.info;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectInfo {
    
    Properties properties = new Properties();
    
    public ProjectInfo() {
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));
        } catch (Exception ex) {
            Logger.getLogger(ProjectInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getVersion() {
        return properties.getProperty("version");
    }
    
    public String getName() {
        return properties.getProperty("name");
    }
    
    public String getProjectUrl() {
        return properties.getProperty("projectUrl");
    }
    
    public String getDeveloperUrl() {
        return properties.getProperty("developerUrl");
    }
    
    public List<Library> getLibrariesUsed() {
        List<Library> libraries = new ArrayList<>();
        libraries.add(new LibraryBuilder()
                .setName("Java Native Access (JNA)")
                .setLicenseShort("Apache License, Version 2.0")
                .setLicenseLong("Java Native Access project (JNA) is dual-licensed under 2 \n"
                        + "alternative Open Source/Free licenses: LGPL 2.1 and \n"
                        + "Apache License 2.0. (starting with JNA version 4.0.0). \n"
                        + "\n"
                        + "You can freely decide which license you want to apply to \n"
                        + "the project.\n"
                        + "\n"
                        + "You may obtain a copy of the LGPL License at:\n"
                        + "\n"
                        + "http://www.gnu.org/licenses/licenses.html\n"
                        + "\n"
                        + "A copy is also included in the downloadable source code package\n"
                        + "containing JNA, in file \"LGPL2.1\", under the same directory\n"
                        + "as this file.\n"
                        + "\n"
                        + "You may obtain a copy of the Apache License at:\n"
                        + "\n"
                        + "http://www.apache.org/licenses/\n"
                        + "\n"
                        + "A copy is also included in the downloadable source code package\n"
                        + "containing JNA, in file \"AL2.0\", under the same directory\n"
                        + "as this file.")
                .setUrl("https://github.com/java-native-access/jna")
                .setVersion("4.2.2")
                .createLibrary());
        return libraries;
    }
    
}

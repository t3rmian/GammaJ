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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectInfo {

    Properties properties = new AlphabeticalOrderedProperties();

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

    public boolean isNewerVersion(String subjectedVersionPom) {
        if (getVersion().contains("SNAPSHOT")) {
            return false;
        }
        String[] subjectedVersioning = subjectedVersionPom.split("\\.");
        String[] projectVersioning = getVersion().split("\\.");
        try {
            for (int i = 0; i < subjectedVersioning.length; i++) {
                int subjectedVersion = Integer.parseInt(subjectedVersioning[i]);
                int projectVersion = Integer.parseInt(projectVersioning[i]);
                if (subjectedVersion > projectVersion) {
                    return true;
                }
            }
        } catch (Exception exception) {
            Logger.getLogger(ProjectInfo.class.getName()).log(Level.SEVERE, null, exception);
            return false;
        }
        return false;
    }

    public String getProjectName() {
        return properties.getProperty("name");
    }

    public String getProjectUrl() {
        return properties.getProperty("projectUrl");
    }

    public String getDeveloperName() {
        return properties.getProperty("developerName");
    }

    public String getDeveloperEmail() {
        return properties.getProperty("developerEmail");
    }

    public String getDeveloperUrl() {
        return properties.getProperty("developerUrl");
    }

    public String getAboutHeader() {
        return getProjectName() + " v" + getVersion();
    }

    public String getAboutContent() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Author: ").append(getDeveloperName()).append("\n")
                .append("Email: ").append(getDeveloperEmail()).append("\n")
                .append("Website: ").append(getDeveloperUrl()).append("\n")
                .append("Repository: ").append(getProjectUrl());
        return stringBuilder.toString();
    }

    public List<Library> getLibrariesUsed() {
        List<Library> libraries = new ArrayList<>();
        libraries.add(new LibraryBuilder()
                .setNameLong("Java Native Access (JNA)")
                .setNameShort("JNA")
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
        libraries.add(new LibraryBuilder()
                .setNameLong("GammaJ (this application)")
                .setNameShort(getProjectName())
                .setLicenseShort("Apache License, Version 2.0")
                .setLicenseLong("Copyright 2016 Damian Terlecki.\n"
                        + "\n"
                        + "Licensed under the Apache License, Version 2.0 (the \"License\");\n"
                        + "you may not use this file except in compliance with the License.\n"
                        + "You may obtain a copy of the License at\n"
                        + "\n"
                        + "     http://www.apache.org/licenses/LICENSE-2.0\n"
                        + "\n"
                        + "Unless required by applicable law or agreed to in writing, software\n"
                        + "distributed under the License is distributed on an \"AS IS\" BASIS,\n"
                        + "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
                        + "See the License for the specific language governing permissions and\n"
                        + "limitations under the License.")
                .setUrl(getProjectUrl())
                .setVersion(getVersion())
                .createLibrary());
        return libraries;
    }

}

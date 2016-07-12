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

import io.github.t3r1jj.gammaj.info.ProjectInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

public class ProjectInfoTest {

    private ProjectInfo properties;

    public ProjectInfoTest() {
    }

    @Before
    public void setUp() {
        properties = new ProjectInfo();
    }

    @Test
    public void testGetVersion() {
        String version = properties.getVersion();
        System.out.println("Version: " + version);
        Assert.assertNotNull(version);
    }

    @Test
    public void testGetName() {
        String name = properties.getProjectName();
        System.out.println("Name: " + name);
        Assert.assertNotNull(name);
    }

    @Test
    public void testGetProjectUrl() {
        String projectUrl = properties.getProjectUrl();
        System.out.println("Project url: " + projectUrl);
        Assert.assertNotNull(projectUrl);
    }

    @Test
    public void testGetDeveloperUrl() {
        String developerUrl = properties.getDeveloperUrl();
        System.out.println("Developer url: " + developerUrl);
        Assert.assertNotNull(developerUrl);
    }

}

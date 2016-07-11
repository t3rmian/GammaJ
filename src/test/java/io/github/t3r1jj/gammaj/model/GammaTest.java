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
import com.sun.jna.platform.win32.WinDef.HDC;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

public class GammaTest {

    private static HDC hdc;
    private static Gamma instance;

    public GammaTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        hdc = User32.INSTANCE.GetDC(null);
        instance = new Gamma(hdc);
    }

    @AfterClass
    public static void tearDownClass() {
        User32.INSTANCE.ReleaseDC(null, hdc);
        hdc = null;
    }

    @After
    public void tearDown() {
        instance.resetGammaRamp();
    }

    @org.junit.Test
    public void testResetGammaRamp() {
        instance.resetGammaRamp();
        assertThat(Gamma.DEFAULT_GAMMA_RAMP, is(equalTo(instance.getGammaRamp())));
    }

    @org.junit.Test
    public void testReinitializeGammaRamp() {
        instance.gamma[0] = 1.5f;
        instance.reinitializeGammaRamp();
        assertThat(Gamma.DEFAULT_GAMMA_RAMP, is(not(equalTo(instance.getGammaRamp()))));
    }

}

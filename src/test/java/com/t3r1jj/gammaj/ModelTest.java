/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t3r1jj.gammaj;

import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
public class ModelTest {

    public ModelTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        System.out.println("resettingGamma");
        Model instance = new Model();

        instance.resetModel();
        assertThat(Model.MY_DEFAULT_GAMMA_RAMP, is(instance.getCurrentGammaRamp()));
        instance.printGammaRamp();
    }
//
//    /**
//     * Test of getDeviceGammaRamp method, of class Model.
//     */
//    @org.junit.Test
//    public void testGetDeviceGammaRamp() {
//        System.out.println("getDeviceGammaRamp");
//        Model instance = new Model();
//        instance.getDeviceGammaRamp();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getGammaRamp method, of class Model.
//     */
//    @org.junit.Test
//    public void testToShortRamp() {
//        System.out.println("getGammaRamp");
//        Model instance = new Model();
//        short[][] expResult = null;
//        short[][] result = instance.getGammaRamp();
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of printGammaRamp method, of class Model.
     */
    @org.junit.Test
    public void testPrintGammaRamp() {
        System.out.println("printGammaRamp");
        Model instance = new Model();
        instance.printGammaRamp();
    }

    @org.junit.Test
    public void testSetGamma() {
        System.out.println("testSetGamma");
        Model instance = new Model();

        instance.setGamma(1.5f);
        assertThat(Model.MY_DEFAULT_GAMMA_RAMP, is(not(instance.getCurrentGammaRamp())));
        instance.printGammaRamp();
    }
    
}

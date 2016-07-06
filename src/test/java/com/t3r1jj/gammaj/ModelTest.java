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

/**
 *
 * @author Damian Terlecki
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

        instance.resetGamma();
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

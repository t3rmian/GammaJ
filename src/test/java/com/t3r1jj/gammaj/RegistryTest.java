package com.t3r1jj.gammaj;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Damian Terlecki
 */
public class RegistryTest {

    public RegistryTest() {
    }

    /**
     * Test of installGammaExtension method, of class GammaRegistry.
     */
    @Test
    public void testInstallGammaExtension() {
        System.out.println("installGammaExtension");
        GammaRegistry instance = new GammaRegistry();
        instance.installGammaExtension();
        assertEquals(256, instance.getGammaExtensionValue());
    }

    /**
     * Test of getGammaExtensionValue method, of class GammaRegistry.
     */
    @Test
    public void testGetGammaExtensionValue() {
        System.out.println("getGammaExtensionValue");
        GammaRegistry instance = new GammaRegistry();
        int expResult = 0;
        int result = instance.getGammaExtensionValue();
        assertEquals(expResult, result);
    }
}

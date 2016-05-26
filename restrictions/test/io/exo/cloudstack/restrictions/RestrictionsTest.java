package io.exo.cloudstack.restrictions;

import com.cloud.exception.InvalidParameterValueException;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class RestrictionsTest {

    private static final String MICRO_OFFER = "Micro";
    private static final String MEGA_OFFER = "Mega";
    private static final String TITAN_OFFER = "Titan";

    private static final long GIGABYTES = (long) java.lang.Math.pow(1024, 3);

    @Test
    public void testFileLoading() throws IOException {
        ServiceOfferingServiceImpl restrictionsManager = new ServiceOfferingServiceImpl();
        Map<String, List<Restriction>> restrictions = restrictionsManager.getRestrictions();
        assertTrue(1 == restrictions.size());
        assertTrue(1 == restrictions.get(MICRO_OFFER).size());
    }

    @Test
    public void testInvalidFileLoading() throws IOException {
        ServiceOfferingServiceImpl restrictionsManager = new ServiceOfferingServiceImpl("restrictions-invalid.yaml");
        Map<String, List<Restriction>> restrictions = restrictionsManager.getRestrictions();
        assertTrue(0 == restrictions.size());
    }

    @Test(expected = InvalidParameterValueException.class)
    public void testInvalidMicroAndWindows() {
        ServiceOfferingService restrictionsManager = new ServiceOfferingServiceImpl();
        restrictionsManager.validate(MICRO_OFFER, "Windows 2012 R2", null);
    }

    @Test(expected = InvalidParameterValueException.class)
    public void testInvalidMicro400GB() {
        ServiceOfferingService restrictionsManager = new ServiceOfferingServiceImpl();
        restrictionsManager.validate(MICRO_OFFER, "Ubuntu 16.04 LTS 64-bit", 400 * GIGABYTES);
    }

    @Test(expected = InvalidParameterValueException.class)
    public void testInvalidMicroDiskUpgrade() {
        ServiceOfferingService restrictionsManager = new ServiceOfferingServiceImpl();
        restrictionsManager.validate(MICRO_OFFER, null, 201 * GIGABYTES);
    }

    @Test
    public void testValidMicroDisk200GB() {
        ServiceOfferingService restrictionsManager = new ServiceOfferingServiceImpl();
        restrictionsManager.validate(MICRO_OFFER, null, 200 * GIGABYTES);
    }

    @Test
    public void testMissingOffering() {
        ServiceOfferingService restrictionsManager = new ServiceOfferingServiceImpl();
        restrictionsManager.validate(null, "Debian 8", 400 * GIGABYTES);
    }
}

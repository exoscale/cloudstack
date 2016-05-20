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
        RestrictionServiceImpl restrictionsManager = new RestrictionServiceImpl();
        Map<String, List<Restriction>> restrictions = restrictionsManager.getRestrictions();
        assertTrue(3 == restrictions.size());
        assertTrue(1 == restrictions.get(MICRO_OFFER).size());
        assertTrue(1 == restrictions.get(MEGA_OFFER).size());
        assertTrue(1 == restrictions.get(TITAN_OFFER).size());
    }

    @Test
    public void testInvalidFileLoading() throws IOException {
        RestrictionServiceImpl restrictionsManager = new RestrictionServiceImpl("restrictions-invalid.yaml");
        Map<String, List<Restriction>> restrictions = restrictionsManager.getRestrictions();
        assertTrue(0 == restrictions.size());
    }

    @Test(expected = InvalidParameterValueException.class)
    public void testInvalidMicroAndWindows() {
        RestrictionService restrictionsManager = new RestrictionServiceImpl();
        restrictionsManager.validate(MICRO_OFFER, "someone", "Windows 2012 R2", null);
    }

    @Test(expected = InvalidParameterValueException.class)
    public void testInvalidMicro400GB() {
        RestrictionService restrictionsManager = new RestrictionServiceImpl();
        restrictionsManager.validate(MICRO_OFFER, "someone", "Ubuntu 16.04 LTS 64-bit", 400 * GIGABYTES);
    }

    @Test(expected = InvalidParameterValueException.class)
    public void testInvalidMicroDiskUpgrade() {
        RestrictionService restrictionsManager = new RestrictionServiceImpl();
        restrictionsManager.validate(MICRO_OFFER, "someone", null, 201 * GIGABYTES);
    }

    @Test
    public void testValidMicroDisk200GB() {
        RestrictionService restrictionsManager = new RestrictionServiceImpl();
        restrictionsManager.validate(MICRO_OFFER, "someone", null, 200 * GIGABYTES);
    }

    @Test
    public void testMissingOffering() {
        RestrictionService restrictionsManager = new RestrictionServiceImpl();
        restrictionsManager.validate(null, "someone", "Debian 8", 400 * GIGABYTES);
    }

    @Test
    public void testValidTitanOrg() {
        RestrictionService restrictionsManager = new RestrictionServiceImpl();
        restrictionsManager.validate(TITAN_OFFER, "c36c1577-ee3d-49c4-8934-e32a56f26405", "Debian 8", 600 * GIGABYTES);
    }

    @Test(expected = InvalidParameterValueException.class)
    public void testInvalidTitanOrg() {
        RestrictionService restrictionsManager = new RestrictionServiceImpl();
        restrictionsManager.validate(TITAN_OFFER, "someone", "Ubuntu 16.04 LTS", 600 * GIGABYTES);
    }

    @Test(expected = InvalidParameterValueException.class)
    public void testMegaOfferingInaccessible() throws IOException {
        RestrictionServiceImpl restrictionsManager = new RestrictionServiceImpl();
        Map<String, List<Restriction>> restrictions = restrictionsManager.getRestrictions();
        for(Restriction restriction : restrictions.get(MEGA_OFFER)) {
            assertTrue("No orgs in authorized list", restriction.getAuthorizedOrgs() == null || restriction.getAuthorizedOrgs().size() == 0);
        }
        // Ensure that an org in another list (TITAN) is not granted access to this offer as well
        restrictionsManager.validate(MEGA_OFFER, "0f286087-85f3-4195-abcf-e67e7cc7eb63", "Ubuntu 16.04 LTS", 50 * GIGABYTES);
    }
}

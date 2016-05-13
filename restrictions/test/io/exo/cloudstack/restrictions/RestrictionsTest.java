package io.exo.cloudstack.restrictions;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class RestrictionsTest {

    @Test
    public void testFileLoading() throws IOException {
        RestrictionServiceImpl restrictionsManager = new RestrictionServiceImpl();
        List<Restriction> restrictions = restrictionsManager.getRestrictions();
        assertTrue(2 == restrictions.size());
    }
}

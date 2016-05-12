package io.exo.cloudstack.restrictions;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class RestrictionsTest {

    @Test
    public void testFileLoading() throws IOException {
        RestrictionsManager restrictionsManager = new RestrictionsManager();
        List<Restriction> restrictions = restrictionsManager.getRestrictions();
        assertTrue(2 == restrictions.size());
    }
}

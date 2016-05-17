package io.exo.cloudstack.restrictions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class to load the Yaml file containing the restrictions.
 */
public class RestrictionList {

    private List<Restriction> restrictions;

    public List<Restriction> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(List<Restriction> restrictions) {
        this.restrictions = restrictions;
    }

    public int size() {
        int result = 0;
        if (restrictions != null) {
            result = restrictions.size();
        }
        return result;
    }

    public Map<String, List<Restriction>> getRestrictionsMap() {
        Map<String, List<Restriction>> result = null;
        if (restrictions != null) {
            result = new HashMap<>(size());
            for(Restriction restriction : restrictions) {
                if (restriction.getServiceOfferingName() != null) {
                    List<Restriction> lr = null;
                    if (result.containsKey(restriction.getServiceOfferingName())) {
                        lr = result.get(restriction.getServiceOfferingName());
                    } else {
                        lr = new ArrayList<>();
                        result.put(restriction.getServiceOfferingName(), lr);
                    }
                    lr.add(restriction);
                }
            }
        }
        return result;
    }
}

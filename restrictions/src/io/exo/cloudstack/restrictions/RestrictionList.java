package io.exo.cloudstack.restrictions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class needed to load the Yaml file containing the restrictions and should only be used to configure the
 * Yaml parser.
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
                String serviceOfferingName = restriction.getServiceOfferingName();
                if (serviceOfferingName != null) {
                    List<Restriction> lr = null;
                    if (result.containsKey(serviceOfferingName)) {
                        lr = result.get(serviceOfferingName);
                    } else {
                        lr = new ArrayList<>();
                        result.put(serviceOfferingName, lr);
                    }
                    lr.add(restriction);
                }
            }
        }
        return result;
    }
}

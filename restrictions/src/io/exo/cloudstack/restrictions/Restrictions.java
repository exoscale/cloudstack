package io.exo.cloudstack.restrictions;

import java.util.List;

public class Restrictions {

    private List<Restriction> restrictions;

    public List<Restriction> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(List<Restriction> restrictions) {
        this.restrictions = restrictions;
    }

    public int size() {
        if (restrictions == null) {
            return 0;
        } else {
            return restrictions.size();
        }
    }
}

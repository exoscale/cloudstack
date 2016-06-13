package com.cloud.capacity;

import java.util.HashMap;
import java.util.Map;

public enum CapacityType {
    CAPACITY_TYPE_MEMORY((short)0, "memory"),
    CAPACITY_TYPE_CPU((short)1, "cpu"),
    CAPACITY_TYPE_STORAGE((short)2, "storage"),
    CAPACITY_TYPE_STORAGE_ALLOCATED((short)3, "storage_allocated"),
    CAPACITY_TYPE_VIRTUAL_NETWORK_PUBLIC_IP((short)4, "virtual_network_public_ip"),
    CAPACITY_TYPE_PRIVATE_IP((short)5, "private_ip"),
    CAPACITY_TYPE_SECONDARY_STORAGE((short)6, "secondary_storage"),
    CAPACITY_TYPE_VLAN((short)7, "vlan"),
    CAPACITY_TYPE_DIRECT_ATTACHED_PUBLIC_IP((short)8, "direct_attached_public_ip"),
    CAPACITY_TYPE_LOCAL_STORAGE((short)9, "local_storage"),
    CAPACITY_TYPE_GPU((short)19, "gpu");

    private static final Map<Short, CapacityType> types = new HashMap<>();
    static  {
        for(CapacityType t : CapacityType.values()) {
            types.put(t.value, t);
        }
    }

    public static CapacityType valueOf(short value) {
        return (types.containsKey(value) ? types.get(value) : null);
    }

    private short value;
    private String name;

    CapacityType(short value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public short getValue() {
        return value;
    }

}

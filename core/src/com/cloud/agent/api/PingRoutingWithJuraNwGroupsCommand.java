package com.cloud.agent.api;

import com.cloud.host.Host;
import com.cloud.utils.Pair;
import com.cloud.utils.Ternary;
import com.cloud.vm.VirtualMachine;

import java.util.HashMap;
import java.util.Map;

public class PingRoutingWithJuraNwGroupsCommand extends PingRoutingWithNwGroupsCommand {
    HashMap<String, Ternary<Long, String, String[]>> gateways;
    HashMap<String, Ternary<Long, String, String[]>> peers;

    public PingRoutingWithJuraNwGroupsCommand(Host.Type type, long id, Map<String, VirtualMachine.State> states, Map<String, HostVmStateReportEntry> hostVmStateReport, HashMap<String,
            Pair<Long, Long>> nwGrpStates, HashMap<String, Ternary<Long, String, String[]>> gateways, HashMap<String, Ternary<Long, String, String[]>> peers) {
        super(type, id, states, hostVmStateReport, nwGrpStates);
        this.gateways = gateways;
        this.peers = peers;
    }

    public HashMap<String, Ternary<Long, String, String[]>> getGateways() {
        return gateways;
    }

    public HashMap<String, Ternary<Long, String, String[]>> getPeers() {
        return peers;
    }
}

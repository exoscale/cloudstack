/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.cloud.hypervisor.kvm.resource;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.cloud.agent.api.PingCommand;
import com.cloud.agent.api.PingRoutingWithJuraNwGroupsCommand;
import com.cloud.agent.api.PingRoutingWithNwGroupsCommand;
import com.cloud.utils.Ternary;
import org.apache.commons.lang.SystemUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.DomainBlockStats;
import org.libvirt.DomainInfo;
import org.libvirt.DomainInterfaceStats;
import org.libvirt.LibvirtException;
import org.libvirt.NodeInfo;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.cloud.agent.api.VmStatsEntry;
import com.cloud.agent.api.to.VirtualMachineTO;
import com.cloud.hypervisor.kvm.resource.LibvirtVMDef.DiskDef;
import com.cloud.hypervisor.kvm.resource.LibvirtVMDef.InterfaceDef;
import com.cloud.template.VirtualMachineTemplate.BootloaderType;
import com.cloud.utils.Pair;
import com.cloud.vm.VirtualMachine;

public class LibvirtComputingResourceTest {

    String _hyperVisorType = "kvm";
    Random _random = new Random();

    /**
        This test tests if the Agent can handle a vmSpec coming
        from a <=4.1 management server.

        The overcommit feature has not been merged in there and thus
        only 'speed' is set.
    */
    @Test
    public void testCreateVMFromSpecLegacy() {
        int id = _random.nextInt(65534);
        String name = "test-instance-1";

        int cpus = _random.nextInt(2) + 1;
        int speed = 1024;
        int minRam = 256 * 1024;
        int maxRam = 512 * 1024;

        String os = "Ubuntu";

        String vncAddr = "";
        String vncPassword = "mySuperSecretPassword";

        LibvirtComputingResource lcr = new LibvirtComputingResource();
        VirtualMachineTO to = new VirtualMachineTO(id, name, VirtualMachine.Type.User, cpus, speed, minRam, maxRam, BootloaderType.HVM, os, false, false, vncPassword);
        to.setVncAddr(vncAddr);
        to.setUuid("b0f0a72d-7efb-3cad-a8ff-70ebf30b3af9");

        LibvirtVMDef vm = lcr.createVMFromSpec(to);
        vm.setHvsType(_hyperVisorType);

        verifyVm(to, vm);
    }

    /**
        This test verifies that CPU topology is properly set for hex-core
    */
    @Test
    public void testCreateVMFromSpecWithTopology6() {
        int id = _random.nextInt(65534);
        String name = "test-instance-1";

        int cpus = 12;
        int minSpeed = 1024;
        int maxSpeed = 2048;
        int minRam = 256 * 1024;
        int maxRam = 512 * 1024;

        String os = "Ubuntu";

        String vncAddr = "";
        String vncPassword = "mySuperSecretPassword";

        LibvirtComputingResource lcr = new LibvirtComputingResource();
        VirtualMachineTO to = new VirtualMachineTO(id, name, VirtualMachine.Type.User, cpus, minSpeed, maxSpeed, minRam, maxRam, BootloaderType.HVM, os, false, false, vncPassword);
        to.setVncAddr(vncAddr);
        to.setUuid("b0f0a72d-7efb-3cad-a8ff-70ebf30b3af9");

        LibvirtVMDef vm = lcr.createVMFromSpec(to);
        vm.setHvsType(_hyperVisorType);

        verifyVm(to, vm);
    }

    /**
        This test verifies that CPU topology is properly set for quad-core
    */
    @Test
    public void testCreateVMFromSpecWithTopology4() {
        int id = _random.nextInt(65534);
        String name = "test-instance-1";

        int cpus = 8;
        int minSpeed = 1024;
        int maxSpeed = 2048;
        int minRam = 256 * 1024;
        int maxRam = 512 * 1024;

        String os = "Ubuntu";

        String vncAddr = "";
        String vncPassword = "mySuperSecretPassword";

        LibvirtComputingResource lcr = new LibvirtComputingResource();
        VirtualMachineTO to = new VirtualMachineTO(id, name, VirtualMachine.Type.User, cpus, minSpeed, maxSpeed, minRam, maxRam, BootloaderType.HVM, os, false, false, vncPassword);
        to.setVncAddr(vncAddr);
        to.setUuid("b0f0a72d-7efb-3cad-a8ff-70ebf30b3af9");

        LibvirtVMDef vm = lcr.createVMFromSpec(to);
        vm.setHvsType(_hyperVisorType);

        verifyVm(to, vm);
    }

    /**
        This test tests if the Agent can handle a vmSpec coming
        from a >4.1 management server.

        It tests if the Agent can handle a vmSpec with overcommit
        data like minSpeed and maxSpeed in there
    */
    @Test
    public void testCreateVMFromSpec() {
        int id = _random.nextInt(65534);
        String name = "test-instance-1";

        int cpus = _random.nextInt(2) + 1;
        int minSpeed = 1024;
        int maxSpeed = 2048;
        int minRam = 256 * 1024;
        int maxRam = 512 * 1024;

        String os = "Ubuntu";

        String vncAddr = "";
        String vncPassword = "mySuperSecretPassword";

        LibvirtComputingResource lcr = new LibvirtComputingResource();
        VirtualMachineTO to =
            new VirtualMachineTO(id, name, VirtualMachine.Type.User, cpus, minSpeed, maxSpeed, minRam, maxRam, BootloaderType.HVM, os, false, false, vncPassword);
        to.setVncAddr(vncAddr);
        to.setUuid("b0f0a72d-7efb-3cad-a8ff-70ebf30b3af9");

        LibvirtVMDef vm = lcr.createVMFromSpec(to);
        vm.setHvsType(_hyperVisorType);

        verifyVm(to, vm);
    }

    private void verifyVm(VirtualMachineTO to, LibvirtVMDef vm) {
        Document domainDoc = parse(vm.toString());
        assertXpath(domainDoc, "/domain/@type", vm.getHvsType());
        assertXpath(domainDoc, "/domain/name/text()", to.getName());
        assertXpath(domainDoc, "/domain/uuid/text()", to.getUuid());
        assertXpath(domainDoc, "/domain/description/text()", to.getOs());
        assertXpath(domainDoc, "/domain/clock/@offset", "utc");
        assertNodeExists(domainDoc, "/domain/features/pae");
        assertNodeExists(domainDoc, "/domain/features/apic");
        assertNodeExists(domainDoc, "/domain/features/acpi");
        assertXpath(domainDoc, "/domain/devices/serial/@type", "pty");
        assertXpath(domainDoc, "/domain/devices/serial/target/@port", "0");
        assertXpath(domainDoc, "/domain/devices/graphics/@type", "vnc");
        assertXpath(domainDoc, "/domain/devices/graphics/@listen", to.getVncAddr());
        assertXpath(domainDoc, "/domain/devices/graphics/@autoport", "yes");
        assertXpath(domainDoc, "/domain/devices/graphics/@passwd", to.getVncPassword());

        assertXpath(domainDoc, "/domain/devices/console/@type", "pty");
        assertXpath(domainDoc, "/domain/devices/console/target/@port", "0");
        assertXpath(domainDoc, "/domain/devices/input/@type", "tablet");
        assertXpath(domainDoc, "/domain/devices/input/@bus", "usb");

        assertXpath(domainDoc, "/domain/memory/text()", String.valueOf( to.getMaxRam() / 1024 ));
        assertXpath(domainDoc, "/domain/currentMemory/text()", String.valueOf( to.getMinRam() / 1024 ));

        assertXpath(domainDoc, "/domain/devices/memballoon/@model", "virtio");
        assertXpath(domainDoc, "/domain/vcpu/text()", String.valueOf(to.getCpus()));

        assertXpath(domainDoc, "/domain/os/type/@machine", "pc");
        assertXpath(domainDoc, "/domain/os/type/text()", "hvm");

        assertNodeExists(domainDoc, "/domain/cpu");
        assertNodeExists(domainDoc, "/domain/os/boot[@dev='cdrom']");
        assertNodeExists(domainDoc, "/domain/os/boot[@dev='hd']");

        assertXpath(domainDoc, "/domain/on_reboot/text()", "restart");
        assertXpath(domainDoc, "/domain/on_poweroff/text()", "destroy");
        assertXpath(domainDoc, "/domain/on_crash/text()", "destroy");
    }

    static Document parse(final String input) {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new ByteArrayInputStream(input.getBytes()));
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new IllegalArgumentException("Cloud not parse: "+input, e);
        }
    }

    static void assertNodeExists(final Document doc, final String xPathExpr) {
        try {
            Assert.assertNotNull(XPathFactory.newInstance().newXPath()
                    .evaluate(xPathExpr, doc, XPathConstants.NODE));
        } catch (XPathExpressionException e) {
            Assert.fail(e.getMessage());
        }
    }

    static void assertXpath(final Document doc, final String xPathExpr,
            final String expected) {
        try {
            Assert.assertEquals(expected, XPathFactory.newInstance().newXPath()
                    .evaluate(xPathExpr, doc));
        } catch (XPathExpressionException e) {
            Assert.fail("Could not evaluate xpath" + xPathExpr + ":"
                    + e.getMessage());
        }
    }

    @Test
    public void testGetNicStats() {
        //this test is only working on linux because of the loopback interface name
        //also the tested code seems to work only on linux
        Assume.assumeTrue(SystemUtils.IS_OS_LINUX);
        Pair<Double, Double> stats = LibvirtComputingResource.getNicStats("lo");
        assertNotNull(stats);
    }

    @Test
    public void testUUID() {
        String uuid = "1";
        LibvirtComputingResource lcr = new LibvirtComputingResource();
        uuid = lcr.getUuid(uuid);
        Assert.assertTrue(!uuid.equals("1"));

        String oldUuid = UUID.randomUUID().toString();
        uuid = oldUuid;
        uuid = lcr.getUuid(uuid);
        Assert.assertTrue(uuid.equals(oldUuid));
    }

    private static final String VMNAME = "test";

    @Test
    public void testGetVmStat() throws LibvirtException {
        Connect connect = Mockito.mock(Connect.class);
        Domain domain = Mockito.mock(Domain.class);
        DomainInfo domainInfo = new DomainInfo();
        Mockito.when(domain.getInfo()).thenReturn(domainInfo);
        Mockito.when(connect.domainLookupByName(VMNAME)).thenReturn(domain);
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.cpus = 8;
        nodeInfo.memory = 8 * 1024 * 1024;
        nodeInfo.sockets = 2;
        nodeInfo.threads = 2;
        nodeInfo.model = "Foo processor";
        Mockito.when(connect.nodeInfo()).thenReturn(nodeInfo);
        // this is testing the interface stats, returns an increasing number of sent and received bytes
        Mockito.when(domain.interfaceStats(Matchers.anyString())).thenAnswer(new Answer<DomainInterfaceStats>() {
            // increment with less than a KB, so this should be less than 1 KB
            final static int increment = 1000;
            int rxBytes = 1000;
            int txBytes = 1000;

            @Override
            public DomainInterfaceStats answer(InvocationOnMock invocation) throws Throwable {
                DomainInterfaceStats domainInterfaceStats = new DomainInterfaceStats();
                domainInterfaceStats.rx_bytes = (rxBytes += increment);
                domainInterfaceStats.tx_bytes = (txBytes += increment);
                return domainInterfaceStats;

            }

        });

        Mockito.when(domain.blockStats(Matchers.anyString())).thenAnswer(new Answer<DomainBlockStats>() {
            // a little less than a KB
            final static int increment = 1000;

            int rdBytes = 0;
            int wrBytes = 1024;

            @Override
            public DomainBlockStats answer(InvocationOnMock invocation) throws Throwable {
                DomainBlockStats domainBlockStats = new DomainBlockStats();

                domainBlockStats.rd_bytes = (rdBytes += increment);
                domainBlockStats.wr_bytes = (wrBytes += increment);
                return domainBlockStats;
            }

        });

        LibvirtComputingResource libvirtComputingResource = new LibvirtComputingResource() {
            @Override
            protected List<InterfaceDef> getInterfaces(Connect conn, String vmName) {
                InterfaceDef interfaceDef = new InterfaceDef();
                return Arrays.asList(interfaceDef);
            }

            @Override
            public List<DiskDef> getDisks(Connect conn, String vmName) {
                DiskDef diskDef = new DiskDef();
                return Arrays.asList(diskDef);
            }

        };
        libvirtComputingResource.getVmStat(connect, VMNAME);
        VmStatsEntry vmStat = libvirtComputingResource.getVmStat(connect, VMNAME);
        // network traffic as generated by the logic above, must be greater than zero
        Assert.assertTrue(vmStat.getNetworkReadKBs() > 0);
        Assert.assertTrue(vmStat.getNetworkWriteKBs() > 0);
        // IO traffic as generated by the logic above, must be greater than zero
        Assert.assertTrue(vmStat.getDiskReadKBs() > 0);
        Assert.assertTrue(vmStat.getDiskWriteKBs() > 0);
    }

    @Test
    public void getCpuSpeed() {
        Assume.assumeTrue(SystemUtils.IS_OS_LINUX);
        NodeInfo nodeInfo = Mockito.mock(NodeInfo.class);
        LibvirtComputingResource.getCpuSpeed(nodeInfo);
    }

    private LibvirtComputingResource setupWithSecurityGroups(boolean activateJura, String ruleResult) {
        LibvirtComputingResource libvirtComputingResource = Mockito.mock(LibvirtComputingResource.class);
        libvirtComputingResource._canBridgeFirewall = true;
        if (activateJura) {
            libvirtComputingResource._juraState = LibvirtComputingResource.JuraState.EXEC;
        } else {
            libvirtComputingResource._juraState = LibvirtComputingResource.JuraState.OFF;
        }
        Mockito.when(libvirtComputingResource.sync()).thenReturn(new HashMap<String, VirtualMachine.State>());
        Mockito.when(libvirtComputingResource.get_rule_logs_for_vms()).thenReturn(ruleResult);
        Mockito.when(libvirtComputingResource.getCurrentStatus(Mockito.anyLong())).thenCallRealMethod();
        return libvirtComputingResource;
    }

    @Test
    public void testCurrentStatusSecurityGroupNoVM() {
        LibvirtComputingResource libvirtComputingResource = setupWithSecurityGroups(false, "");

        PingCommand cmd = libvirtComputingResource.getCurrentStatus(Mockito.anyLong());
        assertTrue(cmd instanceof PingRoutingWithNwGroupsCommand);
        PingRoutingWithNwGroupsCommand pingCmd = (PingRoutingWithNwGroupsCommand) cmd;
        HashMap<String, Pair<Long, Long>> nwGrpStates = pingCmd.getNewGroupStates();

        assertTrue(nwGrpStates.size() == 1);
        assertTrue(nwGrpStates.get("").equals(new Pair<>(-1L, -1L)));
    }

    @Test
    public void testCurrentStatusSecurityGroupMissingVMSG() {
        String missingVM = "i-3-234-DEV";
        LibvirtComputingResource libvirtComputingResource = setupWithSecurityGroups(false, "i-3-523-DEV,523,192.0.2.203,2,bb38d127218745fafc62d4fcd261f0eb,1;" + missingVM + ";i-3-5-DEV,5,192.0.2.103,4,bb38d127218745fafc62d4fcd261f0eb,a;i-3-7-DEV,5,192.0.2.43,4,bb38d127218745fafc62d4fcd261f0eb;");

        PingCommand cmd = libvirtComputingResource.getCurrentStatus(Mockito.anyLong());
        assertTrue(cmd instanceof PingRoutingWithNwGroupsCommand);
        PingRoutingWithNwGroupsCommand pingCmd = (PingRoutingWithNwGroupsCommand) cmd;
        HashMap<String, Pair<Long, Long>> nwGrpStates = pingCmd.getNewGroupStates();

        assertTrue(nwGrpStates.size() == 4);
        assertTrue(nwGrpStates.get(missingVM).equals(new Pair<>(234L, -1L)));
        assertTrue(nwGrpStates.get("i-3-523-DEV").equals(new Pair<>(523L, 1L)));
        assertTrue(nwGrpStates.get("i-3-5-DEV").equals(new Pair<>(5L, -1L)));
        assertTrue(nwGrpStates.get("i-3-7-DEV").equals(new Pair<>(7L, -1L)));
    }

    @Test
    public void testCurrentStatusJuraVM() {
        String juraOutput = "{\n" +
                "  \"vnet0\": {\n" +
                "    \"DomainUUID\": \"95c1279b-8a12-4a26-b6cd-9868ca7400bc\",\n" +
                "    \"DomainName\": \"i-331-22826-VM\",\n" +
                "    \"MAC\": \"06:6d:1e:00:00:5d\",\n" +
                "    \"Peers\": [\n" +
                "      \"159.100.241.198\"\n" +
                "    ],\n" +
                "    \"Gateways\": [\n" +
                "      \"159.100.241.1/24\"\n" +
                "    ],\n" +
                "    \"Firewall\": {\n" +
                "      \"Data\": \"SeqNum1\",\n" +
                "      \"Ingress\": [\n" +
                "        \"-p icmp -m icmp --icmp-type 8/0\",\n" +
                "        \"-p tcp -m state --state NEW -m tcp --dport 22\"\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"vnet1\": {\n" +
                "    \"DomainUUID\": \"f41e825c-488e-4944-91e3-30b5d0ffc672\",\n" +
                "    \"DomainName\": \"i-302-22828-VM\",\n" +
                "    \"MAC\": \"06:e9:44:00:00:6e\",\n" +
                "    \"Peers\": [\n" +
                "      \"159.100.241.215\"\n" +
                "    ],\n" +
                "    \"Gateways\": [\n" +
                "      \"159.100.241.1/24\"\n" +
                "    ],\n" +
                "    \"Firewall\": {\n" +
                "      \"Data\": \"SeqNum14\",\n" +
                "      \"Ingress\": [\n" +
                "        \"-p icmp -m icmp --icmp-type 8/0\",\n" +
                "        \"-p tcp -m state --state NEW -m tcp --dport 22\"\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"vnet2\": {\n" +
                "    \"DomainUUID\": \"a629b35a-6531-4428-8aca-1a104203ef4a\",\n" +
                "    \"DomainName\": \"i-303-22822-VM\",\n" +
                "    \"MAC\": \"06:5f:e4:00:00:61\",\n" +
                "    \"Peers\": [\n" +
                "      \"159.100.241.202\",\n" +
                "      \"159.100.241.238\"\n" +
                "    ],\n" +
                "    \"Gateways\": [\n" +
                "      \"159.100.241.1/24\"\n" +
                "    ],\n" +
                "    \"Firewall\": {\n" +
                "      \"Data\": \"SeqNum3\",\n" +
                "      \"Ingress\": [\n" +
                "        \"-p icmp -m icmp --icmp-type 8/0\",\n" +
                "        \"-p tcp -m state --state NEW -m tcp --dport 22\",\n" +
                "        \"-p tcp -m state --state NEW -m tcp --dport 80\",\n" +
                "        \"-p tcp -m state --state NEW -m tcp --dport 3389\",\n" +
                "        \"-p tcp -m state --state NEW -m tcp --dport 5001\"\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"vnet3\": {\n" +
                "    \"DomainUUID\": \"42cb64d5-9452-4f05-acb4-9b2085e6345b\",\n" +
                "    \"DomainName\": \"i-331-22837-VM\",\n" +
                "    \"MAC\": \"06:e5:b4:00:00:65\",\n" +
                "    \"Peers\": [\n" +
                "      \"159.100.241.206\",\n" +
                "      \"159.100.241.199\"\n" +
                "    ],\n" +
                "    \"Gateways\": [\n" +
                "      \"159.100.241.1/24\"\n" +
                "    ],\n" +
                "    \"Firewall\": {\n" +
                "      \"Data\": \"SeqNum1\",\n" +
                "      \"Ingress\": [\n" +
                "        \"-p icmp -m icmp --icmp-type 8/0\",\n" +
                "        \"-p tcp -m state --state NEW -m tcp --dport 22\"\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
        LibvirtComputingResource libvirtComputingResource = setupWithSecurityGroups(true, juraOutput);
        PingCommand cmd = libvirtComputingResource.getCurrentStatus(Mockito.anyLong());
        assertTrue(cmd instanceof PingRoutingWithJuraNwGroupsCommand);
        PingRoutingWithJuraNwGroupsCommand pingCmd = (PingRoutingWithJuraNwGroupsCommand) cmd;
        HashMap<String, Pair<Long, Long>> nwGrpStates = pingCmd.getNewGroupStates();
        HashMap<String, Ternary<Long, String, String[]>> juraPeers = pingCmd.getPeers();

        assertTrue(nwGrpStates.size() == 4);
        assertTrue(juraPeers.size() == 4);
        assertTrue(nwGrpStates.keySet().equals(juraPeers.keySet()));

        assertTrue(nwGrpStates.get("i-331-22826-VM").equals(new Pair<>(22826L, 1L)));
        assertTrue(nwGrpStates.get("i-302-22828-VM").equals(new Pair<>(22828L, 14L)));
        assertTrue(nwGrpStates.get("i-303-22822-VM").equals(new Pair<>(22822L, 3L)));
        assertTrue(nwGrpStates.get("i-331-22837-VM").equals(new Pair<>(22837L, 1L)));

        Ternary<Long, String, String[]> t = juraPeers.get("i-331-22826-VM");
        assertTrue(t.first().equals(22826L));
        assertTrue((t.second().equals("06:6d:1e:00:00:5d")));
        assertTrue(t.third().length == 1);
        assertTrue((t.third()[0].equals("159.100.241.198")));
    }

    @Test
    public void testCurrentStatusJuraMissingVM() {
        String juraOutput = "{\n" +
                "  \"vnet0\": {\n" +
                "    \"DomainUUID\": \"95c1279b-8a12-4a26-b6cd-9868ca7400bc\",\n" +
                "    \"DomainName\": \"i-331-22826-VM\",\n" +
                "    \"MAC\": \"06:6d:1e:00:00:5d\",\n" +
                "    \"Peers\": [\n" +
                "    ],\n" +
                "    \"Gateways\": [\n" +
                "    ],\n" +
                "    \"Firewall\": {\n" +
                "    }\n" +
                "  },\n" +
                "  \"vnet1\": {\n" +
                "    \"DomainUUID\": \"f41e825c-488e-4944-91e3-30b5d0ffc672\",\n" +
                "    \"DomainName\": \"i-302-22828-VM\",\n" +
                "    \"MAC\": \"06:e9:44:00:00:6e\",\n" +
                "    \"Peers\": [\n" +
                "    ],\n" +
                "    \"Gateways\": [\n" +
                "    ],\n" +
                "    \"Firewall\": {\n" +
                "    }\n" +
                "  },\n" +
                "  \"vnet2\": {\n" +
                "    \"DomainUUID\": \"a629b35a-6531-4428-8aca-1a104203ef4a\",\n" +
                "    \"DomainName\": \"i-303-22822-VM\",\n" +
                "    \"MAC\": \"06:5f:e4:00:00:61\",\n" +
                "    \"Peers\": [\n" +
                "      \"159.100.241.202\",\n" +
                "      \"159.100.241.238\"\n" +
                "    ],\n" +
                "    \"Gateways\": [\n" +
                "      \"159.100.241.1/24\"\n" +
                "    ],\n" +
                "    \"Firewall\": {\n" +
                "      \"Data\": \"SeqNum3\",\n" +
                "      \"Ingress\": [\n" +
                "        \"-p icmp -m icmp --icmp-type 8/0\",\n" +
                "        \"-p tcp -m state --state NEW -m tcp --dport 22\",\n" +
                "        \"-p tcp -m state --state NEW -m tcp --dport 80\",\n" +
                "        \"-p tcp -m state --state NEW -m tcp --dport 3389\",\n" +
                "        \"-p tcp -m state --state NEW -m tcp --dport 5001\"\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
        LibvirtComputingResource libvirtComputingResource = setupWithSecurityGroups(true, juraOutput);
        PingCommand cmd = libvirtComputingResource.getCurrentStatus(Mockito.anyLong());
        assertTrue(cmd instanceof PingRoutingWithJuraNwGroupsCommand);
        PingRoutingWithJuraNwGroupsCommand pingCmd = (PingRoutingWithJuraNwGroupsCommand) cmd;
        HashMap<String, Pair<Long, Long>> nwGrpStates = pingCmd.getNewGroupStates();
        HashMap<String, Ternary<Long, String, String[]>> juraPeers = pingCmd.getPeers();

        assertTrue(nwGrpStates.size() == 3);
        assertTrue(juraPeers.size() == 3);
        assertTrue(nwGrpStates.keySet().equals(juraPeers.keySet()));

        assertTrue(nwGrpStates.get("i-331-22826-VM").equals(new Pair<>(22826L, -1L)));
        assertTrue(nwGrpStates.get("i-302-22828-VM").equals(new Pair<>(22828L, -1L)));
        assertTrue(nwGrpStates.get("i-303-22822-VM").equals(new Pair<>(22822L, 3L)));

        Ternary<Long, String, String[]> t = juraPeers.get("i-331-22826-VM");
        assertTrue(t.first().equals(22826L));
        assertTrue(t.second().equals("06:6d:1e:00:00:5d"));
        assertTrue(t.third().length == 0);
        t = juraPeers.get("i-302-22828-VM");
        assertTrue(t.first().equals(22828L));
        assertTrue(t.second().equals("06:e9:44:00:00:6e"));
        assertTrue(t.third().length == 0);
        t = pingCmd.getGateways().get("i-302-22828-VM");
        assertTrue(t.first().equals(22828L));
        assertTrue(t.second().equals("06:e9:44:00:00:6e"));
        assertTrue(t.third().length == 0);
    }
}

/*
 * Created on Nov 15, 2008
 *
 */
package rain;

import java.util.Calendar;

public class VirtualMachineInfo {

    private VirtualMachine virtualMachine;
    private Calendar startTime;
    private String currentAvailabilityZone;
    private String currentDnsName;
    private String currentPrivateIpAddress;
    private Integer currentState;

    public Integer getCurrentState() {
        return currentState;
    }

    public void setCurrentState(Integer currentState) {
        this.currentState = currentState;
    }

    public String getCurrentPrivateIpAddress() {
        return currentPrivateIpAddress;
    }

    public void setCurrentPrivateIpAddress(String currentPrivateIpAddress) {
        this.currentPrivateIpAddress = currentPrivateIpAddress;
    }
    private String instanceId;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public VirtualMachine getVirtualMachine() {
        return virtualMachine;
    }

    public void setVirtualMachine(VirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public String getCurrentAvailabilityZone() {
        return currentAvailabilityZone;
    }

    public void setCurrentAvailabilityZone(String currentAvailabilityZone) {
        this.currentAvailabilityZone = currentAvailabilityZone;
    }

    public String getCurrentDnsName() {
        return currentDnsName;
    }

    public void setCurrentDnsName(String currentDnsName) {
        this.currentDnsName = currentDnsName;
    }
}

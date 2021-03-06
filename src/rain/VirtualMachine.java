/*
 * Created on Nov 3, 2008
 *
 */
package rain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.sampullara.cli.Argument;

/**
 *  This class holds virtual machine attributes
 * @author juliano
 * (c) 2008 Boltblue International Limited
 */
@Entity
public class VirtualMachine {

    public enum InstanceType {
        //t1.micro, m1.small, m1.large, m1.xlarge, m2.xlarge, m2.2xlarge, m2.4xlarge, c1.medium, c1.xlarge, cc1.4xlarge, cg1.4xlarge

        MICRO("t1.micro"), SMALL("m1.small"), LARGE("m1.large"), EXTRA_LARGE("m1.xlarge"), HIGH_MEMORY_XLARGE("m2.xlarge"), HIGH_MEMORY_DOUBLE_EXTRA_LARGE("m2.2xlarge"),
        HIGH_MEMORY_QUAD_EXTRA_LARGE("M2.4XLARGE"), HIGH_CPU_MEDIUM("c1.medium"), HIGH_CPU_EXTRA_LARGE("c1.xlarge"), CLUSTER_QUAD_EXTRA_LARGE("cc1.4xlarge"),
        CLUSTER_GPU_QUAD_EXTRA_LARGE("cg1.4xlarge");
        private String name;

        InstanceType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
    private String name;
    private String image;
    private String securityGroup;
    private String keypair;
    private String availabilityZone;
    private String kernel;
    private String ramdisk;
    private String userData;
    private List<Volume> volumes;
    private String currentInstance;
    private String staticIpAddress;
    private InstanceType instanceType;
    private String autoRunCommand;
    private Boolean isEBSRootDevice = false;
    private String vpcSubNet;
    private String privateIpAddress;

    


    public String getPrivateIpAddress() {
        return privateIpAddress;
    }

    public void setPrivateIpAddress(String privateIpAddress) {
        this.privateIpAddress = privateIpAddress;
    }

    public String getVpcSubNet() {
        return vpcSubNet;
    }

    public void setVpcSubNet(String vpcSubNet) {
        this.vpcSubNet = vpcSubNet;
    }

   

    public Boolean getIsEBSRootDevice() {
        return isEBSRootDevice;
    }

    public void setIsEBSRootDevice(Boolean isEBSRootDevice) {
        this.isEBSRootDevice = isEBSRootDevice;
    }

    public String getAutoRunCommand() {
        return autoRunCommand;
    }

    public void setAutoRunCommand(String autoRunCommand) {
        this.autoRunCommand = autoRunCommand;
    }

    @Enumerated(EnumType.STRING)
    public InstanceType getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(InstanceType instanceType) {
        this.instanceType = instanceType;
    }

    public String getStaticIpAddress() {
        return staticIpAddress;
    }

    public void setStaticIpAddress(String staticIpAddress) {
        this.staticIpAddress = staticIpAddress;
    }

    public String getCurrentInstance() {
        return currentInstance;
    }

    public void setCurrentInstance(String currentInstance) {
        this.currentInstance = currentInstance;
    }

    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSecurityGroup() {
        return securityGroup;
    }

    public void setSecurityGroup(String group) {
        this.securityGroup = group;
    }

    public String getKeypair() {
        return keypair;
    }

    public void setKeypair(String keypair) {
        this.keypair = keypair;
    }

    public String getAvailabilityZone() {
        return availabilityZone;
    }

    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }

    public String getKernel() {
        return kernel;
    }

    public void setKernel(String kernel) {
        this.kernel = kernel;
    }

    public String getRamdisk() {
        return ramdisk;
    }

    public void setRamdisk(String ramdisk) {
        this.ramdisk = ramdisk;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    @OneToMany(mappedBy = "currentMachine")
    public List<Volume> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<Volume> volumes) {
        this.volumes = volumes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        VirtualMachine other = (VirtualMachine) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}

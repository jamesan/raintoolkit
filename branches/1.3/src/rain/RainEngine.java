/*
 * Created on Nov 3, 2008
 *
 */
package rain;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.AllocateAddressResult;
import com.amazonaws.services.ec2.model.AssociateAddressRequest;
import com.amazonaws.services.ec2.model.AttachVolumeRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.BlockDeviceMapping;
import com.amazonaws.services.ec2.model.CreateSnapshotRequest;
import com.amazonaws.services.ec2.model.CreateSnapshotResult;
import com.amazonaws.services.ec2.model.CreateVolumeRequest;
import com.amazonaws.services.ec2.model.CreateVolumeResult;
import com.amazonaws.services.ec2.model.DeleteSnapshotRequest;
import com.amazonaws.services.ec2.model.DeleteVolumeRequest;
import com.amazonaws.services.ec2.model.DescribeAddressesRequest;
import com.amazonaws.services.ec2.model.DescribeAddressesResult;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsRequest;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.DescribeSnapshotsRequest;
import com.amazonaws.services.ec2.model.DescribeSnapshotsResult;
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.DescribeVolumesRequest;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.DetachVolumeRequest;
import com.amazonaws.services.ec2.model.EbsBlockDevice;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RevokeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Snapshot;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.VolumeAttachment;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;





import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;

public class RainEngine extends BaseEngine {

    private static final long DEFAULT_POLL_INTERVAL = 10000;
    private static final int MAX_MOUNT_RETRIES = 50;
    private static final int MAX_VOLUME_DETACH_RETRIES = 3;
    public static RainEngine instance;
    static Logger logger = Logger.getLogger(RainEngine.class.getName());
    //: 0 (pending) | 16 (running) | 32 (shutting-down) | 48 (terminated) | 64  (stopping) | 80 (stopped)
    public static final int INSTANCE_STATE_PENDING = 0;
    public static final int INSTANCE_STATE_RUNNING = 16;
    public static final int INSTANCE_STATE_SHUTTING_DOWN = 32;
    public static final int INSTANCE_STATE_TERMINATED = 48;
    public static final int INSTANCE_STATE_STOPPING = 64;
    public static final int INSTANCE_STATE_STOPPED = 80;

    public static synchronized RainEngine getInstance() {
        if (instance == null) {
            instance = new RainEngine();
        }

        return instance;
    }
    private JPAVirtualMachineDAO virtualMachineDAO;
    private JPAVolumeDAO volumeDAO;
    private JpaDynamicIpAddressDAO dynamicIPDAO;
    private AmazonEC2 ec2;

    /**
     * @param aws_access_id
     * @param aws_secret_key
     * @param aws_account_id
     * @param glue_home
     * @param endpointURL
     */
    public RainEngine(String aws_access_id, String aws_secret_key,
            String aws_account_id, String glue_home, String endpointURL) {
        super(aws_access_id, aws_secret_key, aws_account_id, glue_home,
                endpointURL);
        initializeEngine();

    }

    public RainEngine() {

        initializeEngine();

    }

    private void initializeEngine() {


        ec2 = new AmazonEC2Client(new BasicAWSCredentials(aws_access_id, aws_secret_key));


        if (endpointURL != null) {
            ec2.setEndpoint(endpointURL);
        }



        virtualMachineDAO = new JPAVirtualMachineDAO();
        volumeDAO = new JPAVolumeDAO();
        dynamicIPDAO = new JpaDynamicIpAddressDAO();

    }

    public void createVirtualMachine(VirtualMachine vm)
            throws AMIDoesNotExistException,
            VirtualMachineAlreadyExistsException,
            VirtualMachineNotFoundException, SecurityGroupNotFoundException,
            KernelNotFoundException, InstanceNotFoundException,
            AvailabilityZoneNotFoundException,
            StaticIpAddressNotFoundException,
            IpAddressAlreadyAssignedException, KeyPairNotFoundException, ImageIsNotKernelException, SubNetNotFoundException, InconsistentSubNetAvailabilityZoneException {

        if (checkAMIExists(vm.getImage()) == null) {
            throw new AMIDoesNotExistException(vm.getImage());
        }

        checkVirtualMachineExists(vm.getName());

        modifyVirtualMachine(vm, null, true);

    }

    public void deleteVirtualMachine(String name)
            throws VirtualMachineNotFoundException {

        VirtualMachine vm = virtualMachineDAO.findByName(name);
        if (vm == null) {
            throw new VirtualMachineNotFoundException(name);
        }

        if (vm.getVolumes() != null && vm.getVolumes().size() > 0) {

            for (Volume v : vm.getVolumes()) {
                v.setCurrentMachine(null);
                volumeDAO.saveOrUpdate(v);
            }

        }

        virtualMachineDAO.delete(vm);

    }

    private void checkVirtualMachineExists(String name)
            throws VirtualMachineAlreadyExistsException {

        VirtualMachine vm = virtualMachineDAO.findByName(name);
        if (vm != null) {
            throw new VirtualMachineAlreadyExistsException(name);
        }
    }

    private Image checkAMIExists(String image) {


        DescribeImagesResult result = ec2.describeImages(new DescribeImagesRequest().withImageIds(image));
        if (result.getImages().size() > 0) {
            return result.getImages().get(0);
        }

        return null;

    }

    public String getVirtualMachineIPAddress(String name) {

        VirtualMachine vm = virtualMachineDAO.findByName(name);
        if (vm == null) {
            return null;
        }


        DescribeInstancesResult instances = ec2.describeInstances();

        for (Reservation r : instances.getReservations()) {
            List<Instance> list = r.getInstances();
            for (Instance i : list) {
                if (vm.getCurrentInstance() != null
                        && i.getInstanceId().equals(vm.getCurrentInstance())
                        && i.getState().getName().equals(InstanceStateName.Running.toString())) {
                    return getPublicDnsNameOrIpAddress(i);
                }

            }

        }

        return null;


    }

    public Integer getVirtualMachineStatus(String name) {
        VirtualMachine vm = virtualMachineDAO.findByName(name);
        if (vm == null) {
            return null;
        }
        if (vm.getCurrentInstance() == null) {
            return null;
        }

        DescribeInstancesResult instances = ec2.describeInstances(new DescribeInstancesRequest().withInstanceIds(vm.getCurrentInstance()));

        if (instances.getReservations().size() == 0) {
            return INSTANCE_STATE_TERMINATED;
        }

        return instances.getReservations().get(0).getInstances().get(0).getState().getCode();


    }

    public String startVirtualMachine(String name)
            throws VirtualMachineNotFoundException,
            CannotStartInstanceException,
            VirtualMachineAlreadyRunningException,
            InconsistentAvailabilityZoneException,
            VolumeAlreadyAttachedException, AddressAlreadyAssignedException,
            VolumeMountFailedException, AutoRunCommandFailedException, InstanceStoppingException {

        VirtualMachine vm = virtualMachineDAO.findByName(name);
        if (vm == null) {
            throw new VirtualMachineNotFoundException(name);
        }

        try {
            Integer currentState = getVirtualMachineStatus(name);

            if (currentState != null && currentState == INSTANCE_STATE_RUNNING) {
                throw new VirtualMachineAlreadyRunningException("");
            }

            String startZone = checkVolumeAvailabilityAndConsistency(vm);

            checkStaticAddressAvailability(vm);

            if (vm.getIsEBSRootDevice() && currentState != null && currentState == INSTANCE_STATE_STOPPING) {
                throw new InstanceStoppingException(vm);
            }


            Instance instance;

            if (vm.getIsEBSRootDevice() && currentState != null && currentState == INSTANCE_STATE_STOPPED) {
                // Need to start a previously stopped machine

                StartInstancesResult result = ec2.startInstances(new StartInstancesRequest().withInstanceIds(vm.getCurrentInstance()));
                instance = waitForInstanceToStartRunning(vm.getCurrentInstance());


            } else {


                // Start the virtual machine
                RunInstancesRequest configuration = new RunInstancesRequest();
                configuration.setImageId(vm.getImage());
                configuration.setMaxCount(1);
                configuration.setMinCount(1);
                configuration.setPlacement(new Placement(startZone));
                configuration.setKernelId(vm.getKernel());
                configuration.setKeyName(vm.getKeypair());
                configuration.setRamdiskId(vm.getRamdisk());
                if (vm.getSecurityGroup() != null && vm.getVpcSubNet()==null) {
                    String[] tmp = vm.getSecurityGroup().split(",");
                    configuration.setSecurityGroupIds(Arrays.asList(tmp));

                }

                VirtualMachine.InstanceType type = vm.getInstanceType();
                if (type == null) {
                    type = VirtualMachine.InstanceType.SMALL;
                }
                configuration.setInstanceType(type.getName());

                if (vm.getUserData() != null) {
                    configuration.setUserData(vm.getUserData());
                }

                configuration.setSubnetId(vm.getVpcSubNet());
                configuration.setPrivateIpAddress(vm.getPrivateIpAddress());
                if(vm.getIsEBSRootDevice())
                    configuration.setDisableApiTermination(Boolean.TRUE);
                


                RunInstancesResult result = ec2.runInstances(configuration);
                instance = result.getReservation().getInstances().get(0);
                vm.setCurrentInstance(instance.getInstanceId());
                virtualMachineDAO.saveOrUpdate(vm);

                instance = waitForInstanceToStartRunning(instance.getInstanceId());


            }


            if (!instance.getState().getName().equals(InstanceStateName.Running.toString())) {
                throw new CannotStartInstanceException(instance.getState().getCode());
            }

            if (vm.getStaticIpAddress() != null) {

                instance = associateIpAddress(vm, instance);
            }

            if (vm.getVolumes() != null) {
                // mount volumes
                attachVolumes(vm);
                instance = waitForPublicIpToBecomeAvailable(vm, instance);
                mountVolumes(vm, instance);

            }

            if (vm.getAutoRunCommand() != null) {
                // Executes autorun command
                executeAutoRuncommand(vm, instance);
            }

            return getPublicDnsNameOrIpAddress(instance);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void executeAutoRuncommand(VirtualMachine vm, Instance instance)
            throws IOException, InterruptedException,
            AutoRunCommandFailedException {

        int exitStatus = executeCommand(vm, instance, vm.getAutoRunCommand());

        if (exitStatus != 0) {
            throw new AutoRunCommandFailedException(vm, exitStatus);
        }

    }

    private String getPublicDnsNameOrIpAddress(Instance instance) {

        if(instance.getPublicDnsName()!=null && !instance.getPublicDnsName().equals(""))
            return instance.getPublicDnsName();

        return instance.getPublicIpAddress();

    }
    private Instance waitForPublicIpToBecomeAvailable(VirtualMachine vm,
            Instance instance) {
        String publicAddress=null;
        do {
             publicAddress=getPublicDnsNameOrIpAddress(instance);
            if (publicAddress==null || publicAddress.equals("")) {
                logger.fine("Waiting for ip address to become available...");
                try {
                    Thread.sleep(DEFAULT_POLL_INTERVAL);
                } catch (InterruptedException e) {
                }

                instance = refreshInstanceInformation(instance);

            }
        } while (publicAddress==null || publicAddress.equals(""));

        return instance;

    }

    private Instance associateIpAddress(VirtualMachine vm, Instance instance) throws UnknownHostException {
        // Associates the ip address and waits until the instance assumes the
        // address

        if(vm.getVpcSubNet()!=null) {
            // Need to get the allocation id first for vpc elastic ips
            DescribeAddressesResult result=ec2.describeAddresses(new DescribeAddressesRequest().withPublicIps(vm.getStaticIpAddress()));
            if(result.getAddresses().size()==0)
                throw new RuntimeException("Static ip address not found: "+vm.getStaticIpAddress());

            ec2.associateAddress(new AssociateAddressRequest().withInstanceId(instance.getInstanceId()).withAllocationId(result.getAddresses().get(0).getAllocationId()));

        }
        else
            ec2.associateAddress(new AssociateAddressRequest(instance.getInstanceId(), vm.getStaticIpAddress()));

        String instanceAddress;
        do {
            instance = refreshInstanceInformation(instance);
            instanceAddress = getPublicDnsNameOrIpAddress(instance);
            instanceAddress = InetAddress.getByName(instanceAddress).getHostAddress();
            if (!instanceAddress.equals(vm.getStaticIpAddress())) {
                try {
                    logger.fine("Waiting for static ip to be assigned (current name= " + instance.getPublicDnsName() + ", ip = " + instanceAddress + ", wanted = " + vm.getStaticIpAddress() + ")");
                    Thread.sleep(DEFAULT_POLL_INTERVAL);
                } catch (InterruptedException e) {
                }
            }

        } while (!instanceAddress.equals(vm.getStaticIpAddress()));

        return instance;

    }

    private Instance refreshInstanceInformation(Instance instance) {

        DescribeInstancesResult result = ec2.describeInstances(new DescribeInstancesRequest().withInstanceIds(instance.getInstanceId()));
        if (result.getReservations().size() > 0) {
            instance = result.getReservations().get(0).getInstances().get(0);
        }

        return instance;

    }

    private void mountVolumes(VirtualMachine vm, Instance currentInstance)
            throws IOException, InterruptedException,
            VolumeMountFailedException {

        // Executes ssh in order to mount the volumes for the given vm

        // This assumes that the private key (identity) is available in the
        // 'keys' directory

        List<Volume> volumes = vm.getVolumes();
        for (Volume vol : volumes) {
            if (vol.getMountPoint() != null) {
                mountVolume(vol, vm, currentInstance);
            }
        }

    }

    public int executeCommand(VirtualMachine vm, Instance currentInstance,
            String command) throws IOException, InterruptedException {

        String instanceAddress=getPublicDnsNameOrIpAddress(currentInstance);

        String[] cmdLine = new String[]{
            "ssh",
            "-o",
            "StrictHostKeyChecking=false",
            "-i",
            glue_home + File.separatorChar + "keys" + File.separatorChar
            + vm.getKeypair() + ".identity",
            "root@" + instanceAddress, command};

        String cmd = "";
        for (String s : cmdLine) {
            cmd = cmd + s + " ";
        }

        logger.fine("Executing: " + cmd);

        Process p = Runtime.getRuntime().exec(cmdLine);

        int exitStatus = p.waitFor();
        return exitStatus;

    }

    private void mountVolume(Volume vol, VirtualMachine vm,
            Instance currentInstance) throws IOException,
            VolumeMountFailedException, InterruptedException {

        // Retry a number of times in case of failure since ssh may not yet have
        // started

        String device = vol.getMountDevice() != null ? vol.getMountDevice()
                : vol.getDevice();

        for (int i = 0; i < MAX_MOUNT_RETRIES; i++) {
            int status = executeCommand(vm, currentInstance, "mkdir -p "
                    + vol.getMountPoint() + " && mount " + device + " "
                    + vol.getMountPoint());
            if (status == 0) {
                return;
            }

            logger.fine("Mount failed , waiting to try again...");
            Thread.sleep(DEFAULT_POLL_INTERVAL);

        }

        throw new VolumeMountFailedException(vm, vol);

    }

    private void attachVolumes(VirtualMachine vm) {


        List<Volume> volumes = vm.getVolumes();

        if (volumes == null || volumes.size() == 0) {
            return;
        }

        List<String> pendingVolumeIds = new ArrayList<String>();
        for (Volume vol : volumes) {

            ec2.attachVolume(new AttachVolumeRequest(vol.getVolumeId(), vm.getCurrentInstance(), vol.getDevice()));
            pendingVolumeIds.add(vol.getVolumeId());

        }

        logger.fine("Waiting for volumes to attach...");
        do {

            DescribeVolumesResult result = ec2.describeVolumes(new DescribeVolumesRequest(pendingVolumeIds));
            for (com.amazonaws.services.ec2.model.Volume v : result.getVolumes()) {

                List<VolumeAttachment> attachments = v.getAttachments();
                if (attachments.size() > 0) {
                    for (VolumeAttachment att : attachments) {
                        if (att.getState().equals("attached")) {
                            logger.fine("Volume " + v.getVolumeId()
                                    + " attached");
                            pendingVolumeIds.remove(v.getVolumeId());
                        }
                    }
                }

            }
            if (pendingVolumeIds.size() > 0) {
                try {
                    logger.fine("Sleeping...");
                    Thread.sleep(DEFAULT_POLL_INTERVAL);
                } catch (InterruptedException e) {
                }
            }

        } while (pendingVolumeIds.size() > 0);

        logger.fine("All volumes attached");
    }

    private Instance waitForInstanceToStartRunning(String instanceId) {
        int state;
        Instance instance;
        do {
            DescribeInstancesResult result = ec2.describeInstances(new DescribeInstancesRequest().withInstanceIds(Collections.singletonList(instanceId)));
            instance = result.getReservations().get(0).getInstances().get(0);
            state = instance.getState().getCode();
            if (state == 0) {
                try {
                    Thread.sleep(DEFAULT_POLL_INTERVAL);
                } catch (InterruptedException e) {
                }
                if (result.getReservations().size() < 1) {
                    throw new RuntimeException("Instance "
                            + instance.getInstanceId()
                            + " vanished while waiting for it to start");
                }


            }
        } while (state == 0);

        return instance;

    }

    private String checkVolumeAvailabilityAndConsistency(VirtualMachine vm)
            throws InconsistentAvailabilityZoneException,
            VolumeAlreadyAttachedException {
        String availabilityZone = vm.getAvailabilityZone();

        String availabilityZoneToCheck = availabilityZone;
        List<Volume> volumes = vm.getVolumes();
        if (volumes != null && volumes.size() > 0) {

            String[] volumeIds = new String[volumes.size()];
            for (int i = 0; i < volumes.size(); i++) {
                volumeIds[i] = volumes.get(i).getVolumeId();
            }

            for (int t = 0; t < MAX_VOLUME_DETACH_RETRIES + 1; t++) {
                DescribeVolumesResult result = ec2.describeVolumes(new DescribeVolumesRequest(Arrays.asList(volumeIds)));

                for (int i = 0; i < result.getVolumes().size(); i++) {
                    com.amazonaws.services.ec2.model.Volume info = result.getVolumes().get(i);
                    if (availabilityZoneToCheck == null) {
                        availabilityZoneToCheck = info.getAvailabilityZone();
                    }
                    if (!availabilityZoneToCheck.equals(info.getAvailabilityZone())) {
                        throw new InconsistentAvailabilityZoneException();
                    }
                    List<VolumeAttachment> attachments = info.getAttachments();
                    if (attachments != null && attachments.size() > 0) {

                        // Sometimes a volume can get struck in EC2, attached to
                        // an instance that is no longer running
                        // If we detect that this is the case, then we force the
                        // volume to be detached and repeat the check
                        VolumeAttachment attachment = attachments.get(0);
                        String attachedInstanceId = attachment.getInstanceId();
                        if (isInstanceRunning(attachedInstanceId)) {
                            throw new VolumeAlreadyAttachedException(volumes.get(i));
                        } else if (t < MAX_VOLUME_DETACH_RETRIES) {
                            DetachVolumeRequest detachRequest = new DetachVolumeRequest(info.getVolumeId());
                            detachRequest.setForce(Boolean.TRUE);
                            detachRequest.setInstanceId(attachment.getInstanceId());

                            ec2.detachVolume(detachRequest);
                            try {
                                Thread.sleep(DEFAULT_POLL_INTERVAL);
                            } catch (InterruptedException e) {
                            }
                            break;
                        } else {
                            throw new VolumeAlreadyAttachedException(volumes.get(i));
                        }

                    }

                }
                return availabilityZoneToCheck;
            }
            throw new RuntimeException(
                    "Code should never get into this path, this is a bug");

        }

        return availabilityZone;


    }

    private boolean isInstanceRunning(String id) {

        DescribeInstancesResult result = ec2.describeInstances(new DescribeInstancesRequest().withInstanceIds(Collections.singletonList(id)));

        for (Reservation r : result.getReservations()) {

            for (Instance instance : r.getInstances()) {

                if (instance.getInstanceId().equals(id) && instance.getState().getName().equals(InstanceStateName.Running.toString())) {
                    return true;
                }
            }

        }

        return false;

    }

    private void checkStaticAddressAvailability(VirtualMachine vm)
            throws AddressAlreadyAssignedException {
        String staticIpAddress = vm.getStaticIpAddress();
        if (staticIpAddress != null) {
            DescribeAddressesResult result = ec2.describeAddresses(new DescribeAddressesRequest().withPublicIps(Collections.singletonList(staticIpAddress)));
            Address info = result.getAddresses().get(0);
            if (info.getInstanceId() != null
                    && !info.getInstanceId().equals("")) {
                throw new AddressAlreadyAssignedException(staticIpAddress, info.getInstanceId());
            }
        }
    }

    public String createVolume(String name, Integer size,
            String availabilityZone, String snapshot) throws
            VolumeAlreadyExistsException, SnapshotNotFoundException {

        Volume volume = null;
        if (name != null) {
            volume = volumeDAO.findByName(name);
            if (volume != null) {
                throw new VolumeAlreadyExistsException(volume);
            }
        }

        volume = new Volume();
        volume.setName(name);

        if (snapshot != null) {
            checkSnapshotAvailability(snapshot);
        }

        String volumeSize = "";
        if (size != null) {
            volumeSize = "" + size;
        }

        CreateVolumeRequest request = new CreateVolumeRequest();

        request.setSnapshotId(snapshot);
        request.setSize(size);
        request.setAvailabilityZone(availabilityZone);

        CreateVolumeResult result = ec2.createVolume(request);
        if (name != null) {
            volume.setVolumeId(result.getVolume().getVolumeId());
            volumeDAO.saveOrUpdate(volume);
        }

        return result.getVolume().getVolumeId();

    }

    private void checkSnapshotAvailability(String snapshot)
            throws SnapshotNotFoundException {
        DescribeSnapshotsResult result = ec2.describeSnapshots();
        boolean foundSnapshot = false;

        for (Snapshot i : result.getSnapshots()) {
            if (i.getSnapshotId().equals(snapshot)) {
                foundSnapshot = true;
                break;
            }

        }
        if (!foundSnapshot) {
            throw new SnapshotNotFoundException(snapshot);
        }
    }

    public String createVolume(String name, String volumeId)
            throws VolumeAlreadyExistsException,
            VolumeNotFoundException {

        Volume vol = volumeDAO.findByName(name);
        if (vol != null) {
            throw new VolumeAlreadyExistsException(vol);
        }

        DescribeVolumesResult result = ec2.describeVolumes(new DescribeVolumesRequest().withVolumeIds(Collections.singletonList(volumeId)));
        if (result == null || result.getVolumes().isEmpty()) {
            throw new VolumeNotFoundException(volumeId);
        }
        vol = new Volume();
        vol.setName(name);
        vol.setVolumeId(volumeId);
        volumeDAO.saveOrUpdate(vol);

        return result.getVolumes().get(0).getVolumeId();

    }

    public void attachVolume(String volume, String virtualMachine,
            String device, String mountDevice, String mountPoint)
            throws VolumeNotFoundException, VolumeAlreadyAttachedException,
            VirtualMachineNotFoundException, MountPointAlreadyInUseException,
            DeviceAlreadyInUseException {

        Volume vol = volumeDAO.findByName(volume);
        if (vol == null) {
            throw new VolumeNotFoundException(volume);
        }

        if (vol.getCurrentMachine() != null) {
            throw new VolumeAlreadyAttachedException(vol);
        }

        VirtualMachine vm = virtualMachineDAO.findByName(virtualMachine);
        if (vm == null) {
            throw new VirtualMachineNotFoundException(virtualMachine);
        }

        List<Volume> volumes = vm.getVolumes();
        if (volumes == null) {
            volumes = new ArrayList<Volume>();
            vm.setVolumes(volumes);
        } else {
            // Make sure no other volume is attached to the same vm using same
            // device or mount point
            for (Volume v : volumes) {
                if (mountPoint != null && v.getMountPoint() != null
                        && v.getMountPoint().equals(mountPoint)) {
                    throw new MountPointAlreadyInUseException(mountPoint);
                }
                if (v.getDevice().equals(device)) {
                    throw new DeviceAlreadyInUseException(device);
                }
                if (mountDevice != null) {
                    if (v.getMountDevice() != null
                            && v.getMountDevice().equals(mountDevice)) {
                        throw new DeviceAlreadyInUseException(mountDevice);
                    }
                }

            }
        }
        vol.setMountPoint(mountPoint);
        vol.setDevice(device);
        vol.setMountDevice(mountDevice);
        volumes.add(vol);
        vol.setCurrentMachine(vm);

        volumeDAO.saveOrUpdate(vol);
        virtualMachineDAO.saveOrUpdate(vm);

    }

    public void modifyVirtualMachine(VirtualMachine vm, String newName)
            throws VirtualMachineNotFoundException,
            VirtualMachineAlreadyExistsException,
            SecurityGroupNotFoundException, KernelNotFoundException,
            AMIDoesNotExistException, InstanceNotFoundException,
            AvailabilityZoneNotFoundException,
            StaticIpAddressNotFoundException,
            IpAddressAlreadyAssignedException, KeyPairNotFoundException, ImageIsNotKernelException, SubNetNotFoundException, InconsistentSubNetAvailabilityZoneException {
        modifyVirtualMachine(vm, newName, false);
    }

    private void modifyVirtualMachine(VirtualMachine vm, String newName,
            boolean create) throws VirtualMachineNotFoundException,
            VirtualMachineAlreadyExistsException,
            SecurityGroupNotFoundException, KernelNotFoundException,
            AMIDoesNotExistException, InstanceNotFoundException,
            AvailabilityZoneNotFoundException,
            StaticIpAddressNotFoundException,
            IpAddressAlreadyAssignedException, KeyPairNotFoundException, ImageIsNotKernelException, SubNetNotFoundException, InconsistentSubNetAvailabilityZoneException {

        VirtualMachine current;

        if (!create) {
            current = virtualMachineDAO.findByName(vm.getName());
            if (current == null) {
                throw new VirtualMachineNotFoundException(vm.getName());
            }

        } else {
            current = vm;
        }

        if (vm.getAvailabilityZone() != null) {
            if (vm.getAvailabilityZone().equals("none")) {
                current.setAvailabilityZone(null);
            } else {
                checkAvailabilityZone(vm.getAvailabilityZone());
                current.setAvailabilityZone(vm.getAvailabilityZone());
            }
        }
        if (vm.getCurrentInstance() != null) {
            checkInstance(vm.getCurrentInstance());
            current.setCurrentInstance(vm.getCurrentInstance());
        }

        if (vm.getImage() != null) {
            Image image = checkImage(vm.getImage());
            current.setImage(vm.getImage());
            if (image.getRootDeviceType().equals("ebs")) {
                current.setIsEBSRootDevice(true);
            } else {
                current.setIsEBSRootDevice(false);
            }
        }

        if (vm.getInstanceType() != null) {
            // Todo: check if selected kernel matched selected instance type
            current.setInstanceType(vm.getInstanceType());
        }

        if (vm.getKernel() != null) {
            checkKernel(vm.getKernel());
            current.setKernel(vm.getKernel());
        }
        if (vm.getKeypair() != null) {
            checkKeyPair(vm.getKeypair());
            current.setKeypair(vm.getKeypair());
        }
        if (newName != null) {
            checkVirtualMachineExists(newName);
            current.setName(newName);
        }

        if (vm.getRamdisk() != null) {
            current.setRamdisk(vm.getRamdisk());
        }

        if (vm.getSecurityGroup() != null) {
            checkSecurityGroups(vm.getSecurityGroup());
            current.setSecurityGroup(vm.getSecurityGroup());
        }

        if (vm.getAutoRunCommand() != null) {
            current.setAutoRunCommand(vm.getAutoRunCommand());
        }

        if (vm.getStaticIpAddress() != null) {
            if (vm.getStaticIpAddress().equals("allocate")) {
                String ipAddress = allocateStaticIpAddress();
                current.setStaticIpAddress(ipAddress);
            } else {
                checkIpAddress(vm.getStaticIpAddress());
                VirtualMachine vm2 = virtualMachineDAO.findByStaticIpAddress(vm.getStaticIpAddress());
                if (vm2 != null && !vm2.getName().equals(vm.getName())) {
                    throw new IpAddressAlreadyAssignedException(vm2);
                }
                current.setStaticIpAddress(vm.getStaticIpAddress());
            }
        }

        if (vm.getUserData() != null) {
            current.setUserData(vm.getUserData());
        }

        if(vm.getVpcSubNet()!=null) {
            checkVpcSubNet(vm);
            current.setVpcSubNet(vm.getVpcSubNet());
        }
        //TODO: check if the private ip really belongs to the subnet
        if(vm.getPrivateIpAddress()!=null)
            current.setPrivateIpAddress(vm.getPrivateIpAddress());


        virtualMachineDAO.saveOrUpdate(current);

    }

    private void checkKeyPair(String keypair) throws KeyPairNotFoundException {

        DescribeKeyPairsResult result = ec2.describeKeyPairs(new DescribeKeyPairsRequest().withKeyNames(Collections.singletonList(keypair)));
        if (result.getKeyPairs().size() == 0) {
            throw new KeyPairNotFoundException(keypair);
        }


    }

    private void checkIpAddress(String staticIpAddress)
            throws StaticIpAddressNotFoundException {

        DescribeAddressesResult result = ec2.describeAddresses(new DescribeAddressesRequest().withPublicIps(Collections.singletonList(staticIpAddress)));
        if (result.getAddresses().size() == 0) {
            throw new StaticIpAddressNotFoundException(staticIpAddress);
        }


    }

    private String allocateStaticIpAddress() {


        AllocateAddressResult result = ec2.allocateAddress();
        return result.getPublicIp();

    }

    private void checkAvailabilityZone(String availabilityZone)
            throws AvailabilityZoneNotFoundException {


        DescribeAvailabilityZonesResult result = ec2.describeAvailabilityZones(new DescribeAvailabilityZonesRequest().withZoneNames(Collections.singletonList(availabilityZone)));
        if (result.getAvailabilityZones().size() == 0) {
            throw new AvailabilityZoneNotFoundException(availabilityZone);
        }



    }

    private void checkInstance(String currentInstance)
            throws InstanceNotFoundException {


        DescribeInstancesResult result = ec2.describeInstances(new DescribeInstancesRequest().withInstanceIds(currentInstance));
        if (result.getReservations().size() == 0) {
            throw new InstanceNotFoundException(currentInstance);
        }


    }

    private Image checkImage(String image) throws AMIDoesNotExistException {

        Image ami = checkAMIExists(image);

        if (ami == null) {
            throw new AMIDoesNotExistException(image);
        }


        return ami;

    }

    private void checkKernel(String kernel) throws KernelNotFoundException, ImageIsNotKernelException {


        DescribeImagesResult result = ec2.describeImages(new DescribeImagesRequest().withImageIds(Collections.singletonList(kernel)));
        if (result.getImages().size() == 0) {
            throw new KernelNotFoundException(kernel);
        }
        Image image = result.getImages().get(0);
        if (!image.getImageType().equals("kernel")) {
            throw new ImageIsNotKernelException(kernel);
        }





    }

    private void checkSecurityGroups(String groups)
            throws SecurityGroupNotFoundException {



        String[] tmp = groups.split(",");
        DescribeSecurityGroupsResult result = ec2.describeSecurityGroups(new DescribeSecurityGroupsRequest().withGroupNames(Arrays.asList(tmp)));

        for (String group : tmp) {
            boolean found = false;
            for (SecurityGroup i : result.getSecurityGroups()) {
                if (i.getGroupName().equals(group)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new SecurityGroupNotFoundException(group);
            }
        }


    }

    public void terminateVirtualMachine(String name, boolean forceEBSTermination)
            throws VirtualMachineNotRunningException,
            VirtualMachineNotFoundException,
            EBSVirtualMachineTerminateException {

        VirtualMachine vm = virtualMachineDAO.findByName(name);
        if (vm == null) {
            throw new VirtualMachineNotFoundException(name);
        }

        if (vm.getIsEBSRootDevice() && !forceEBSTermination) {
            throw new EBSVirtualMachineTerminateException(vm);
        }


        Integer state = getVirtualMachineStatus(name);
        if (state != null && state != INSTANCE_STATE_STOPPED && state != INSTANCE_STATE_RUNNING) {
            throw new VirtualMachineNotRunningException(name);
        }


        ec2.terminateInstances(new TerminateInstancesRequest().withInstanceIds(Collections.singletonList(vm.getCurrentInstance())));

        vm.setCurrentInstance(null);

        virtualMachineDAO.saveOrUpdate(vm);


    }

    public void stopVirtualMachine(String name, boolean force) throws VirtualMachineNotFoundException, VirtualMachineIsNotEBSBackedException, VirtualMachineNotRunningException {

        VirtualMachine vm = virtualMachineDAO.findByName(name);
        if (vm == null) {
            throw new VirtualMachineNotFoundException(name);
        }

        if (!vm.getIsEBSRootDevice()) {
            throw new VirtualMachineIsNotEBSBackedException(vm);
        }

        Integer state = getVirtualMachineStatus(name);
        if (state == null || state != INSTANCE_STATE_RUNNING) {
            throw new VirtualMachineNotRunningException(name);

        }

        ec2.stopInstances(new StopInstancesRequest().withInstanceIds(vm.getCurrentInstance()).withForce(force));


    }

    public List<VirtualMachineInfo> describeVirtualMachines(String[] names)
            throws VirtualMachineNotFoundException {
        List<VirtualMachineInfo> list = new ArrayList<VirtualMachineInfo>();
        List<VirtualMachine> vms;
        if (names == null) {
            vms = virtualMachineDAO.findAll();
        } else {
            vms = new ArrayList<VirtualMachine>();
            for (String name : names) {
                VirtualMachine vm = virtualMachineDAO.findByName(name);
                if (vm == null) {
                    throw new VirtualMachineNotFoundException(name);
                }
                vms.add(vm);
            }
        }
        try {

            ArrayList<Instance> instances = new ArrayList<Instance>();
            DescribeInstancesResult result = ec2.describeInstances();

            for (Reservation r : result.getReservations()) {
                instances.addAll(r.getInstances());
            }

            for (VirtualMachine vm : vms) {
                VirtualMachineInfo info = new VirtualMachineInfo();
                info.setVirtualMachine(vm);

                list.add(info);
                if (vm.getCurrentInstance() != null) {
                    Iterator<Instance> it = instances.iterator();
                    while (it.hasNext()) {
                        Instance instance = it.next();

                        if (instance.getInstanceId().equals(
                                vm.getCurrentInstance())) {


                            info.setCurrentState(instance.getState().getCode());


                            if ((instance.getState().getCode() == INSTANCE_STATE_RUNNING || instance.getState().getCode() == INSTANCE_STATE_STOPPED || instance.getState().getCode() == INSTANCE_STATE_STOPPING)) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(instance.getLaunchTime());
                                info.setStartTime(cal);
                                info.setCurrentAvailabilityZone(instance.getPlacement().getAvailabilityZone());
                                info.setCurrentDnsName(getPublicDnsNameOrIpAddress(instance));
                                info.setInstanceId(instance.getInstanceId());
                                info.setCurrentPrivateIpAddress(instance.getPrivateIpAddress());
                                it.remove();

                            }
                        }
                    }
                }
            }
            // Any instances left in the array after that are unamed instance

            if (names == null) {
                for (Instance instance : instances) {
                    VirtualMachineInfo info = new VirtualMachineInfo();
                    info.setCurrentState(instance.getState().getCode());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(instance.getLaunchTime());
                    info.setStartTime(cal);
                    info.setCurrentAvailabilityZone(instance.getPlacement().getAvailabilityZone());
                    info.setCurrentDnsName(instance.getPublicDnsName());
                    info.setInstanceId(instance.getInstanceId());
                    list.add(info);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;

    }

    public String createSnapshotByName(String volume)
            throws VolumeNotFoundException {

        Volume vol = volumeDAO.findByName(volume);
        if (vol == null) {
            throw new VolumeNotFoundException(volume);
        }
        return createSnapshotById(vol.getVolumeId());

    }

    public String createSnapshotById(String volumeId) {


        CreateSnapshotResult result = ec2.createSnapshot(new CreateSnapshotRequest().withVolumeId(volumeId));
        return result.getSnapshot().getSnapshotId();


    }

    public String backupVolume(String volumeName, String volumeId,
            long maximumRetentionAge) throws VolumeNotFoundException,
            SnapshotNotFoundException {

        if (volumeName != null) {
            Volume volume = volumeDAO.findByName(volumeName);
            if (volume == null) {
                throw new VolumeNotFoundException(volumeName);
            }
            volumeId = volume.getVolumeId();
        }

        String snapshotId = createSnapshotById(volumeId);

        if (maximumRetentionAge > 0) {

            List<SnapshotDescription> snapshots = describeSnapshots(null,
                    volumeId);
            long now = System.currentTimeMillis();
            for (SnapshotDescription i : snapshots) {
                long snapshotTime = i.getSnapshotTime().getTimeInMillis();
                if (now - snapshotTime > maximumRetentionAge) {
                    logger.fine("Deleting snapshot " + i.getSnapshotId() + ", "
                            + i.getSnapshotTime().getTime());
                    deleteSnapshot(i.getSnapshotId());
                }

            }
        }
        return snapshotId;
    }

    public List<SnapshotDescription> describeSnapshots(String name, String id)
            throws VolumeNotFoundException {
        Volume volume = null;
        if (name != null) {
            volume = volumeDAO.findByName(name);
            if (volume == null) {
                throw new VolumeNotFoundException(name);
            }
            id = volume.getVolumeId();
        }

        DescribeSnapshotsResult descriptionResult = ec2.describeSnapshots();

        List<SnapshotDescription> result = new ArrayList<SnapshotDescription>();

        for (Snapshot i : descriptionResult.getSnapshots()) {

            if (id == null || i.getVolumeId().equals(id)) {
                SnapshotDescription description = new SnapshotDescription();
                description.setSnapshotId(i.getSnapshotId());
                description.setPercentComplete(i.getProgress());
                Calendar cal = Calendar.getInstance();
                cal.setTime(i.getStartTime());
                description.setSnapshotTime(cal);
                description.setStatus(i.getState());

                Volume vol;
                if (id == null) {
                    vol = volumeDAO.findByVolumeId(i.getVolumeId());
                } else {
                    vol = volume;
                }
                description.setVolume(vol);
                description.setVolumeId(i.getVolumeId());
                result.add(description);

            }
        }

        return result;
    }

    public List<VolumeDescription> describeVolumes(String volumeName,
            String volumeId) throws VolumeNotFoundException {
        Volume volume = null;
        if (volumeName != null) {
            volume = volumeDAO.findByName(volumeName);
            if (volume == null) {
                throw new VolumeNotFoundException(volumeName);
            }
            volumeId = volume.getVolumeId();
        }

        List<VolumeDescription> volumes = new ArrayList<VolumeDescription>();


        DescribeVolumesResult result = ec2.describeVolumes();

        for (com.amazonaws.services.ec2.model.Volume i : result.getVolumes()) {
            if (volumeId == null || i.getVolumeId().equals(volumeId)) {
                VolumeDescription description = new VolumeDescription();
                Volume namedVolume = volumeDAO.findByVolumeId(i.getVolumeId());
                description.setVolume(namedVolume);
                description.setVolumeId(i.getVolumeId());
                Calendar cal = Calendar.getInstance();
                cal.setTime(i.getCreateTime());
                description.setCreateTime(cal);
                description.setAvailabilityZone(i.getAvailabilityZone());
                description.setStatus(i.getState());
                description.setSize(i.getSize());
                VolumeAttachment attachment = (i.getAttachments() == null || i.getAttachments().size() == 0) ? null : i.getAttachments().get(0);
                if (attachment != null) {
                    Calendar cal2 = Calendar.getInstance();
                    cal2.setTime(attachment.getAttachTime());
                    description.setAttachTime(cal2);
                    description.setAttachedInstanceId(attachment.getInstanceId());
                    description.setAttachedInstance(virtualMachineDAO.findByInstanceId(attachment.getInstanceId()));
                    description.setAttachedDevice(attachment.getDevice());

                }
                volumes.add(description);

            }
        }



        return volumes;

    }

    public String modifyVolume(String name, String newName, String volume)
            throws VolumeNotFoundException, VolumeAlreadyExistsException,
            VolumeAlreadyInUseException {

        Volume vol = volumeDAO.findByName(name);
        if (vol == null) {
            throw new VolumeNotFoundException(name);
        }


        checkVolumeAvailability(volume);


        Volume vol2 = volumeDAO.findByVolumeId(volume);

        if (vol2 != null) {
            throw new VolumeAlreadyInUseException(vol2);
        }

        if (newName != null) {
            vol.setName(newName);
        }
        vol.setVolumeId(volume);
        volumeDAO.saveOrUpdate(vol);

        return vol.getVolumeId();

    }

    private void checkVolumeAvailability(String volume) throws
            VolumeNotFoundException {

        DescribeVolumesResult result = ec2.describeVolumes(new DescribeVolumesRequest().withVolumeIds(volume));

        if (result.getVolumes().size() == 0) {
            throw new VolumeNotFoundException(volume);
        }

    }

    public void deleteSnapshot(String snapshotId)
            throws SnapshotNotFoundException {


        DescribeSnapshotsResult result = ec2.describeSnapshots(new DescribeSnapshotsRequest().withSnapshotIds(snapshotId));
        if (result.getSnapshots().size() == 0) {
            throw new SnapshotNotFoundException(snapshotId);
        }


        ec2.deleteSnapshot(new DeleteSnapshotRequest(snapshotId));


    }

    public void deleteEC2Volume(String volumeId) throws VolumeNotFoundException {


        DescribeVolumesResult result = ec2.describeVolumes(new DescribeVolumesRequest().withVolumeIds(volumeId));

        if (result.getVolumes().size() == 0) {
            throw new VolumeNotFoundException(volumeId);
        }


        ec2.deleteVolume(new DeleteVolumeRequest(volumeId));
        Volume vol = volumeDAO.findByVolumeId(volumeId);
        if (vol != null) {
            volumeDAO.delete(vol);
        }



    }

    public void deleteVolume(String volumeName) throws VolumeNotFoundException {

        Volume vol = volumeDAO.findByName(volumeName);
        if (vol == null) {
            throw new VolumeNotFoundException(volumeName);
        }

        deleteEC2Volume(vol.getVolumeId());

    }

    public void detachVolume(String volumeName) throws VolumeNotFoundException,
            VolumeNotAttachedException {

        Volume vol = volumeDAO.findByName(volumeName);
        if (vol == null) {
            throw new VolumeNotFoundException(volumeName);
        }

        if (vol.getCurrentMachine() == null) {
            throw new VolumeNotAttachedException(vol);
        }
        VirtualMachine currentMachine = vol.getCurrentMachine();
        vol.setCurrentMachine(null);
        vol.setMountPoint(null);
        vol.setDevice(null);

        volumeDAO.saveOrUpdate(vol);

        currentMachine.getVolumes().remove(vol);

        virtualMachineDAO.saveOrUpdate(currentMachine);

    }

    public void authorizeIPAddress(String ipAddress, String securityGroup,
            String protocol, int startPort, int endPort) throws
            SecurityGroupNotFoundException {

        DescribeSecurityGroupsResult result = ec2.describeSecurityGroups(new DescribeSecurityGroupsRequest().withGroupNames(securityGroup));
        if (result.getSecurityGroups().size() == 0) {
            throw new SecurityGroupNotFoundException(securityGroup);
        }

        IpPermission permission = new IpPermission().withIpProtocol(protocol).withFromPort(startPort).withToPort(endPort).withIpRanges(ipAddress);

        AuthorizeSecurityGroupIngressRequest request = new AuthorizeSecurityGroupIngressRequest();
        request.setGroupName(securityGroup);
        request.setIpPermissions(Collections.singletonList(permission));

        ec2.authorizeSecurityGroupIngress(request);

    }

    public void revokeIPAddress(String ipAddress, String securityGroup,
            String protocol, int startPort, int endPort) throws
            SecurityGroupNotFoundException {

        DescribeSecurityGroupsResult result = ec2.describeSecurityGroups(new DescribeSecurityGroupsRequest().withGroupNames(securityGroup));
        if (result.getSecurityGroups().size() == 0) {
            throw new SecurityGroupNotFoundException(securityGroup);
        }
        IpPermission permission = new IpPermission().withIpProtocol(protocol).withFromPort(startPort).withToPort(endPort).withIpRanges(ipAddress);

        RevokeSecurityGroupIngressRequest request = new RevokeSecurityGroupIngressRequest();
        request.setGroupName(securityGroup);
        request.setIpPermissions(Collections.singletonList(permission));
        ec2.revokeSecurityGroupIngress(request);

    }

    public String getCurrentInternetIPAddress() throws IOException {

        InputStream in = null;
        try {
            URL u = new URL("http://checkip.amazonaws.com");
            in = u.openStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            return reader.readLine();

        } finally {
            if (in != null) {
                in.close();
            }
        }

    }

    public String[] getSecurityGroupNames() {

        DescribeSecurityGroupsResult result = ec2.describeSecurityGroups();

        String[] names = new String[result.getSecurityGroups().size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = result.getSecurityGroups().get(i).getGroupName();
        }

        return names;

    }

    public void createDynamicIpAddress(String name, String value)
            throws DynamicIpAddressAlreadyExistsException {

        DynamicIpAddress ip = dynamicIPDAO.findByName(name);
        if (ip != null) {
            throw new DynamicIpAddressAlreadyExistsException(name);
        }

        ip = new DynamicIpAddress();
        ip.setName(name);
        ip.setCurrentValue(value);
        dynamicIPDAO.saveOrUpdate(ip);

    }

    public void deleteDynamicIpAddress(String name)
            throws DynamicIpAddressNotFoundException {

        DynamicIpAddress ip = dynamicIPDAO.findByName(name);
        if (ip == null) {
            throw new DynamicIpAddressNotFoundException(name);
        }

        dynamicIPDAO.delete(ip);

    }

    public void updateDynamicIpAddress(String name, String newValue)
            throws DynamicIpAddressNotFoundException {

        DynamicIpAddress ip = dynamicIPDAO.findByName(name);
        if (ip == null) {
            throw new DynamicIpAddressNotFoundException(name);
        }

        String oldValue = ip.getCurrentValue();
        if (oldValue.equals(newValue)) // Dont do anything
        {
            return;
        }

        // In every rule where the old ip address appears, change it
        // to the new ip value

        DescribeSecurityGroupsResult result = ec2.describeSecurityGroups();
        String oldIpRange = oldValue + "/32";
        String newIpRange = newValue + "/32";
        for (SecurityGroup d : result.getSecurityGroups()) {

            List<IpPermission> permissions = d.getIpPermissions();
            for (IpPermission p : permissions) {

                for (String ipRange : p.getIpRanges()) {

                    if (ipRange.equals(oldIpRange)) {
                        // Remove the security permissions


                        RevokeSecurityGroupIngressRequest revokeRequest = new RevokeSecurityGroupIngressRequest(d.getGroupName(), Collections.singletonList(p));

                        ec2.revokeSecurityGroupIngress(revokeRequest);
                        p.setIpRanges(Collections.singleton(newIpRange));
                        AuthorizeSecurityGroupIngressRequest authorizeRequest = new AuthorizeSecurityGroupIngressRequest(d.getGroupName(), Collections.singletonList(p));

                        ec2.authorizeSecurityGroupIngress(authorizeRequest);
                    }
                }

            }

        }
        ip.setCurrentValue(newValue);
        dynamicIPDAO.saveOrUpdate(ip);

    }

    public List<DynamicIpAddress> getDynamicIpAddresses() {
        return dynamicIPDAO.findAll();
    }

    private void checkVpcSubNet(VirtualMachine vm) throws SubNetNotFoundException, InconsistentSubNetAvailabilityZoneException {

        DescribeSubnetsResult result=ec2.describeSubnets(new DescribeSubnetsRequest().withSubnetIds(vm.getVpcSubNet()));

        if(result.getSubnets().isEmpty())
            throw new SubNetNotFoundException(vm.getVpcSubNet());

        Subnet sn=result.getSubnets().get(0);
        if(vm.getAvailabilityZone()!=null && !sn.getAvailabilityZone().equals(vm.getAvailabilityZone()))
            throw new InconsistentSubNetAvailabilityZoneException(vm.getVpcSubNet());

     // TODO: compare if machine private ip address really is in the subnet



    }
}

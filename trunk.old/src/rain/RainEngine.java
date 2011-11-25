/*
 * Created on Nov 3, 2008
 *
 */
package rain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.security.AWSCredentials;

import rain.authorization.AWSPermissionDAO;
import rain.authorization.JPAAWSPermissionDAO;
import rain.authorization.JPAPrincipalDAO;
import rain.authorization.PrincipalDAO;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.xerox.amazonws.ec2.AddressInfo;
import com.xerox.amazonws.ec2.AttachmentInfo;
import com.xerox.amazonws.ec2.AvailabilityZone;
import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.GroupDescription;
import com.xerox.amazonws.ec2.ImageDescription;
import com.xerox.amazonws.ec2.InstanceType;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.KeyPairInfo;
import com.xerox.amazonws.ec2.LaunchConfiguration;
import com.xerox.amazonws.ec2.ReservationDescription;
import com.xerox.amazonws.ec2.SnapshotInfo;
import com.xerox.amazonws.ec2.TerminatingInstanceDescription;
import com.xerox.amazonws.ec2.VolumeInfo;
import com.xerox.amazonws.ec2.GroupDescription.IpPermission;
import com.xerox.amazonws.ec2.ReservationDescription.Instance;

public class RainEngine extends BaseEngine {

	private static final long DEFAULT_POLL_INTERVAL = 10000;

	private static final int MAX_MOUNT_RETRIES = 50;

	private static final int MAX_VOLUME_DETACH_RETRIES = 3;

	public static RainEngine instance;

	static Logger logger = Logger.getLogger(RainEngine.class.getName());

	public static synchronized RainEngine getInstance() {
		if (instance == null)
			instance = new RainEngine();

		return instance;
	}

	private JPAVirtualMachineDAO virtualMachineDAO;
	private JPAVolumeDAO volumeDAO;
	private JpaDynamicIpAddressDAO dynamicIPDAO;

	private Jec2 ec2;

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
		if (endpointURL != null)
			ec2 = new Jec2(aws_access_id, aws_secret_key, false, endpointURL);
		else
			ec2 = new Jec2(aws_access_id, aws_secret_key, false);

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
			IpAddressAlreadyAssignedException, KeyPairNotFoundException {

		if (!checkAMIExists(vm.getImage()))
			throw new AMIDoesNotExistException(vm.getImage());

		checkVirtualMachineExists(vm.getName());

		modifyVirtualMachine(vm, null, true);

	}

	public void deleteVirtualMachine(String name)
			throws VirtualMachineNotFoundException {

		VirtualMachine vm = virtualMachineDAO.findByName(name);
		if (vm == null)
			throw new VirtualMachineNotFoundException(name);

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
		if (vm != null)
			throw new VirtualMachineAlreadyExistsException(name);
	}

	private boolean checkAMIExists(String image) {

		try {
			List<ImageDescription> descriptions = ec2
					.describeImages(Collections.singletonList(image));
			if (descriptions.size() > 0)
				return true;
		} catch (EC2Exception e) {
			logger.log(Level.FINE, "Error describing images: ", e);
			return false;
		}

		return false;
	}

	public String getVirtualMachineStatus(String name) {

		VirtualMachine vm = virtualMachineDAO.findByName(name);
		if (vm == null)
			return null;

		try {
			List<ReservationDescription> instances = ec2
					.describeInstances(new String[] {});

			for (ReservationDescription r : instances) {
				List<Instance> list = r.getInstances();
				for (Instance i : list) {
					if (vm.getCurrentInstance() != null
							&& i.getInstanceId()
									.equals(vm.getCurrentInstance())
							&& i.getStateCode() == 16)
						return i.getDnsName();

				}

			}

			return null;
		} catch (EC2Exception e) {
			throw new RuntimeException(e);
		}

	}

	public String startVirtualMachine(String name)
			throws VirtualMachineNotFoundException,
			CannotStartInstanceException,
			VirtualMachineAlreadyRunningException,
			InconsistentAvailabilityZoneException,
			VolumeAlreadyAttachedException, AddressAlreadyAssignedException,
			VolumeMountFailedException, AutoRunCommandFailedException {

		VirtualMachine vm = virtualMachineDAO.findByName(name);
		if (vm == null)
			throw new VirtualMachineNotFoundException(name);

		try {
			String currentDnsName = getVirtualMachineStatus(name);

			if (currentDnsName != null)
				throw new VirtualMachineAlreadyRunningException(currentDnsName);

			String startZone = checkVolumeAvailabilityAndConsistency(vm);

			checkStaticAddressAvailability(vm);

			// Start the virtual machine
			LaunchConfiguration configuration = new LaunchConfiguration(vm
					.getImage());
			configuration.setAvailabilityZone(startZone);
			configuration.setKernelId(vm.getKernel());
			configuration.setKeyName(vm.getKeypair());
			configuration.setRamdiskId(vm.getRamdisk());
			if (vm.getSecurityGroup() != null) {
				String[] tmp = vm.getSecurityGroup().split(",");
				configuration.setSecurityGroup(Arrays.asList(tmp));

			}

			VirtualMachine.InstanceType type = vm.getInstanceType();
			if (type == null)
				type = VirtualMachine.InstanceType.SMALL;
			switch (type) {
			case SMALL:
				configuration.setInstanceType(InstanceType.DEFAULT);
				break;
			case LARGE:
				configuration.setInstanceType(InstanceType.LARGE);
				break;
			case EXTRA_LARGE:
				configuration.setInstanceType(InstanceType.XLARGE);
			case HIGH_CPU_LARGE:
				configuration.setInstanceType(InstanceType.MEDIUM_HCPU);
				break;
			case HIGH_CPU_EXTRA_LARGE:
				configuration.setInstanceType(InstanceType.XLARGE_HCPU);

			}

			if (vm.getUserData() != null)
				configuration.setUserData(vm.getUserData().getBytes());

			ReservationDescription reservation = ec2
					.runInstances(configuration);
			Instance instance = reservation.getInstances().get(0);
			vm.setCurrentInstance(instance.getInstanceId());
			virtualMachineDAO.saveOrUpdate(vm);

			instance = waitForInstanceToStartRunning(instance);

			if (instance.getStateCode() != 16)
				throw new CannotStartInstanceException(instance.getStateCode());

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

			return instance.getDnsName();

		} catch (EC2Exception e) {
			throw new RuntimeException(e);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

	}

	private void executeAutoRuncommand(VirtualMachine vm, Instance instance)
			throws IOException, InterruptedException,
			AutoRunCommandFailedException {

		int exitStatus = executeCommand(vm, instance, vm.getAutoRunCommand());

		if (exitStatus != 0)
			throw new AutoRunCommandFailedException(vm, exitStatus);

	}

	private Instance waitForPublicIpToBecomeAvailable(VirtualMachine vm,
			Instance instance) throws EC2Exception {

		do {
			if (instance.getDnsName() == null
					|| instance.getDnsName().equals("")) {
				logger.fine("Waiting for ip address to become available...");
				try {
					Thread.sleep(DEFAULT_POLL_INTERVAL);
				} catch (InterruptedException e) {

				}

				instance = refreshInstanceInformation(instance);

			}
		} while (instance.getDnsName() == null
				|| instance.getDnsName().equals(""));

		return instance;

	}

	private Instance associateIpAddress(VirtualMachine vm, Instance instance)
			throws EC2Exception, UnknownHostException {
		// Associates the ip address and waits until the instance assumes the
		// address

		ec2.associateAddress(instance.getInstanceId(), vm.getStaticIpAddress());
		String instanceAddress;
		do {
			instance = refreshInstanceInformation(instance);
			instanceAddress = instance.getDnsName();
			instanceAddress = InetAddress.getByName(instanceAddress)
					.getHostAddress();
			if (!instanceAddress.equals(vm.getStaticIpAddress())) {
				try {
					Thread.sleep(DEFAULT_POLL_INTERVAL);
				} catch (InterruptedException e) {

				}
			}

		} while (!instanceAddress.equals(vm.getStaticIpAddress()));

		return instance;

	}

	private Instance refreshInstanceInformation(Instance instance)
			throws EC2Exception {

		List<ReservationDescription> reservations = ec2
				.describeInstances(Collections.singletonList(instance
						.getInstanceId()));
		if (reservations.size() > 0) {
			instance = reservations.get(0).getInstances().get(0);
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
			if (vol.getMountPoint() != null)
				mountVolume(vol, vm, currentInstance);
		}

	}

	public int executeCommand(VirtualMachine vm, Instance currentInstance,
			String command) throws IOException, InterruptedException {

		String[] cmdLine = new String[] {
				"ssh",
				"-o",
				"StrictHostKeyChecking=false",
				"-i",
				glue_home + File.separatorChar + "keys" + File.separatorChar
						+ vm.getKeypair() + ".identity",
				"root@" + currentInstance.getDnsName(), command };

		String cmd = "";
		for (String s : cmdLine)
			cmd = cmd + s + " ";

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
			if (status == 0)
				return;

			logger.fine("Mount failed , waiting to try again...");
			Thread.sleep(DEFAULT_POLL_INTERVAL);

		}

		throw new VolumeMountFailedException(vm, vol, currentInstance);

	}

	private void attachVolumes(VirtualMachine vm) throws EC2Exception {

		List<Volume> volumes = vm.getVolumes();
		List<String> pendingVolumeIds = new ArrayList<String>();
		for (Volume vol : volumes) {
			ec2.attachVolume(vol.getVolumeId(), vm.getCurrentInstance(), vol
					.getDevice());
			pendingVolumeIds.add(vol.getVolumeId());

		}
		// Now wait until all the volumes are in the attached state
		List<VolumeInfo> info;

		logger.fine("Waiting for volumes to attach...");
		do {

			info = ec2.describeVolumes(pendingVolumeIds);
			for (VolumeInfo i : info) {
				List<AttachmentInfo> attachments = i.getAttachmentInfo();
				if (attachments.size() > 0) {
					for (AttachmentInfo att : attachments) {
						if (att.getStatus().equals("attached")) {
							logger.fine("Volume " + i.getVolumeId()
									+ " attached");
							pendingVolumeIds.remove(i.getVolumeId());
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

	private Instance waitForInstanceToStartRunning(Instance instance)
			throws EC2Exception {
		int state;
		do {
			state = instance.getStateCode();
			if (state == 0) {
				try {
					Thread.sleep(DEFAULT_POLL_INTERVAL);
				} catch (InterruptedException e) {

				}
				List<ReservationDescription> refresh = ec2
						.describeInstances(Collections.singletonList(instance
								.getInstanceId()));
				if (refresh.size() < 1)
					throw new RuntimeException("Instance "
							+ instance.getInstanceId()
							+ " vanished while waiting for it to start");
				instance = refresh.get(0).getInstances().get(0);

			}
		} while (state == 0);

		return instance;

	}

	private String checkVolumeAvailabilityAndConsistency(VirtualMachine vm)
			throws EC2Exception, InconsistentAvailabilityZoneException,
			VolumeAlreadyAttachedException {
		String availabilityZone = vm.getAvailabilityZone();

		String availabilityZoneToCheck = availabilityZone;
		List<Volume> volumes = vm.getVolumes();
		if (volumes != null) {

			String[] volumeIds = new String[volumes.size()];
			for (int i = 0; i < volumes.size(); i++) {
				volumeIds[i] = volumes.get(i).getVolumeId();
			}

			for (int t = 0; t < MAX_VOLUME_DETACH_RETRIES + 1; t++) {
				List<VolumeInfo> volumesInfo = ec2.describeVolumes(volumeIds);

				for (int i = 0; i < volumesInfo.size(); i++) {
					VolumeInfo info = volumesInfo.get(i);
					if (availabilityZoneToCheck == null)
						availabilityZoneToCheck = info.getZone();
					if (!availabilityZoneToCheck.equals(info.getZone()))
						throw new InconsistentAvailabilityZoneException();
					List<AttachmentInfo> attachments = info.getAttachmentInfo();
					if (attachments != null && attachments.size() > 0) {

						// Sometimes a volume can get struck in EC2, attached to
						// an instance that is no longer running
						// If we detect that this is the case, then we force the
						// volume to be detached and repeat the check
						AttachmentInfo attachment = attachments.get(0);
						String attachedInstanceId = attachment.getInstanceId();
						if (isInstanceRunning(attachedInstanceId))
							throw new VolumeAlreadyAttachedException(volumes
									.get(i));
						else if (t < MAX_VOLUME_DETACH_RETRIES) {
							ec2.detachVolume(info.getVolumeId(), attachment
									.getInstanceId(), attachment.getDevice(),
									true);
							try {
								Thread.sleep(DEFAULT_POLL_INTERVAL);
							} catch (InterruptedException e) {
							}
							break;
						} else
							throw new VolumeAlreadyAttachedException(volumes
									.get(i));

					}

				}
				return availabilityZoneToCheck;
			}

		}

		throw new RuntimeException(
				"Code should never get into this path, this is a bug");
	}

	private boolean isInstanceRunning(String id) throws EC2Exception {

		List<ReservationDescription> reservations = ec2
				.describeInstances(Collections.singletonList(id));

		for (ReservationDescription reservation : reservations) {

			for (Instance instance : reservation.getInstances()) {

				if (instance.getInstanceId().equals(id) && instance.isRunning())
					return true;
			}

		}

		return false;

	}

	private void checkStaticAddressAvailability(VirtualMachine vm)
			throws EC2Exception, AddressAlreadyAssignedException {
		String staticIpAddress = vm.getStaticIpAddress();
		if (staticIpAddress != null) {
			List<AddressInfo> addresses = ec2.describeAddresses(Collections
					.singletonList(staticIpAddress));
			AddressInfo info = addresses.get(0);
			if (info.getInstanceId() != null
					&& !info.getInstanceId().equals(""))
				throw new AddressAlreadyAssignedException(staticIpAddress, info
						.getInstanceId());
		}
	}

	public String createVolume(String name, Integer size,
			String availabilityZone, String snapshot) throws EC2Exception,
			VolumeAlreadyExistsException, SnapshotNotFoundException {

		Volume volume = null;
		if (name != null) {
			volume = volumeDAO.findByName(name);
			if (volume != null)
				throw new VolumeAlreadyExistsException(volume);
		}

		volume = new Volume();
		volume.setName(name);

		if (snapshot != null)
			checkSnapshotAvailability(snapshot);

		String volumeSize = "";
		if (size != null)
			volumeSize = "" + size;

		VolumeInfo info = ec2.createVolume(volumeSize, snapshot,
				availabilityZone);
		if (name != null) {
			volume.setVolumeId(info.getVolumeId());
			volumeDAO.saveOrUpdate(volume);
		}

		return info.getVolumeId();

	}

	private void checkSnapshotAvailability(String snapshot)
			throws EC2Exception, SnapshotNotFoundException {
		List<SnapshotInfo> snapshots = ec2.describeSnapshots(new String[] {});
		boolean foundSnapshot = false;

		for (SnapshotInfo i : snapshots) {
			if (i.getSnapshotId().equals(snapshot)) {
				foundSnapshot = true;
				break;
			}

		}
		if (!foundSnapshot)
			throw new SnapshotNotFoundException(snapshot);
	}

	public String createVolume(String name, String volumeId)
			throws VolumeAlreadyExistsException, EC2Exception,
			VolumeNotFoundException {

		Volume vol = volumeDAO.findByName(name);
		if (vol != null)
			throw new VolumeAlreadyExistsException(vol);

		List<VolumeInfo> info = ec2.describeVolumes(Collections
				.singletonList(volumeId));
		if (info == null || info.size() == 0)
			throw new VolumeNotFoundException(volumeId);
		vol = new Volume();
		vol.setName(name);
		vol.setVolumeId(volumeId);
		volumeDAO.saveOrUpdate(vol);

		return info.get(0).getVolumeId();

	}

	public void attachVolume(String volume, String virtualMachine,
			String device, String mountDevice, String mountPoint)
			throws VolumeNotFoundException, VolumeAlreadyAttachedException,
			VirtualMachineNotFoundException, MountPointAlreadyInUseException,
			DeviceAlreadyInUseException {

		Volume vol = volumeDAO.findByName(volume);
		if (vol == null)
			throw new VolumeNotFoundException(volume);

		if (vol.getCurrentMachine() != null)
			throw new VolumeAlreadyAttachedException(vol);

		VirtualMachine vm = virtualMachineDAO.findByName(virtualMachine);
		if (vm == null)
			throw new VirtualMachineNotFoundException(virtualMachine);

		List<Volume> volumes = vm.getVolumes();
		if (volumes == null) {
			volumes = new ArrayList<Volume>();
			vm.setVolumes(volumes);
		} else {
			// Make sure no other volume is attached to the same vm using same
			// device or mount point
			for (Volume v : volumes) {
				if (mountPoint != null && v.getMountPoint() != null
						&& v.getMountPoint().equals(mountPoint))
					throw new MountPointAlreadyInUseException(mountPoint);
				if (v.getDevice().equals(device))
					throw new DeviceAlreadyInUseException(device);
				if (mountDevice != null) {
					if (v.getMountDevice() != null
							&& v.getMountDevice().equals(mountDevice))
						throw new DeviceAlreadyInUseException(mountDevice);
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
			IpAddressAlreadyAssignedException, KeyPairNotFoundException {
		modifyVirtualMachine(vm, newName, false);
	}

	private void modifyVirtualMachine(VirtualMachine vm, String newName,
			boolean create) throws VirtualMachineNotFoundException,
			VirtualMachineAlreadyExistsException,
			SecurityGroupNotFoundException, KernelNotFoundException,
			AMIDoesNotExistException, InstanceNotFoundException,
			AvailabilityZoneNotFoundException,
			StaticIpAddressNotFoundException,
			IpAddressAlreadyAssignedException, KeyPairNotFoundException {

		VirtualMachine current;

		if (!create) {
			current = virtualMachineDAO.findByName(vm.getName());
			if (current == null)
				throw new VirtualMachineNotFoundException(vm.getName());

		} else
			current = vm;

		if (vm.getAvailabilityZone() != null) {
			if (vm.getAvailabilityZone().equals("none"))
				current.setAvailabilityZone(null);
			else {
				checkAvailabilityZone(vm.getAvailabilityZone());
				current.setAvailabilityZone(vm.getAvailabilityZone());
			}
		}
		if (vm.getCurrentInstance() != null) {
			checkInstance(vm.getCurrentInstance());
			current.setCurrentInstance(vm.getCurrentInstance());
		}

		if (vm.getImage() != null) {
			checkImage(vm.getImage());
			current.setImage(vm.getImage());
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

		if (vm.getRamdisk() != null)
			current.setRamdisk(vm.getRamdisk());

		if (vm.getSecurityGroup() != null) {
			checkSecurityGroups(vm.getSecurityGroup());
			current.setSecurityGroup(vm.getSecurityGroup());
		}

		if (vm.getAutoRunCommand() != null)
			current.setAutoRunCommand(vm.getAutoRunCommand());

		if (vm.getStaticIpAddress() != null) {
			if (vm.getStaticIpAddress().equals("allocate")) {
				String ipAddress = allocateStaticIpAddress();
				current.setStaticIpAddress(ipAddress);
			} else {
				checkIpAddress(vm.getStaticIpAddress());
				VirtualMachine vm2 = virtualMachineDAO.findByStaticIpAddress(vm
						.getStaticIpAddress());
				if (vm2 != null && !vm2.getName().equals(vm.getName()))
					throw new IpAddressAlreadyAssignedException(vm2);
				current.setStaticIpAddress(vm.getStaticIpAddress());
			}
		}

		if (vm.getUserData() != null)
			current.setUserData(vm.getUserData());

		virtualMachineDAO.saveOrUpdate(current);

	}

	private void checkKeyPair(String keypair) throws KeyPairNotFoundException {

		try {
			List<KeyPairInfo> keypairs = ec2.describeKeyPairs(Collections
					.singletonList(keypair));
			if (keypairs.size() == 0)
				throw new KeyPairNotFoundException(keypair);
		} catch (EC2Exception e) {
			throw new KeyPairNotFoundException(keypair);
		}

	}

	private void checkIpAddress(String staticIpAddress)
			throws StaticIpAddressNotFoundException {

		try {
			List<AddressInfo> addresses = ec2.describeAddresses(Collections
					.singletonList(staticIpAddress));
			if (addresses.size() == 0)
				throw new StaticIpAddressNotFoundException(staticIpAddress);
		} catch (EC2Exception e) {
			throw new StaticIpAddressNotFoundException(staticIpAddress);
		}

	}

	private String allocateStaticIpAddress() {

		try {
			String ipAddress = ec2.allocateAddress();
			return ipAddress;
		} catch (EC2Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void checkAvailabilityZone(String availabilityZone)
			throws AvailabilityZoneNotFoundException {

		try {
			List<AvailabilityZone> zones = ec2
					.describeAvailabilityZones(Collections
							.singletonList(availabilityZone));
			if (zones.size() == 0)
				throw new AvailabilityZoneNotFoundException(availabilityZone);

		} catch (EC2Exception e) {
			throw new AvailabilityZoneNotFoundException(availabilityZone);
		}

	}

	private void checkInstance(String currentInstance)
			throws InstanceNotFoundException {

		try {
			List<ReservationDescription> instances = ec2
					.describeInstances(Collections
							.singletonList(currentInstance));
			if (instances.size() == 0)
				throw new InstanceNotFoundException(currentInstance);
		} catch (EC2Exception e) {
			throw new InstanceNotFoundException(currentInstance);
		}

	}

	private void checkImage(String image) throws AMIDoesNotExistException {

		if (!checkAMIExists(image))
			throw new AMIDoesNotExistException(image);
	}

	private void checkKernel(String kernel) throws KernelNotFoundException {

		try {
			List<ImageDescription> images = ec2.describeImages(Collections
					.singletonList(kernel));
			if (images.size() == 0)
				throw new KernelNotFoundException(kernel);

			// TODO: how to check if image is really a kernel? Typica doesnt
			// seem to expose this

		} catch (EC2Exception e) {
			throw new KernelNotFoundException(kernel);
		}

	}

	private void checkSecurityGroups(String groups)
			throws SecurityGroupNotFoundException {

		try {

			String[] tmp = groups.split(",");
			List<GroupDescription> groupInfo = ec2
					.describeSecurityGroups(Arrays.asList(tmp));

			for (String group : tmp) {
				boolean found = false;
				for (GroupDescription i : groupInfo) {
					if (i.getName().equals(group)) {
						found = true;
						break;
					}
				}
				if (!found)
					throw new SecurityGroupNotFoundException(group);
			}
		} catch (EC2Exception e) {
			throw new SecurityGroupNotFoundException(e.getErrors().toString());
		}

	}

	public void terminateVirtualMachine(String name)
			throws VirtualMachineNotRunningException,
			VirtualMachineNotFoundException {

		VirtualMachine vm = virtualMachineDAO.findByName(name);
		if (vm == null)
			throw new VirtualMachineNotFoundException(name);

		String ipAddress = getVirtualMachineStatus(name);
		if (ipAddress == null)
			throw new VirtualMachineNotRunningException(name);

		try {
			ec2.terminateInstances(Collections.singletonList(vm
					.getCurrentInstance()));

		} catch (EC2Exception e) {
			throw new RuntimeException(e);
		}

	}

	public List<VirtualMachineInfo> describeVirtualMachines(String[] names)
			throws VirtualMachineNotFoundException {
		List<VirtualMachineInfo> list = new ArrayList<VirtualMachineInfo>();
		List<VirtualMachine> vms;
		if (names == null)
			vms = virtualMachineDAO.findAll();
		else {
			vms = new ArrayList<VirtualMachine>();
			for (String name : names) {
				VirtualMachine vm = virtualMachineDAO.findByName(name);
				if (vm == null)
					throw new VirtualMachineNotFoundException(name);
				vms.add(vm);
			}
		}
		try {

			ArrayList<Instance> instances = new ArrayList<Instance>();
			List<ReservationDescription> reservations = ec2
					.describeInstances(new String[] {});

			for (ReservationDescription r : reservations)
				instances.addAll(r.getInstances());

			for (VirtualMachine vm : vms) {
				VirtualMachineInfo info = new VirtualMachineInfo();
				info.setVirtualMachine(vm);
				list.add(info);
				if (vm.getCurrentInstance() != null) {
					Iterator<Instance> it = instances.iterator();
					while (it.hasNext()) {
						Instance instance = it.next();

						if (instance.getInstanceId().equals(
								vm.getCurrentInstance())
								&& instance.isRunning()) {
							info.setStartTime(instance.getLaunchTime());
							info.setCurrentAvailabilityZone(instance
									.getAvailabilityZone());
							info.setCurrentDnsName(instance.getDnsName());
							info.setInstanceId(instance.getInstanceId());
							info.setCurrentPrivateIpAddress(instance
									.getPrivateIpAddress());
							it.remove();

						}

					}
				}
			}
			// Any instances left in the array after that are unamed instance

			if (names == null) {
				for (Instance instance : instances) {
					VirtualMachineInfo info = new VirtualMachineInfo();
					info.setStartTime(instance.getLaunchTime());
					info.setCurrentAvailabilityZone(instance
							.getAvailabilityZone());
					info.setCurrentDnsName(instance.getDnsName());
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
		if (vol == null)
			throw new VolumeNotFoundException(volume);
		return createSnapshotById(vol.getVolumeId());

	}

	public String createSnapshotById(String volumeId) {

		try {
			SnapshotInfo info = ec2.createSnapshot(volumeId);
			return info.getSnapshotId();
		} catch (EC2Exception e) {
			throw new RuntimeException(e);

		}

	}

	public String backupVolume(String volumeName, String volumeId,
			long maximumRetentionAge) throws VolumeNotFoundException,
			SnapshotNotFoundException {

		if (volumeName != null) {
			Volume volume = volumeDAO.findByName(volumeName);
			if (volume == null)
				throw new VolumeNotFoundException(volumeName);
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
			if (volume == null)
				throw new VolumeNotFoundException(name);
			id = volume.getVolumeId();
		}

		List<SnapshotInfo> snapshots;
		try {
			snapshots = ec2.describeSnapshots(new String[] {});
		} catch (EC2Exception e) {
			throw new RuntimeException(e);
		}
		List<SnapshotDescription> result = new ArrayList<SnapshotDescription>();

		for (SnapshotInfo i : snapshots) {

			if (id == null || i.getVolumeId().equals(id)) {
				SnapshotDescription description = new SnapshotDescription();
				description.setSnapshotId(i.getSnapshotId());
				description.setPercentComplete(i.getProgress());
				description.setSnapshotTime(i.getStartTime());
				description.setStatus(i.getStatus());

				Volume vol;
				if (id == null)
					vol = volumeDAO.findByVolumeId(i.getVolumeId());
				else
					vol = volume;
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
			if (volume == null)
				throw new VolumeNotFoundException(volumeName);
			volumeId = volume.getVolumeId();
		}

		List<VolumeDescription> volumes = new ArrayList<VolumeDescription>();

		try {
			List<VolumeInfo> info = ec2.describeVolumes(new String[] {});

			for (VolumeInfo i : info) {
				if (volumeId == null || i.getVolumeId().equals(volumeId)) {
					VolumeDescription description = new VolumeDescription();
					Volume namedVolume = volumeDAO.findByVolumeId(i
							.getVolumeId());
					description.setVolume(namedVolume);
					description.setVolumeId(i.getVolumeId());
					description.setCreateTime(i.getCreateTime());
					description.setAvailabilityZone(i.getZone());
					description.setStatus(i.getStatus());
					description.setSize(Integer.parseInt(i.getSize()));
					AttachmentInfo attachment = (i.getAttachmentInfo() == null || i
							.getAttachmentInfo().size() == 0) ? null : i
							.getAttachmentInfo().get(0);
					if (attachment != null) {
						description.setAttachTime(attachment.getAttachTime());
						description.setAttachedInstanceId(attachment
								.getInstanceId());
						description.setAttachedInstance(virtualMachineDAO
								.findByInstanceId(attachment.getInstanceId()));
						description.setAttachedDevice(attachment.getDevice());

					}
					volumes.add(description);

				}
			}

		} catch (EC2Exception e) {
			throw new RuntimeException(e);
		}

		return volumes;

	}

	public String modifyVolume(String name, String newName, String volume)
			throws VolumeNotFoundException, VolumeAlreadyExistsException,
			VolumeAlreadyInUseException {

		Volume vol = volumeDAO.findByName(name);
		if (vol == null)
			throw new VolumeNotFoundException(name);

		try {
			checkVolumeAvailability(volume);
		} catch (EC2Exception e) {
			throw new RuntimeException(e);
		}

		Volume vol2 = volumeDAO.findByVolumeId(volume);

		if (vol2 != null)
			throw new VolumeAlreadyInUseException(vol2);

		if (newName != null)
			vol.setName(newName);
		vol.setVolumeId(volume);
		volumeDAO.saveOrUpdate(vol);

		return vol.getVolumeId();

	}

	private void checkVolumeAvailability(String volume) throws EC2Exception,
			VolumeNotFoundException {

		List<VolumeInfo> volumes = ec2.describeVolumes(new String[] {});

		for (VolumeInfo i : volumes) {
			if (i.getVolumeId().equals(volume)) {
				return;
			}
		}

		throw new VolumeNotFoundException(volume);

	}

	public void deleteSnapshot(String snapshotId)
			throws SnapshotNotFoundException {

		try {
			List<SnapshotInfo> snapshots = ec2
					.describeSnapshots(new String[] {});
			boolean found = false;
			for (SnapshotInfo i : snapshots) {
				if (i.getSnapshotId().equals(snapshotId)) {
					found = true;
					break;
				}

			}
			if (!found)
				throw new SnapshotNotFoundException(snapshotId);

			ec2.deleteSnapshot(snapshotId);
		} catch (EC2Exception e) {
			throw new RuntimeException(e);
		}

	}

	public void deleteEC2Volume(String volumeId) throws VolumeNotFoundException {

		// Figure out if the volume exists

		try {
			List<VolumeInfo> volumes = ec2.describeVolumes(new String[] {});
			boolean found = false;
			for (VolumeInfo i : volumes) {
				if (i.getVolumeId().equals(volumeId)) {
					found = true;
					break;
				}
			}
			if (!found)
				throw new VolumeNotFoundException(volumeId);

			ec2.deleteVolume(volumeId);
			Volume vol = volumeDAO.findByVolumeId(volumeId);
			if (vol != null)
				volumeDAO.delete(vol);

		} catch (EC2Exception e) {
			throw new RuntimeException(e);
		}

	}

	public void deleteVolume(String volumeName) throws VolumeNotFoundException {

		Volume vol = volumeDAO.findByName(volumeName);
		if (vol == null)
			throw new VolumeNotFoundException(volumeName);

		deleteEC2Volume(vol.getVolumeId());

	}

	public void detachVolume(String volumeName) throws VolumeNotFoundException,
			VolumeNotAttachedException {

		Volume vol = volumeDAO.findByName(volumeName);
		if (vol == null)
			throw new VolumeNotFoundException(volumeName);

		if (vol.getCurrentMachine() == null)
			throw new VolumeNotAttachedException(vol);
		VirtualMachine currentMachine = vol.getCurrentMachine();
		vol.setCurrentMachine(null);
		vol.setMountPoint(null);
		vol.setDevice(null);

		volumeDAO.saveOrUpdate(vol);

		currentMachine.getVolumes().remove(vol);

		virtualMachineDAO.saveOrUpdate(currentMachine);

	}

	public void authorizeIPAddress(String ipAddress, String securityGroup,
			String protocol, int startPort, int endPort) throws EC2Exception,
			SecurityGroupNotFoundException {

		List<GroupDescription> list = ec2
				.describeSecurityGroups(new String[] { securityGroup });
		if (list == null || list.size() == 0)
			throw new SecurityGroupNotFoundException(securityGroup);

		ec2.authorizeSecurityGroupIngress(securityGroup, protocol, startPort,
				endPort, ipAddress);

	}

	public void revokeIPAddress(String ipAddress, String securityGroup,
			String protocol, int startPort, int endPort) throws EC2Exception,
			SecurityGroupNotFoundException {

		List<GroupDescription> list = ec2
				.describeSecurityGroups(new String[] { securityGroup });
		if (list == null || list.size() == 0)
			throw new SecurityGroupNotFoundException(securityGroup);

		ec2.revokeSecurityGroupIngress(securityGroup, protocol, startPort,
				endPort, ipAddress);

	}

	public String getCurrentInternetIPAddress() throws IOException {

		InputStream in = null;
		try {
			URL u = new URL("http://checkip.dyndns.org");
			in = u.openStream();

			byte[] b = new byte[512];

			int c;

			c = in.read(b);
			if (c != -1) {
				String response = new String(b, 0, c);
				Pattern ipPattern = Pattern
						.compile("Current IP Address: ([0-9|\\.]+)");
				Matcher m = ipPattern.matcher(response);
				if (m.find())
					return m.group(1);

			}

			return null;

		} finally {
			if (in != null)
				in.close();
		}

	}

	public String[] getSecurityGroupNames() throws EC2Exception {

		List<GroupDescription> groups = ec2
				.describeSecurityGroups(new String[] {});

		String[] names = new String[groups.size()];
		for (int i = 0; i < groups.size(); i++)
			names[i] = groups.get(i).getName();

		return names;

	}

	public void createDynamicIpAddress(String name, String value)
			throws DynamicIpAddressAlreadyExistsException {

		DynamicIpAddress ip = dynamicIPDAO.findByName(name);
		if (ip != null)
			throw new DynamicIpAddressAlreadyExistsException(name);

		ip = new DynamicIpAddress();
		ip.setName(name);
		ip.setCurrentValue(value);
		dynamicIPDAO.saveOrUpdate(ip);

	}

	public void deleteDynamicIpAddress(String name)
			throws DynamicIpAddressNotFoundException {

		DynamicIpAddress ip = dynamicIPDAO.findByName(name);
		if (ip == null)
			throw new DynamicIpAddressNotFoundException(name);

		dynamicIPDAO.delete(ip);

	}

	public void updateDynamicIpAddress(String name, String newValue)
			throws DynamicIpAddressNotFoundException, EC2Exception {

		DynamicIpAddress ip = dynamicIPDAO.findByName(name);
		if (ip == null)
			throw new DynamicIpAddressNotFoundException(name);

		String oldValue = ip.getCurrentValue();
		if (oldValue.equals(newValue)) // Dont do anything
			return;

		// In every rule where the old ip address appears, change it
		// to the new ip value

		List<GroupDescription> list = ec2
				.describeSecurityGroups(new String[] {});
		String oldIpRange = oldValue + "/32";
		String newIpRange = newValue + "/32";
		for (GroupDescription d : list) {

			List<IpPermission> permissions = d.getPermissions();
			for (IpPermission p : permissions) {

				for (String ipRange : p.getIpRanges()) {

					if (ipRange.equals(oldIpRange)) {
						// Remove the security permissions
						ec2.revokeSecurityGroupIngress(d.getName(), p
								.getProtocol(), p.getFromPort(), p.getToPort(),
								ipRange);
						ec2.authorizeSecurityGroupIngress(d.getName(), p
								.getProtocol(), p.getFromPort(), p.getToPort(),
								newIpRange);
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

}

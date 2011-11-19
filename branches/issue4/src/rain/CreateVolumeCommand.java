/*
 * Created on Nov 6, 2008
 *
 */
package rain;

import com.sampullara.cli.Argument;
import com.xerox.amazonws.ec2.EC2Exception;

public class CreateVolumeCommand extends BaseCommand {

	static {
		thisClass = CreateVolumeCommand.class;
	}

	@Argument(description = "Volume name", alias = "n")
	private String name;
	@Argument(description = "Volume size in Gb", alias = "s")
	private Integer size;
	@Argument(description = "Existing volume id", alias = "i")
	private String volume;
	@Argument(description = "Availability zone", alias = "z")
	private String availabilityZone;

	@Argument(description = "Snapshot id - populates the volume with the given snapshot data")
	private String snapshot;
	@Argument(description = "New volume name (modify only)")
	private String newName;
	@Argument(description = "Modify existing volume")
	private boolean modify;

	public void run() {

		if (!modify) {
			if ((size != null && volume != null)
					|| (size == null && volume == null && snapshot == null)
					|| (volume != null && snapshot != null)) {
				System.err
						.println("You must either set the size (for a new volume), provide an existing volume id  or provide a snapshot id (but not all at the same time)");
				System.exit(1);
				return;

			}

			if (volume != null && name == null) {
				System.err.println("You must specify a name for the volume id "
						+ volume);
				System.exit(1);
			}
			if ((size != null || snapshot != null) && availabilityZone == null) {
				System.err
						.println("You must specify an availability zone with the -z option");
				System.exit(1);
				return;
			}
		}
		else {
			if(name==null) {
				System.err.println("You must specify a volume name");
				System.exit(1);
				
				
			}
			if(volume==null) {
				System.err.println("You must specify a volume id to replace the current volume");
				System.exit(1);
				
			}
		}

		RainEngine engine = RainEngine.getInstance();
		try {
			String volumeId;
			if (!modify) {
				if (size != null || snapshot != null)
					volumeId = engine.createVolume(name, size,
							availabilityZone, snapshot);
				else
					volumeId = engine.createVolume(name, volume);
			} else
				volumeId=engine.modifyVolume(name, newName, volume);

			System.out.println(volumeId);
			System.exit(0);
			return;

		} catch (EC2Exception e) {
			System.err.println("Unexpected error while executing command:");
			e.printStackTrace();

		} catch (VolumeAlreadyExistsException e) {
			System.err.println("Volume " + e.getVolume().getName() + " already exists");

		} catch (VolumeNotFoundException e) {
			System.err.println("Volume " + e.getVolumeId() + " not found");
		} catch (SnapshotNotFoundException e) {
			System.err.println("Snapshot not found: " + e.getSnapshot());
		} catch (VolumeAlreadyInUseException e) {
			System.err.println("Volume "+e.getVolume().getName()+" already uses EC2 volume "+e.getVolume().getVolumeId());
		}

		System.exit(1);

	}

}

/*
 * Created on Nov 16, 2008
 *
 */
package rain;

import java.util.List;

import com.sampullara.cli.Argument;

public class DescribeVolumesCommand extends BaseCommand {

	static {
		thisClass=DescribeVolumesCommand.class;
	}
	
	
	@Argument(description="Volume name", alias="n")
	private String volumeName;
	
	@Argument(description="Volume id")
	private String volumeId;
	
	public void run() {
	
		RainEngine engine=RainEngine.getInstance();
		output.startSection("");
		try {
			List<VolumeDescription> volumes=engine.describeVolumes(volumeName,volumeId);
			
			for(VolumeDescription vol: volumes) {
				output.startLine();
				output.printValue("VolumeName",(vol.getVolume()!=null?vol.getVolume().getName():"n/a"));
				output.printValue("VolumeId",vol.getVolumeId());
				output.printValue("CreationTime",formatIso8601Date(vol.getCreateTime()));
				output.printValue("Size",""+vol.getSize());
				output.printValue("Status",""+vol.getStatus());
				output.printValue("AvailabilityZone",vol.getAvailabilityZone());
				output.printValue("AttachedSince",(vol.getAttachTime()==null?"n/a":formatIso8601Date(vol.getAttachTime())));
				if(vol.getAttachTime()!=null)
					output.printValue("AttachedTo",(vol.getAttachedInstance()==null?vol.getAttachedInstanceId():vol.getAttachedInstance().getName()));
				else
					output.printValue("AttachedTo","n/a");
				
				output.printValue("AttachedDevice",(vol.getAttachedDevice()==null?"n/a":vol.getAttachedDevice()));
				
				output.endLine();
				
				
			}
		} catch (VolumeNotFoundException e) {
		
			output.printError("Volume not found: "+e.getMessage());
			System.exit(-1);
		}
		output.endSection();
		System.exit(0);
		
	}

}

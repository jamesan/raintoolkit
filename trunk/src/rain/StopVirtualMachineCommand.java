/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rain;

import com.sampullara.cli.Argument;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author juliano
 */
public class StopVirtualMachineCommand extends BaseCommand {

    static {
        thisClass=StopVirtualMachineCommand.class;
    }

    @Argument(description="Virtual Machine Name",alias="n", required=true)
    private String name;

    @Argument(description="Force termination", alias="f", required=false)
    private boolean force;

    public void run() {
        try {
            RainEngine engine = RainEngine.getInstance();
            engine.stopVirtualMachine(name, force);
        } catch (VirtualMachineNotFoundException ex) {
            output.printError("Virtual machine not found: "+name);
        } catch (VirtualMachineIsNotEBSBackedException ex) {
            output.printError("Virtual machine "+name+" is not EBS-backed.");
        } catch (VirtualMachineNotRunningException ex) {
            output.printError("Virtual Machine "+name+" is not running.");
        }
    }

}

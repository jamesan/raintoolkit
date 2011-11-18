/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rain;

/**
 *
 * @author juliano
 */
public class InstanceStoppingException extends Exception {
    private final VirtualMachine vm;

    public InstanceStoppingException(VirtualMachine vm) {
        this.vm=vm;
    }

}

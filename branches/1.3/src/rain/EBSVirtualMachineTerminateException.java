/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rain;

/**
 *
 * @author juliano
 */
class EBSVirtualMachineTerminateException extends Exception {
    private final VirtualMachine vm;

    public EBSVirtualMachineTerminateException(VirtualMachine vm) {
        this.vm=vm;
    }

}

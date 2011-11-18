/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rain;

/**
 *
 * @author juliano
 */
class VirtualMachineIsNotEBSBackedException extends Exception {
    private final VirtualMachine vm;

    public VirtualMachineIsNotEBSBackedException(VirtualMachine vm) {
        this.vm=vm;
    }

}

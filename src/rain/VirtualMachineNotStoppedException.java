/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rain;

/**
 *
 * @author juliano
 */
class VirtualMachineNotStoppedException  extends Exception {
    private final VirtualMachine vm;

    public VirtualMachineNotStoppedException(VirtualMachine vm) {
        this.vm=vm;
    }

}

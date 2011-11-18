/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rain;

/**
 *
 * @author juliano
 */
public class InconsistentSubNetAvailabilityZoneException extends Exception{
    private final String vpcSubNet;

    public InconsistentSubNetAvailabilityZoneException(String vpcSubNet) {
        this.vpcSubNet=vpcSubNet;
    }

}

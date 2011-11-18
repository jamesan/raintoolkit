/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rain;

/**
 *
 * @author juliano
 */
public class SubNetNotFoundException extends Exception {
    private final String vpcSubNet;

    public SubNetNotFoundException(String vpcSubNet) {
        this.vpcSubNet=vpcSubNet;
    }

}

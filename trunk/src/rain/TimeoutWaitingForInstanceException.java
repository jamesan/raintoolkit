/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rain;

/**
 *
 * @author juliano
 */
public class TimeoutWaitingForInstanceException extends Exception {

    protected String instanceId;

    /**
     * Get the value of instanceId
     *
     * @return the value of instanceId
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * Set the value of instanceId
     *
     * @param instanceId new value of instanceId
     */
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public TimeoutWaitingForInstanceException(String instanceId) {
        this.instanceId=instanceId;
        
    }
    
}

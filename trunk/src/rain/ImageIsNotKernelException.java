/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rain;

/**
 *
 * @author juliano
 */
class ImageIsNotKernelException extends Exception {

    private String kernel;

    public ImageIsNotKernelException(String kernel) {
        this.kernel=kernel;
    }

    public String getKernel() {
        return kernel;
    }

    public void setKernel(String kernel) {
        this.kernel = kernel;
    }


}

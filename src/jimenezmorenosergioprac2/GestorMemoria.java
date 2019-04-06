package jimenezmorenosergioprac2;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio Jiménez Moreno
 */
public class GestorMemoria implements Runnable{
    // Variables
    private MonitorMemoria monitor;

    public GestorMemoria(MonitorMemoria monitor) {
        this.monitor = monitor;
    }
    
    @Override
    public void run() {
        boolean fin = false;
        System.out.println("GESTOR - Va a iniciar la ejecución");
        while(fin){
            try {
                monitor.atenderPeticionLiberacion();
                monitor.atenderPeticion();
            } catch (InterruptedException ex) {
                System.out.println("GESTOR - Va a finalizar");
                fin = true;
            }
        }
    }
    
}

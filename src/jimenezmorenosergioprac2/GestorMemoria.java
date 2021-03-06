package jimenezmorenosergioprac2;

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
        while(!fin){
            try {
                monitor.atenderPeticionLiberacion();
                monitor.atenderPeticion();
            }catch (Exception ex) {
                System.out.println("GESTOR - Solicitada finalizacion");
                fin = true;
            }
        }
        System.out.println("GESTOR - Finalinalizado");
    }
    
}

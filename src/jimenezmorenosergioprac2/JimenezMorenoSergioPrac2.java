package jimenezmorenosergioprac2;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio Jiménez Moreno
 */
public class JimenezMorenoSergioPrac2 {

    public enum Tipo{CARGA, FALLO};
    public final static int PAGINA_MIN = 4;
    public final static int PAGINA_ALEATORIA = 4;
    public static final int TIEMPO = 1;
    public static final int TIEMPO_ESPERA = 3;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Hilo(PRINCIPAL) inicia su ejecución");
        // Creamos el servicio de ejecución
        ExecutorService ejecucion = Executors.newCachedThreadPool();
        
        // Lista de tareas para su interrupción
        ArrayList<Future<?>> listaTareas = new ArrayList();
        
        System.out.println("Hilo(PRINCIPAL) crea y ejecuta el GestoMemoria");
        // Variables
        MonitorMemoria monitor =  new MonitorMemoria();
        GestorMemoria gestor =  new GestorMemoria(monitor);
        
        // Ejecucion del GestorMemoria
        Future<?> tarea = ejecucion.submit(gestor);    
        listaTareas.add(tarea);
        
        System.out.println("Hilo(PRINCIPAL) crea y ejecuta los Procesos durante "+TIEMPO_ESPERA+" minutos");
        Date inicio = new Date();
        int idProceso = 1;
        while( (new Date().getMinutes() - inicio.getMinutes()) < TIEMPO_ESPERA ){
            int totalPaginas = ThreadLocalRandom.current().nextInt(PAGINA_ALEATORIA)+PAGINA_MIN;
            Proceso nuevoProceso = new Proceso(monitor, idProceso, totalPaginas);
            Future<?> tareaProceso = ejecucion.submit(gestor);
            listaTareas.add(tareaProceso);
        }
        
        System.out.println("Hilo(PRINCIPAL) va a cancelar las tareas restantes.");
        for ( Future<?>  tareaActual : listaTareas )
            tareaActual.cancel(true);
        
        ejecucion.shutdown();
        
        try {
            ejecucion.awaitTermination(TIEMPO, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            System.out.println("Hilo(PRINCIPAL) error en la espera de la finalización.");
        }
        
        System.out.println("Hilo(PRINCIPAL) finaliza su ejecución");
    }
    
}

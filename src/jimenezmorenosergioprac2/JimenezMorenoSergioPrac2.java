package jimenezmorenosergioprac2;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Sergio Jiménez Moreno
 */
public class JimenezMorenoSergioPrac2 {

    public enum Tipo{CARGA, FALLO};
    public static final int PAGINA_MIN = 4;
    public static final int PAGINA_ALEATORIA = 5;
    public static final int TIEMPO = 1;
    public static final int TIEMPO_PROCESO = 3;
    public static final int TIEMPO_ESPERA = 3;
    public static final int PRIMERA_POSICION = 0;
    public static final int SEM_EXM = 1;
    public static final int SEM_SINC = 0;
    
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
        Future<?> tareaGestor = ejecucion.submit(gestor);
        listaTareas.add(tareaGestor);
        
        System.out.println("Hilo(PRINCIPAL) crea y ejecuta los Procesos durante "+TIEMPO_ESPERA+" minutos");
        int idProceso = 1;
        Date inicio = new Date();
        Date fin;
        do{
            int totalPaginas = ThreadLocalRandom.current().nextInt(PAGINA_ALEATORIA)+PAGINA_MIN;
            Proceso nuevoProceso = new Proceso(monitor, idProceso, totalPaginas);
            Future<?> tareaProceso = ejecucion.submit(nuevoProceso);
            listaTareas.add(tareaProceso);
            idProceso++;
            try {
                TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(TIEMPO_PROCESO)+TIEMPO);
            } catch (InterruptedException ex) {
                System.out.println("Hilo(PRINCIPAL) error en la creación de procesos.");
            }
            fin = new Date();
        }while( compruebaTiempo(inicio, fin) < TIEMPO_ESPERA );
        
        System.out.println("Hilo(PRINCIPAL) va a cancelar las tareas restantes.");
        for ( Future<?>  tareaActual : listaTareas )
            tareaActual.cancel(true);
        
        ejecucion.shutdown();
        
        try {
            ejecucion.awaitTermination(TIEMPO, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            System.out.println("Hilo(PRINCIPAL) error en la espera de la finalización.");
        }
        System.out.println("Hilo(PRINCIPAL) procesos finalizados, mostrando peticiones no atendidas :");
        monitor.visualiza();
        
        System.out.println("Hilo(PRINCIPAL) finaliza su ejecución");
    }
    
    public static long compruebaTiempo(Date inicio, Date fin){
        long tiempoServicio;
        tiempoServicio = fin.getTime() - inicio.getTime();
        tiempoServicio = TimeUnit.MINUTES.convert(tiempoServicio, TimeUnit.MILLISECONDS);
        return tiempoServicio;
    } 
}

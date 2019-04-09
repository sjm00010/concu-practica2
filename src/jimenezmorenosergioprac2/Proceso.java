package jimenezmorenosergioprac2;

import java.security.Timestamp;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jimenezmorenosergioprac2.JimenezMorenoSergioPrac2.PRIMERA_POSICION;
import jimenezmorenosergioprac2.JimenezMorenoSergioPrac2.Tipo;

/**
 *
 * @author Sergio Jiménez Moreno
 */
public class Proceso implements Callable<Integer>{
    //Constantes
    private final static int MIN_ITERACIONES = 8;
    private final static int RANGO_ITERACIONES = 4;
    private final static int MIN_PAGINA = 1;
    private final static int PAGINA_INVALIDA = -1;
    private final static int TIEMPO_MIN = 1;
    private final static int TIEMPO_ALEATORIO = 1;
    
    // Variables
    private MonitorMemoria monitor;
    private int id;
    private int numIteraciones;
    private int paginasDisponibles;

    public Proceso(MonitorMemoria monitor, int id, int paginasDisponibles) {
        this.monitor = monitor;
        this.id = id;
        this.numIteraciones = generaInteraciones();
        this.paginasDisponibles = paginasDisponibles;
    }
    
    @Override
    public Integer call() {
        try {
            Date inicio = new Date();
            System.out.println("PROCESO("+id+") - Inicio : "+inicio);
            peticionMarcos();
            for (int i = 0; i < numIteraciones; i++) {
                int paginaSolicitada = generaPagina();
                if( !monitor.compruebaPagina(id, paginaSolicitada) ){
                    peticionFallo(paginaSolicitada);
                }
                TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(TIEMPO_ALEATORIO)+TIEMPO_MIN);
            }
            peticionLiberacion();
            Date fin = new Date();
            int tiempo = fin.getSeconds() - inicio.getSeconds();
            System.out.println("PROCESO("+id+") - Finalizado, tiempo de ejecución : "+tiempo+" segundos");
        } catch (InterruptedException ex) {
            System.out.println("PROCESO("+id+") - Proceso interrumpido.");
        }
        return 0;
    }
    
    private int generaInteraciones(){
        return ThreadLocalRandom.current().nextInt(RANGO_ITERACIONES)+MIN_ITERACIONES;
    }
    
    private int generaPagina(){
        return ThreadLocalRandom.current().nextInt(paginasDisponibles)+MIN_PAGINA;
    }
    
    private void peticionMarcos() throws InterruptedException{
        Semaphore esperaProceso = new Semaphore(PRIMERA_POSICION);
        Peticion peticionCarga = new Peticion(id, PAGINA_INVALIDA, Tipo.CARGA, esperaProceso);
        monitor.addPeticion(peticionCarga);
        esperaProceso.acquire();
    }
    
    private void peticionFallo(int pagina) throws InterruptedException{
        // El semaforo debe estar dentro del monitor
        Semaphore esperaProceso = new Semaphore(PRIMERA_POSICION);
        Peticion peticionCarga = new Peticion(id, pagina, Tipo.FALLO, esperaProceso);
        monitor.addPeticion(peticionCarga);
        esperaProceso.acquire();
    }
    
    private void peticionLiberacion() throws InterruptedException{
        monitor.addPeticionLiberacion(id);
    }
}

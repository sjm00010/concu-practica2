package jimenezmorenosergioprac2;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import jimenezmorenosergioprac2.JimenezMorenoSergioPrac2.Tipo;

/**
 *
 * @author Sergio Jiménez Moreno
 */
public class Proceso implements Runnable{
    //Constantes
    private final static int MIN_ITERACIONES = 8;
    private final static int RANGO_ITERACIONES = 5;
    private final static int MIN_PAGINA = 1;
    private final static int PAGINA_INVALIDA = -1;
    private final static int TIEMPO_MIN = 1;
    private final static int TIEMPO_ALEATORIO = 2;
    
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
    public void run() {
        try {
            Date inicio = new Date();
            System.out.println("PROCESO("+id+") - Inicio : "+inicio);
            peticionMarcos();
            for (int i = 0; i < numIteraciones; i++) {
                TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(TIEMPO_ALEATORIO)+TIEMPO_MIN);
                int paginaSolicitada = generaPagina();
                if( !monitor.compruebaPagina(id, paginaSolicitada) ){
                    peticionFallo(paginaSolicitada);
                }
            }
            if(Thread.currentThread().isInterrupted()){
                throw new InterruptedException();
            }
                peticionLiberacion();
                Date fin = new Date();
                tiempoProceso(inicio, fin);
        } catch (InterruptedException ex) {
            System.out.println("PROCESO("+id+") - Proceso interrumpido.");
        }
    }
    
    private int generaInteraciones(){
        return ThreadLocalRandom.current().nextInt(RANGO_ITERACIONES)+MIN_ITERACIONES;
    }
    
    private int generaPagina(){
        return ThreadLocalRandom.current().nextInt(paginasDisponibles)+MIN_PAGINA;
    }
    
    private void peticionMarcos() throws InterruptedException{
        Peticion peticionCarga = new Peticion(id, PAGINA_INVALIDA, Tipo.CARGA);
        monitor.addPeticion(peticionCarga);
    }
    
    private void peticionFallo(int pagina) throws InterruptedException{
        Peticion peticionCarga = new Peticion(id, pagina, Tipo.FALLO);
        monitor.addPeticion(peticionCarga);
    }
    
    private void peticionLiberacion() throws InterruptedException{
        monitor.addPeticionLiberacion(id);
    }
    
    private void tiempoProceso(Date inicio, Date fin){
        long tiempoServicio;
        tiempoServicio = fin.getTime() - inicio.getTime();
        tiempoServicio = TimeUnit.SECONDS.convert(tiempoServicio, TimeUnit.MILLISECONDS);
        System.out.println("PROCESO("+id+") - Finalizado, tiempo de ejecución : "+tiempoServicio+" segundos");
    }
}

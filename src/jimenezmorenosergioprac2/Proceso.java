package jimenezmorenosergioprac2;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Sergio Jiménez Moreno
 */
public class Proceso implements Callable<Integer>{
    //Constantes
    private final static int MIN_ITERACIONES = 8;
    private final static int RANGO_ITERACIONES = 4;
    private final static int MIN_PAGINA = 1;
    
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
    public Integer call() throws Exception {
        Date inicio = new Date();
        System.out.println("PROCESO("+id+") - Inicio : "+inicio);
        return 0;
    }
    
    private int generaInteraciones(){
        return ThreadLocalRandom.current().nextInt(RANGO_ITERACIONES)+MIN_ITERACIONES;
    }
    
    private int generaPagina(){
        return ThreadLocalRandom.current().nextInt(paginasDisponibles)+MIN_PAGINA;
    }
}

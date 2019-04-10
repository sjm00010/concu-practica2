package jimenezmorenosergioprac2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import static jimenezmorenosergioprac2.JimenezMorenoSergioPrac2.NULO;
import static jimenezmorenosergioprac2.JimenezMorenoSergioPrac2.PRIMERA_POSICION;
import static jimenezmorenosergioprac2.JimenezMorenoSergioPrac2.SEM_EXM;
import static jimenezmorenosergioprac2.JimenezMorenoSergioPrac2.SEM_SINC;
import jimenezmorenosergioprac2.JimenezMorenoSergioPrac2.Tipo;

/**
 *
 * @author Sergio Jiménez Moreno
 */
public class MonitorMemoria {
    // Constantes
    private final static int MAX_MARCOS = 4;
    private final static int MARCOS_INICIALES = 2;
    private final static int NUM_MARCOS = 20;
    
    // Variables
    private int numMarcos;
    private int numMarcosMedios;
    private int numFallos;
    private int numProcesos;
    private int numProcesosFinalizados;
    private Semaphore exmMonitor;
    private HashMap<Integer,RepresentaProceso> listaProcesos;
    private HashMap<Integer,Semaphore> listaSemaforos;
    private List<Peticion> listaPetiones;
    private List<Integer> listaPetionesLiberacion;

    public MonitorMemoria() {
        numMarcos = NUM_MARCOS;
        exmMonitor = new Semaphore(SEM_EXM);
        listaProcesos = new HashMap<>();
        listaSemaforos = new HashMap<>();
        listaPetiones = new ArrayList<>();
        listaPetionesLiberacion = new ArrayList<>();
        numFallos = NULO;
        numProcesos = NULO;
        numProcesosFinalizados = NULO;
        numMarcosMedios = NULO;
        
    }
    
    // Funciones publicas
    public boolean compruebaPagina(int idProceso, int pagina) throws InterruptedException{
        exmMonitor.acquire();
        try{
            boolean resultado = false;
            RepresentaProceso proceso = listaProcesos.get(idProceso);
            if(proceso != null){
                resultado = proceso.compruebaPagina(pagina);
            }
            return resultado;
        }finally{
            exmMonitor.release();
        }
    }
    
    public void addPeticion(Peticion peticion) throws InterruptedException{
        exmMonitor.acquire();
        RepresentaProceso proceso = null;
        try {
            if(peticion.getTipo() == Tipo.CARGA){
                RepresentaProceso nuevoProceso = new RepresentaProceso(peticion.getIdProceso());
                listaProcesos.put(peticion.getIdProceso(), nuevoProceso);
                listaSemaforos.put(peticion.getIdProceso(),new Semaphore(SEM_SINC));
                numProcesos++;
            }else{
                numFallos++;
            }
            listaPetiones.add(peticion);
            proceso = listaProcesos.get(peticion.getIdProceso());
        }finally{
            exmMonitor.release();
            if(proceso != null)
                bloqueaProceso(proceso.getIdProceso());
        }
    }
    
    public void addPeticionLiberacion(int idProceso) throws InterruptedException{
        exmMonitor.acquire();
        try {
            listaPetionesLiberacion.add(idProceso);
            numProcesosFinalizados++;
        }finally{
            exmMonitor.release();
        }
    }
    
    public void atenderPeticionLiberacion() throws InterruptedException{
        exmMonitor.acquire();
        try {
            while(!listaPetionesLiberacion.isEmpty()){
                liberarMarcos(listaPetionesLiberacion.get(PRIMERA_POSICION));
                listaPetionesLiberacion.remove(PRIMERA_POSICION);
            }
        }finally{
            exmMonitor.release();
        }
    }
    
    public void atenderPeticion() throws InterruptedException{
        exmMonitor.acquire();
        try{
            if(!listaPetiones.isEmpty()){
                Peticion peticion;
                if (numMarcos > 1) {
                    peticion = listaPetiones.remove(PRIMERA_POSICION);
                    if (peticion.getTipo() == Tipo.CARGA) {
                        asignarMarcosInicio(peticion.getIdProceso());
                    } else {
                        falloPagina(peticion.getIdProceso(), peticion.getPagina());
                    }
                } else {
                    peticion = removeFallo();
                    if(peticion != null)
                        falloPagina(peticion.getIdProceso(), peticion.getPagina());       
                }
                if(peticion != null){
                    RepresentaProceso proceso = listaProcesos.get(peticion.getIdProceso());
                    desbloqueaProceso(peticion.getIdProceso());
                }
            }
        }finally{
            exmMonitor.release();
        }
    }
    
    // Funciones auxiliares
    private void asignarMarcosInicio(int idProceso){
        numMarcos -= MARCOS_INICIALES;
        RepresentaProceso proceso = listaProcesos.get(idProceso);
        proceso.paginasInicio();
    }
    
    private boolean asignarMarcos(int idProceso, int pagina){
        boolean resultado = false;
        RepresentaProceso proceso = listaProcesos.get(idProceso);
        if(proceso != null){
            if (proceso.paginasAsignadas() < MAX_MARCOS ){
                numMarcos--;
                proceso.addPagina(pagina);
                resultado = true;
            }
        }
        return resultado;
    }
    
    private void falloPagina(int idProceso, int pagina){
        boolean asignado = false;
        
        if( numMarcos > NULO ){
            asignado = asignarMarcos(idProceso, pagina);
        }
        
        if(!asignado){
            RepresentaProceso proceso = listaProcesos.get(idProceso);
            if(proceso != null){
                proceso.sustituyePagina(pagina);
            }
        }
    }
    
    private void liberarMarcos(int idProceso){
        RepresentaProceso proceso = listaProcesos.remove(idProceso);
        if(proceso != null){
            numMarcos += proceso.paginasAsignadas();
            numMarcosMedios += proceso.paginasAsignadas();
        }
    }
    
    private Peticion removeFallo(){
        Peticion resultado = null;
        for (int i = 0; i < listaPetiones.size(); i++) {
            if(listaPetiones.get(i).getTipo() == Tipo.FALLO){
                resultado = listaPetiones.remove(i);
                i = listaPetiones.size();
            }
        }
        return resultado;
    }
    
    private void bloqueaProceso(int idProceso) throws InterruptedException{
        listaSemaforos.get(idProceso).acquire();
    }
    
    private void desbloqueaProceso(int idProceso) throws InterruptedException{
        listaSemaforos.get(idProceso).release();
    }
    
    public void visualiza() {
        System.out.println("Peticiones de Liberación ("+listaPetionesLiberacion.size()+") : "+listaPetionesLiberacion);
        System.out.println("Peticiones de Asignación de Página ("+listaPetiones.size()+") : "+listaPetiones);
        System.out.println("Numero medio de marcos : "+numMarcosMedios/numProcesos);
        System.out.println("Fallo medio de paginas : "+numFallos/numProcesos);
        System.out.println("Número de procesos que no han concluido : "+(numProcesos-numProcesosFinalizados));
    }

}

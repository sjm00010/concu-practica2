package jimenezmorenosergioprac2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import jimenezmorenosergioprac2.JimenezMorenoSergioPrac2.Tipo;

/**
 *
 * @author Sergio Jiménez Moreno
 */
public class MonitorMemoria {
    // Constantes
    private final static int MAX_MARCOS = 4;
    private final static int NUM_MARCOS = 20;
    
    // Variables
    private int numMarcos;
    private Semaphore exmMonitor;
    private HashMap<Integer,RepresentaProceso> listaProcesos;
    private List<Peticion> listaPetiones;
    private List<Integer> listaPetionesLiberacion;

    public MonitorMemoria() {
        numMarcos = NUM_MARCOS;
        exmMonitor = new Semaphore(1);
        listaProcesos = new HashMap<>();
        listaPetiones = new ArrayList<>();
        listaPetionesLiberacion = new ArrayList<>();
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
        try {
            listaPetiones.add(peticion);
        }finally{
            exmMonitor.release();
        }
    }
    
    public void addPeticionLiberacion(int idProceso) throws InterruptedException{
        exmMonitor.acquire();
        try {
            listaPetionesLiberacion.add(idProceso);
        }finally{
            exmMonitor.release();
        }
    }
    
    public void atenderPeticionLiberacion() throws InterruptedException{
        exmMonitor.acquire();
        try {
            while(!listaPetionesLiberacion.isEmpty()){
                liberarMarcos(listaPetionesLiberacion.get(0));
                listaPetionesLiberacion.remove(0);
            }
        }finally{
            exmMonitor.release();
        }
    }
    
    public void atenderPeticion() throws InterruptedException{
        exmMonitor.acquire();
        Peticion peticion = null;
        try{
            if (numMarcos > 1) {
                peticion = listaPetiones.remove(0);
                if (peticion.getTipo() == Tipo.CARGA) {
                    asignarMarcosInicio(peticion.getIdProceso());
                } else {
                    falloPagina(peticion.getIdProceso(), peticion.getPagina());
                }
            } else {
                peticion = removeFallo();
                falloPagina(peticion.getIdProceso(), peticion.getPagina());
                
            }
        }finally{
            peticion.desbloqueaProceso();
            exmMonitor.release();
        }
    }
    
    // Funciones auxiliares
    private void asignarMarcosInicio(int idProceso){
        numMarcos -= 2;
        RepresentaProceso nuevoProceso = new RepresentaProceso(idProceso);
        listaProcesos.put(idProceso, nuevoProceso);
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
        
        if( numMarcos > 0 ){
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
    
    public void visualiza() {
        System.out.println("Peticiones de Liberación : "+listaPetionesLiberacion);
        System.out.println("Peticiones de Fallo de Página : "+listaPetiones);
    }

}

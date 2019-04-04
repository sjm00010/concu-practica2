package jimenezmorenosergioprac2;

import java.util.List;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Sergio Jiménez Moreno
 */
public class MonitorMemoria {
    // Variables
    private int numMarcos;
    private Semaphore exmMonitor;
    private List<RepresentaProceso> listaProcesos;
    
    
    
    // Funciones auxiliares
    private void asignarMarcosInicio(int idProceso){
        numMarcos -= 2;
        RepresentaProceso nuevoProceso = new RepresentaProceso(idProceso);
        listaProcesos.add(nuevoProceso);
    }
    
    private void asignarMarcos(int idProceso, int pagina){
        numMarcos--;
        for (RepresentaProceso proceso : listaProcesos) {
            if(proceso.getIdProceso() == idProceso){
                proceso.addPagina(pagina);
                //finalizar bucle, sin break T.T
            }
        }
    }
    
    private void falloPagina(int idProceso, int pagina){
        if( numMarcos > 0){
            asignarMarcos(idProceso, pagina);
        }else{
            for (RepresentaProceso proceso : listaProcesos) {
                if(proceso.getIdProceso() == idProceso){
                    proceso.sustituyePagina(pagina);
                    //finalizar bucle, sin break T.T
                }
            }
        }
    }
    
    private void liberarMarcos(Peticion peticion){
        RepresentaProceso encontrado = null;
        for (RepresentaProceso proceso : listaProcesos) {
            if(proceso.getIdProceso() == peticion.getIdProceso()){
                numMarcos += proceso.paginasAsignadas();
                encontrado = proceso;
                //finalizar bucle, sin break T.T
            }
        }
        listaProcesos.remove(encontrado);
    }
}

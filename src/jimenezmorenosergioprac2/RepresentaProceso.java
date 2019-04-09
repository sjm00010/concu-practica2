package jimenezmorenosergioprac2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import static jimenezmorenosergioprac2.JimenezMorenoSergioPrac2.SEM_SINC;

/**
 *
 * @author Sergio Jiménez Moreno
 */
public class RepresentaProceso {
    // Variables
    private int idProceso;
    private List<Integer> listaPaginas;
    private Semaphore exmProceso;

    public RepresentaProceso(int idProceso) {
        this.idProceso = idProceso;
        this.listaPaginas = new ArrayList<>();
        this.exmProceso = new Semaphore(SEM_SINC);
    }

    public int getIdProceso() {
        return idProceso;
    }
    
    public void paginasInicio(){
        this.listaPaginas.add(1);
        this.listaPaginas.add(2);
    }
    
    public void addPagina(int pagina){
        listaPaginas.add(pagina);
    }
    
    public void sustituyePagina(int pagina){
        listaPaginas.remove(0);
        listaPaginas.add(pagina);
    }
    
    public int paginasAsignadas(){
        return listaPaginas.size();
    }
    
    public boolean compruebaPagina(int pagina){
        boolean resultado = false;
        for (Integer paginaActual : listaPaginas) {
            if(paginaActual == pagina)
                resultado = true;
        }
        return resultado;
    }
    
    public void bloqueaProceso() throws InterruptedException{
        exmProceso.acquire();
    }
    
    public void desbloqueaProceso() throws InterruptedException{
        exmProceso.release();
    }    
}

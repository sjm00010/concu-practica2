package jimenezmorenosergioprac2;

import java.util.concurrent.Semaphore;
import jimenezmorenosergioprac2.JimenezMorenoSergioPrac2.Tipo;

/**
 *
 * @author Sergio Jiménez Moreno
 */
public class Peticion {
    private int idProceso;
    private int pagina;
    private Tipo tipo;
    private Semaphore blockProceso;

    public Peticion(int idProceso, int pagina, Tipo tipo, Semaphore blockProceso) {
        this.idProceso = idProceso;
        this.pagina = pagina;
        this.tipo = tipo;
        this.blockProceso = blockProceso;
    }

    public int getIdProceso() {
        return idProceso;
    }

    public int getPagina() {
        return pagina;
    }

    public Tipo getTipo() {
        return tipo;
    }
    
    public void desbloqueaProceso() {
        blockProceso.release();
    }
}

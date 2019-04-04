package jimenezmorenosergioprac2;

import jimenezmorenosergioprac2.JimenezMorenoSergioPrac2.Tipo;

/**
 *
 * @author Sergio Jiménez Moreno
 */
public class Peticion {
    private int idProceso;
    private int pagina;
    private Tipo tipo;

    public Peticion(int idProceso, int pagina, Tipo tipo) {
        this.idProceso = idProceso;
        this.pagina = pagina;
        this.tipo = tipo;
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
    
}

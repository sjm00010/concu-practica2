/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jimenezmorenosergioprac2;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 *
 * @author PC
 */
public class MonitorMemoria {
    // Variables
    private int numMarcos;
    private Semaphore exmMonitor;
    private ArrayList<RepresentaProceso> listaProcesos;
    
    // Funciones
    private void asignarMarcosInicio(int id) throws InterruptedException{
        exmMonitor.acquire();
        numMarcos -= 2;
        listaProcesos
    }
}

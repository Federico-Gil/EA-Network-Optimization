/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package montecarlo.parallelfail;

import java.util.Random;

import grafo.Grafo;

/**
 * @author LAPTOP
 */
public class SubProcess extends Thread {

    public SubProcess(Random gen, long ct, Grafo g) {
        generador = gen;
        cantidadTotal = ct;
        this.grafo = g;
    }
    
    public long getCantidadDentro() {
        return cantidadDentro;
    }

    public void setCantidadDentro(long cantidadDentro) {
        this.cantidadDentro = cantidadDentro;
    }

    public long getCantidadTotal() {
        return cantidadTotal;
    }
    
    @Override
    public void run() {
        cantidadDentro = Math.round(grafo.monteCarlo((int) cantidadTotal, (float) 0.05)[3]);
    }
    
    private long cantidadDentro = 0;
    private long cantidadTotal = 0;
    private Random generador = null;
    private Grafo grafo = null;
}
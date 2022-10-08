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

    public SubProcess(Random gen, int ct, Grafo g) {
        generador = gen;
        cantidadTotal = ct;
        this.grafo = g;
    }
    
    public long getCantidadDentro() {
        return cantidadDentro;
    }

    public void setCantidadDentro(int cantidadDentro) {
        this.cantidadDentro = cantidadDentro;
    }

    public long getCantidadTotal() {
        return cantidadTotal;
    }

    public void setGenerador(Random generador) {
        this.generador = generador;
    }

    public Random getGenerador() {
        return generador;
    }
    
    @Override
    public void run() {
        this.cantidadDentro = (int) Math.round(grafo.monteCarlo((int) cantidadTotal, (float) 0.05)[3]);
    }
    
    private Integer cantidadDentro = 0;
    private Integer cantidadTotal = 0;
    private Random generador = null;
    private Grafo grafo = null;
}
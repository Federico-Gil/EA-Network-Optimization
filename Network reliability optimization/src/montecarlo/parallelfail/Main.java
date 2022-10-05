/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package montecarlo.parallelfail;

public class Main {
    public static void main(String [] args){
        //time the simulation
        long startTime = System.currentTimeMillis();
        
        Simulation sim = new Simulation(8, (int) 1e6);
        System.out.println(sim.pi());    

        long endTime = System.currentTimeMillis();
        System.out.println("That took " + (endTime - startTime) + " milliseconds");
    }
}
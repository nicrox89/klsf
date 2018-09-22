/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package klsf;

import control.GraphManager;
import entity.Color;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author nicolarusso
 */
public class KLSF {
    
    private GraphManager graphManager;
    
    public KLSF(String path){
        this.graphManager = new GraphManager();
        this.graphManager.ReadGraph(path);
    }

    private void greedy() {
        //System.out.println("Numero colori: " + graphManager.getGraph().getColorList().size());
        //System.out.println("Label più piccola: " + graphManager.getGraph().getMinLabelLength());

        long startGreedy = System.currentTimeMillis();
        this.graphManager.greedyIvan(0.5);
        long endGreedy = System.currentTimeMillis();
        long differenzaTempoGreedy = endGreedy - startGreedy;
        if (differenzaTempoGreedy > 1000) {
            int seconds = (int) differenzaTempoGreedy / 1000;
            long milliseconds = differenzaTempoGreedy - (seconds * 1000);
            String time = seconds + "." + milliseconds;
            System.out.println("tempo Greedy " + time + " secondi\n");
        } else {
            System.out.println("tempo Greedy " + differenzaTempoGreedy + " millisecondi\n");
        }

        //ArrayList<Color> selectedColorList = graphManager.getGraph().getSelectedColorList();
    }

    private void random() {
        
        long startRandom = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            this.graphManager.randomGlobal(0.5);
        }

        long endRandom = System.currentTimeMillis();
        long differenzaTempoRandom = endRandom - startRandom;
        if (differenzaTempoRandom > 1000) {
            System.out.println("tempo Random " + differenzaTempoRandom / 1000 + " secondi\n");
        } else {
            System.out.println("tempo Random " + differenzaTempoRandom + " millisecondi\n");
        }
    }

    private void saa() {

        //Random initial choise
        /*this.graphManager.getGraph().resetColorCheck();
        Collections.shuffle(this.graphManager.getGraph().getColorList());
        double percentage = 0.5;
        for(int i=0; i<((int) (this.graphManager.getGraph().getColorList().size() * percentage)); i++){
            this.graphManager.getGraph().getColorList().get(i).setChecked(true);
            this.graphManager.getGraph().getColorList().get(i).setSolution(true);
        }*/

        //split original color list into two lists of initial solution colors and excluded colors        
        this.graphManager.colorSplit();

        //Simulated Annealing Algorithm - metaheuristic
        double keepingRatio = 1.0;
        double swapColorRatio = 0.0;
        int temperature = 1000000000;
        double coolingRate = 0.00003;

        long startSAA = System.currentTimeMillis();
        this.graphManager.SAA(keepingRatio, swapColorRatio, temperature, coolingRate);
        long endSAA = System.currentTimeMillis();

        long differenzaTempoSAA = endSAA - startSAA;
        if (differenzaTempoSAA > 1000) {
            int seconds = (int) differenzaTempoSAA / 1000;
            long milliseconds = differenzaTempoSAA - (seconds * 1000);
            String time = seconds + "." + milliseconds;
            System.out.println("tempo SAA " + time + " secondi\n");
        } else {
            System.out.println("tempo SAA " + differenzaTempoSAA + " millisecondi\n");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //String path="/Users/nicolarusso/Desktop/graph.txt";
        String rootPath="C:\\Users\\ilari\\Desktop\\cerrons\\";
        //String path=rootPath+"graph.txt";
        //String path=rootPath+"graphMultipleLabels.txt";
        String path=rootPath+"GrafiColorati3Colori/50_200_50_13_6.mlst";
        //String path = "/Users/nicolarusso/Desktop/GrafiColorati3Colori/50_200_50_13_6_test.mlst";
        //String path="/Users/nicolarusso/Desktop/GrafiColorati3Colori/500_4000_500_63_5.mlst";
        //String path="/Users/nicolarusso/Desktop/GrafiColorati3Colori/10000_80000_10000_1250_5.mlst";
        //String path="/Users/nicolarusso/Desktop/GrafiColorati3Colori/10000_160000_10000_625_5.mlst";

        KLSF klsf = new KLSF(path);

        System.out.println("\n\n\n******************** Greedy ********************\n");
        klsf.greedy();
        System.out.println("********************** END **********************\n");
        
//        System.out.println("\n\n\n******************** Random ********************\n");
//        klsf.random();
//        System.out.println("********************** END **********************\n");
        
        System.out.println("\n\n\n********* Simulated Annealing Algorithm ********\n");
        klsf.saa();
        System.out.println("********************** END **********************\n");

    }

}

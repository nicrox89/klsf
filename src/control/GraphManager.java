/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import comparator.AscendingOrder;
import entity.Arch;
import entity.Color;
import entity.Graph;
import entity.Label;
import entity.Node;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearIntExpr;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nicolarusso
 * @param graph is the initial graph
 */
public class GraphManager {

    private Graph graph;
    private int colors;
    private int components;
    public ArrayList<Color> chosenColorList;
    public ArrayList<Color> excludedColorList;

    /**
     * Initialize the graph
     */
    public GraphManager() {
        this.graph = new Graph();
        this.colors = 0;
        this.components = 0;
        chosenColorList = new ArrayList<Color>();
        excludedColorList = new ArrayList<Color>();
    }

    /**
     *
     * @param path
     */
    public void ReadGraph(String path) {
        try {
            String line = null;
            String items[];

            BufferedReader in = new BufferedReader(new FileReader(path));

            //Get first options line
            line = in.readLine();
            items = line.split(" ");

            //Initialize Graph
            this.graph.init(Integer.valueOf(items[0]), Integer.valueOf(items[2]) + 1);

            //Get data lines
            line = in.readLine();
            int id_arch=0;
            while (line != null) {

                //Split line and selectedArch data
                items = line.split(" ");

                //Color Array
                ArrayList<Color> archColors = new ArrayList<Color>();
                for (int i = 2; i < items.length; i++) {
                    //get the color with the id number present in the mlst file
                    archColors.add(this.graph.getColorList().get(Integer.valueOf(items[i])));
                    //Increase the number of the occurrences for the color
                    this.graph.getColorList().get(Integer.valueOf(items[i])).increaseCounter();
                }

                //Sort array of the label
                //this.graph.sortAsc(archColors);
                
                //Collections.sort(archColors);
                System.out.println("\nLabel colors Ascending Sorted\n"); 
                AscendingOrder ord = new AscendingOrder(); 
                Collections.sort(archColors, ord); 
                System.out.println(archColors);
                
                
                
                Label label = null;

                //Make sorted label
                //ci vuole un metodo per ordinare i colori
                String labelStr = "";
                for (Color color : archColors) {
                    labelStr += "-" + color.getColor();
                }
                //remove first character -
                labelStr = labelStr.substring(1);
                
                //Collections.sort(archColors);
                //System.out.println(archColors);
                
                //Insert and count labels
                if (this.graph.getLabels().containsKey(labelStr)) {
                    label = this.graph.getLabels().get(labelStr);
                    label.increaseArchs();
                    System.out.println(label.getArchs());
                } else {
                    label = new Label(archColors);
                    label.increaseArchs();
                    this.graph.getLabelList().add(label);
                    this.graph.getLabels().put(labelStr, label);
                }

                //Make the readed Arch Object
                Arch arch = new Arch(this.graph.getNodeList().get(Integer.valueOf(items[0])),
                        this.graph.getNodeList().get(Integer.valueOf(items[1])),
                        label, id_arch++);

                //Add Arch Object to archList
                this.graph.getArchList().add(arch);

                //Add redundace arch
                //Add Arch Object to their nodes
                this.graph.getNodeList().get(Integer.valueOf(items[0])).getArchList().add(arch);
                this.graph.getNodeList().get(Integer.valueOf(items[1])).getArchList().add(arch);

                if (arch.getLabel().getColors().size() < this.getGraph().getMinLabelLength()) {
                    this.getGraph().setMinLabelLength(arch.getLabel().getColors().size());
                }

                //Read next line
                line = in.readLine();
            }
            in.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(GraphManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GraphManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return graph
     */
    public Graph getGraph() {
        return graph;
    }
    
    /**
     * Method that create subgraphs checking connected nodes
     *
     * @param graph
     * @return
     */
    public ArrayList<Graph> getSubGraphs(Graph graph) {
        ArrayList<Graph> graphList = new ArrayList<Graph>();
        int index = -1;
        while (graph.nodeUnchecked() > -1) {
            index = graph.nodeUnchecked();
            Graph subGraph = new Graph();
            this.connectNodes(subGraph, graph.getNodeList().get(index));
            graphList.add(subGraph);
        }
        return graphList;
    }

    /**
     * Recursive function to check all node connection No memory consumption
     *
     * @param graph
     * @param node
     */
    //To delete loop is necessary control if a node is checked when find to node
    //We can add a second boolean vars that indicate all children visited to remove cycles
    public void connectNodes(Graph graph, Node node) {
        if (!node.isChecked()) {
            graph.getNodeList().add(node);
            node.setChecked(true);
            for (Arch arch : node.getArchList()) {
                if (arch.isChecked()) {
                    if (!graph.getArchList().contains(arch)) {
                        graph.getArchList().add(arch);
                        if (node.getId() == arch.getFromNode().getId()) {
                            connectNodes(graph, arch.getToNode());
                        } else {
                            connectNodes(graph, arch.getFromNode());
                        }
                    }
                }
            }
        }
    }

    public void greedyDesc() {
        //this.getGraph().sort();
        //this.getGraph().loadLabels();
        //System.out.println("Numero di label: "+this.getGraph().getLabelList().size());
        this.getGraph().randomize();
        this.graph.resetNodesCheck();
        this.graph.resetArchsCheck();
        this.graph.resetColorCheck();

        int colors = 0;
        String label = "";
        ArrayList<Graph> subGraphs = null;

        for (Color color : this.graph.getColorList()) {
            color.setChecked(true);
            colors++;
            label += " " + String.valueOf(color.getColor());

            //After selection of a color, read all archs in the root graph and set 
            //true if the label contains the selected colors
            for (int i = 0; i < this.graph.getArchList().size(); i++) {

                //Get Arch in position i
                Arch selectedArch = this.graph.getArchList().get(i);
                ArrayList<Color> colorList = selectedArch.getLabel().getColors();

                //Check the label
                if (this.graph.allColorChecked(colorList)) {

                    //Control if arch is in solution
                    if (!selectedArch.isChecked()) {
                        selectedArch.setChecked(true);
                    }
                }
            }
            this.graph.resetNodesCheck();
            subGraphs = this.getSubGraphs(this.graph);
            //System.out.print("colors: " + colors + " ");
            //System.out.print("subgraphs: " + subGraphs.size());
            //System.out.println(" label: [" + label + " ] ");

            if (subGraphs.size() == 1) {
                if (colors <= this.colors | this.colors == 0) {
                    this.colors = colors;
                    System.out.print("colors: " + colors + " ");
                    System.out.print("subgraphs: " + subGraphs.size());
                    System.out.println(" label: [" + label + " ] ");
                }
                break;
            }

        }
    }

    
    /**
     * 
     * @param percentage 
     */
    public void greedy(double percentage) {
        //Set color limit based on the percentage input parameter
        int colorLimit = (int) (this.graph.getColorList().size() * percentage);
        this.greedy(colorLimit);
    }
    
    /**
     * 
     * @param colorLimit 
     */
    public void greedy(int colorLimit) {
        
        
        //Sort color list by the color most present
        //this.getGraph().sort();
        //Insert color clone or add sort method in color for id asc
        Collections.sort(this.graph.getLabelList());
        System.out.println("labels sorted");
        for (Label l : this.graph.getLabelList()) {
            System.out.println(l.getColors());
        }
                
        Collections.reverse(this.graph.getLabelList());
        System.out.println("labels sorted reverse");
        for (Label l : this.graph.getLabelList()) {
            System.out.println(l.getColors());
            System.out.println(l.getArchs());
        }
              
        Collections.sort(this.graph.getColorList());
        Collections.reverse(this.graph.getColorList());
        System.out.println("colors sorted reverse");
        for (Color c : this.graph.getColorList()) {
            System.out.println(c.getColor());
        }
        //this.getGraph().randomize();
        
        this.graph.resetNodesCheck();
        this.graph.resetColorCheck();
        this.graph.resetSolutionCheck();
        
        ArrayList<Integer> colorSolution = new ArrayList<Integer>();
        ArrayList<Graph> subGraphs = null;
        
        //Set colors founds
        int colors = 0;
        int rate = 3;
        for (int j = 0; j < rate ; j++) {
            //Select the color of the most frequent label
            ArrayList<Color> topLabelColors=this.graph.getLabelList().get(j).getColors();
            for(Color color : topLabelColors){
                if (!color.isSolution()){
                    color.setChecked(true);
                    color.setSolution(true);
                    colorSolution.add(color.getColor());
                    colors++;
                }
            }  
        }
        
        
        this.graph.resetArchsCheck();
        //After selection of a color, read all archs in the root graph and set 
        //true if the label contains the selected colors
        for (int i = 0; i < this.graph.getArchList().size(); i++) {

            //Get Arch in position i
            Arch selectedArch = this.graph.getArchList().get(i);
            ArrayList<Color> colorList = selectedArch.getLabel().getColors();

            //Check the label
            if (this.graph.allColorChecked(colorList)) {
                selectedArch.setChecked(true);
            }
        }
        
        //Verify number of components using actual Solution due to checked colors
        int components = this.getSubGraphs(this.graph).size();
        
        System.out.println("components after label colors selection:" + components);
        
        
        
        
        //Read all color from ordered color list
        while (colors < colorLimit & components > 1) {
            //Check if we have reached color limit or only one component
                //Init actual best color that reduce components number
                Color bestColor = null;
                
                    //Read new color to test solution with actual colors
                    for (Color color : this.graph.getColorList()) {
                        //If i haven't selected it
                        if (!color.isSolution()) {
                            if(bestColor==null)
                                bestColor=color;
                            
                            this.graph.resetArchsCheck();
                            //Insert is in actual solution
                            color.setChecked(true);

                            //After selection of a color, read all archs in the root graph and set 
                            //true if the label contains the selected colors
                            for (int i = 0; i < this.graph.getArchList().size(); i++) {
                                  
                                //Get Arch in position i
                                Arch selectedArch = this.graph.getArchList().get(i);
                                
                                ArrayList<Color> colorList = selectedArch.getLabel().getColors();
                                //Check the label
                                if (this.graph.allColorChecked(colorList)) {
                                    selectedArch.setChecked(true);
                                }
                                
                            }

                            this.graph.resetNodesCheck();
                            //Verify number of components using actual Solution due to checked colors
                            subGraphs = this.getSubGraphs(this.graph);

                            if (subGraphs.size() < components) {
                                components = subGraphs.size();
                                bestColor = color;
                            }
                            //else bestColor = null;
                            //Reset color to test previous Solution with other color
                            color.setChecked(false);
                        }
                    }
                    
                //Sign choosen color and add it in Solution
                bestColor.setChecked(true);
                bestColor.setSolution(true);
                //label += " " + String.valueOf(bestColor.getColor());
                colorSolution.add(bestColor.getColor());
                colors++;

            }
            
            System.out.print("colors: " + colors + " ");
            System.out.print(colorSolution + " ");
            System.out.print("subgraphs: " + components);
            //System.out.println(" label: [" + label + " ] ");
            //Collections.sort(colorSolution);
            System.out.println("\n label colori non ordinati: " + colorSolution.toString());
            this.graph.sortIntAsc(colorSolution);
            System.out.println(" label colori ordinati: " + colorSolution.toString());
               

    }

    public void randomGlobal(double percentage) {
        int colorLimit = (int) (this.graph.getColorList().size() * percentage);
        //this.getGraph().sort();
        //this.getGraph().randomize();
        Collections.shuffle(this.graph.getColorList());
        this.graph.resetNodesCheck();
        this.graph.resetColorCheck();
        this.graph.resetSolutionCheck();

        int colors = 0;
        //String label = "";
        ArrayList<Integer> colorSolution = new ArrayList<Integer>();

        ArrayList<Graph> subGraphs = null;
        int components = this.graph.getNodeList().size();

        for (Color color : this.graph.getColorList()) {

            if (colors < colorLimit & components > 1) {

                Color bestColor = null;

                if (colors >= this.graph.getMinLabelLength() - 1) {

                    for (Color color2 : this.graph.getColorList()) {

                        if (!color2.isSolution()) {
                            this.graph.resetArchsCheck();
                            color2.setChecked(true);

                            //After selection of a color, read all archs in the root graph and set 
                            //true if the label contains the selected colors
                            for (int i = 0; i < this.graph.getArchList().size(); i++) {

                                //Get Arch in position i
                                Arch selectedArch = this.graph.getArchList().get(i);
                                ArrayList<Color> colorList = selectedArch.getLabel().getColors();

                                //Check the label
                                if (this.graph.allColorChecked(colorList)) {
                                    selectedArch.setChecked(true);
                                }
                            }

                            this.graph.resetNodesCheck();
                            subGraphs = this.getSubGraphs(this.graph);

                            if (subGraphs.size() < components) {
                                components = subGraphs.size();
                                bestColor = color2;
                            }

                            color2.setChecked(false);
                        }
                    }

                    if (bestColor == null) {
                        bestColor = color;
                    }

                } else {
                    bestColor = color;
                }

                bestColor.setChecked(true);
                bestColor.setSolution(true);
                //label += " " + String.valueOf(bestColor.getColor());
                colorSolution.add(bestColor.getColor());
                colors++;

            } else if (this.components == 0 | this.components > components) {
                this.components = components;

                System.out.print("colors: " + colors + " ");
                System.out.print("subgraphs: " + components);
                //System.out.println(" label: [" + label + " ] ");
                System.out.println("\n label colori non ordinati: " + colorSolution.toString());
                this.graph.sortIntAsc(colorSolution);
                System.out.println(" label colori ordinati: " + colorSolution.toString());
                break;
            }
        }
    }

    public int countComponents(Graph graph) {
        graph.resetArchsCheck();
        graph.resetNodesCheck();

        for (int i = 0; i < graph.getArchList().size(); i++) {

            //Get Arch in position i
            Arch selectedArch = graph.getArchList().get(i);
            ArrayList<Color> colorList = selectedArch.getLabel().getColors();

            //Check the label
            if (graph.allColorChecked(colorList)) {
                selectedArch.setChecked(true);
            }
        }

        ArrayList<Graph> subGraphs = this.getSubGraphs(graph);
        return subGraphs.size();
    }
    
    public void colorSplit() {

        for (Color colorToClassify : this.getGraph().getColorList()) {
            if (colorToClassify.isSolution() == true & colorToClassify.isChecked() == true) {
                chosenColorList.add(colorToClassify);
            } else {
                excludedColorList.add(colorToClassify);
            }
        }
        //Both ColorList are sorted by frequency 
        System.out.println(" Lista di colori scelti nella soluzione: " + chosenColorList.toString());
        System.out.println(" Lista di colori non scelti nella soluzione: " + excludedColorList.toString() + "\n\n");

    }
    
    /**
     * @param keepingRatio - number of position 
     * @param swapColorRatio - number of colors to swap
     * @param temperature
     * @param coolingRate 
     */
    public void SAA(double keepingRatio, int swapColor, double temperature, double coolingRate) {

        int bestComponents = countComponents(graph);
        System.out.println("start components: " + bestComponents);
        
        ArrayList<Color> colorToKeep = new ArrayList<Color>();
        ArrayList<Color> colorToSwap = new ArrayList<Color>();
        
        ArrayList<Color> startSolution = (ArrayList<Color>) chosenColorList.clone();
        ArrayList<Color> excludedColors = (ArrayList<Color>) excludedColorList.clone();
        
        ArrayList<Color> bestSolution = new ArrayList<Color>();
        while (temperature > 1) {
        if (keepingRatio < 1.0) {
            int positionsToKeep = (int) (startSolution.size() * keepingRatio);
            Collections.shuffle(startSolution);
            colorToKeep =  new ArrayList<Color>(startSolution.subList(0, positionsToKeep+1));
            colorToSwap = new ArrayList<Color>(startSolution.subList(colorToKeep.size(), startSolution.size()));
        }
        

        //while (temperature > 1) {

            int currentEnergy = countComponents(graph);

            ArrayList<Color> chosenColorListRemoved = new ArrayList<Color>();
            ArrayList<Color> excludedColorListRemoved = new ArrayList<Color>();

            ArrayList<Integer> chosenColorListIndex = new ArrayList<Integer>();
            ArrayList<Integer> excludedColorListIndex = new ArrayList<Integer>();

            //Random randomGenerator = new Random();
            //int index1 = randomGenerator.nextInt(chosenColorList.size());
            for (int i = 0; i < swapColor; i++) {
                chosenColorListIndex.add(i, (int) (colorToSwap.size() * Math.random()));
                excludedColorListIndex.add(i, (int) (excludedColors.size() * Math.random()));

                chosenColorListRemoved.add(i, colorToSwap.get(chosenColorListIndex.get(i)));
                excludedColorListRemoved.add(i, excludedColors.get(excludedColorListIndex.get(i)));

                chosenColorListRemoved.get(i).setChecked(false);
                chosenColorListRemoved.get(i).setSolution(false);
                excludedColorListRemoved.get(i).setChecked(true);
                excludedColorListRemoved.get(i).setSolution(true);

                //colorToSwap.remove((int) chosenColorListIndex.get(i));
                //excludedColors.remove((int) excludedColorListIndex.get(i));
            }

            for (int i = swapColor; i > 0; i--) {
                
                colorToSwap.set((int) chosenColorListIndex.get(i-1), excludedColorListRemoved.get(i-1));
                excludedColors.set((int) excludedColorListIndex.get(i-1), chosenColorListRemoved.get(i-1));
                
                //colorToSwap.add((int) chosenColorListIndex.get(i - 1), excludedColorListRemoved.get(i - 1));
                //excludedColors.add((int) excludedColorListIndex.get(i - 1), chosenColorListRemoved.get(i - 1));
            }

            int neighbourEnergy = countComponents(graph);

            //System.out.println("current components: " + currentEnergy);
            //System.out.println("new components: " + neighbourEnergy);
            double acceptanceProbability = 0.0;

            if (neighbourEnergy < currentEnergy) {
                acceptanceProbability = 1.0;
            } else {
                acceptanceProbability = Math.exp((currentEnergy - neighbourEnergy) / temperature);
            }

            if (acceptanceProbability < Math.random()) {
                for (int i = swapColor; i > 0; i--) {
                    chosenColorListRemoved.get(i-1).setChecked(true);
                    chosenColorListRemoved.get(i-1).setSolution(true);
                    excludedColorListRemoved.get(i-1).setChecked(false);
                    excludedColorListRemoved.get(i-1).setSolution(false);
                    colorToSwap.set((int) chosenColorListIndex.get(i-1), excludedColorListRemoved.get(i-1));
                    excludedColors.set((int) excludedColorListIndex.get(i-1), chosenColorListRemoved.get(i-1));
                    //colorToSwap.add((int) chosenColorListIndex.get(i - 1), chosenColorListRemoved.get(i - 1));
                    //excludedColors.add((int) excludedColorListIndex.get(i - 1), excludedColorListRemoved.get(i - 1));
                }
            }else{
                bestSolution = null;
                bestSolution = new ArrayList<Color>();
                for(Color c : colorToKeep)
                    bestSolution.add(c);
                
                for(Color c : colorToSwap)
                    bestSolution.add(c);
                bestComponents = currentEnergy;
                //System.out.println(bestSolution.size());
            }

            temperature *= 1 - coolingRate;
        }

        System.out.println("Final solution components: " + bestComponents);
        System.out.println(" Lista di colori finale: " + bestSolution);
    }
    
    public double modelSolution(double percentage) {
        
        int numNodes=this.graph.getNodeList().size();
        int numEdge=this.graph.getArchList().size();
        int numColors=this.graph.getColorList().size();
        int colorLimit = (int) (numColors * percentage);
        double components = 0;
        try {
            //this.graph.getColorList().size();
            IloCplex model = new IloCplex();
            
            //make source node
            int numEdgeSN=0;
            Node source =new Node(numNodes);//may be ++
            for(Node node : this.graph.getNodeList()){
                Arch arch = new Arch(source, node ,null,numEdgeSN++);
                source.getArchList().add(arch);
            }
            
            
            
            //insert model variables for the source's edge
            IloIntVar[] sourceEdges = model.boolVarArray(numNodes);
            System.out.println("source edges:");
            for(int i=0;i<source.getArchList().size();i++){
                String sourceEdgeStr = "sEdge->S-" + source.getArchList().get(i).getToNode().getId();
                sourceEdges[i] = model.intVar(0, 1, sourceEdgeStr);
                System.out.println(sourceEdges[i]);
            }
            System.out.println();
            
            //insert model variables for the model/graph's edge
            IloIntVar[] graphEdges = model.boolVarArray(numEdge); 
            System.out.println("graph edges:");
            for(Arch a : this.graph.getArchList()){
                String graphEdgeStr = "gEdge->" + a.getFromNode().getId() + "-" + a.getToNode().getId();
                graphEdges[a.getId()]=model.intVar(0, 1, graphEdgeStr);
                System.out.println(graphEdges[a.getId()]);
            }
            System.out.println();
            //for(int i=0;i<numEdge;i++){
            //    graphEdges[i] = model.intVar(0, 1, "gEdge->" + this.graph.getArchList().get(i).getFromNode() + "-" + this.graph.getArchList().get(i).getToNode());
            //}
            
            //insert model variables for the colors
            IloIntVar[] colors = model.boolVarArray(numColors);
            System.out.println("colors:");
            for (Color c: this.graph.getColorList()) {
                String colorsStr = "color-" + c.getColor();
                colors[c.getColor()] = model.intVar(0, 1, colorsStr);
                System.out.println(colors[c.getColor()]);
            }
            System.out.println();
            
            
            
            //insert model variables for source's flow (number, lower bound, upper bound)
            IloIntVar[] sourceFlowEdges = model.intVarArray(numNodes , 0, numNodes);
            System.out.println("sourceFlowEdges:");
            for (int i = 0; i < numNodes; i++) {
                String sourceFlowEdgesStr = "sFlow->S-" + this.graph.getNodeList().get(i).getId();
                sourceFlowEdges[i] = model.intVar(0, numNodes, sourceFlowEdgesStr);
                System.out.println(sourceFlowEdges[i]);
            }
            System.out.println();
            
            //insert model variables for graph's flow
            IloIntVar[] graphFlowEdgesPositives = model.intVarArray(numEdge, 0, numNodes);
            IloIntVar[] graphFlowEdgesNegatives = model.intVarArray(numEdge, 0, numNodes);
            System.out.println("sourceGraphEdges:");
            for (int i = 0; i < numEdge; i++) {
                String graphFlowEdgesPositivesStr = "gFlow->" + this.graph.getArchList().get(i).getFromNode().getId() + "-" + this.graph.getArchList().get(i).getToNode().getId();
                String graphFlowEdgesNegativesStr = "gFlow->" + this.graph.getArchList().get(i).getToNode().getId() + "-" + this.graph.getArchList().get(i).getFromNode().getId();
                graphFlowEdgesPositives[i] = model.intVar(0, numNodes, graphFlowEdgesPositivesStr);
                graphFlowEdgesNegatives[i] = model.intVar(0, numNodes, graphFlowEdgesNegativesStr);
                System.out.println("incoming:"+graphFlowEdgesPositives[i]+"\toutcoming:"+graphFlowEdgesNegatives[i]);
            } 
            System.out.println();
            
            
            
            //Objective function
            //minimize source's edges as number of components
            IloLinearNumExpr objective = model.linearNumExpr();
            for(int i = 0; i < numNodes; i++){
                objective.addTerm(sourceEdges[i], 1);
            }
            model.addMinimize(objective);
            System.out.println("objective function : minimize"+objective);
            
            
            
            //constraint 1  
            //constraint on model's colors
            System.out.println("constraint 1 :");
            
            IloLinearNumExpr chosenColors = model.linearNumExpr();
            for(int i=0;i<numColors;i++){
                chosenColors.addTerm(1.0, colors[i]);
            }
            //model's colors <= limit of colors fixed(K) (Cp<=K)
            model.addLe(chosenColors, colorLimit);
            System.out.println(chosenColors + " <= " + colorLimit);
            
            //constraint 2
            System.out.println("constraint 2 :");  

            //for each edge
            for (int i = 0; i < numEdge; i++) {
                //for each color of the ith edge's label
                for (Color c: this.graph.getArchList().get(i).getLabel().getColors()) {
                    //for each graph's color
                    IloLinearIntExpr colorsConstraintTerm = model.linearIntExpr();
                    IloLinearIntExpr edgesConstraintTerm = model.linearIntExpr();
                    colorsConstraintTerm.addTerm(colors[c.getColor()], 1);
                    edgesConstraintTerm.addTerm(graphEdges[i], 1);
                    //compare color-edge 
                    model.addGe(colorsConstraintTerm, edgesConstraintTerm);
                    System.out.println(colorsConstraintTerm + " >= " + edgesConstraintTerm);
                }
            }
            
            
            //constraint 3
            IloLinearIntExpr sourceFlow = model.linearIntExpr();
            for (int i = 0; i < source.getArchList().size(); i++){
                    sourceFlow.addTerm(sourceFlowEdges[i], 1);
            }
            model.addEq(sourceFlow,numNodes);
            System.out.println("\nconstraint 3\n"+sourceFlow + " = " + numNodes+"\n");
            
            
            //constraint 4
            System.out.println("constraint 4");
            for(Node n: this.graph.getNodeList()){
                IloLinearIntExpr totalFlow = model.linearIntExpr();
                totalFlow.addTerm(sourceFlowEdges[n.getId()],1);
                for(Arch a: n.getArchList()){
                    totalFlow.addTerm(graphFlowEdgesPositives[a.getId()], 1);
                    totalFlow.addTerm(graphFlowEdgesNegatives[a.getId()], -1);
                }
                model.addEq(totalFlow, 1);
                System.out.println(totalFlow + " = 1");
            }
            System.out.println(); 
            
            
            
            //constraint 5
            //System.out.println("constraint 5 :");
            for (Arch a: this.graph.getArchList()) {                            
                IloLinearIntExpr internalPositiveFlowConstraint = model.linearIntExpr();
                IloLinearIntExpr graphEdgesConstraint1 = model.linearIntExpr();
                internalPositiveFlowConstraint.addTerm(graphFlowEdgesPositives[a.getId()], 1);
                graphEdgesConstraint1.addTerm(graphEdges[a.getId()], numNodes);
                model.addLe(internalPositiveFlowConstraint, graphEdgesConstraint1);

                IloLinearIntExpr internalNegativeFlowConstraint = model.linearIntExpr();
                IloLinearIntExpr graphEdgesConstraint2 = model.linearIntExpr();
                internalNegativeFlowConstraint.addTerm(graphFlowEdgesNegatives[a.getId()], 1);
                graphEdgesConstraint2.addTerm(graphEdges[a.getId()], numNodes);
                model.addLe(internalNegativeFlowConstraint, graphEdgesConstraint2);
            }   
            
            //constraint 6    
            //System.out.println("constraint 6 :");
            for (Node n: this.graph.getNodeList()) {
                IloLinearIntExpr sourceFlowConstraint = model.linearIntExpr();
                IloLinearIntExpr sourceEdgesConstraint = model.linearIntExpr();
                sourceFlowConstraint.addTerm(sourceFlowEdges[n.getId()], 1);
                sourceEdgesConstraint.addTerm(sourceEdges[n.getId()], numNodes);
                model.addLe(sourceFlowConstraint, sourceEdgesConstraint);
            } 
            
            //constraint 5+6    ??
            //System.out.println("constraint 5+6 :");
//            for (Node n: this.graph.getNodeList()) {
//                IloLinearIntExpr sourceFlowConstraint = model.linearIntExpr();
//                IloLinearIntExpr sourceEdgesConstraint = model.linearIntExpr();
//                sourceFlowConstraint.addTerm(sourceFlowEdges[n.getId()], 1);
//                sourceEdgesConstraint.addTerm(sourceEdges[n.getId()], numNodes);
//                model.addLe(sourceFlowConstraint, sourceEdgesConstraint);
//            } 
            
                                       
                
            //Model Solution
            
            model.setParam(IloCplex.DoubleParam.TimeLimit,3600);
            
            if (model.solve()) {
                System.out.println("Model Solution: " + (model.getObjValue()));
                components = model.getObjValue();
                model.end();
            }
            
        } catch (IloException ex) {
            Logger.getLogger(GraphManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return components;
    } 
}

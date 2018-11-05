/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

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
                    archColors.add(this.graph.getColorList().get(Integer.valueOf(items[i])));
                    this.graph.getColorList().get(Integer.valueOf(items[i])).increaseCounter();
                }

                //Sort array of the label
                //this.graph.sortAsc(archColors);
                Collections.sort(archColors);

                Label label = null;

                //Make sorted label
                String labelStr = "";
                for (Color color : archColors) {
                    labelStr += "-" + color.getColor();
                }
                labelStr = labelStr.substring(1);

                //Insert and count labels
                if (this.graph.getLabels().containsKey(labelStr)) {
                    label = this.graph.getLabels().get(labelStr);
                    label.increaseArchs();
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
        
        //Sort color list by the color most present
        //this.getGraph().sort();
        Collections.sort(this.graph.getColorList());
        Collections.reverse(this.graph.getColorList());
        //this.getGraph().randomize();
        
        this.graph.resetNodesCheck();
        this.graph.resetColorCheck();
        this.graph.resetSolutionCheck();

        //Set colors founds
        int colors = 0;
        //String label = "";
        
        ArrayList<Integer> colorSolution = new ArrayList<Integer>();

        
        ArrayList<Graph> subGraphs = null;
        
        //Set initial number on components based on number of nodes
        //because all nodes in solution are disconnected
        int components = this.graph.getNodeList().size();

        //Read all color from ordered color list
        for (Color color : this.graph.getColorList()) {
            //Check if we have reached color limit or only one component
            if (colors < colorLimit & components > 1) {
                //Init actual best color that reduce components number
                Color bestColor = null;
                
                //Check if actual number of colors cover at least one label size
                //for selection
                if (colors >= this.graph.getMinLabelLength() - 1) {
                    //Read new color to test solution with actual colors
                    for (Color color2 : this.graph.getColorList()) {
                        //If i haven't selected it
                        if (!color2.isSolution()) {
                            this.graph.resetArchsCheck();
                            //Insert is in actual solution
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
                            //Verify number of components using actual Solution due to checked colors
                            subGraphs = this.getSubGraphs(this.graph);

                            if (subGraphs.size() < components) {
                                components = subGraphs.size();
                                bestColor = color2;
                            }
                            //Reset color to test previous Solution with other color
                            color2.setChecked(false);
                        }
                    }
                    //If I have never found new color to decrease components
                    //I can add the most present color not checked
                    if (bestColor == null) {
                        bestColor = color; // Is the last, we need the first
                        //we need a for that read the ordered list and get the first unchecked color
                    }

                //If I haven't reached minimum number of colors for label
                //I add most present color not checked
                } else {
                    bestColor = color;
                }
                //Sign choosen color and add it in Solution
                bestColor.setChecked(true);
                bestColor.setSolution(true);
                //label += " " + String.valueOf(bestColor.getColor());
                colorSolution.add(bestColor.getColor());
                colors++;

            } else {

                System.out.print("colors: " + colors + " ");
                System.out.print("subgraphs: " + components);
                //System.out.println(" label: [" + label + " ] ");
                //Collections.sort(colorSolution);
                System.out.println("\n label colori non ordinati: " + colorSolution.toString());
                this.graph.sortIntAsc(colorSolution);
                System.out.println(" label colori ordinati: " + colorSolution.toString());
                break;
            }

        }

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
    
    public void SAA(int temperature, double coolingRate) {
        this.SAA(1.0, 1.0, temperature, coolingRate);
    }

    public void SAA(int swapColorRatio, int temperature, double coolingRate) {
        this.SAA(1.0, swapColorRatio, temperature, coolingRate);
    }

    public void SAA(double keepingRatio, double swapColorRatio, double temperature, double coolingRate) {

        ArrayList<Color> colorToKeep = new ArrayList<Color>();
        
        if (keepingRatio < 1.0) {
            int positionsToKeep = (int) (chosenColorList.size() * keepingRatio);
            
            for (int i = 0; i < positionsToKeep; i++) {
                colorToKeep.add(chosenColorList.get(0));
                chosenColorList.remove(0);
            }
        }
        
        
        ArrayList<Color> bestSolution = new ArrayList<Color>();
        bestSolution = (ArrayList<Color>) chosenColorList.clone();

        int bestComponents = countComponents(graph);
        System.out.println("start components: " + bestComponents);

        

        int swapColor = (int) (colorToKeep.size() * swapColorRatio);

        while (temperature > 1) {

            int currentEnergy = countComponents(graph);

            ArrayList<Color> chosenColorListRemoved = new ArrayList<Color>();
            ArrayList<Color> excludedColorListRemoved = new ArrayList<Color>();

            ArrayList<Integer> chosenColorListIndex = new ArrayList<Integer>();
            ArrayList<Integer> excludedColorListIndex = new ArrayList<Integer>();

            //Random randomGenerator = new Random();
            //int index1 = randomGenerator.nextInt(chosenColorList.size());
            for (int i = 0; i < swapColor; i++) {
                chosenColorListIndex.add(i, (int) (chosenColorList.size() * Math.random()));
                excludedColorListIndex.add(i, (int) (excludedColorList.size() * Math.random()));

                chosenColorListRemoved.add(i, chosenColorList.get(chosenColorListIndex.get(i)));
                excludedColorListRemoved.add(i, excludedColorList.get(excludedColorListIndex.get(i)));

                chosenColorListRemoved.get(i).setChecked(false);
                chosenColorListRemoved.get(i).setSolution(false);
                excludedColorListRemoved.get(i).setChecked(true);
                excludedColorListRemoved.get(i).setSolution(true);

                chosenColorList.remove((int) chosenColorListIndex.get(i));
                excludedColorList.remove((int) excludedColorListIndex.get(i));
            }

            for (int i = swapColor; i > 0; i--) {
                chosenColorList.add((int) chosenColorListIndex.get(i - 1), excludedColorListRemoved.get(i - 1));
                excludedColorList.add((int) excludedColorListIndex.get(i - 1), chosenColorListRemoved.get(i - 1));
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
                    chosenColorList.add((int) chosenColorListIndex.get(i - 1), chosenColorListRemoved.get(i - 1));
                    excludedColorList.add((int) excludedColorListIndex.get(i - 1), excludedColorListRemoved.get(i - 1));
                }
            }

            if (neighbourEnergy < bestComponents) {
                bestSolution = (ArrayList<Color>) chosenColorList.clone();
                bestComponents = currentEnergy;
            }

            temperature *= 1 - coolingRate;
        }

        System.out.println("Final solution components: " + bestComponents);
        colorToKeep.addAll(bestSolution);
        System.out.println(" Lista di colori finale: " + colorToKeep);
    }
    
    public int modelSolution(double percentage) {
        
        int numNodes=this.graph.getNodeList().size();
        int numEdge=this.graph.getArchList().size();
        int numColors=this.graph.getColorList().size();
        int colorLimit = (int) (numColors * percentage);
        int components = 0;
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
            for(int i=0;i<source.getArchList().size();i++){
                sourceEdges[i] = model.intVar(0, 1, "sEdge->" + source.getId() + "-" + source.getArchList().get(i).getToNode().getId());
            }
            
            //insert model variables for the model/graph's edge
            IloIntVar[] graphEdges = model.boolVarArray(numEdge); 
            for(Arch a : this.graph.getArchList()){
                graphEdges[a.getId()]=model.intVar(0, 1, "gEdge->" + a.getFromNode() + "-" + a.getToNode());
            }
            //for(int i=0;i<numEdge;i++){
            //    graphEdges[i] = model.intVar(0, 1, "gEdge->" + this.graph.getArchList().get(i).getFromNode() + "-" + this.graph.getArchList().get(i).getToNode());
            //}
            
            //insert model variables for the colors
            IloIntVar[] colors = model.boolVarArray(numColors);
            for (int i = 0; i < numColors; i++) {
                colors[i] = model.intVar(0, 1, "color-" + this.graph.getColorList().get(i).getColor());
            }
            
            
            
            //insert model variables for source's flow (number, lower bound, upper bound)
            IloIntVar[] sourceFlowEdges = model.intVarArray(numNodes , 0, numNodes);
            for (int i = 0; i < numNodes; i++) {
                sourceFlowEdges[i] = model.intVar(0, numNodes, "sFlow->" + source.getId() + "-" + this.graph.getNodeList().get(i).getId());        
            }
            
            //insert model variables for graph's flow
            IloIntVar[] graphFlowEdgesPositives = model.intVarArray(numEdge, 0, numNodes);
            IloIntVar[] graphFlowEdgesNegatives = model.intVarArray(numEdge, 0, numNodes);
            for (int i = 0; i < numEdge; i++) {
                graphFlowEdgesPositives[i] = model.intVar(0, numNodes, "gFlow->" + this.graph.getArchList().get(i).getFromNode().getId() + "-" + this.graph.getArchList().get(i).getToNode().getId());
                graphFlowEdgesNegatives[i] = model.intVar(0, numNodes, "gFlow->" + this.graph.getArchList().get(i).getToNode().getId() + "-" + this.graph.getArchList().get(i).getFromNode().getId());
            } 
            
            
            
            
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
                    //confronto colore-arco 
                    model.addGe(colorsConstraintTerm, edgesConstraintTerm);
                    System.out.println(colorsConstraintTerm + " >= " + edgesConstraintTerm);
                }
            }
            
            
            //constraint 3
            IloLinearIntExpr sourceFlow = model.linearIntExpr();
            for (int i = 0; i < source.getArchList().size(); i++){
                    sourceFlow.addTerm(sourceFlowEdges[i], 1);
            }
            //System.out.println(sommaFlussoSorgente + " = " + g.numeroNodi);
            model.addEq(sourceFlow,numNodes);
            
            
            //constraint 4
            for(Node n: this.graph.getNodeList()){
                IloLinearIntExpr totalFlow = model.linearIntExpr();
                totalFlow.addTerm(sourceFlowEdges[n.getId()],1);
                for(Arch a: n.getArchList()){
                    totalFlow.addTerm(graphFlowEdgesPositives[a.getId()], 1);
                    totalFlow.addTerm(graphFlowEdgesNegatives[a.getId()], 1);
                }
                model.addEq(totalFlow, 1);
            }
            
            
            
            
            //vincolo 5
            //vincolo che collega il flusso interno  con le variabili arco
            //System.out.println("vincolo 5 :");
            for (int i = 0; i < numEdge; i++) {
                IloLinearIntExpr vincoloFlussointerno_1_1 = model.linearIntExpr();
                IloLinearIntExpr vincoloFlussoInterno_1_2 = model.linearIntExpr();
                vincoloFlussointerno_1_1.addTerm(graphFlowEdgesPositives[i], 1);
                vincoloFlussoInterno_1_2.addTerm(graphEdges[i], numNodes);
                model.addLe(vincoloFlussointerno_1_1, vincoloFlussoInterno_1_2);
                //System.out.println(vincoloFlussointerno_1_1 + " <= " + vincoloFlussoInterno_1_2);

                IloLinearIntExpr vincoloFlussoInterno_2_1 = model.linearIntExpr();
                IloLinearIntExpr vincoloFlussoInterno_2_2 = model.linearIntExpr();
                vincoloFlussoInterno_2_1.addTerm(graphFlowEdgesNegatives[i], 1);
                vincoloFlussoInterno_2_2.addTerm(graphEdges[i], numNodes);
                model.addLe(vincoloFlussoInterno_2_1, vincoloFlussoInterno_2_2);
                //System.out.println(vincoloFlussoInterno_2_1 + " <= " + vincoloFlussoInterno_2_2);
            }   
            //vincolo6    
            //vincolo che collega il flusso sorgente  con le variabili arco sorgente
            //System.out.println("vincolo 6 :");
            for (int i = 0; i < numNodes; i++) {
                IloLinearIntExpr vincoloFlussoSorgente_1_1 = model.linearIntExpr();
                IloLinearIntExpr vincoloFlussoSorgente_1_2 = model.linearIntExpr();
                vincoloFlussoSorgente_1_1.addTerm(sourceFlowEdges[i], 1);
                vincoloFlussoSorgente_1_2.addTerm(sourceEdges[i], numNodes);
                model.addLe(vincoloFlussoSorgente_1_1, vincoloFlussoSorgente_1_2);
                //System.out.println(vincoloFlussoSorgente_1_1 + " <= " + vincoloFlussoSorgente_1_2);
            } 
                    
                    
                
            //Model Solution
            ArrayList<Arch> solutionArch = new ArrayList<>();
            model.setParam(IloCplex.DoubleParam.TimeLimit,3600);
            
            if (model.solve()) {
                System.out.println("Soluzione componenti modello matematico: " + (model.getObjValue()));
                
                model.end();
            }
            
        } catch (IloException ex) {
            Logger.getLogger(GraphManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return components;
    } 
}

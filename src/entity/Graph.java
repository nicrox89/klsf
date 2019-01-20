/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author nicolarusso
 */
public class Graph {

    private ArrayList<Node> nodeList;
    private ArrayList<Arch> archList;
    private ArrayList<Color> colorList;
    private ArrayList<Label> labelList;
    private HashMap<String, Label> labels;
    private int minLabelLength;
    private int id;

    public Graph() {
        this.nodeList = new ArrayList<Node>();
        this.archList = new ArrayList<Arch>();
        this.colorList = new ArrayList<Color>();
        this.labelList = new ArrayList<Label>();
        this.labels = new HashMap<String, Label>();
        this.minLabelLength = 0;
    }

    public Graph(ArrayList<Node> nodeList, ArrayList<Arch> archList, ArrayList<Color> colorList) {
        this.nodeList = nodeList;
        this.archList = archList;
        this.colorList = colorList;
        this.minLabelLength = this.colorList.size();
    }

    public void init(int nodes, int colors) {
        this.makeEmptyNodes(nodes);
        this.makeColors(colors);
        this.minLabelLength = colors;
    }

    public ArrayList<Node> getNodeList() {
        return nodeList;
    }

    public void setNodeList(ArrayList<Node> nodeList) {
        this.nodeList = nodeList;
    }

    public ArrayList<Arch> getArchList() {
        return archList;
    }

    public void setArchList(ArrayList<Arch> archList) {
        this.archList = archList;
    }

    public ArrayList<Color> getColorList() {
        return colorList;
    }
    
    public ArrayList<Color> getSelectedColorList() {
        ArrayList<Color> colorList = new ArrayList<Color>();
        
        for(Color color : this.colorList)
            if(color.isChecked())
                colorList.add(color);
            
        return colorList;
    }

    public void setColorList(ArrayList<Color> colorList) {
        this.colorList = colorList;
    }

    public ArrayList<Label> getLabelList() {
        return labelList;
    }

    public void setLabelList(ArrayList<Label> labelList) {
        this.labelList = labelList;
    }

    public int getMinLabelLength() {
        return minLabelLength;
    }

    public void setMinLabelLength(int minLabelLength) {
        this.minLabelLength = minLabelLength;
    }

    public HashMap<String, Label> getLabels() {
        return labels;
    }

    public void setLabels(HashMap<String, Label> labels) {
        this.labels = labels;
    }
    
    

    /**
     * Generate empty Nodes List
     *
     * @param num
     */
    public void makeEmptyNodes(int num) {
        for (int i = 0; i < num; i++) {
            this.nodeList.add(i, new Node(i));
        }
    }

    /**
     * Generate base Color List
     *
     * @param num
     */
    public void makeColors(int num) {
        for (int i = 0; i < num; i++) {
            this.colorList.add(i, new Color(i));
        }
    }

    public int nodeUnchecked() {
        int index = -1;
        for (Node node : this.nodeList) {
            if (!node.isChecked()) {
                index = node.getId();
                break;
            }
        }
        return index;
    }

    public boolean allColorChecked(ArrayList<Color> colorList) {
        boolean checked = true;
        for (Color color : colorList) {
            if (!color.isChecked()) {
                checked = false;
            }
        }
        return checked;
    }
    
    public void removeEqualsLabels(){
        int size=this.labelList.size();
        for(int i=0; i<size; i++){
            
        }
    }

    public boolean containLabel(ArrayList<Color> colorList, ArrayList<Color> label) {
        boolean contains = false;
        int equalColors = 0;
        if(colorList.size()==label.size()){
            for (Color color : colorList) {
                for (Color color2 : label) {
                    if (color.getColor() == color2.getColor()) {
                        equalColors++;
                        break;
                    }
                }
            }
            if (equalColors == colorList.size()) {
                contains = true;
            }
        }
        return contains;
    }

    public void loadLabels() {
        for (Arch arch : this.archList) {
            if (this.labelList.isEmpty()) {
                this.labelList.add(new Label(arch.getLabel().getColors()));
            } else {
                boolean exists=false;
                for (Label label : this.labelList) {
                    if (this.containLabel(label.getColors(), arch.getLabel().getColors())) {
                        exists=true;
                        break;
                    }
                }
                if(!exists)
                    this.labelList.add(new Label(arch.getLabel().getColors()));
            }
        }
    }

    /**
     * comment
     */
    public void resetNodesCheck() {
        for (Node node : this.nodeList) {
            node.setChecked(false);
        }
    }

    public void resetColorCheck() {
        for (Color color : this.colorList) {
            color.setChecked(false);
        }
    }
    
    public void resetSolutionCheck() {
        for (Color color : this.colorList) {
            color.setSolution(false);
        }
    }

    public void resetArchsCheck() {
        for (Arch arch : this.archList) {
            arch.setChecked(false);
        }
    }

    public void randomize() {
        Collections.shuffle(this.colorList);
    }

    
    public void sortIntAsc(ArrayList<Integer> arr){
        Collections.sort(arr, new IntComparatorAsc());
    }
    

    public class IntComparatorAsc implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            if (o1 > o2) {
                return 1;
            } else {
                return -1;
            }
        }
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    

}

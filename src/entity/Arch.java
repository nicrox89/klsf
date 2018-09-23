/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.ArrayList;

/**
 *
 * @author nicolarusso
 * @param fromNode is the first Node of a Arch
 * @param toNode is the second Node of a Arch
 * @param colorList is a list of the label's color
 */
public class Arch {
    private Node fromNode;
    private Node toNode;
    private Label label;
    private boolean checked;
    private int flow;
    
    public Arch(){
        this.fromNode=null;
        this.toNode=null;
        this.label=null;
        this.checked=false;
        this.flow = 1;
    }
    
    public Arch(Node fromNode, Node toNode, Label label){
        this.fromNode=fromNode;
        this.toNode=toNode;
        this.label = label;
        this.checked=false;
        this.flow = 1;
    }

    public Node getFromNode() {
        return fromNode;
    }

    public void setFromNode(Node fromNode) {
        this.fromNode = fromNode;
    }

    public Node getToNode() {
        return toNode;
    }

    public void setToNode(Node toNode) {
        this.toNode = toNode;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    
    
}

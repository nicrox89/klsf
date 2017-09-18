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
 * @param id is the identificator of a Node
 * @param checked is a flag that sign when a Node is inserted in solution
 */
public class Node {
    private int id;
    private boolean checked;
    ArrayList<Arch> archList;
    
    public Node(){
        this.id=-1;
        this.checked=false;
        this.archList=new ArrayList<Arch>();
    }
    
    public Node(int id){
        this.id=id;
        this.checked=false;
        this.archList=new ArrayList<Arch>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public ArrayList<Arch> getArchList() {
        return archList;
    }

    public void setArchList(ArrayList<Arch> archList) {
        this.archList = archList;
    }
    
    

    
}

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
 */
public class Label {
    private ArrayList<Color> colors;
    private boolean checked;
    private int archs;
    private int id;
    
    public Label(ArrayList<Color> label){
        this.colors=label;
        this.checked=false;
        this.archs=0;
    }

    public ArrayList<Color> getColors() {
        return colors;
    }

    public void setColors(ArrayList<Color> colors) {
        this.colors = colors;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getArchs() {
        return archs;
    }

    public void setArchs(int archs) {
        this.archs = archs;
    }
    
    public void increaseArchs(){
        this.archs++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
    
}

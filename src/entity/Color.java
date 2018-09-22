/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

/**
 *
 * @author nicolarusso
 * @param color is the identificator of a color
 * @param checked is a boolean that sign when a color is used for label
 * combination
 */
public class Color implements Comparable<Color> {

    private int color;
    private boolean checked;
    private boolean solution;
    private int archs;

    public Color(int color) {
        this.color = color;
        this.checked = false;
        this.solution = false;
        this.archs = 0;
    }
    
    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isSolution() {
        return solution;
    }

    public void setSolution(boolean solution) {
        this.solution = solution;
    }

    public int getArchs() {
        return archs;
    }

    public void resetCounter() {
        this.archs = 0;
    }

    public void increaseCounter() {
        this.archs++;
    }

    @Override
    public String toString() {
        return Integer.toString(color);
    }
    
    @Override
    public int compareTo(Color otherColor) {
        return Integer.compare(archs, otherColor.getArchs());
    }
    
    

}

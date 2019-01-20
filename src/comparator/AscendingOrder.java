/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comparator;

import entity.Color;
import java.util.Comparator;

/**
 *
 * @author ilari
 */
public class AscendingOrder implements Comparator<Color> 
{ 
    @Override
    public int compare(Color c1, Color c2) 
    { 
        return Integer.compare(c1.getColor(),c2.getColor()); 
    } 
} 


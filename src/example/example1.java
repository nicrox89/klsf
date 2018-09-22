/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 *
 * @author nicolarusso
 */
public class example1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IloException {
        //Function MAX z=x_1+2x_2
       IloCplex model = new IloCplex();
       IloNumVar x1 = model.numVar(0.0, Double.MAX_VALUE, "x1");
       IloNumVar x2 = model.numVar(0.0, Double.MAX_VALUE, "x2");
       
       IloLinearNumExpr f = model.linearNumExpr();
       f.addTerm(x1, 1);
       f.addTerm(x2, 2);
       model.addMaximize(f);
       
       //constraint x1 <= 10
       model.addLe(x1, 10.0, "c1");
       
       //constraint x1 >= x1
       model.addGe(x2, x1, "c2");
       
       //2x_1+3x_2 <= 100
       IloLinearNumExpr c3 = model.linearNumExpr();
       c3.addTerm(x1, 1);
       c3.addTerm(x2, 3);
       model.addLe(c3, 100, "c3");
       
       
       if(model.solve()){
           System.out.println("Solution status = "+ model.getStatus());
           System.out.println("Solution value = "+ model.getObjValue());
       }else{
           System.out.println("Solution status = "+ model.getStatus());
       }
       model.end();
    }
    
}

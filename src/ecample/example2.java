/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecample;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 *
 * @author nicolarusso
 */
public class example2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IloException {
       //Function MAX z=y-bj
       IloCplex model = new IloCplex();
       IloNumVar x1 = model.intVar(0, Integer.MAX_VALUE, "x1");
       IloNumVar x2 = model.intVar(0, Integer.MAX_VALUE, "x2");
       IloNumVar x3 = model.intVar(0, Integer.MAX_VALUE, "x3");
       IloNumVar x4 = model.intVar(0, Integer.MAX_VALUE, "x4");
       
       IloLinearNumExpr f = model.linearNumExpr();
       f.addTerm(x1, 1);
       f.addTerm(x2, 1);
       f.addTerm(x3, 1);
       f.addTerm(x4, 1);
       model.addMaximize(f);
       
       //constraint x1 <= 3
       model.addLe(x1, 3.0, "c1");
       //constraint x2 <= 4
       model.addLe(x2, 4.0, "c2");
       //constraint x3 <= 7
       model.addLe(x3, 7.0, "c3");
       //constraint x4 <= 10
       model.addLe(x4, 10.0, "c4");
       // constraint <=100
       model.addLe(f, 100, "c5");
       
       
       if(model.solve()){
           System.out.println("Solution status = "+ model.getStatus());
           System.out.println("Solution value = "+ model.getObjValue());
       }else{
           System.out.println("Solution status = "+ model.getStatus());
       }
       model.end();
    }
    
}

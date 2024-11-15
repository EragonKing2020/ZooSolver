package tests;
import java.util.Arrays;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

public class Test_comparison_opposite {

	public static void main(String[] args) {
		/*
		// Many to Many
		// ELEMENT
		solveModel(false, false, false, new int[][] {{1,1},{-1,1}}); // No strategy
		// Error, we obtains b[0][0] = 0 while no 0 in a[0].
		solveModel(true, false, false, new int[][] {{1,1},{-1,1}}); // Search on a and b, Lower bound
		solveModel(true, true, false, new int[][] {{1,1},{-1,1}}); // Search on a and b, Upper bound
		// Setting a strategy only on a and b is not helping => the aapos and bbpos variables are not instanciated and the constraint is not used !
		
		// GCC
		solveModel(false, false, true, new int[][] {{0,1},{-1,0}}); // No strategy
		// No problem with this one.
		solveModel(true, false, true, new int[][] {{0,1},{-1,0}}); // Search on a and b, Lower bound
		solveModel(true, true, true, new int[][] {{0,1},{-1,0}}); // Search on a and b, Upper bound
		// With a strategy this is good too.
		*/
		
		Model model = new Model();
		IntVar[][] a = model.intVarMatrix("a", 2, 3, 0, 4);
		IntVar[][] b = model.intVarMatrix("b", 3, 2, 0, 1);
		IntVar[][] aocc = model.intVarMatrix("aocc", 2, 3, 0, 5);
		IntVar[][] bocc = model.intVarMatrix("bocc", 3, 2, 0, 5);
		oppositeCSP2(model, a, b, aocc, bocc);
		/*IntVar[][] aapos = model.intVarMatrix("aapos", 2, 3, 0, 5);
		IntVar[][] bbpos = model.intVarMatrix("bbpos", 2, 3, 0, 5);
		oppositeCSP(model, a, b, aapos, bbpos);*/
		//oppositeCSP1(model, a, ArrayUtils.flatten(b));
		Solver solver = model.getSolver();
		solver.setSearch(Search.minDomLBSearch(ArrayUtils.flatten(a)));
		solver.findSolution();
		System.out.println(model);
		
		
		// For a one to many, we can use element, but in a more concise way :
		// if b can be linked to only one a, then we have b[] and not b[][] (one for each b and not many for each b)
		// Then we can replace the m.element(aaid,bb,aapos[i][j],0) by arithm(b[j], "=", i).
		
		// Another way is to flatten the matrix a, and set element(j, aFlat, pos),
		// With pos constrained to vary only between the values correponding to a[b[j]].
		// Using this methods, we remove a loop (the i loop).
	}
	
	static void oppositeCSP(Model m, IntVar[][] a, IntVar[][] b, IntVar[][] aapos, IntVar[][] bbpos){
        for(int i=0;i<a.length;i++){
            for(int j=0;j<b.length;j++){
                IntVar[] aa = a[i];
                IntVar[] bb = b[j];
                System.out.println(aa.length + "-" + bb.length);
                IntVar aaid = m.intVar(i); //I think consts use singletons, so doing it messy
                IntVar bbid = m.intVar(j);
                m.ifOnlyIf(m.element(bbid,aa,bbpos[i][j],0),m.element(aaid,bb,aapos[i][j],0)); // Question : Fonctionne seulement car lien unique animal -> cage ?
            }
        }
    }
	
	static void oppositeCSP1(Model m, IntVar[][] a, IntVar[] b) {
		for (int j = 0; j < b.length; j ++) {
			IntVar index = m.intVar(0, a.length * a[0].length);
			m.arithm(index, ">=", b[j], "*", a[0].length).post();
			IntVar bPlus = b[j].add(1).intVar();
			m.arithm(index, "<", bPlus, "*", a[0].length).post();
			m.element(m.intVar(j), ArrayUtils.flatten(a), index, 0).post();
		}
	}
	
	static void oppositeCSP2(Model m, IntVar[][] a, IntVar[][] b, IntVar[][] aocc, IntVar[][] bocc){
        int al = a.length;
        int bl = b.length;
        int[] avals = new int[bl];
        for(int i=0;i<bl;i++) avals[i]=(i);
        for(int i=0;i<al;i++)
            m.globalCardinality(a[i],avals,aocc[i],false).post();
        
        int[] bvals = new int[al];
        for(int i=0;i<al;i++) bvals[i]=(i);
        for(int i=0;i<bl;i++)
            m.globalCardinality(b[i],bvals,bocc[i],false).post();

        for(int i=0;i<al;i++) for(int j=0;j<bl;j++)
        	m.arithm(aocc[i][j], "=", bocc[j][i]).post();
            //m.ifOnlyIf(m.arithm(aocc[i][j], ">",0), m.arithm(bocc[j][i], ">",0));
    }
	
	static void solveModel(Boolean hasSearch, Boolean searchUB, Boolean oppGcc, int[][] initVal) {
		System.out.println("Model : hasSearch = " + hasSearch + ", searchUB = " + searchUB + ", oppGcc = " + oppGcc + ".");
		Model model = new Model();
		IntVar[][] a = model.intVarMatrix("a", 1, 2, 0, 1);
		IntVar[][] b = model.intVarMatrix("b", 1, 2, 0, 1);
		if (initVal[0][0] >= 0) a[0][0].eq(initVal[0][0]).post();
		if (initVal[0][1] >= 0) a[0][1].eq(initVal[0][1]).post();
		if (initVal[1][0] >= 0) b[0][0].eq(initVal[1][0]).post();
		if (initVal[1][1] >= 0) b[0][1].eq(initVal[1][1]).post();
		IntVar[][] aaposocc;
		IntVar[][] bbposocc;
		if (oppGcc) {
			aaposocc = model.intVarMatrix("aocc", 1, 1, 0, 5);
			bbposocc = model.intVarMatrix("bocc", 1, 1, 0, 5);
			oppositeCSP2(model, a, b, aaposocc, bbposocc);
		}
		else {
			aaposocc = model.intVarMatrix("aapos", 1, 2, 0, 5);
			bbposocc = model.intVarMatrix("bbpos", 1, 2, 0, 5);
			oppositeCSP(model, a, b, aaposocc, bbposocc);
		}
		Solver solver = model.getSolver();
		if (hasSearch)	{
			if (searchUB)
				solver.setSearch(Search.minDomUBSearch(ArrayUtils.concat(a[0], b[0])));
			else
				solver.setSearch(Search.minDomLBSearch(ArrayUtils.concat(a[0], b[0])));
		}
		solver.findSolution();
		
		for (int i = 0; i < a.length; i ++) {
			IntVar[] aa = a[i];
			IntVar[] bb = b[i];
			if (oppGcc)
				System.out.println("gcc([" + aa[0] + "," + aa[1] + "], [0], " + Arrays.toString(aaposocc[0]) + ") <=> elem([" + bb[0] + "," + bb[1] + "], [0], " + Arrays.toString(bbposocc[0]) + ")");
			else
				for (int j = 0; j < b.length; j ++)
					System.out.println("elem(" + j + ",[" + aa[0] + "," + aa[1] + "]," + bbposocc[i][j] + ") <=> elem("+ i + ",[" + bb[0] + "," + bb[1] + "]," + aaposocc[i][j] + ")");
		}
	}
}

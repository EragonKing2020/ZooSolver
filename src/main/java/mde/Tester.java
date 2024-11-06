package mde;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;

import zoo.Animal;
import zoo.Cage;
import zoo.Park;
import zoo.Species;

public class Tester {

	public static void main(String[] args) {
		//test0();
		//test1(getFileNameTime(), true);
		test2(getFileNameTime(), 5, false);
		test2(getFileNameTime(), 5, true);
		//test2(getFileNameTime(), 8);
	}
	
	public static void test0() {
		Park park = createPark("park", 2, new int[] {2,3}, 1, new int[] {1});
		solveAndWritePark(park, "results" + File.separator + "resTest.txt", true);
	}
	
	public static void test1(String fileName, Boolean oppositeGCC) {
		for (int i = 2; i < 5; i ++) {
			System.out.println("Occurence " + i);
			writeToFile(fileName, "Occurence " + i + "\r\n");
			
			Park parkTest = createPark("park " + i, 2, new int[] {10,2}, 2, new int[] {i,2}, fileName);
			
			solveAndWritePark(parkTest, fileName, oppositeGCC);
        }
	}
	
	public static void test2(String fileName, int n, Boolean oppositeGCC) {
		int k = 1;
		int l = 1;
		float[][] times = new float[n][n];
		for (int i = 0; i < n * n; i ++) {
			String occurence = "Occurence " + i + ", " + k + "-" + l + "," + ((oppositeGCC) ? "oppositeGCC"  : "oppositeELE");
			System.out.println(occurence);
			writeToFile(fileName, occurence);
			
			Park park = createPark("park " + i, 2, new int[] {n,n}, 2, new int[] {k, l}, fileName);
			
			times[k - 1][l - 1] = solveAndWritePark(park, fileName, oppositeGCC);
			if (k == 1) {
				if (l == n) {
					k = n;
					l = 2;
				}
				else {
					k = l + 1;
					l = 1;
				}
			}
			else {
				if (l == n) {
					l = k + 1;
					k = n;
				}
				else {
					k --;
					l ++;
				}
			}
		}
		System.out.println(Arrays.deepToString(times));
		writeToFile(fileName, Arrays.deepToString(times).replace("], ", "],\n"));
		float totalTime = (float) 0.;
		for (float[] tt : times)
			for (float t : tt)
				totalTime += t;
		System.out.println("Total time : " + totalTime);
		writeToFile(fileName, "\nTotal time : " + totalTime);
	}
	
	public static String getFileNameTime() {
		Instant instant = Instant.now();
		ZoneId z = ZoneId.of( "Europe/Paris" );
		ZonedDateTime zdt = instant.atZone(z);
		String fileName = "results" + File.separator + "res" + zdt.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".txt";
		return fileName;
	}
	
	public static float solveAndWritePark(Park park, String fileName) {
		return solveAndWritePark(park, fileName, false);
	}
	
	public static float solveAndWritePark(Park park, String fileName, Boolean oppositeGCC) {
		Model model = new Model();
		
		Solution solution = App.solvePark(model, park, oppositeGCC);
		
		if(solution != null){
            for(var c:park.getCages()){
                int maxCard = c.eClass().getEReferences().getFirst().getUpperBound();
                int[] values = new int[maxCard];
                IntVar[] linkVar = App.cage2animal.get(c);
                for(int j=0;j<maxCard;j++){
                    values[j] = linkVar[j].getValue();
                    if(values[j]!=park.getAnimals().size()) ZooBuilder.putInCage(park.getAnimals().get(values[j]), c);
                }
            }
        }
		System.out.println("Zoo Config");
		writeToFile(fileName, "Zoo Config\r\n");
        for (Cage c : park.getCages()){
            System.out.println(c.getName());
            writeToFile(fileName, c.getName());
            for(Animal a : c.getAnimals()){
            	String out = a.getName()+" : "+park.getSpecs().get(App.animal2species.get(a)[0].getValue()).getName();
                System.out.println(out);
                writeToFile(fileName, out + "\r\n");
            }
        }
        model.getSolver().printShortStatistics();
        System.out.println("Time : " + model.getSolver().getTimeCount());
		writeToFile(fileName, "Time : " + model.getSolver().getTimeCount() + "\r\n");
		return model.getSolver().getTimeCount();
	}
	
	public static Park createPark(String name, int nbCages, int[] sizeCages, int nbSpecies, int[] nbAnimals, String fileName) {
		Park park = createPark(name, nbCages, sizeCages, nbSpecies, nbAnimals);
		writeToFile(fileName, name + " - capacity cages : " + Arrays.toString(sizeCages) + ", nbAnimals by species : " + Arrays.toString(nbAnimals) + "\r\n");
		return park;
	}
	
	public static Park createPark(String name, int nbCages, int[] sizeCages, int nbSpecies, int[] nbAnimals) {
		Park park = ZooBuilder.initPark(name);
		
		for (int iCage = 0; iCage < nbCages; iCage ++)
			ZooBuilder.makeCage("Cage " + iCage, sizeCages[iCage], park);
		
		for (int iSpecie = 0; iSpecie < nbSpecies; iSpecie ++) {
			Species specie = ZooBuilder.makeSpecies("Specie " + iSpecie, park);
			for (int iAnimal = 0; iAnimal < nbAnimals[iSpecie]; iAnimal ++)
				ZooBuilder.makeAnimal("AnimalS" + iSpecie + " " + iAnimal, specie, park);
		}
		return park;
	}
	
	public static void writeToFile(String fileName, String string) {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true), "utf-8"))) {
		    writer.write(string);
		    writer.close();
		}
		catch (IOException e){
	    	 System.err.println("Caught IOException: " + e.getMessage());
	    }
	}
	
}

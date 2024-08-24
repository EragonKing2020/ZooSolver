package mde;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtext.xbase.lib.Exceptions;

import java.util.List;

import org.chocosolver.solver.*;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import zoo.*; //This is the model we're trying to solve the problems for, it's designed in xcore

public class App {
    //Ecore Stuff
    static ZooFactory z = ZooFactory.eINSTANCE;

    static Park initPark(String name){
        Park out = z.createPark();
        out.setName(name);
        return out;
    }

    static Cage makeCage(String name, Park park){
        Cage out = z.createCage();
        out.setName(name);
        park.getCages().add(out);
        return out;
    }

    static Animal makeAnimal(String name, Park park){
        Animal out = z.createAnimal();
        out.setName(name);
        park.getAnimals().add(out);
        return out;
    }

    static void putInCage(Animal animal, Cage cage){
        cage.getAnimals().add(animal);
    }


    //Choco
    static IntVar[] makeLinkVar(Model m, int maxCard, int minCard, int numberOfTargets){ //,int data)
        int lb=0;
        int ub=numberOfTargets;
        if (minCard!=0) //get null pointer out of the domain
            ub--; //null pointer is 0 or numberOfTargets, depending on where you start counting
            // lb++; //it's one XOR the other
        return m.intVarArray(maxCard, lb,ub);
    }

    static void initLinkVar(IntVar[] linkvar, int[] data, int n){ //-1 is no data
        initLinkVar(linkvar, data, data, n);
    }

    static void initLinkVar(IntVar[] linkvar, int[] lb, int[] ub, int n){
        for(int i=0;i<n;i++){
            if(lb[i]!=-1 & ub[i]!=-1) try {
                linkvar[i].updateBounds(lb[i], ub[i], null);
            } catch (Exception e){}
        }
    }

    static void oppositeCSP(Model m, List<IntVar[]> a, List<IntVar[]> b){
        for(int i=0;i<a.size();i++){
            for(int j=0;j<b.size();j++){
                IntVar[] aa = a.get(i);
                IntVar[] bb = b.get(j);
                IntVar aaid = m.intVar(i); //I think consts use singletons, so doing it messy
                IntVar bbid = m.intVar(j);
                IntVar aapos = m.intVar(0,100); //here's the real mess, these are kinda dangling
                IntVar bbpos = m.intVar(0,100); //but it doesn't matter, the copies will all have the same values and they don't really play much of a role in propagation
                m.ifOnlyIf(m.element(bbid,aa,bbpos,0),m.element(aaid,bb,aapos,0));
            }

        }

    }

    static IntVar[] navCSP(Model m, IntVar[] source, List<IntVar[]> sources, int s, int ss, int lb, int ub){
        int sss = s*ss;
        IntVar[] out = m.intVarArray(sss,lb,ub);
        for(int i=0;i<sss;i++){
            //pointer
            //element
        }

        return out;
    }




    //ECore <-> Choco: OCL Environement
    public static void main(String[] args) {
        //Make a Zoo
        Park p = initPark("myZoo");
        ResourceSetImpl rs = new ResourceSetImpl();
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
        rs.getPackageRegistry().put(ZooPackage.eNS_URI,ZooPackage.eINSTANCE);
        Resource res = rs.createResource(URI.createFileURI("myZoo.xmi"));
        res.getContents().add(p);

        Cage lions = makeCage("Lions",p);
        Animal lea = makeAnimal("Lea",p);
        Animal leo = makeAnimal("Leo",p);
        putInCage(lea, lions);
        putInCage(leo, lions);

        // Cage capys = makeCage("Capybaras");
        // Animal clem = makeAnimal("Clem");
        try{
            res.save(null);
        } catch (Throwable _e) {
            throw Exceptions.sneakyThrow(_e);
        }

        //Load a Zoo
        res = rs.getResource(URI.createFileURI("myZoo.xmi"), true);
        Park p2 = (Park) res.getContents().get(0);
        
        // Add a new animal, but don't asign a cage
        Animal lou = makeAnimal("Lou", p2);
        for (Cage c : p2.getCages()){
            for(Animal a : c.getAnimals()){
                System.out.println(a.getName());
            }
        }
        // Lou isn't in the cage


        // Lets get Choco to put Lou in the Cage
        Model m = new Model();

        // Here we make our Orders of Objects, List<> provides indexOf
        List<Animal> csp_animals = p2.getAnimals(); 
        List<Cage> csp_cages = p2.getCages();

        // Lets make some variables for these Objects
        IntVar[] cage2lions = m.intVarArray("cage2lions",3, 0, csp_animals.size());
        try {
            cage2lions[0].updateBounds(0, 0, null); //we have data
            cage2lions[1].updateBounds(1, 1, null); //we have data
        } catch (Exception e){}

        IntVar leocage = m.intVar("leocage",csp_cages.indexOf(leo.getCage())); //we have data
        IntVar leacage = m.intVar("leacage",csp_cages.indexOf(lea.getCage())); //we have data
        IntVar loucage = m.intVar("loucage",0,1); //what cage does Lou go in ?! 



        //Opposite
        IntVar leocagepos = m.intVar(0, csp_animals.size());
        IntVar leoID = m.intVar(csp_animals.indexOf(leo));
        m.ifOnlyIf(m.arithm(leocage, "=", 0), m.element(leoID, cage2lions, leocagepos,0));

        IntVar leacagepos = m.intVar(0, csp_animals.size());
        IntVar leaID = m.intVar(csp_animals.indexOf(lea));
        m.ifOnlyIf(m.arithm(leacage, "=", 0), m.element(leaID, cage2lions, leacagepos,0));

        IntVar loucagepos = m.intVar(0, csp_animals.size());
        IntVar louID = m.intVar(csp_animals.indexOf(lou));
        m.ifOnlyIf(m.arithm(loucage, "=", 0), m.element(louID, cage2lions, loucagepos,0));





        Solution solution = m.getSolver().findSolution();
        if(solution != null){
            if(loucage.getValue() < csp_cages.size())
                lou.setCage(csp_cages.get(loucage.getValue()));
            System.out.println(solution.toString());
        }

        System.out.println("New Zoo Config");
        for (Cage c : p2.getCages()){
            for(Animal a : c.getAnimals()){
                System.out.println(a.getName());
            }
        }
    }
}
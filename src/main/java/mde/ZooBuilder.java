package mde;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtext.xbase.lib.Exceptions;

import zoo.Animal;
import zoo.Cage;
import zoo.Park;
import zoo.ZooFactory;
import zoo.ZooPackage;

public class ZooBuilder {

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
    }
}

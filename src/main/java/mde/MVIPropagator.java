package mde;

import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.ESat;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

public class MVIPropagator extends Propagator<IntVar>{
    IntVar x,y; //x implies y

    @Override
    public void propagate(int evtmask) throws ContradictionException {
        IntIterableRangeSet zero =  new IntIterableRangeSet(0);

        if(y.isInstantiatedTo(0)) x.removeAllValuesBut(zero, this);
        if(x.getLB()>0) y.updateLowerBound(1, this);
    }

    @Override
    public ESat isEntailed() {
        if(y.isInstantiatedTo(0) && x.getLB()>0) return ESat.FALSE;
        if(x.isInstantiatedTo(0)) return ESat.TRUE;
        return ESat.UNDEFINED;
    }

    public MVIPropagator(IntVar x, IntVar y){
        super(new IntVar[]{x,y});
        this.x = x;
        this.y = y;
    }
}

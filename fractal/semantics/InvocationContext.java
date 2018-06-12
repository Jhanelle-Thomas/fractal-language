/*
 * InvocationContext.java
 * Created on 29-Oct-2017, 11:52:40 PM
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fractal.semantics;

import fractal.values.Fractal;

/**
 * An invocation context holds all the information necessary for a fractal to
 * be rendered at a particular level and scale.  Most importantly, it contains
 * the current binding for the implicit reference of 'self'.  Fractals exhibit
 * the behaviour that they do by appropriately manipulating the instance of
 * InvocationContext that is returned from their deriveContext method.  
 * For example, the deriveContext method of PrimitiveFractal creates a new
 * context with itself as the fractal, the level one less than previously, and
 * the distance scaled by its scale factor.
 * @author newts
 */
public class InvocationContext {
    
    Fractal self;
    int level;
    double distance;

    public InvocationContext(Fractal f, int l, double d) {
        self = f;
        level = l;
        distance = d;
    }

    public Fractal getSelf() {
        return self;
    }

    public int getLevel() {
        return level;
    }

    public double getDistance() {
        return distance;
    }
    
}

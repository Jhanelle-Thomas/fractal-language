package fractal.semantics;

import fractal.sys.FractalUnboundException;
import java.util.*;
import fractal.values.FractalValue;

/**
 * An instance of class <code>Environment</code> maintains a
 * collection of bindings from valid identifiers to integers.
 * It supports storing and retrieving bindings, just as would
 * be expected in any frame.
 *
 * @author <a href="mailto:dcoore@uwimona.edu.jm">Daniel Coore</a>
 * @version 1.0
 */
public class Environment {

    HashMap<String, FractalValue> frame;	// the current frame
    Environment parent;		                // parent environment

    /**
     * Create a new (empty) top level Environment.
     *
     */
    public Environment() {
	frame = new HashMap<>();
	parent = null;
    }

    /**
     * Create a new environment with an empty first frame, inheriting the
     * bindings of the given environment.
     *
     * @param p The parent environment of the new environment
     */
    public Environment(Environment p) {
	frame = new HashMap<>();
	parent = p;
    }

    /**
     * Create a new <code>Environment</code> instance that is
     * initialised with the given collection of bindings
     * (presented as separate arrays of names and values).
     *
     * @param ids the collection of identifiers to be bound.
     * @param values the corresponding collection of values
     * for the identifiers.  Note that the two arrays must
     * have the same length.
     * @param parent The parent of this environment 
     */
    public Environment(String[] ids, FractalValue[] values, Environment parent) {
	frame = new HashMap<>();
	this.parent = parent;
	for (int i = 0; i < ids.length; i++) {
	    frame.put(ids[i], values[i]);
	}
    }
    
    /**
     * Create a new <code>Environment</code> instance that is
     * initialised with the given collection of bindings
     * (presented as separate lists of names and values).
     *
     * @param ids The list of identifiers to be bound
     * @param values The corresponding list of values to be bound
     * @param parent The environment being extended.
     */
    public Environment(ArrayList<String> ids, ArrayList<FractalValue> values, Environment parent) {
        frame = new HashMap<>();
        this.parent = parent;
        for (int i = 0; i < ids.size(); i++) {
            frame.put(ids.get(i), values.get(i));
        }
    }

    /**
     * Create an instance of a global environment suitable for
     * evaluating an program.
     *
     * @return the <code>Environment</code> created.
     */
    public static Environment makeGlobalEnv() {
	Environment result =  new Environment();
	// add definitions for any primitive procedures or
	// constants here
	return result;
    }

    /**
     * Store a binding for the given identifier to the given
     * int within this environment.
     *
     * @param id the name to be bound
     * @param value the value to which the name is bound.
     */
    public void put(String id, FractalValue value) {
	frame.put(id, value);
    }

    /**
     * Return the int associated with the given identifier.
     *
     * @param id the identifier.
     * @return the int associated with the identifier in
     * this environment.
     * @exception FractalUnboundException if <code>id</code> is unbound
     */
    public FractalValue get(String id) throws FractalUnboundException {
	FractalValue result = frame.get(id);
	if (result == null)
	    if (parent == null)
		throw new FractalUnboundException("Unbound variable " + id);
	    else
		return parent.get(id);
	else
	    return result;
    }

    /**
     * Create a string representation of this environment.
     *
     * @return a string of all the names bound in this environment.
     */
    @Override
    public String toString() {
	StringBuffer result = new StringBuffer();
        result.append("[");

        for (String key : frame.keySet()) {
            result = result.append(key);
            result.append(" ");
        }
        
        result.append("]");
	return result.toString();
    }
}

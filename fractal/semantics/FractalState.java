/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fractal.semantics;

import cs34q.turtle.Turtle;
import cs34q.turtle.TurtleDisplay;
import fractal.values.Fractal;
import fractal.values.FractalValue;
import java.util.ArrayList;
import java.util.Stack;

/**
 * The state for the PrimitiveFractal language.  It contains an environment and the
 * turtle state.
 * @author newts
 */
public class FractalState {
    public static final int INITIAL_DEFAULT_LEVEL = 8;
    public static final double INITIAL_DEFAULT_SCALE = 100.0;
    
    private final FractalState parent;
    private Environment environment;
    private Turtle turtleState;
    private TurtleDisplay display;
    private Stack<Turtle> turtleStack;
    // private FractalRenderingContext oldContext;
    private InvocationContext context;
    private int defaultLevel;
    private double defaultScale;
    
    public FractalState() {
        parent = null;
        environment = Environment.makeGlobalEnv();
        turtleState = new Turtle(0, 0, 0, true);
        turtleStack = new Stack<>();
        display = new TurtleDisplay();
        turtleState.addListener(display);
        context = null;
        defaultLevel = INITIAL_DEFAULT_LEVEL; 
        defaultScale = INITIAL_DEFAULT_SCALE; 
    }
    
    /**
     * Create a derived state that has a new invocation context containing
     * the given fractal at its level of approximation and scale
     * @param state The parent state of the new state
     * @param fractal The fractal referenced by 'self' in this context
     * @param level The (current) level of approximation of the fractal
     * @param scale The current scale at which the fractal is being rendered.
     */
    public FractalState(FractalState state, Fractal fractal, int level, double scale) {
        parent = state;
        context = new InvocationContext(fractal, level, scale);
    }

    /**
     * Create a derived state that differs from the given one only in its 
     * (fractal) invocation context.
     * @param state The parent state of this new state
     * @param context The invocation context to be used in this new state
     */
    
    public FractalState(FractalState state, InvocationContext context) {
        parent = state;
        this.context = context;
    }
    
    /**
     * Create a derived state that differs from the given one only in its 
     * environment.
     * @param state The parent state for this new state
     * @param env The environment that will be used by this state.
     */
    public FractalState(FractalState state, Environment env) {
        parent = state;
        environment = env;
    }
    
    /**
     * Create a new state with the given context as the current context.  The
     * old context is not lost, but rather shadowed by the given one.
     * @param context The current invocation context
     * @return The state that results from setting the given context as current.
     */    
    public FractalState extendContext(InvocationContext context) {
        return new FractalState(this, context);
    }

    /**
     *
     * @return The current invocation context of this state.
     */
    public InvocationContext getContext() {
        return context;
    }

    /**
     * Derive a new state from this one with an environment extended from
     * the current environment, containing the given bindings.
     * @param ids The identifiers of the new frame
     * @param vals The associated values of the new frame
     * @return The freshly created child state with the extended environment as 
     * its current environment
     */
    public FractalState extendEnvironment(ArrayList<String> ids, ArrayList<FractalValue> vals) {
        Environment newEnv = new Environment(ids, vals, getEnvironment());
        return new FractalState(this, newEnv);
    }
    
    /**
     * Derive a new state from this one with an environment extended from
     * the current environment, containing the given bindings.
     * @param ids The identifiers of the new frame
     * @param vals The associated values of the new frame
     * @return The freshly created child state with the extended environment as 
     * its current environment
     */
    public FractalState extendEnvironment(String[] ids, FractalValue[] vals) {
        Environment newEnv = new Environment(ids, vals, getEnvironment());
        return new FractalState(this, newEnv);
    }

    /**
     *
     * @return The current environment of this state.
     */
    public Environment getEnvironment() {
        Environment result;
        if (environment == null) {
            result = parent.getEnvironment();
            environment = result;
            return result;
        } else
            return environment;
    }
    
    /**
     * Retrieve the internal stack of turtle states used to support SAVE and 
     * RESTORE commands.
     * @return The stack of turtle states
     */
    protected Stack<Turtle> getTurtleStack() {
        Stack<Turtle> result;
        if (turtleStack == null) {
            result = parent.getTurtleStack();
            turtleStack = result;
            return result;
        } else
            return turtleStack;
    }

    public Turtle getTurtleState() {
        Turtle result;
        if (turtleState == null) {
            result = parent.getTurtleState();
            turtleState = result;
            return result;
        } else
            return turtleState;
    }

    public void setTurtleState(Turtle newState) {
//        Turtle oldState = turtleState;
//        double sx = oldState.getX();
//        double sy = oldState.getY();
//        double dx = newState.getX();
//        double dy = newState.getY();
//        if (oldState.isPenDown() && (sx != dx || sy != dy)) {
//            display.drawSegment(sx, sy, dx, dy);
//        }
//        this.turtleState = newState;
//        updateDisplay();
        if (turtleState == null) {
            parent.setTurtleState(newState);
            turtleState = newState;
        } else
            turtleState = newState;
    }
    
    protected void updateDisplay() {
        display.showTurtle(turtleState.getBearingInRads(), turtleState.getX(), 
                           turtleState.getY());
    }

    /**
     *
     * @return The display responsible for showing the trace of the turtle as 
     * it moves.
     */
    public TurtleDisplay getDisplay() {
        TurtleDisplay result;
        if (display == null) {
            result = parent.getDisplay();
            display = result;
            return result; 
        } else
            return display;
    }
    
    /**
     * Push a newly created clone of the current turtle state onto the internal
     * stack
     */
    public void pushTurtle() {
        Stack<Turtle> s = getTurtleStack();
        s.push(turtleState.deriveDisplaced(0));
//        if (turtleStack == null) {
//            turtleStack = parent.getTurtleStack();
//        }
//        turtleStack.push(turtleState.deriveDisplaced(0));
    }
    
    /**
     * Pop the turtle state that was last pushed from the stack and restore it
     * as the current state.
     */
    public void popTurtle() {
//        if (turtleStack == null) {
//            turtleStack = parent.getTurtleStack();
//        }
//        setTurtleState(turtleStack.pop());
        Stack<Turtle> s = getTurtleStack();
        setTurtleState(s.pop());
    }

    /**
     *
     * @return The current fractal being drawn (null if there is none).
     */
    public Fractal getCurrentFractal() {
        return context.getSelf();
    }
    
    public int getCurrentLevel() {
        if (context == null)
            return -1;
        else
            return context.getLevel();
    }
    
    public double getCurrentScale() {
        if (context == null)
            return 1;
        else
            return context.getDistance();
    }

    /**
     *
     * @return The current level of approximation to which fractals will be drawn
     */
    public int getDefaultLevel() {
        return defaultLevel;
    }

    /**
     * Set the current value of the level of fractal to be drawn.
     * @param currentLevel The value to set the current level to
     */
    public void setDefaultLevel(int currentLevel) {
        this.defaultLevel = currentLevel;
    }

    /**
     *
     * @return The current default scale for drawing fractals
     */
    public double getDefaultScale() {
        return defaultScale;
    }

    /**
     * Set the current default value of the scale factor for drawing fractals.
     * Used by the render command, if an explicit scale is not supplied.
     * @param currentScale The scale to use as the new default.
     */
    public void setDefaultScale(double currentScale) {
        this.defaultScale = currentScale;
    }

    @Override
    public String toString() {
        return "[" + environment + ", " + turtleState  + ", " + turtleStack + 
                ", " + context + "]";
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fractal.semantics;

import cs34q.turtle.TurtleDisplay;
import fractal.syntax.ASTProgram;
import fractal.syntax.ASTStmtSequence;
import fractal.sys.FractalException;
import fractal.values.Fractal;
import fractal.values.FractalValue;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JFrame;

/**
 * This class provides a useful starting point for implementing a FRACTAL
 * interpreter. It will set up a default window with a turtle display, and it
 * also provides an implementation of visitProgram that allows for its
 * internally maintained state to be used across invocations of visitProgram, so
 * that the state may persist across those calls. It can also be used so that an
 * externally prepared state is passed instead. This gives this class the
 * flexibility of being used as a stand-alone interpreter that interacts with a
 * command line, or a sub-system of a larger program that has the ability to
 * provide a prepared state (e.g. from restoring a previously saved state).
 *
 * @author newts
 */
public abstract class AbstractFractalEvaluator implements
        Visitor<FractalState, FractalValue> {

    private String interpID;
    private JFrame displayFrame;
    private FractalState persistentState;
    public static final int DEFAULT_WIDTH = 600;
    public static final int DEFAULT_HEIGHT = 600;

    public AbstractFractalEvaluator() {
        this("FRACTAL Display");
    }
    
    public AbstractFractalEvaluator(String name) {
        interpID = name;
        persistentState = new FractalState();
    }

    public AbstractFractalEvaluator(String name, int width, int height) {
        this(name);
//        TurtleDisplay display = persistentState.getDisplay();
//        display.setForeground(Color.BLACK);
//        display.setBackground(Color.WHITE);
//        display.clear();
//        display.setSize(width, height);
        _setFrame(_makeDefaultFrame(width, height));
    }
    
    
    
    /**
     * Set the dimensions of the Turtle display (and its containing frame).
     * @param width The desired width of the display.
     * @param height The desired height of the display.
     */
    public void setDisplaySize(int width, int height) {
        _setFrame(_makeDefaultFrame(width, height));
    }
    
    /**
     * Set the foreground colour of the Turtle display.
     * @param col The foreground colour to be used.
     */
    public void setDisplayForeground(Color col) {
        persistentState.getDisplay().setForeground(col);
    }
    
    /**
     * Set the background colour of the Turtle display.
     * @param col The background colour to be used.
     */
    public void setDisplayBackground(Color col) {
        TurtleDisplay display = persistentState.getDisplay();
        display.setBackground(col);
        display.clear();
    }

    private void _setFrame(JFrame frame) {
        displayFrame = frame;
        TurtleDisplay display = persistentState.getDisplay();
        displayFrame.add(display);
        displayFrame.setVisible(true);
        try {
            Thread.sleep(500);
            while (!display.isDisplayable()) {
                System.out.println("Waiting for Display to be visible...");
                Thread.sleep(1000);
            }
        } catch (InterruptedException ie) {
            System.out.println("Interrupted while in sleep");
        }
    }

    public void setFrame(JFrame frame) {
        _setFrame(frame);
    }

    private JFrame _makeDefaultFrame(int width, int height) {
        JFrame frame = new JFrame(interpID);
        frame.setSize(width, height);
        frame.getContentPane().setLayout(new GridLayout(1, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }

    public JFrame makeDefaultFrame(int width, int height) {
        return _makeDefaultFrame(width, height);
    }

    /**
     *
     * @return The persistent state associated with this interpreter. It is
     * initialised upon creation of this instance of the interpreter, and it is
     * used as the starting state for each call from the interpreter shell
     * (REPL).
     */
    public FractalState getPersistentState() {
        return persistentState;
    }

    /**
     * Evaluate a Fractal program with respect to the given state. If null is
     * passed as the state, then the interpreter's persistent state will be
     * used. This permits programs to be supplied incrementally.
     *
     * @param form The program to be evaluated
     * @param state The top level context for that evaluation. If null is
     * passed, then the interpreter's internally maintained persistent state
     * will be used. If a non-null state is provided, then the evaluation of the
     * program will not affect the persistent state.
     * @return The result of evaluating the program.
     * @throws FractalException if there is a problem in evaluating the program
     */
    @Override
    public FractalValue visitFractalProgram(ASTProgram form, FractalState state) 
            throws FractalException {
        ASTStmtSequence body = form.getStatements();
        if (state == null) {
            return body.visit(this, persistentState);
        } else {
            return body.visit(this, state);
        }
    }
    
    /**
     * Internal procedure to interpret the current InvocationContext instance
     * when rendering a fractal (either from a direct call to render or a 
     * reference to self, or an internally generated invocation).
     * @param fractal The fractal being invoked
     * @param state The current state at the time of the invocation
     * @return The result of the invocation
     * @throws FractalException if a problem arises while evaluating the fractal
     */
    protected FractalValue invokeFractal(Fractal fractal, FractalState state) 
            throws FractalException {
        InvocationContext context = state.getContext();
        if (context.getLevel() == 0 || context.getDistance() < 2) {
            return displaceTurtle(state, context.getDistance());
        } else {
            return fractal.getBody().visit(this, state.extendContext(fractal.deriveContext(context)));
        }
    }
    
    /**
     * Internal procedure to move the turtle of the given state by the given
     * distance
     *
     * @param state The state to be mutated
     * @param dist The distance to move the turtle
     * @return The result of moving the turtle
     */
    protected FractalValue displaceTurtle(FractalState state, double dist) {
        state.getTurtleState().displace(dist);
        return FractalValue.NO_VALUE;
    }
}

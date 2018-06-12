/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fractal.syntax;

import fractal.semantics.Visitor;
import fractal.sys.FractalException;

/**
 * The ASTNode subclass representing a REPEAT statement.
 * @author newts
 */
public class ASTRepeat extends ASTStatement {
    
    public String loopVar;
    public ASTExp countExp;
    public ASTStmtSequence body;
    
    /**
     * Create a new instance of the Repeat command from a given count expression
     * and statement sequence for its body.
     * @param lv The name of the loop counter variable
     * @param count The expression determining the number of repetitions
     * @param body The sequence of statements to be repeated.
     */
    public ASTRepeat(String lv, ASTExp count, ASTStmtSequence body) {
        loopVar = lv;
        countExp = count;
        this.body = body;
    }
    
    /**
     * 
     * @return The name of the loop counter
     */
    public String getLoopVar() {
        return loopVar;
    }

    /**
     *
     * @return The expression determining the number of repetitions
     */
    public ASTExp getCountExp() {
        return countExp;
    }

    /**
     *
     * @return The sequence of statements defining the statements to be repeated.
     */
    public ASTStmtSequence getBody() {
        return body;
    }

    @Override
    public <S, T> T visit(Visitor<S, T> v, S state) throws FractalException {
        return v.visitASTRepeat(this, state);
    }
    
}

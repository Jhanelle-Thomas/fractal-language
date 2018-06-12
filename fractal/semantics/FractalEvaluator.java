/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fractal.semantics;

import java.util.*;
import fractal.values.Fractal;
import fractal.values.PrimitiveFractal;
import fractal.syntax.ASTFracExp;
import fractal.syntax.ASTStatement;

import fractal.syntax.ASTDefine;
import fractal.syntax.ASTExpAdd;
import fractal.syntax.ASTExpDiv;
import fractal.syntax.ASTExpLit;
import fractal.syntax.ASTExpMod;
import fractal.syntax.ASTExpMul;
import fractal.syntax.ASTExpSub;
import fractal.syntax.ASTExpVar;
import fractal.syntax.ASTFracCompose;
import fractal.syntax.ASTFracInvocation;
import fractal.syntax.ASTFracSequence;
import fractal.syntax.ASTFracVar;
import fractal.syntax.ASTFractal;
import fractal.syntax.ASTRender;
import fractal.syntax.ASTRepeat;
import fractal.syntax.ASTRestoreStmt;
import fractal.syntax.ASTSaveStmt;
import fractal.syntax.ASTSelf;
import fractal.syntax.ASTStmtSequence;
import fractal.syntax.ASTTCmdBack;
import fractal.syntax.ASTTCmdClear;
import fractal.syntax.ASTTCmdForward;
import fractal.syntax.ASTTCmdHome;
import fractal.syntax.ASTTCmdLeft;
import fractal.syntax.ASTTCmdPenDown;
import fractal.syntax.ASTTCmdPenUp;
import fractal.syntax.ASTTCmdRight;
import fractal.sys.FractalException;
import fractal.values.FractalValue;

/**
 *
 * @author newts
 */
public class FractalEvaluator extends AbstractFractalEvaluator {

    @Override
    public FractalValue visitASTStmtSequence(ASTStmtSequence seq, FractalState state) throws FractalException {
        ASTStatement s;
        ArrayList<ASTStatement> stats =  seq.getSeq();
        Iterator iter = stats.iterator();
        FractalValue result = FractalValue.NO_VALUE;

        while(iter.hasNext()){
          s = (ASTStatement) iter.next();
          result = s.visit(this, state);
        }

        return result;
    }

    @Override
    public FractalValue visitASTSaveStmt(ASTSaveStmt form, FractalState state) throws FractalException {
        state.pushTurtle();
        return FractalValue.NO_VALUE;
    }

    @Override
    public FractalValue visitASTRestoreStmt(ASTRestoreStmt form, FractalState state) throws FractalException {
        state.popTurtle();
        return FractalValue.NO_VALUE;
    }

    @Override
    public FractalValue visitASTRender(ASTRender form, FractalState state) throws FractalException {
        FractalValue f = form.getFractal().visit(this, state);

        int l;
        if (form.getLevel() != null)
          l = form.getLevel().visit(this,state).intValue();
        else
          l = -1;

        double s = form.getScale().visit(this,state).realValue();

        InvocationContext con = new InvocationContext(f.fractalValue(), l, s);
        FractalState ns = state.extendContext(con);
        invokeFractal(f.fractalValue(), ns);
        return FractalValue.NO_VALUE;
    }

    @Override
    public FractalValue visitASTDefine(ASTDefine form, FractalState state) throws FractalException {
        Environment env = state.getEnvironment();
        env.put(form.getVar(), form.getValueExp().visit(this, state));
        return FractalValue.NO_VALUE;
    }

    @Override
    public FractalValue visitASTRepeat(ASTRepeat form, FractalState state) throws FractalException {
	      int reps = form.getCountExp().visit(this, state).intValue();
        Environment env = state.getEnvironment();
        String v = form.getLoopVar();
        FractalValue result = FractalValue.NO_VALUE;


        for(int i=0; i < reps; i++){
          if(v != null && !v.equals(""))
            env.put(v, FractalValue.make(i));

          result = form.getBody().visit(this, state);
        }
        return result;
    }

    @Override
    public FractalValue visitASTFracInvocation(ASTFracInvocation form, FractalState state) throws FractalException {
        Fractal f = form.getFractal();
        invokeFractal(f, state);
        return FractalValue.NO_VALUE;
    }

    @Override
    public FractalValue visitASTFracSequence(ASTFracSequence form, FractalState state) throws FractalException {
	// Create and return an instance of SequencedFractal here
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FractalValue visitASTFracCompose(ASTFracCompose form, FractalState state) throws FractalException {
	// Create and return an instance of CompositeFractal here
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FractalValue visitASTFracVar(ASTFracVar form, FractalState state) throws FractalException {
        Environment env = state.getEnvironment();
        FractalValue v = env.get(form.getVar());
        return v;
    }

    @Override
    public FractalValue visitASTFractal(ASTFractal form, FractalState state) throws FractalException {
	     // Create and return an instance of a PrimitiveFractal here
        double exp = form.getExp().visit(this,state).realValue();
        ArrayList<ASTStatement> body = form.getBody();

        PrimitiveFractal res = new PrimitiveFractal(exp,body,state);
        return res;
    }

    @Override
    public FractalValue visitASTSelf(ASTSelf form, FractalState state) throws FractalException {
        Fractal f = state.getCurrentFractal();
        int l = state.getCurrentLevel();
        double d = state.getCurrentScale();
        InvocationContext con = new InvocationContext(f, l, d);
        FractalState ns = state.extendContext(con);
        invokeFractal(f, ns);
        return FractalValue.NO_VALUE;
    }

    @Override
    public FractalValue visitASTTCmdLeft(ASTTCmdLeft form, FractalState state) throws FractalException {
        double v = form.getAngle().visit(this, state).realValue();
        state.getTurtleState().turn(v);
        return FractalValue.NO_VALUE;
    }

    @Override
    public FractalValue visitASTTCmdRight(ASTTCmdRight form, FractalState state) throws FractalException {
        double v = -form.getAngle().visit(this, state).realValue();
        state.getTurtleState().turn(v);
        return FractalValue.NO_VALUE;
    }

    @Override
    public FractalValue visitASTTCmdForward(ASTTCmdForward form, FractalState state) throws FractalException {
        double v = form.getLength().visit(this, state).realValue();
        double sf = state.getCurrentScale();
        displaceTurtle(state, v * sf);
        return FractalValue.NO_VALUE;
    }

    @Override
    public FractalValue visitASTTCmdBack(ASTTCmdBack form, FractalState state) throws FractalException {
        double v = -form.getLength().visit(this, state).realValue();
        double sf = state.getCurrentScale();
        displaceTurtle(state, v * sf);
        return FractalValue.NO_VALUE;
    }

    @Override
    public FractalValue visitASTTCmdPenDown(ASTTCmdPenDown form, FractalState state) throws FractalException {
        state.getTurtleState().setPenDown(true);
        return FractalValue.NO_VALUE;
    }

    @Override
    public FractalValue visitASTTCmdPenUp(ASTTCmdPenUp form, FractalState state) throws FractalException {
        state.getTurtleState().setPenDown(false);
        return FractalValue.NO_VALUE;
    }

    @Override
    public FractalValue visitASTTCmdClear(ASTTCmdClear form, FractalState state) throws FractalException {
        state.getDisplay().clear();
        return FractalValue.NO_VALUE;
    }

    @Override
    public FractalValue visitASTTCmdHome(ASTTCmdHome form, FractalState state) throws FractalException {
        state.getTurtleState().home();
        return FractalValue.NO_VALUE;
    }

    @Override
    public FractalValue visitASTExpAdd(ASTExpAdd form, FractalState state) throws FractalException {
        FractalValue v1, v2;

        v1 = form.getFirst().visit(this, state);
        v2 = form.getSecond().visit(this, state);

        if (v1.isNumber() && v2.isNumber()) {
            return v1.add(v2);
        } else
            throw new UnsupportedOperationException("Crash and Burn!!! :D");
    }

    @Override
    public FractalValue visitASTExpSub(ASTExpSub form, FractalState state) throws FractalException {
        FractalValue v1, v2;

        v1 = form.getFirst().visit(this, state);
        v2 = form.getSecond().visit(this, state);

        if (v1.isNumber() && v2.isNumber()) {
            return v1.sub(v2);
        } else
            throw new UnsupportedOperationException("Crash and Burn!!! :D");
    }

    @Override
    public FractalValue visitASTExpMul(ASTExpMul form, FractalState state) throws FractalException {
        FractalValue v1, v2;

        v1 = form.getFirst().visit(this, state);
        v2 = form.getSecond().visit(this, state);

        if (v1.isNumber() && v2.isNumber()) {
            return v1.mul(v2);
        } else
            throw new UnsupportedOperationException("Crash and Burn!!! :D");
    }

    @Override
    public FractalValue visitASTExpDiv(ASTExpDiv form, FractalState state) throws FractalException {
        FractalValue v1, v2;

        v1 = form.getFirst().visit(this, state);
        v2 = form.getSecond().visit(this, state);

        if (v1.isNumber() && v2.isNumber()) {
            return v1.div(v2);
        } else
            throw new UnsupportedOperationException("Crash and Burn!!! :D");
    }

    @Override
    public FractalValue visitASTExpMod(ASTExpMod form, FractalState state) throws FractalException {
        FractalValue v1, v2;

        v1 = form.getFirst().visit(this, state);
        v2 = form.getSecond().visit(this, state);

        if (v1.isNumber() && v2.isNumber()) {
            return v1.mod(v2);
        } else
            throw new UnsupportedOperationException("Crash and Burn!!! :D");
    }

    @Override
    public FractalValue visitASTExpLit(ASTExpLit form, FractalState state) throws FractalException {
        return form.getValue();
    }

    @Override
    public FractalValue visitASTExpVar(ASTExpVar form, FractalState state) throws FractalException {
        Environment env = state.getEnvironment();
        FractalValue v = env.get(form.getVar());
        return v;
    }

}

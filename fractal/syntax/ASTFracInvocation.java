/*
 * ASTFracInvocation.java
 * Created on 31-Oct-2017, 1:13:33 PM
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fractal.syntax;

import fractal.semantics.Visitor;
import fractal.sys.FractalException;
import fractal.values.Fractal;

/**
 * An ASTFracInvocation
 * @author newts
 */
public class ASTFracInvocation extends ASTFracExp {
    
    Fractal fractal;
    
    public ASTFracInvocation(Fractal f) {
        fractal = f;
    }

    public Fractal getFractal() {
        return fractal;
    }
    
    @Override
    public <S, T> T visit(Visitor<S, T> v, S state) throws FractalException {
        return v.visitASTFracInvocation(this, state);
    }
    

}

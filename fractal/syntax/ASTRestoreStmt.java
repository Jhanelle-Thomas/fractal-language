package fractal.syntax;

import fractal.semantics.Visitor;
import fractal.sys.FractalException;

/**
  Class ASTRestoreStmt.
  Intermediate representation class autogenerated by CS34Q semantic generator.
  Created on Sat Oct 12 03:13:16 2013
*/
public class ASTRestoreStmt extends ASTStatement {

  public ASTRestoreStmt () {
  }

  @Override
  public <S, T> T visit(Visitor<S, T> v, S state) throws FractalException {
    return v.visitASTRestoreStmt(this, state);
  }

}

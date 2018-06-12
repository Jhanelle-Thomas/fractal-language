package fractal.syntax;

import fractal.semantics.Visitor;
import fractal.sys.FractalException;

/**
  Class ASTExpMod.
  Intermediate representation class autogenerated by CS34Q semantic generator.
  Created on Sat Oct 12 03:13:16 2013
*/
public class ASTExpMod extends ASTExp {
  ASTExp first;
  ASTExp second;

  public ASTExpMod (ASTExp first,ASTExp second) {
    this.first = first;
    this.second = second;
  }

  public ASTExp getFirst() {
    return first;
  }

  public ASTExp getSecond() {
    return second;
  }

  @Override
  public <S, T> T visit(Visitor<S, T> v, S state) throws FractalException {
    return v.visitASTExpMod(this, state);
  }

}

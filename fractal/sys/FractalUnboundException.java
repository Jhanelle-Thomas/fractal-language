/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fractal.sys;

import fractal.syntax.ASTNode;

/**
 *
 * @author newts
 */
public class FractalUnboundException extends FractalException {
    private static final long serialVersionUID = 1L;

    public FractalUnboundException(String message) {
        super(message);
    }

    public FractalUnboundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FractalUnboundException(ASTNode node, String message) {
        super(node, message);
    }

    public FractalUnboundException(ASTNode node, String message, Throwable cause) {
        super(node, message, cause);
    }
    
    public FractalUnboundException(ASTNode node) {
        super(node, "Unbound variable " + node);
    }
}

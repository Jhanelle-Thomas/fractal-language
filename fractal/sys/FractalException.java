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
public class FractalException extends Exception {
    private static final long serialVersionUID = 1L;
    
    private ASTNode source;
    
    public FractalException() {
        super();
    }
    
    public FractalException(String message) {
        super (message);
    }
    
    public FractalException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public FractalException(ASTNode node, String message) {
        super(message);
        source = node;
    }
    
    public FractalException(ASTNode node, String message, Throwable cause) {
        super(message, cause);
        source = node;
    }
    
    public ASTNode getSource() {
        return source;
    }
    
    protected void setSource(ASTNode src) {
        source = src;
    }
}

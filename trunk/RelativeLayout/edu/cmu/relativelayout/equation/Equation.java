package edu.cmu.relativelayout.equation;

import java.util.List;

/**
 * This is the interface all classes that support equations need to implement.
 * 
 * @author Rachael Bennett (srbennett@gmail.com)
 * @version 0.1
 */
public interface Equation {
  /**
   * Gets the coefficient of the variable specified in the parameter.
   * 
   * @param v the variable to get the coefficient of
   */
  public double getCoefficient(Variable v);

  /**
   * The right hand side of this equation, when all variables are on the left.
   */
  public double getRightHandSide();

  /**
   * Returns a list of all the variables used in this equation.
   */
  public List<Variable> getVariables();

}
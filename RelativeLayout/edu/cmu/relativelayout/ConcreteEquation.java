/**
 * ConcreteEquation.java<br>
 * Contains class ConcreteEquation.
 */
package edu.cmu.relativelayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.cmu.relativelayout.equation.Equation;
import edu.cmu.relativelayout.equation.Variable;

/**
 * A simple concrete implementation of the Equation interface.
 * 
 * @author Brian Ellis (phoenix1701@gmail.com)
 */
class ConcreteEquation implements Equation {

  /*
   * (non-Javadoc)
   * 
   * @see edu.cmu.relativelayout.equation.Equation#getCoefficient(edu.cmu.relativelayout.equation.Variable)
   */
  public double getCoefficient(Variable theV) {
    return this.values.get(theV);
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.cmu.relativelayout.equation.Equation#getRightHandSide()
   */
  public double getRightHandSide() {
    return this.rightHandSide;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.cmu.relativelayout.equation.Equation#getVariables()
   */
  public List<Variable> getVariables() {
    return new ArrayList<Variable>(this.values.keySet());
  }

  /**
   * Sets the coefficient for the given variable to the given value. If the variable was not previously in the equation,
   * this method adds it to the equation. Note that setting a coefficient to zero is NOT the same as removing the
   * variable!
   * 
   * @param coefficient The coefficient for this variable.
   * @param v The variable for this coefficient.
   */
  public void setCoefficient(double coefficient, Variable v) {
    this.values.put(v, coefficient);
  }

  /**
   * Sets the right hand side of the equation to this value.
   */
  public void setRightHandSide(double rhs) {
    this.rightHandSide = rhs;
  }

  @Override
  public String toString() {
    String ret = "";
    Iterator<Variable> iter = this.getVariables().iterator();

    while (iter.hasNext()) {
      Variable element = iter.next();
      ret += "(" + this.getCoefficient(element) + " x " + element.getName() + ")";
      if (iter.hasNext()) {
        ret += " + ";
      }
    }

    ret += " = " + this.getRightHandSide();

    return ret;
  }

  /**
   * Stores the equation's rhs.
   */
  private double rightHandSide;

  /**
   * Stores the coefficients for each variable in this equation.
   */
  private HashMap<Variable, Double> values = new HashMap<Variable, Double>();
}

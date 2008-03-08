package edu.cmu.relativelayout;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.cmu.relativelayout.equation.Equation;
import edu.cmu.relativelayout.equation.Variable;

/**
 * A constraint object that associates {@link Binding}s with a particular component to be bound.
 * 
 * @author Brian Ellis (phoenix1701@gmail.com)
 */
public class RelativeConstraints {

  /**
   * Constructor for RelativeConstraints.
   */
  public RelativeConstraints() {
  }

  /**
   * Constructor for RelativeConstraints. Takes a list of {@link Binding}s that will be immediately added to the
   * constraints.
   * 
   * @param theBindings
   */
  public RelativeConstraints(Binding... theBindings) {
    this.addBindings(theBindings);
  }

  /**
   * Adds an {@link Binding} to this constraint.
   * 
   * @param theBinding The Binding to add.
   */
  public void addBinding(Binding theBinding) {
    this.bindings.add((Binding) theBinding.clone());
  }

  /**
   * Adds all of the specified {@link Binding}s to this constraint.
   * 
   * @param theBindings The Bindings to add.
   */
  public void addBindings(Binding... theBindings) {
    for (Binding binding : theBindings) {
      this.addBinding(binding);
    }
  }

  @Override
  public String toString() {
    return "RelativeConstraints: " + this.bindings.toString();
  }

  /**
   * Returns a list of all the equations associated with this constraint. Note that if no component has been specified
   * using setConstrainedObject when this method is called, the list will be empty because no equations can be resolved
   * until all their variables are bound.
   * 
   * @return A list of all equations associated with the constraint.
   */
  List<Equation> getEquations() {
    List<Equation> ret = new LinkedList<Equation>();
    for (Iterator<Binding> iter = this.bindings.iterator(); iter.hasNext();) {
      Binding element = iter.next();
      ret.add(element.getEquation());
    }
    return ret;
  }

  /**
   * Returns a list of all the variables associated with this constraint. Note that if no component has been specified
   * using setConstrainedObject when this method is called, the list will be empty or incomplete because unbound
   * variables cannot be resolved.
   * 
   * @return A list of all variables associated with the constraint.
   */
  List<Variable> getVariables() {
    List<Variable> ret = new LinkedList<Variable>();
    for (Iterator<Binding> iter = this.bindings.iterator(); iter.hasNext();) {
      Binding element = iter.next();
      ret.add(element.getPrimaryVariable());
    }
    return ret;
  }

  /**
   * Sets the component for all {@link RelativePosition}s in this constraint. If some Anchors contradict others, an
   * exception will be thrown.
   * 
   * @param object The component to use.
   * @throws InconsistentConstraintException if any two Anchors supply contradictory information about the positioning
   *           of the component.
   */
  void setConstrainedObject(Component object) throws InconsistentConstraintException {
    HashMap<Variable, Binding> knownVars = new HashMap<Variable, Binding>();
    for (Iterator<Binding> iter = this.bindings.iterator(); iter.hasNext();) {
      Binding thisBinding = iter.next();
      thisBinding.setComponent(object);

      // Check for invalid bindings:
      if (!thisBinding.isValid()) {
        throw new InvalidBindingException(thisBinding);
      }

      // Check for variable conflicts:
      if (knownVars.containsKey(thisBinding.getPrimaryVariable())) {
        // System.out.println("Conflict: " + element + " and " + knownVars.get(element.getPrimaryVariable()) + " both
        // claim variable " + element.getPrimaryVariable() + ".");
        Binding conflictingBinding = knownVars.get(thisBinding.getPrimaryVariable());
        if (thisBinding.hasDimensionalVariable() && !thisBinding.usesDimensionalVariable()) {
          // System.out.println("Telling " + element + " to use dimensional variable instead.");
          thisBinding.setUsesDimensionalVariable(true);
          if (knownVars.containsKey(thisBinding.getPrimaryVariable())) {
            // System.out.println("There is still a conflict!");
            conflictingBinding = knownVars.get(thisBinding.getPrimaryVariable());
            throw new InconsistentConstraintException(thisBinding, conflictingBinding);
          }
        } else if (conflictingBinding.hasDimensionalVariable() && !conflictingBinding.usesDimensionalVariable()) {
          // System.out.println("Telling " + otherAnchor + " to use dimensional variable instead.");
          conflictingBinding.setUsesDimensionalVariable(true);
          if (knownVars.containsKey(conflictingBinding.getPrimaryVariable())) {
            // System.out.println("There is still a conflict!");
            thisBinding = conflictingBinding;
            conflictingBinding = knownVars.get(thisBinding.getPrimaryVariable());
            throw new InconsistentConstraintException(thisBinding, conflictingBinding);
          }
        } else {
          throw new InconsistentConstraintException(thisBinding, conflictingBinding);
        }
      }
      knownVars.put(thisBinding.getPrimaryVariable(), thisBinding);
    }
  }

  /**
   * The {@link Binding}s that make up this constraint.
   */
  List<Binding> bindings = new ArrayList<Binding>();

}

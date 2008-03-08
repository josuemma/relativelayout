/**
 * RelativeVariable.java<br>
 * Created Dec 11, 2006 by phoenix<br>
 * Contains class: RelativeVariable
 */
package edu.cmu.relativelayout;

import java.awt.Component;
import java.util.HashMap;

import edu.cmu.relativelayout.equation.Variable;

/**
 * A subclass of Variable that uses a Component and a VariableType to determine its identity instead of a String. Note
 * that if you happen to create a Variable that has the exact same name as would be given to a RelativeVariable, the two
 * Variables will *not* be considered the same Variable.
 * 
 * @author Brian Ellis (phoenix1701@gmail.com)
 */
class RelativeVariable extends Variable {

  /**
   * A map of all known RelativeVariables by name.
   */
  private static HashMap<String, Variable> map = new HashMap<String, Variable>();

  /**
   * Gets a Variable for given component and VariableType. Calling get multiple times with the same component and
   * VariableType is guaranteed to result in the same object being returned each time.
   * 
   * @param theComponent The component to associate with this Variable.
   * @param theType The VariableType to associate with this Variable.
   * @return A RelativeVariable associated with the given component and VariableType.
   */
  public static Variable get(Component theComponent, VariableType theType) {
    String componentName = theComponent.getClass().getName() + "@" + String.format("0x%h", theComponent.hashCode());
    String variableName = componentName + "." + theType.toString();
    if (RelativeVariable.map.containsKey(variableName)) {
      return RelativeVariable.map.get(variableName);
    } else {
      RelativeVariable v = new RelativeVariable(variableName);
      v.component = theComponent;
      v.variableType = theType;
      RelativeVariable.map.put(variableName, v);
      return v;
    }
  }

  /**
   * Private constructor. Calls the superclass constructor.
   */
  private RelativeVariable(String name) {
    super(name);
  }

  /**
   * @return The component associated with this Variable.
   */
  public Component getComponent() {
    return this.component;
  }

  /**
   * @return The VariableType of this Variable.
   */
  public VariableType getVariableType() {
    return this.variableType;
  }

  /**
   * The name of this RelativeVariable.
   */
  @SuppressWarnings("unused")
  private String name;

  /**
   * The VariableType of this Variable.
   */
  private VariableType variableType;

  /**
   * The component associated with this Variable.
   */
  private Component component;
}

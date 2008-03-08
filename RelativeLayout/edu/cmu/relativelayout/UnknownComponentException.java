/*
 * UnknownComponentException.java Contains class UnknownComponentException. Author: Administrator
 */
package edu.cmu.relativelayout;

import java.awt.Container;

/**
 * Thrown when a {@link Binding} in a layout that is being validated refers to a component that has not yet been added
 * to the layout by the time it is validated. Note that unless debug mode is turned on, layouts are validated as lazily
 * as possible, so it is perfectly safe to refer to a component in a {@link Binding} before it is added, as long as it
 * is added eventually.
 */
public class UnknownComponentException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  UnknownComponentException(RelativeVariable v, Container parent) {
    this.variable = v;
    this.parent = parent;
  }

  @Override
  public String getMessage() {
    return "A binding refers to the component " + this.variable.getComponent()
        + ", which has not been added to the parent container " + this.parent + ".";
  }

  private RelativeVariable variable;

  private Container parent;
}

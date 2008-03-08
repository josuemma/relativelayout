/**
 * Binding.java<br>
 * Created Dec 17, 2006 by phoenix<br>
 * Contains class: Binding
 */
package edu.cmu.relativelayout;

import java.awt.Component;
import java.awt.Container;

import edu.cmu.relativelayout.equation.Equation;
import edu.cmu.relativelayout.equation.Variable;

/**
 * A Binding represents a dependency of some component's position on the position either of another component or of the
 * surrounding container. To lay out a component at a particular place, construct one or more Bindings to tie that
 * component's position to that of other objects, and then add all the Bindings to a {@link RelativeConstraints} object,
 * with which you can then add the component to its parent. For example, to place a JButton 12 pixels below and to the
 * right of the upper right corner of its containing JFrame:
 * 
 * <pre>
 * JFrame frame = new JFrame();
 * frame.setLayout(new {@link RelativeLayout}());
 *         
 * JButton button = new JButton(&quot;Hello&quot;);
 * 
 * // Create two Bindings, one for the button's left edge and the other for its top edge:
 * Binding buttonLeft = new Binding({@link Edge}.LEFT, 12, {@link Direction}.RIGHT, {@link Edge}.LEFT, frame);
 * Binding buttonTop = new Binding( {@link Edge}.TOP, 12, {@link Direction}.BELOW, {@link Edge}.TOP, frame);
 * 
 * // Now, create a RelativeConstraints object and add the bindings to it:
 * {@link RelativeConstraints} buttonConstraints = new {@link RelativeConstraints}();
 * buttonConstraints.addBindings(buttonLeft, buttonTop);
 * 
 * // Lastly, add the button to the frame with the constraints:
 * frame.add(button, buttonConstraints);
 * 
 * frame.setVisible(true);
 * </pre>
 * 
 * You can think of each Binding like an English sentence: the <code>buttonLeft</code> binding in the example above
 * would be read, "my left edge is 12 pixels to the right of the left edge of <code>frame</code>".<br>
 * <br>
 * It is important to keep in mind that you can define Bindings between components and other components as well as
 * components and the enclosing frame. This allows you to easily do things like placing text fields to the right of
 * labels:
 * 
 * <pre>
 * Binding rightOfLabel = new Binding(Edge.LEFT, 8, Direction.RIGHT, Edge.RIGHT, label);
 * RelativeConstraints fieldConstraints = new RelativeConstraints();
 * fieldConstraints.addBinding(rightOfLabel);
 * frame.add(field, fieldConstraints);
 * </pre>
 * 
 * Bindings can also be used to make components dynamically resize themselves. To make a component resize horizontally,
 * simply make a Binding for the component's left edge and another for the component's right edge. For example:
 * 
 * <pre>
 * JFrame frame = new JFrame();
 * frame.setLayout(new {@link RelativeLayout}());
 * 
 * JTextArea area = new JTextArea(&quot;Hello, world!&quot;);
 * 
 * // Create three Bindings, one for the area's left edge, another for its top edge, and another for its right edge:
 * Binding areaLeft = new Binding({@link Edge}.LEFT, 12, {@link Direction}.RIGHT, {@link Edge}.LEFT, frame);
 * Binding areaTop = new Binding({@link Edge}.TOP, 12, {@link Direction}.BELOW, {@link Edge}.TOP, frame);
 * Binding areaRight = new Binding({@link Edge}.RIGHT, 12, {@link Direction}.LEFT, {@link Edge}.RIGHT, frame);
 * 
 * // Now, create a RelativeConstraints object and add the bindings to it:
 * {@link RelativeConstraints} areaConstraints = new {@link RelativeConstraints}();
 * areaConstraints.addBindings(areaLeft, areaTop, areaRight);
 * 
 * // Lastly, add the area to the frame with the constraints:
 * frame.add(area, areaConstraints);
 * 
 * frame.setVisible(true);
 * </pre>
 * 
 * The <code>area</code> component above will now always keep a 12 pixel margin on both the left and right, regardless
 * of how big the window is.<br>
 * <br>
 * Note that when using Bindings to make components dynamically resize, it is important which component is named in the
 * Binding constructor and which is added to the frame with the constraint; the "floating" component (the component
 * added to the frame with the constraint) will change size due to changes in the "fixed" component (the component named
 * in the Binding constructor), but <em>not</em> vice versa.
 * 
 * @author Brian Ellis (phoenix1701@gmail.com)
 */
public class Binding implements Cloneable {

  /**
   * A constant that represents whatever object contains the component associated with this binding.
   */
  public static final Component PARENT = null;

  /**
   * Constructs a Binding given the Edge of a "floating" component, a distance, a Direction, the Edge of a "fixed"
   * component, and the fixed component itself. The parameters are described below; for examples of use, see the class
   * documentation for {@link Binding}.
   * 
   * @param myEdge The edge of the "floating" component that is to be bound.
   * @param distance The distance from the bound edge of the floating component to the fixed edge of the fixed
   *          component, in pixels.
   * @param direction The direction of a line drawn from the fixed component's edge to the floating component's edge.
   * @param fixedEdge The edge of the fixed component to which <code>myEdge</code> of the floating component will be
   *          bound.
   * @param fixedComponent The component to which the floating component will be bound.
   */
  public Binding(Edge myEdge, int distance, Direction direction, Edge fixedEdge, Component fixedComponent) {
    RelativePosition r = new RelativePosition(myEdge, distance, direction, fixedEdge);
    this.relativePosition = r;
    this.fixedComponent = fixedComponent;
  }

  /**
   * Constructs a Binding given a {@link RelativePosition} and a fixed component to resolve that position against.
   * 
   * @param fixedComponent
   * @param relativePosition
   */
  public Binding(RelativePosition relativePosition, Component fixedComponent) {
    this.relativePosition = relativePosition;
    this.fixedComponent = fixedComponent;
  }

  /**
   * @see java.lang.Object#clone()
   */
  @Override
  public Object clone() {
    Binding ret = new Binding(this.relativePosition, this.fixedComponent);
    if (this.component != null) {
      ret.setComponent(this.component);
    }
    return ret;
  }

  /**
   * Returns the fixed component for this Binding.
   * 
   * @return the value of fixedComponent
   */
  public Component getFixedComponent() {
    return this.fixedComponent;
  }

  /**
   * Returns a {@link RelativePosition} object describing the position in this Binding. This may have been passed in
   * using the {@link Binding#Binding(RelativePosition, Component)} constructor, or may have been generated on the fly
   * in the {@link Binding#Binding(Edge, int, Direction, Edge, Component)} constructor.
   * 
   * @return the value of relativePosition
   */
  public RelativePosition getRelativePosition() {
    return this.relativePosition;
  }

  /**
   * Returns a boolean indicating whether this Binding is "valid"; that is, whether the relationship it describes is
   * possible.
   * 
   * @return <code>true</code> if the Binding is valid, <code>false</code> otherwise.
   * @see edu.cmu.relativelayout.RelativePosition#isValid()
   */
  public boolean isValid() {
    return this.relativePosition.isValid();
  }

  /**
   * Sets the fixed component associated with this Binding. Since Bindings are copied when they are added to a
   * {@link RelativeConstraints} object, you can change the fixed component of a Binding after adding it to a
   * {@link RelativeConstraints} object and reuse the Binding that way.
   * 
   * @param aFixedComponent The new fixed component.
   */
  public void setFixedComponent(Component aFixedComponent) {
    this.fixedComponent = aFixedComponent;
  }

  /**
   * Returns a human-readable String representation of this Binding. Useful for troubleshooting and testing.
   */
  @Override
  public String toString() {
    // We don't call toString on the component, because Swing components tend to have very chatty toString() methods
    // which would make it difficult to read the output. Instead we synthesize Object's toString() method, which just
    // returns the memory address of the object (also known as its hashCode).
    String componentName =
        (this.component != null ? this.component.getClass().getName() + "@"
            + String.format("0x%h", this.component.hashCode()) : "(undefined)");
    String fixedComponentName =
        (this.fixedComponent != null ? this.fixedComponent.getClass().getName() + "@"
            + String.format("0x%h", this.fixedComponent.hashCode()) : "(undefined)");
    String ret =
        this.relativePosition.getMyEdge() + " edge of " + componentName + " is " + this.relativePosition.getDistance()
            + " pixels " + this.relativePosition.getDirection() + " of " + this.relativePosition.getFixedEdge()
            + " edge of " + fixedComponentName;
    return ret;
  }

  /**
   * 
   * @return
   */
  Component getComponent() {
    return this.component;
  }

  /**
   * Returns the Equation for this Binding.
   */
  Equation getEquation() {
    return this.equation;
  }

  /**
   * Returns the primary Variable for this Binding.
   */
  Variable getPrimaryVariable() {
    return RelativeVariable.get(this.component, variableTypeForEdge(this.relativePosition.getMyEdge()));
  }

  /**
   * Returns true if this Binding's equation involves a dimensional variable, whether or not that variable is the
   * primary variable for this Binding.
   * 
   * @return true if this Binding has a dimensional variable, false otherwise
   */
  boolean hasDimensionalVariable() {
    return (this.relativePosition.getMyEdge() != Edge.LEFT && this.relativePosition.getMyEdge() != Edge.TOP);
  }

  boolean isHorizontal() {
    return (this.relativePosition.getDirection() == Direction.LEFT || this.relativePosition.getDirection() == Direction.RIGHT);
  }

  /**
   * Sets the "floating" component for this Binding. This sets up the equation.
   * 
   * @param component
   */
  void setComponent(Component component) {
    this.component = component;
    if (this.fixedComponent == null) {
      this.fixedComponent = this.component.getParent();
    }
    // Special case: they set the fixed object to an ancestor instead of the parent component, like setting it to a
    // frame instead of the frame's content pane:
    if (this.fixedComponent instanceof Container && ((Container) this.fixedComponent).isAncestorOf(this.component)) {
      this.fixedComponent = this.component.getParent();
    }
    this.setupEquation();
  }

  /**
   * Sets whether this Binding uses a dimensional variable. See usesDimensionalVariable() for more info about what this
   * means.
   * 
   * @param flag true if this Binding should use a dimensional variable, false otherwise.
   */
  void setUsesDimensionalVariable(boolean flag) {
    this.usesDimensionalVariable = flag;
  }

  /**
   * Returns true if this Binding is using a dimensional variable. The Binding is using a dimensional variable if the
   * equation for this Binding defines either the width or height (as opposed to x or y) of a component.
   * 
   * @return <code>true</code> if the Binding is using a dimensional variable, <code>false</code> otherwise.
   */
  boolean usesDimensionalVariable() {
    return this.usesDimensionalVariable;
  }

  /**
   * Sets up the equation for this Binding, after which it can be retrieved using getEquation().
   */
  private void setupEquation() {
    // Let's say we've got "my right side is ten pixels to the left of B's left side."
    // We want to represent that as Ax + Aw = Bx - 10, which is Ax + Aw - Bx = -10
    this.equation = new ConcreteEquation();

    Edge myEdge = this.relativePosition.getMyEdge();
    Edge fixedEdge = this.relativePosition.getFixedEdge();

    // Determine what variable we're specifying:
    VariableType myType = variableTypeForEdge(myEdge);

    this.equation.setCoefficient(1, RelativeVariable.get(this.component, myType));

    // Now we have Ax = 0;

    // If we're not working with top or left, we need to also specify width or height:
    if (myEdge == Edge.RIGHT) {
      this.equation.setCoefficient(1, RelativeVariable.get(this.component, VariableType.WIDTH));
    }
    if (myEdge == Edge.BOTTOM) {
      this.equation.setCoefficient(1, RelativeVariable.get(this.component, VariableType.HEIGHT));
    }
    if (myEdge == Edge.VERTICAL_CENTER) {
      this.equation.setCoefficient(0.5, RelativeVariable.get(this.component, VariableType.HEIGHT));
    }
    if (myEdge == Edge.HORIZONTAL_CENTER) {
      this.equation.setCoefficient(0.5, RelativeVariable.get(this.component, VariableType.WIDTH));
    }

    // So now we have Ax + Aw = 0.

    VariableType theirType = variableTypeForEdge(this.relativePosition.getFixedEdge());

    this.equation.setCoefficient(-1, RelativeVariable.get(this.fixedComponent, theirType));
    // If we're not working with top or left, we need to also specify width or height:
    if (fixedEdge == Edge.RIGHT) {
      this.equation.setCoefficient(-1, RelativeVariable.get(this.fixedComponent, VariableType.WIDTH));
    }
    if (fixedEdge == Edge.BOTTOM) {
      this.equation.setCoefficient(-1, RelativeVariable.get(this.fixedComponent, VariableType.HEIGHT));
    }
    if (fixedEdge == Edge.VERTICAL_CENTER) {
      this.equation.setCoefficient(-0.5, RelativeVariable.get(this.fixedComponent, VariableType.HEIGHT));
    }
    if (fixedEdge == Edge.HORIZONTAL_CENTER) {
      this.equation.setCoefficient(-0.5, RelativeVariable.get(this.fixedComponent, VariableType.WIDTH));
    }

    // So now we have Ax + Aw - Bx = 0.

    if (this.relativePosition.getDirection() == Direction.RIGHT
        || this.relativePosition.getDirection() == Direction.BELOW) {
      this.equation.setRightHandSide(this.relativePosition.getDistance());
    } else {
      this.equation.setRightHandSide(0 - this.relativePosition.getDistance());
    }

    // So now we have Ax + Aw - Bx = -10. QED.
  }

  /**
   * Returns the {@link VariableType} associated with a particular {@link Edge}, given the current state of this
   * Binding (i.e., whether it uses a dimensional variable).
   * 
   * @param theEdge The edge to get a VariableType for.
   * @return The VariableType for the given Edge.
   */
  private VariableType variableTypeForEdge(Edge theEdge) {
    VariableType theType = null;
    if (theEdge == Edge.LEFT) {
      theType = VariableType.X;
    } else if (theEdge == Edge.TOP) {
      theType = VariableType.Y;
    } else if (theEdge == Edge.RIGHT || theEdge == Edge.HORIZONTAL_CENTER) {
      theType = this.usesDimensionalVariable() ? VariableType.WIDTH : VariableType.X;
    } else if (theEdge == Edge.BOTTOM || theEdge == Edge.VERTICAL_CENTER) {
      theType = this.usesDimensionalVariable() ? VariableType.HEIGHT : VariableType.Y;
    } else {
      // We hit some other enum constant we didn't know about...?
      assert false;
    }
    return theType;
  }

  /**
   * The equation associated with this Binding. Will be null until setupEquation() is called.
   */
  private ConcreteEquation equation;

  /**
   * The {@link RelativePosition} object associated with this Binding.
   */
  private RelativePosition relativePosition;

  /**
   * The fixed component for this Binding. This will be null until set using setFixedComponent() (which happens when the
   * Binding is added to a constraint).
   */
  private Component fixedComponent;

  /**
   * The component object for this Binding. This will be null until the Binding is added to a constraint and the
   * constraint is added to a layout.
   */
  private Component component;

  /**
   * Indicates whether this Binding should use width/height instead of x/y as its "primary" variable. The ramifications
   * of this have to do with the matrix, since each variable is defined by one and only one equation. If we know
   * something about a component's X or Y values, we want the equation that tells us that to define the X or Y value,
   * even if that equation also involves the component's width or height. If, however, we get another equation that also
   * involves the component's X or Y but doesn't involve its width or height, we "demote" one of the equations to
   * specifying the width or height of the component instead of its X/Y position. This field determines whether this
   * Binding has been demoted in this way.
   */
  private boolean usesDimensionalVariable;
}

/**
 * 
 */
package edu.cmu.relativelayout;

/**
 * Represents the position of an edge at some offset from another edge. RelativePositions are designed to be reused in
 * multiple {@link Binding}s using the {@link Binding#Binding(RelativePosition, java.awt.Component)} constructor. If
 * you don't need reusability, you can also construct a {@link Binding} without having to create a RelativePosition by
 * using the {@link Binding#Binding(Edge, int, Direction, Edge, java.awt.Component)} constructor. <br>
 * <br>
 * RelativePositions are immutable; that is, once constructed, they cannot be changed.
 * 
 * @author Brian Ellis (phoenix1701@gmail.com)
 */
public class RelativePosition {

  /**
   * Constructs an RelativePosition with the given parameters. See
   * {@link Binding#Binding(Edge, int, Direction, Edge, java.awt.Component)} for more information.
   * 
   * @param myEdge The edge of a component that will be defined by this RelativePosition.
   * @param distance How far that edge will be from the edge of some fixed component, in pixels.
   * @param direction The direction the edge will be in from the edge of some fixed component.
   * @param fixedEdge The edge of some fixed component from which the distance should be measured.
   * @see Binding#Binding(Edge, int, Direction, Edge, java.awt.Component)
   */
  public RelativePosition(Edge myEdge, int distance, Direction direction, Edge fixedEdge) {
    this.myEdge = myEdge;
    this.distance = distance;
    this.direction = direction;
    this.fixedEdge = fixedEdge;
  }

  /**
   * Accessor method for direction, as set in the constructor.
   * 
   * @return the value of direction
   */
  public Direction getDirection() {
    return this.direction;
  }

  /**
   * Accessor method for distance, as set in the constructor.
   * 
   * @return the value of distance
   */
  public int getDistance() {
    return this.distance;
  }

  /**
   * Accessor method for fixedEdge, as set in the constructor.
   * 
   * @return the value of fixedEdge
   */
  public Edge getFixedEdge() {
    return this.fixedEdge;
  }

  /**
   * Accessor method for myEdge, as set in the constructor.
   * 
   * @return the value of myEdge
   */
  public Edge getMyEdge() {
    return this.myEdge;
  }

  /**
   * Returns <code>true</code> if this RelativePosition is "valid" -- that is, does not result in a nonsensical
   * relationship. See {@link InvalidBindingException} for a more formal definition of what this means.
   * 
   * @return <code>true</code> if the RelativePosition is valid, <code>false</code> otherwise.
   */
  public boolean isValid() {
    boolean componentAxisIsVertical =
        (this.myEdge == Edge.LEFT || this.myEdge == Edge.HORIZONTAL_CENTER || this.myEdge == Edge.RIGHT);
    boolean fixedAxisIsVertical =
        (this.fixedEdge == Edge.LEFT || this.fixedEdge == Edge.HORIZONTAL_CENTER || this.fixedEdge == Edge.RIGHT);
    boolean directionIsHorizontal = (this.direction == Direction.LEFT || this.direction == Direction.RIGHT);

    // '^' is boolean XOR, so this returns true iff all three are either true or false.
    return !((componentAxisIsVertical ^ fixedAxisIsVertical) || (fixedAxisIsVertical ^ directionIsHorizontal));
  }

  /**
   * Returns a human readable String representation of this RelativePosition. Useful for debugging and troubleshooting.
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String ret =
        this.myEdge + " edge is " + this.distance + " pixels " + this.direction + " of " + this.fixedEdge + " edge";
    return ret;
  }

  /**
   * The edge of the component that this RelativePosition deals with.
   */
  private Edge myEdge;

  /**
   * The distance between myEdge and fixedEdge.
   */
  private int distance;

  /**
   * The directional relation between the component and the fixed object that this RelativePosition deals with.
   */
  private Direction direction;

  /**
   * The edge of the fixedObject that this RelativePosition deals with.
   */
  private Edge fixedEdge;

}

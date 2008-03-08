/**
 * BindingFactory.java<br>
 * Contains class BindingFactory.
 */
package edu.cmu.relativelayout;

import java.awt.Component;

/**
 * A factory for quickly creating Bindings to lay out simple interfaces. When you create a BindingFactory, you can set
 * top, left, bottom, and right margins, as well as horizontal and vertical spacing for controls. When you subsequently
 * ask the BindingFactory for Bindings, it uses this information to generate them.
 * 
 * @author Brian Ellis (phoenix1701@gmail.com)
 * 
 */
public class BindingFactory {

  private static BindingFactory myInstance;

  /**
   * This method returns the default instance of BindingFactory. This instance uses the default margins and spacing by
   * default, and can be referenced when you do not wish to construct a BindingFactory specifically for your use.
   */
  public static synchronized BindingFactory getBindingFactory() {
    if (BindingFactory.myInstance == null) {
      BindingFactory.myInstance = new BindingFactory();
    }

    return BindingFactory.myInstance;
  }

  /**
   * Constructs a BindingFactory with the given margins and spacing.
   */
  public BindingFactory() {
    // Use the defaults as specified by the field initializers.
  }

  /**
   * Constructs a BindingFactory with the given margins and spacing.
   * 
   * @param topMargin the distance between the top of the container and controls bound using topEdgeBinding()
   * @param leftMargin the distance between the left edge of the container and controls bound using leftEdgeBinding()
   * @param bottomMargin the distance between the bottom of the container and controls bound using bottomEdgeBinding()
   * @param rightMargin the distance between the right edge of the container and controls bound using rightEdgeBinding()
   * @param horizontalSpacing the distance between controls bound together using leftOf() or rightOf()
   * @param verticalSpacing the distance between controls bound together using above() or below()
   */
  public BindingFactory(int topMargin, int leftMargin, int bottomMargin, int rightMargin, int horizontalSpacing,
                        int verticalSpacing) {
    super();
    this.topMargin = topMargin;
    this.leftMargin = leftMargin;
    this.bottomMargin = bottomMargin;
    this.rightMargin = rightMargin;
    this.horizontalSpacing = horizontalSpacing;
    this.verticalSpacing = verticalSpacing;
  }

  /**
   * Returns a Binding that will place a component above the given fixed component.
   * 
   * @param fixed the component to place this one next to
   * @return the Binding
   */
  public Binding above(Component fixed) {
    return new Binding(Edge.BOTTOM, this.verticalSpacing, Direction.ABOVE, Edge.TOP, fixed);
  }

  /**
   * Returns a Binding that will place a component below the given fixed component.
   * 
   * @param fixed the component to place this one next to
   * @return the Binding
   */
  public Binding below(Component fixed) {
    return new Binding(Edge.TOP, this.verticalSpacing, Direction.BELOW, Edge.BOTTOM, fixed);
  }

  /**
   * Returns a Binding that will align a component's bottom edge to that of the given fixed component.
   * 
   * @param fixed the component to align to
   * @return the Binding
   */
  public Binding bottomAlignedWith(Component fixed) {
    return new Binding(Edge.BOTTOM, 0, Direction.ABOVE, Edge.BOTTOM, fixed);
  }

  /**
   * Returns a Binding that will place a component just above the bottom edge of its container.
   * 
   * @return the Binding
   */
  public Binding bottomEdge() {
    return new Binding(Edge.BOTTOM, this.bottomMargin, Direction.ABOVE, Edge.BOTTOM, null);
  }

  /**
   * Returns a Binding that will place a component directly above the bottom edge of its container. The difference
   * between this method and bottomEdge() is that this method ignores the margin and puts the component directly against
   * the edge of the container.
   * 
   * @return the Binding
   */
  public Binding directBottomEdge() {
    return new Binding(Edge.BOTTOM, 0, Direction.ABOVE, Edge.BOTTOM, null);
  }

  /**
   * Returns a Binding that will place a component directly to the right of the left edge of its container. The
   * difference between this method and leftEdge() is that this method ignores the margin and puts the component
   * directly against the edge of the container.
   * 
   * @return the Binding
   */
  public Binding directLeftEdge() {
    return new Binding(Edge.LEFT, 0, Direction.RIGHT, Edge.LEFT, null);
  }

  /**
   * Returns a Binding that will place a component directly above the given fixed component. The difference between this
   * and above() is that this method ignores the vertical spacing and places the component directly adjacent to (i.e.,
   * zero pixels away from) the fixed component.
   * 
   * @param fixed the component to place this one next to
   * @return the Binding
   */
  public Binding directlyAbove(Component fixed) {
    return new Binding(Edge.BOTTOM, 0, Direction.ABOVE, Edge.TOP, fixed);
  }

  /**
   * Returns a Binding that will place a component directly below of the given fixed component. The difference between
   * this and below() is that this method ignores the vertical spacing and places the component directly adjacent to
   * (i.e., zero pixels away from) the fixed component.
   * 
   * @param fixed the component to place this one next to
   * @return the Binding
   */
  public Binding directlyBelow(Component fixed) {
    return new Binding(Edge.TOP, 0, Direction.BELOW, Edge.BOTTOM, fixed);
  }

  /**
   * Returns a Binding that will place a component directly to the left of the given fixed component. The difference
   * between this and leftOf() is that this method ignores the horizontal spacing and places the component directly
   * adjacent to (i.e., zero pixels away from) the fixed component.
   * 
   * @param fixed the component to place this one next to
   * @return the Binding
   */
  public Binding directlyLeftOf(Component fixed) {
    return new Binding(Edge.RIGHT, 0, Direction.LEFT, Edge.LEFT, fixed);
  }

  /**
   * Returns a Binding that will place a component directly to the right of the given fixed component. The difference
   * between this and rightOf() is that this method ignores the horizontal spacing and places the component directly
   * adjacent to (i.e., zero pixels away from) the fixed component.
   * 
   * @param fixed the component to place this one next to
   * @return the Binding
   */
  public Binding directlyRightOf(Component fixed) {
    return new Binding(Edge.LEFT, 0, Direction.RIGHT, Edge.RIGHT, fixed);
  }

  /**
   * Returns a Binding that will place a component directly to the left of the right edge of its container. The
   * difference between this method and rightEdge() is that this method ignores the margin and puts the component
   * directly against the edge of the container.
   * 
   * @return the Binding
   */
  public Binding directRightEdge() {
    return new Binding(Edge.RIGHT, 0, Direction.LEFT, Edge.RIGHT, null);
  }

  /**
   * Returns a Binding that will place a component directly below the top edge of its container. The difference between
   * this method and topEdge() is that this method ignores the margin and puts the component directly against the edge
   * of the container.
   * 
   * @return the Binding
   */
  public Binding directTopEdge() {
    return new Binding(Edge.TOP, 0, Direction.BELOW, Edge.TOP, null);
  }

  /**
   * Returns the current bottom margin of this BindingFactory.
   * 
   * @return the margin
   */
  public int getBottomMargin() {
    return this.bottomMargin;
  }

  /**
   * Returns the current horizontal spacing used by this BindingFactory.
   * 
   * @return the horizontal spacing
   */
  public int getHorizontalSpacing() {
    return this.horizontalSpacing;
  }

  /**
   * Returns the current left margin of this BindingFactory.
   * 
   * @return the margin
   */
  public int getLeftMargin() {
    return this.leftMargin;
  }

  /**
   * Returns the current right margin of this BindingFactory.
   * 
   * @return the margin
   */
  public int getRightMargin() {
    return this.rightMargin;
  }

  /**
   * Returns the current top margin of this BindingFactory.
   * 
   * @return the margin
   */
  public int getTopMargin() {
    return this.topMargin;
  }

  /**
   * Returns the current vertical spacing used by this BindingFactory.
   * 
   * @return the vertical spacing
   */
  public int getVerticalSpacing() {
    return this.verticalSpacing;
  }

  /**
   * Returns a Binding that will align a component's horizontal center to that of the given fixed component. This can be
   * used to align one component's center directly above or below another's.
   * 
   * @param fixed the component to align to
   * @return the Binding
   */
  public Binding horizontallyCenterAlignedWith(Component fixed) {
    return new Binding(Edge.HORIZONTAL_CENTER, 0, Direction.RIGHT, Edge.HORIZONTAL_CENTER, fixed);
  }

  /**
   * Returns a Binding that will align a component's left edge to that of the given fixed component.
   * 
   * @param fixed the component to align to
   * @return the Binding
   */
  public Binding leftAlignedWith(Component fixed) {
    return new Binding(Edge.LEFT, 0, Direction.RIGHT, Edge.LEFT, fixed);
  }

  /**
   * Returns a Binding that will place a component just to the right of the left edge of its container.
   * 
   * @return the Binding
   */
  public Binding leftEdge() {
    return new Binding(Edge.LEFT, this.leftMargin, Direction.RIGHT, Edge.LEFT, null);
  }

  /**
   * Returns a Binding that will place a component to the left of the given fixed component.
   * 
   * @param fixed the component to place this one next to
   * @return the Binding
   */
  public Binding leftOf(Component fixed) {
    return new Binding(Edge.RIGHT, this.horizontalSpacing, Direction.LEFT, Edge.LEFT, fixed);
  }

  /**
   * Returns a Binding that will align a component's right edge to that of the given fixed component.
   * 
   * @param fixed the component to align to
   * @return the Binding
   */
  public Binding rightAlignedWith(Component fixed) {
    return new Binding(Edge.RIGHT, 0, Direction.LEFT, Edge.RIGHT, fixed);
  }

  /**
   * Returns a Binding that will place a component just to the left of the right edge of its container.
   * 
   * @return the Binding
   */
  public Binding rightEdge() {
    return new Binding(Edge.RIGHT, this.rightMargin, Direction.LEFT, Edge.RIGHT, null);
  }

  /**
   * Returns a Binding that will place a component to the right of the given fixed component.
   * 
   * @param fixed the component to place this one next to
   * @return the Binding
   */
  public Binding rightOf(Component fixed) {
    return new Binding(Edge.LEFT, this.horizontalSpacing, Direction.RIGHT, Edge.RIGHT, fixed);
  }

  /**
   * Sets the bottom margin of this BindingFactory. Subsequently created Bindings will use this margin.
   * 
   * @param bottomMargin the new margin
   */
  public void setBottomMargin(int bottomMargin) {
    this.bottomMargin = bottomMargin;
  }

  /**
   * Sets the horizontal spacing used by this BindingFactory. Subsequently created Bindings will use this spacing.
   * 
   * @param horizontalSpacing the new spacing
   */
  public void setHorizontalSpacing(int horizontalSpacing) {
    this.horizontalSpacing = horizontalSpacing;
  }

  /**
   * Sets the left margin of this BindingFactory. Subsequently created Bindings will use this margin.
   * 
   * @param leftMargin the new margin
   */
  public void setLeftMargin(int leftMargin) {
    this.leftMargin = leftMargin;
  }

  /**
   * Sets the margins for this BindingFactory.
   * 
   * @param topMargin the distance between the top of the container and controls bound using topEdgeBinding()
   * @param leftMargin the distance between the left edge of the container and controls bound using leftEdgeBinding()
   * @param bottomMargin the distance between the bottom of the container and controls bound using bottomEdgeBinding()
   * @param rightMargin the distance between the right edge of the container and controls bound using rightEdgeBinding()
   */
  public void setMargin(int topMargin, int leftMargin, int bottomMargin, int rightMargin) {
    this.topMargin = topMargin;
    this.leftMargin = leftMargin;
    this.bottomMargin = bottomMargin;
    this.rightMargin = rightMargin;
  }

  /**
   * Sets the right margin of this BindingFactory. Subsequently created Bindings will use this margin.
   * 
   * @param rightMargin the new margin
   */
  public void setRightMargin(int rightMargin) {
    this.rightMargin = rightMargin;
  }

  /**
   * Sets the spacing for the BindingFactory
   * 
   * @param horizontalSpacing the distance between controls bound together using leftOf() or rightOf()
   * @param verticalSpacing the distance between controls bound together using above() or below()
   */
  public void setSpacing(int horizontalSpacing, int verticalSpacing) {
    this.horizontalSpacing = horizontalSpacing;
    this.verticalSpacing = verticalSpacing;
  }

  /**
   * Sets the top margin of this BindingFactory. Subsequently created Bindings will use this margin.
   * 
   * @param topMargin the new margin
   */
  public void setTopMargin(int topMargin) {
    this.topMargin = topMargin;
  }

  /**
   * Sets the vertical spacing used by this BindingFactory. Subsequently created Bindings will use this spacing.
   * 
   * @param verticalSpacing the new spacing
   */
  public void setVerticalSpacing(int verticalSpacing) {
    this.verticalSpacing = verticalSpacing;
  }

  /**
   * Returns a Binding that will align a component's top edge to that of the given fixed component.
   * 
   * @param fixed the component to align to
   * @return the Binding
   */
  public Binding topAlign(Component fixed) {
    return new Binding(Edge.TOP, 0, Direction.BELOW, Edge.TOP, fixed);
  }

  /**
   * Returns a Binding that will place a component just below the top edge of its container.
   * 
   * @return the Binding
   */
  public Binding topEdge() {
    return new Binding(Edge.TOP, this.topMargin, Direction.BELOW, Edge.TOP, null);
  }

  /**
   * Returns a Binding that will align a component's vertical center to that of the given fixed component. This can be
   * used to align one component's center directly to the left or right of another's.
   * 
   * @param fixed the component to align to
   * @return the Binding
   */
  public Binding verticallyCenterAlignedWith(Component fixed) {
    return new Binding(Edge.VERTICAL_CENTER, 0, Direction.BELOW, Edge.VERTICAL_CENTER, fixed);
  }

  private int topMargin = 8;
  private int leftMargin = 8;
  private int bottomMargin = 8;
  private int rightMargin = 8;
  private int horizontalSpacing = 4;
  private int verticalSpacing = 4;
}

package edu.cmu.relativelayout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.cmu.relativelayout.equation.Equation;
import edu.cmu.relativelayout.equation.Variable;
import edu.cmu.relativelayout.matrix.RelativeMatrix;

/**
 * The main RelativeLayout class. A LayoutManager implementation that uses Bindings and RelativeConstraints to lay out
 * components.
 * 
 * @author Brian Ellis (phoenix1701@gmail.com)
 */
public class RelativeLayout implements LayoutManager2 {

  /**
   * Returns whether RelativeLayout is in debugging mode. See {@link RelativeLayout#setDebugMode(boolean)} for more
   * information.
   * 
   * @return <code>true</code> if the debugging flag is set, <code>false</code> otherwise.
   */
  public static boolean isDebugMode() {
    return RelativeMatrix.isDebugMode();
  }

  /**
   * Sets whether RelativeLayout is in debugging mode. If <code>debug</code> is set to <code>true</code>,
   * RelativeLayout will attempt to validate your entire layout every time a constraint is added or changed. This takes
   * a long time, reducing the performance of RelativeLayout by over an order of magnitude on complex layouts, but it
   * will cause any exceptions thrown while laying out the window to be traceable back to the exact line of code that
   * caused the problem. Due to the considerable performance penalty, debugging is off by default.
   * 
   * @param debug <code>true</code> if debugging should be turned on, <code>false</code> otherwise
   */
  public static void setDebugMode(boolean debug) {
    RelativeMatrix.setDebugMode(debug);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.LayoutManager2#addLayoutComponent(java.awt.Component, java.lang.Object)
   */
  public void addLayoutComponent(Component theComp, Object theConstraints) {
    if (theConstraints instanceof RelativeConstraints) {
      RelativeConstraints relativeConstraints = (RelativeConstraints) theConstraints;

      // Following line may throw InconsistentConstraintException:
      relativeConstraints.setConstrainedObject(theComp);

      this.constraints.put(theComp, relativeConstraints);
      for (Variable v : relativeConstraints.getVariables()) {
        this.componentAssociations.put(v, theComp);
      }
    } else {
      throw new IllegalArgumentException("RelativeLayouts must use RelativeConstraints objects.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String, java.awt.Component)
   */
  public void addLayoutComponent(String theName, Component theComp) {
    throw new IllegalArgumentException("RelativeLayouts must use RelativeConstraints objects.");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.LayoutManager2#getLayoutAlignmentX(java.awt.Container)
   */
  public float getLayoutAlignmentX(Container theTarget) {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.LayoutManager2#getLayoutAlignmentY(java.awt.Container)
   */
  public float getLayoutAlignmentY(Container theTarget) {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.LayoutManager2#invalidateLayout(java.awt.Container)
   */
  public void invalidateLayout(Container theTarget) {
    this.backend = null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
   */
  public void layoutContainer(Container theParent) {
    RelativeMatrix myBackend = this.getBackend();

    initializeMatrixForContainer(theParent, myBackend);

    // Generate solutions:
    Map<Variable, Double> solutions = myBackend.solve();
    // System.out.println(solutions);

    setComponentBoundsFromVariables(theParent, solutions);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.LayoutManager2#maximumLayoutSize(java.awt.Container)
   */
  public Dimension maximumLayoutSize(Container theTarget) {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
   */
  public Dimension minimumLayoutSize(Container theParent) {
    return new Dimension(0, 0);
  }

  /**
   * Returns the preferred size of the given container given the components that are to be laid out inside it. This
   * method is called when a container using a RelativeLayout is packed using the {@link java.awt.Window#pack()} method.<br>
   * <br>
   * Since RelativeLayout is different from other layout managers in that the size of the window determines the size of
   * the controls instead of vice versa, the behavior of this method (and therefore of the
   * {@link java.awt.Window#pack()} method of a window that uses a RelativeLayout) is a little complicated. If the
   * window has dynamically sized components in it (that is, anything whose size depends on the size of the window), the
   * window will be "maximized" -- that is, it will be made as big as possible on the screen. If, however, the window
   * does <em>not</em> have dynamically sized components (that is, no controls resize if you resize the window), then
   * the window will be sized to fit its contents precisely. This will work even if there are controls anchored to the
   * right or bottom of the window, but if there are, the window may be larger than desired in some cases.<br>
   * <br>
   * Note that due to the complexity of adding center-aligned components into the mix, the layout will <em>not</em>
   * take into account components that are center-aligned in the window. If such components exist, you may need to
   * manually set the size of the window instead of, or in addition to, calling {@link java.awt.Window#pack()}.
   * 
   * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
   */
  public Dimension preferredLayoutSize(Container theParent) {
    synchronized (theParent.getTreeLock()) {
      RelativeMatrix myBackend = this.getBackend();

      initializeMatrixForContainer(theParent, myBackend);

      // Check for dynamic sizes
      boolean hasDynamicWidth = false;
      boolean hasDynamicHeight = false;
      for (Component thisComponent : this.constraints.keySet()) {
        RelativeConstraints theseConstraints = this.constraints.get(thisComponent);
        for (Binding thisBinding : theseConstraints.bindings) {
          // If any of our bindings uses a dimensional variable, we have a dynamic size.
          if (thisBinding.usesDimensionalVariable()) {
            if (thisBinding.isHorizontal()) {
              hasDynamicWidth = true;
            } else {
              hasDynamicHeight = true;
            }
            break;
          }
        }
      }

      // Calculate the screen size if we're going to need it.
      int screenWidth = 0;
      int screenHeight = 0;
      if (hasDynamicWidth || hasDynamicHeight) {
        GraphicsConfiguration graphicsConfig = theParent.getGraphicsConfiguration();
        if (graphicsConfig == null) {
          screenWidth = 640;
          screenHeight = 480;
        } else {
          GraphicsDevice gs = graphicsConfig.getDevice();
          DisplayMode dm = gs.getDisplayMode();
          screenWidth = dm.getWidth();
          screenHeight = dm.getHeight();
        }

        // If we found dynamic sizes, return the maximum size possible, and we're done.
        if (hasDynamicWidth && hasDynamicHeight) {
          return new Dimension(screenWidth, screenHeight);
        }
      }

      // Otherwise, we have to generate solutions:
      Map<Variable, Double> solutions = myBackend.solve();

      // Now we find the max and min extents from all four corners:
      double maxLeftExtent = 0.0, maxTopExtent = 0.0;
      double minRightExtent = theParent.getSize().getWidth(), minBottomExtent = theParent.getSize().getHeight();

      for (Variable v : solutions.keySet()) {
        RelativeVariable rv = (RelativeVariable) v;

        boolean isRightExtent = false, isBottomExtent = false;

        if (rv.getComponent() == theParent) {
          // We can ignore the container itself.
          continue;
        }

        try {
          // Now, if this component has a Binding to the right or bottom edge of the window, we're going to take note of
          // that fact for later:
          if (this.constraints.get(rv.getComponent()) == null) {
            throw new UnknownComponentException(rv, theParent);
          }
          List<Binding> bindings = this.constraints.get(rv.getComponent()).bindings;
          for (Binding b : bindings) {
            if (b.getFixedComponent() == theParent && (b.getRelativePosition().getFixedEdge() == Edge.RIGHT)) {
              isRightExtent = true;
            } else if (b.getFixedComponent() == theParent && (b.getRelativePosition().getFixedEdge() == Edge.BOTTOM)) {
              isBottomExtent = true;
            }
          }

        } catch (NullPointerException e) {
          e.printStackTrace();
        }

        if (rv.getVariableType() == VariableType.X) {
          // This variable is the x-coordinate of a component.
          if (isRightExtent) {
            // And the component had a binding to the right edge of the window.
            double myX = solutions.get(rv);
            // See how far from the right this component extends, and take the min:
            if (myX < minRightExtent) {
              minRightExtent = myX;
            }
          } else {
            // The component did not have a binding to the right edge of the window.
            double myX = solutions.get(rv) + rv.getComponent().getPreferredSize().width;
            // See how far from the left this component extends, and take the max:
            if (myX > maxLeftExtent) {
              maxLeftExtent = myX;
            }
          }
        } else if (rv.getVariableType() == VariableType.Y) {
          // This variable is the y-coordinate of a component.
          if (isBottomExtent) {
            // And the component had a binding to the bottom edge of the window.
            double myY = solutions.get(rv);
            // See how far from the bottom this component extends, and take the min:
            if (myY < minBottomExtent) {
              minBottomExtent = myY;
            }
          } else {
            // The component did not have a binding to the bottom edge of the window.
            double myY = solutions.get(rv) + rv.getComponent().getPreferredSize().height;

            // See how far from the top this component extends, and take the max:
            if (myY > maxTopExtent) {
              maxTopExtent = myY;
            }
          }
        }
      }
      // Note: what are we forgetting here? That's right, center-aligned components! Something to do for later, I
      // guess...

      // Okay. So now we have all the info that we need, but there's just one problem. The minRightExtent and
      // minBottomExtent all depend on the width and height of the window -- if the width is -1, and the minLeftExtent
      // is -25, that means that the extent of the furthest control from the right isn't -25, but rather 24. So we need
      // to subtract:
      minRightExtent = theParent.getSize().getWidth() - minRightExtent;
      minBottomExtent = theParent.getSize().getHeight() - minBottomExtent;

      // Now, to find out how wide and tall the window needs to be to fit all this, worst-case, we just add the two
      // extents together. This may make the window larger than strictly necessary, but it's better to be too big than
      // too small.

      Insets insets = theParent.getInsets();
      Dimension finalSize =
          new Dimension((int) maxLeftExtent + (int) minRightExtent + insets.left + insets.right, (int) maxTopExtent
              + (int) minBottomExtent + insets.top + insets.bottom);

      if (hasDynamicHeight) {
        finalSize.height = screenHeight;
      }
      if (hasDynamicWidth) {
        finalSize.width = screenWidth;
      }

      // theParent.setSize(oldSize);
      return finalSize;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
   */
  public void removeLayoutComponent(Component theComp) {
    this.constraints.remove(theComp);
    for (Iterator<Variable> iter = this.componentAssociations.keySet().iterator(); iter.hasNext();) {
      Variable var = iter.next();
      if (this.componentAssociations.get(var) == theComp) {
        iter.remove();
        getBackend().removeEquation(var);
      }
    }
  }

  /**
   * A helper method that adds default identities for the given component. This is important so that if no
   * RelativePosition is defined that explicitly involves some variable of that component, it will default to something
   * reasonable (0 for X and Y, and the component's preferred size for Width and Height).
   * 
   * @param myBackend The matrix we want to add identities to.
   * @param c The component for which we want to add identities.
   */
  private void addIdentitiesForComponent(RelativeMatrix myBackend, Component c, boolean isParent) {

    Dimension size;

    if (!isParent) {
      size = c.getPreferredSize();
    } else {
      size = c.getSize();
    }

    ConcreteEquation identity = new ConcreteEquation();
    Variable variable = RelativeVariable.get(c, VariableType.WIDTH);
    this.componentAssociations.put(variable, c);
    identity.setCoefficient(1, variable);
    identity.setRightHandSide(size.width);
    myBackend.addEquation(variable, identity);

    identity = new ConcreteEquation();
    variable = RelativeVariable.get(c, VariableType.HEIGHT);
    this.componentAssociations.put(variable, c);
    identity.setCoefficient(1, variable);
    identity.setRightHandSide(size.height);
    myBackend.addEquation(variable, identity);

    identity = new ConcreteEquation();
    variable = RelativeVariable.get(c, VariableType.X);
    this.componentAssociations.put(variable, c);
    identity.setCoefficient(1, variable);
    identity.setRightHandSide(0);
    myBackend.addEquation(variable, identity);

    identity = new ConcreteEquation();
    variable = RelativeVariable.get(c, VariableType.Y);
    this.componentAssociations.put(variable, c);
    identity.setCoefficient(1, variable);
    identity.setRightHandSide(0);
    myBackend.addEquation(variable, identity);
  }

  /**
   * Gets an instance of the backend matrix, creating one if necessary. This method should be used rather than accessing
   * this.backend directly to avoid crashes or duplicated instances.
   */
  private RelativeMatrix getBackend() {
    if (this.backend == null) {
      this.backend = new RelativeMatrix();
    }
    return this.backend;
  }

  /**
   * Initializes the given matrix for the given container by setting up identities in the matrix for each component's x,
   * y, width, and height as well as that of the container itself. x and y are set to 0, and width and height are set to
   * either the size or the preferredSize of the component, whichever is available.
   * 
   * @param theParent The container we are laying out.
   * @param myBackend The backend matrix we are using.
   */
  private void initializeMatrixForContainer(Container theParent, RelativeMatrix myBackend) {

    // Initialize the matrix with the preferred size for all components.
    for (Component c : this.constraints.keySet()) {
      addIdentitiesForComponent(myBackend, c, false);
    }

    // And don't forget the window!
    addIdentitiesForComponent(myBackend, theParent, true);

    // Add all the constraints' equations to the matrix:
    for (RelativeConstraints element : this.constraints.values()) {
      List<Equation> equations = element.getEquations();
      List<Variable> variables = element.getVariables();
      for (int i = 0; i < equations.size(); i++) {
        myBackend.addEquation(variables.get(i), equations.get(i));
      }
    }
  }

  /**
   * Takes a map of variables and their bound values, and sets the bounds of all the components in the layout based on
   * their variables' values.
   * 
   * @param theContainer
   * @param solutions The map of variables and their bound values.
   */
  private void setComponentBoundsFromVariables(Container theContainer, Map<Variable, Double> solutions) {
    for (Variable v : solutions.keySet()) {
      // Associate this variable with a component and a variable type:
      Component comp = this.componentAssociations.get(v);

      if (comp == null) {
        // We might have a stale matrix, but if there's no component associated with this variable, we can just skip it
        // because we're guaranteed not to have any constraints that depend on that component anyway. This is a bit
        // voodoo, but it works.
        continue;
      }

      if (comp == theContainer) {
        // We don't want to change the bounds of the parent, ever!
        continue;
      }

      Rectangle bounds = comp.getBounds();

      // Set that attribute of the relevant component to the value in the solution:
      double value = solutions.get(v);

      RelativeVariable rv = (RelativeVariable) v;

      Insets insets = comp.getParent().getInsets();

      if (rv.getVariableType() == VariableType.X) {
        comp.setBounds(insets.left + (int) value, bounds.y, bounds.width, bounds.height);
      } else if (rv.getVariableType() == VariableType.Y) {
        comp.setBounds(bounds.x, insets.top + (int) value, bounds.width, bounds.height);
      } else if (rv.getVariableType() == VariableType.WIDTH) {
        comp.setBounds(bounds.x, bounds.y, (int) value, bounds.height);
      } else if (rv.getVariableType() == VariableType.HEIGHT) {
        comp.setBounds(bounds.x, bounds.y, bounds.width, (int) value);
      } else {
        throw new IllegalArgumentException("Encountered an illegal variable (" + v.getName()
            + ") while laying out a container.");
      }
    }
  }

  /**
   * The mapping between each component and the constraint with which it was associated when it was added to the layout.
   */
  private Map<Component, RelativeConstraints> constraints = new HashMap<Component, RelativeConstraints>();

  /**
   * The mapping between each variable in the back-end matrix and the component with which it is associated. This is
   * technically no longer needed because we're using RelativeVariables, which store their component association, so it
   * should probably be refactored out.
   */
  private Map<Variable, Component> componentAssociations = new HashMap<Variable, Component>();

  /**
   * The backend matrix we're using to lay out this container. Don't access or set this directly; use getBackend()
   * instead, which will automatically create one if needed.
   */
  private RelativeMatrix backend;

}

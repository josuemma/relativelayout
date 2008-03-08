/**
 * InconsistentConstraintException.java<br>
 * Created Dec 11, 2006 by phoenix<br>
 * Contains class: InconsistentConstraintException
 */
package edu.cmu.relativelayout;

/**
 * Indicates that two or more {@link Binding}s in a single {@link RelativeConstraints} are inconsistent with one
 * another; that is, they specify two things about the location or size of a component that cannot both be true.
 * 
 * @author Brian Ellis (phoenix1701@gmail.com)
 */
public class InconsistentConstraintException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructor for InconsistentConstraintException.
   * 
   * @param aBinding
   * @param anotherBinding
   */
  public InconsistentConstraintException(Binding aBinding, Binding anotherBinding) {
    this.binding = aBinding;
    this.otherBinding = anotherBinding;
  }

  /**
   * @see java.lang.Throwable#getMessage()
   */
  @Override
  public String getMessage() {
    return "The binding \"" + this.binding + "\" is inconsistent with the binding \"" + this.otherBinding
        + "\", which had already been added to this constraint.";
  }

  private Binding binding;
  private Binding otherBinding;
}

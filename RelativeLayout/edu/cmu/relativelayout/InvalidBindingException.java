/**
 * InvalidBindingException.java<br>
 * Created Dec 12, 2006 by phoenix<br>
 * Contains class: InvalidBindingException
 */
package edu.cmu.relativelayout;

/**
 * Indicates that a {@link Binding} is invalid; that is, it is not physically possible. This could be as a result of
 * saying something like "the left edge of X should be below the right edge of Y" -- left and right edges cannot be
 * above or below other edges. Similarly, "the left edge of X should be below the top edge of Y" will also raise an
 * InvalidBindingException, because you cannot relate edges that are perpendicular to each other.<br>
 * <br>
 * 
 * @author Brian Ellis (phoenix1701@gmail.com)
 */
public class InvalidBindingException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor for InvalidBindingException.
   * 
   * @param binding
   */
  public InvalidBindingException(Binding binding) {
    this.binding = binding;
  }

  @Override
  public String getMessage() {
    return "The binding \"" + this.binding + "\" is not physically possible.";
  }

  private Binding binding;
}

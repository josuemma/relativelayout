package edu.cmu.relativelayout.matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Jama.LUDecomposition;
import Jama.Matrix;
import edu.cmu.relativelayout.Binding;
import edu.cmu.relativelayout.InconsistentConstraintException;
import edu.cmu.relativelayout.InvalidBindingException;
import edu.cmu.relativelayout.UnknownComponentException;
import edu.cmu.relativelayout.equation.Equation;
import edu.cmu.relativelayout.equation.Variable;

/**
 * A matrix class that solves {@link Equation}s for {@link Variable}s.
 * 
 * @author Rachael Bennett (srbennett@gmail.com)
 */
public class RelativeMatrix {

  private class MatrixPrimitive {
    public double[][] body;
    public double[][] solution;
  }

  /**
   * If <code>true</code>, matrices will be solved whenever an equation is added or removed.
   */
  private static boolean debug = false;

  /**
   * Returns <code>true</code> if RelativeMatrix is currently in debugging mode. See
   * {@link RelativeMatrix#setDebugMode(boolean)} for more information on debugging mode.
   */
  public static boolean isDebugMode() {
    return RelativeMatrix.debug;
  }

  /**
   * Sets whether RelativeMatrix is in debugging mode. While in debugging mode, matrices will be solved whenever an
   * equation is added or removed. This is very slow for complex interfaces (potentially over ten times slower than when
   * debugging mode is turned off), but has the advantage that any {@link InvalidBindingException},
   * {@link InconsistentConstraintException}, or {@link UnknownComponentException} that is raised while solving the
   * matrix will point back to the exact line of code where the offending {@link Binding} was added to the matrix,
   * making debugging much easier.
   */
  public static void setDebugMode(boolean isDebugging) {
    RelativeMatrix.debug = isDebugging;
  }

  /**
   * Constructor for RelativeMatrix.
   */
  public RelativeMatrix() {
  }

  /**
   * Adds the given {@link Equation} to the matrix with the given {@link Variable} as its primary variable. See
   * <code>Binding#usesDimensionalVariable(boolean)</code> for information on primary variables.
   */
  public void addEquation(Variable variable, Equation equation) {
    this.equations.put(variable, equation);
    if (RelativeMatrix.debug) {
      solve();
    }
  }

  /**
   * Removes the {@link Equation} whose primary {@link Variable} is the given variable from the matrix.
   */
  public void removeEquation(Variable variable) {
    this.equations.remove(variable);
    if (RelativeMatrix.debug) {
      solve();
    }
  }

  /**
   * Solves this matrix and returns a map containing keys for every variable that has been added to the matrix whose
   * values are the solutions for those variables.
   */
  public Map<Variable, Double> solve() {
    return convertBack(actuallySolveMatrix());
  }

  /**
   * Returns a string representation of the matrix.
   */
  @Override
  public String toString() {
    MatrixPrimitive prim = this.toMatrixPrimitive();
    Matrix m = new Matrix(prim.body);

    StringBuilder builder = new StringBuilder();

    String line;
    ArrayList<Variable> variables = new ArrayList<Variable>(getAllVariables());
    for (int i = 0; i < variables.size(); i++) {
      line = variables.get(i).toString();
      for (int j = 0; j < variables.size(); j++) {
        builder.append(line + "\t" + m.get(i, j));
      }
      builder.append("\t" + prim.solution[i][0]);
    }
    return builder.toString();
  }

  /**
   * Solves the matrix represented by <code>body</code> with the solution vector represented by <code>sol</code>,
   * and returns the result as a JAMA {@link Matrix}. Note that although <code>sol</code> is one-dimensional, it is a
   * vertical vector and therefore is represented by a two-dimensional array where the values are in sol[0][<em>n</em>]
   * for each <em>n</em>.
   */
  private Matrix actuallySolveMatrix() {
    MatrixPrimitive prim = this.toMatrixPrimitive();
    Matrix m = new Matrix(prim.body);
    LUDecomposition decomp = new LUDecomposition(m);
    Matrix solution = new Matrix(prim.solution);

    try {
      return decomp.solve(solution);
    } catch (RuntimeException e) {
      if (e.getMessage().equals("Matrix is singular.")) {
        throw new AmbiguousLayoutException();
      } else {
        throw e;
      }
    }
  }

  /**
   * Converts the given JAMA {@link Matrix}, which must be in reduced-row echelon form, into a map containing each
   * variable in the matrix and its value.
   * 
   * @param solutionMatrix
   * @return
   */
  private Map<Variable, Double> convertBack(Matrix solutionMatrix) {
    Map<Variable, Double> solutionMap = new HashMap<Variable, Double>();

    ArrayList<Variable> allVariablesList = new ArrayList<Variable>(getAllVariables());

    for (int j = 0; j < allVariablesList.size(); j++) {
      solutionMap.put(allVariablesList.get(j), solutionMatrix.get(j, 0));
    }

    return solutionMap;
  }

  /**
   * Returns a set containing all variables that have been added to this matrix.
   */
  private Set<Variable> getAllVariables() {
    Iterator<Equation> equationsList = this.equations.values().iterator();
    HashSet<Variable> variables = new HashSet<Variable>();
    Equation incorporating;

    while (equationsList.hasNext()) {
      incorporating = equationsList.next();
      variables.addAll(incorporating.getVariables());
    }
    return variables;
  }

  /**
   * Generates a {@link MatrixPrimitive} from the {@link Equation}s that have been added to this matrix.
   */
  private MatrixPrimitive toMatrixPrimitive() {
    // Assign the variables to an ArrayList so we get a consistent ordering.
    ArrayList<Variable> allVariablesList = new ArrayList<Variable>(getAllVariables());
    MatrixPrimitive ret = new MatrixPrimitive();
    int numVariables = allVariablesList.size();
    ret.body = new double[numVariables][numVariables];
    ret.solution = new double[numVariables][1];

    List<Variable> equationVariable;
    Variable variable;

    Equation incorporating;

    for (int i = 0; i < ret.body.length; i++) {
      if (this.equations.containsKey(allVariablesList.get(i))) {
        incorporating = this.equations.get(allVariablesList.get(i));
        equationVariable = incorporating.getVariables();
        ret.body[i][i] = 1;
        ret.solution[i][0] = incorporating.getRightHandSide();

        for (int j = 0; j < equationVariable.size(); j++) {
          variable = equationVariable.get(j);
          ret.body[i][allVariablesList.indexOf(variable)] = incorporating.getCoefficient(variable);
        }

      } else {
        ret.body[i][i] = 1;
      }
    }
    return ret;
  }

  /**
   * The map of variables and the equations that define them.
   */
  private HashMap<Variable, Equation> equations = new HashMap<Variable, Equation>();

}

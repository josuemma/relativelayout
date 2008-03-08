/**
 * 
 */
package edu.cmu.relativelayout.test;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import edu.cmu.relativelayout.Binding;
import edu.cmu.relativelayout.Direction;
import edu.cmu.relativelayout.Edge;
import edu.cmu.relativelayout.RelativeConstraints;
import edu.cmu.relativelayout.RelativeLayout;

/**
 * @author Brian Ellis (phoenix1701@gmail.com)
 * 
 */
public class TestUI {

  public static void main(String[] args) {
    TestUI.test();
  }

  public static void test() {
    JFrame frame = new JFrame();
    frame.setLayout(new RelativeLayout());

    JTextArea area = new JTextArea("Some text here.");
    JLabel okLabel = new JLabel("OK");
    JButton addButton = new JButton("Add");
    JButton editButton = new JButton("Edit");

    Binding toTheRightInside = new Binding(Edge.LEFT, 12, Direction.RIGHT, Edge.LEFT, frame);

    Binding belowInside = new Binding(Edge.TOP, 12, Direction.BELOW, Edge.TOP, frame);

    Binding aboveInside = new Binding(Edge.BOTTOM, 12, Direction.ABOVE, Edge.BOTTOM, frame);

    Binding toTheLeftInside = new Binding(Edge.RIGHT, 15, Direction.LEFT, Edge.RIGHT, frame);

    Binding toTheLeftOutside = new Binding(Edge.RIGHT, 7, Direction.LEFT, Edge.LEFT, addButton);

    Binding belowOutside = new Binding(Edge.TOP, 3, Direction.BELOW, Edge.BOTTOM, addButton);

    Binding alignBottoms = new Binding(Edge.BOTTOM, 8, Direction.ABOVE, Edge.BOTTOM, frame);

    RelativeConstraints areaConstraint = new RelativeConstraints();
    areaConstraint.addBinding(toTheRightInside);
    areaConstraint.addBinding(belowInside);
    areaConstraint.addBinding(aboveInside);
    areaConstraint.addBinding(toTheLeftOutside);

    RelativeConstraints addButtonConstraint = new RelativeConstraints();
    addButtonConstraint.addBinding(belowInside);
    addButtonConstraint.addBinding(toTheLeftInside);

    RelativeConstraints editButtonConstraint = new RelativeConstraints();
    editButtonConstraint.addBinding(belowOutside);
    editButtonConstraint.addBinding(toTheLeftInside);

    RelativeConstraints okLabelConstraint = new RelativeConstraints();
    okLabelConstraint.addBinding(toTheLeftInside);
    // okLabelConstraint.addAnchor(aboveInside, frame);
    okLabelConstraint.addBinding(alignBottoms);

    frame.add(area, areaConstraint);
    frame.add(addButton, addButtonConstraint);
    frame.add(editButton, editButtonConstraint);
    frame.add(okLabel, okLabelConstraint);

    frame.setSize(600, 200);
    frame.setVisible(true);

  }

}

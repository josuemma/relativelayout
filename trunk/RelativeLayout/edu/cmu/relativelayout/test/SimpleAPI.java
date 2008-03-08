/**
 * 
 */
package edu.cmu.relativelayout.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import edu.cmu.relativelayout.Binding;
import edu.cmu.relativelayout.Direction;
import edu.cmu.relativelayout.Edge;
import edu.cmu.relativelayout.RelativeConstraints;
import edu.cmu.relativelayout.RelativeLayout;
import edu.cmu.relativelayout.RelativePosition;

/**
 * @author Brian Ellis (phoenix1701@gmail.com)
 */
public class SimpleAPI {

  public static void main(String[] args) {
    // testDialog();
    SimpleAPI.testMailInterface();
    // testCentering();
    // testStaticLayout();
    // testCorners();
    // testSanityChecking();
  }

  public static void testCentering() {
    JFrame frame = new JFrame();
    frame.setLayout(new RelativeLayout());

    JPanel corner = new JPanel();
    corner.setPreferredSize(new Dimension(96, 96));
    Border b = BorderFactory.createBevelBorder(BevelBorder.RAISED);
    corner.setBorder(b);

    JButton button = new JButton("Hello");

    Binding twelvePixelsFromLeft = new Binding(Edge.LEFT, 12, Direction.RIGHT, Edge.LEFT, Binding.PARENT);
    Binding twelvePixelsFromTop = new Binding(Edge.TOP, 12, Direction.BELOW, Edge.TOP, Binding.PARENT);
    Binding verticallyCentered = new Binding(Edge.VERTICAL_CENTER, 0, Direction.ABOVE, Edge.VERTICAL_CENTER, corner);
    Binding horizontallyCenteredWithOffset =
        new Binding(Edge.HORIZONTAL_CENTER, (96 + 12) / 2, Direction.RIGHT, Edge.HORIZONTAL_CENTER,
                    Binding.PARENT);
    // RelativePosition twelvePixelsToTheRightOf = new
    // RelativePosition(RelativePosition.Edge.LEFT, 12,
    // RelativePosition.Direction.RIGHT, RelativePosition.Edge.RIGHT);
    // RelativePosition twelvePixelsFromRight = new
    // RelativePosition(RelativePosition.Edge.RIGHT, 12,
    // RelativePosition.Direction.LEFT, RelativePosition.Edge.RIGHT);

    RelativeConstraints cornerConstraint = new RelativeConstraints();
    cornerConstraint.addBinding(twelvePixelsFromLeft);
    cornerConstraint.addBinding(twelvePixelsFromTop);

    RelativeConstraints buttonConstraint = new RelativeConstraints();
    buttonConstraint.addBinding(verticallyCentered);
    // buttonConstraint.addRelation(twelvePixelsToTheRightOf, corner);
    // buttonConstraint.addRelation(twelvePixelsFromRight, RelativePosition.PARENT_CONTAINER);
    buttonConstraint.addBinding(horizontallyCenteredWithOffset);

    frame.getContentPane().add(corner, cornerConstraint);
    frame.getContentPane().add(button, buttonConstraint);

    frame.setSize(300, 150);
    frame.setVisible(true);
  }

  public static void testDialog() {
    JFrame frame = new JFrame();
    frame.setLayout(new RelativeLayout());

    JLabel label =
        new JLabel("RelativeLayout is much easier to use.  Are you sure you want to go on using other layouts?");
    JTextArea area = new JTextArea("Some text here.");
    area.setBorder(new BevelBorder(BevelBorder.LOWERED));
    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Cancel");

    Binding leftMarginTwelve = new Binding(Edge.LEFT, 12, Direction.RIGHT, Edge.LEFT, frame);
    Binding rightMarginTwelve = new Binding(Edge.RIGHT, 12, Direction.LEFT, Edge.RIGHT, frame);
    Binding topMarginTwelve = new Binding(Edge.TOP, 12, Direction.BELOW, Edge.TOP, frame);
    Binding bottomMarginTwelve = new Binding(Edge.BOTTOM, 12, Direction.ABOVE, Edge.BOTTOM, frame);
    Binding twelveToTheLeftOf = new Binding(Edge.RIGHT, 12, Direction.LEFT, Edge.LEFT, okButton);

    Binding twelveAbove = new Binding(Edge.BOTTOM, 12, Direction.ABOVE, Edge.TOP, okButton);
    Binding twelveBelow = new Binding(Edge.TOP, 12, Direction.BELOW, Edge.BOTTOM, label);

    RelativeConstraints labelConstraint = new RelativeConstraints();
    labelConstraint.addBindings(leftMarginTwelve, topMarginTwelve);

    RelativeConstraints okButtonConstraint = new RelativeConstraints();
    okButtonConstraint.addBinding(bottomMarginTwelve);
    okButtonConstraint.addBinding(rightMarginTwelve);

    RelativeConstraints cancelButtonConstraint = new RelativeConstraints();
    cancelButtonConstraint.addBindings(twelveToTheLeftOf, bottomMarginTwelve);

    RelativeConstraints areaConstraint = new RelativeConstraints();
    areaConstraint.addBindings(twelveAbove, twelveBelow, leftMarginTwelve, rightMarginTwelve);

    frame.add(label, labelConstraint);
    frame.add(area, areaConstraint);
    frame.add(okButton, okButtonConstraint);
    frame.add(cancelButton, cancelButtonConstraint);

    frame.setSize(600, 200);
    frame.setVisible(true);

  }

  public static void testSanityChecking() {
    JFrame frame = new JFrame();
    frame.setLayout(new RelativeLayout());

    JButton one = new JButton("One");
    JButton two = new JButton("Two");
    JButton three = new JButton("Three");

    RelativePosition tenToTheRight = new RelativePosition(Edge.LEFT, 10, Direction.RIGHT, Edge.RIGHT);

    Binding wtf = new Binding(Edge.VERTICAL_CENTER, 12, Direction.LEFT, Edge.VERTICAL_CENTER, two);
    Binding moreWtf = new Binding(Edge.VERTICAL_CENTER, 12, Direction.LEFT, Edge.HORIZONTAL_CENTER, three);
    Binding stillMoreWtf = new Binding(Edge.TOP, 12, Direction.ABOVE, Edge.LEFT, one);

    System.out.println("Sane relation valid: " + tenToTheRight.isValid());
    System.out.println("RelativePosition 1 valid: " + wtf.isValid());
    System.out.println("RelativePosition 2 valid: " + moreWtf.isValid());
    System.out.println("RelativePosition 3 valid: " + stillMoreWtf.isValid());

    RelativeConstraints c1 = new RelativeConstraints();
    c1.addBinding(wtf);

    RelativeConstraints c2 = new RelativeConstraints();
    c2.addBinding(moreWtf);

    RelativeConstraints c3 = new RelativeConstraints();
    c3.addBinding(stillMoreWtf);

    c3.addBinding(new Binding(tenToTheRight, two));
    c2.addBinding(new Binding(tenToTheRight, one));

    frame.add(one, c1);
    frame.add(two, c2);
    frame.add(three, c3);

    frame.setSize(500, 500);
    frame.setVisible(true);
  }

  public static void testStaticLayout() {
    JFrame frame = new JFrame();
    frame.setLayout(new RelativeLayout());

    JButton one = new JButton("One");
    JButton two = new JButton("Two");
    JButton three = new JButton("Three");

    RelativeConstraints oneConstraints =
        new RelativeConstraints(new Binding(Edge.LEFT, 12, Direction.RIGHT, Edge.LEFT, frame),
                                new Binding(Edge.TOP, 12, Direction.BELOW, Edge.TOP, frame));
    RelativeConstraints twoConstraints =
        new RelativeConstraints(new Binding(Edge.LEFT, 12, Direction.RIGHT, Edge.RIGHT, one),
                                new Binding(Edge.TOP, 12, Direction.BELOW, Edge.TOP, frame));
    RelativeConstraints threeConstraints =
        new RelativeConstraints(new Binding(Edge.LEFT, 12, Direction.RIGHT, Edge.RIGHT, two),
                                new Binding(Edge.TOP, 12, Direction.BELOW, Edge.TOP, frame));

    frame.add(one, oneConstraints);
    frame.add(two, twoConstraints);
    frame.add(three, threeConstraints);
    frame.pack();
    frame.setSize(frame.getSize().width + 12, frame.getSize().height + 12);
    frame.setVisible(true);
  }

  /**
   * 
   */
  @SuppressWarnings("unused")
  private static void testCorners() {
    JFrame frame = new JFrame();
    frame.setLayout(new RelativeLayout());

    JButton one = new JButton("One");
    JButton two = new JButton("Two");
    JButton three = new JButton("Three");
    JButton four = new JButton("Four");
    JButton five = new JButton("Five");

    final int WINDOW_PADDING = 12;

    frame.add(one, new RelativeConstraints(new Binding(Edge.LEFT, WINDOW_PADDING, Direction.RIGHT, Edge.LEFT, frame),
                                           new Binding(Edge.TOP, WINDOW_PADDING, Direction.BELOW, Edge.TOP, frame)));
    frame.add(two, new RelativeConstraints(new Binding(Edge.RIGHT, WINDOW_PADDING, Direction.LEFT, Edge.RIGHT, frame),
                                           new Binding(Edge.TOP, WINDOW_PADDING, Direction.BELOW, Edge.TOP, frame)));
    frame.add(three, new RelativeConstraints(new Binding(Edge.LEFT, WINDOW_PADDING, Direction.RIGHT, Edge.LEFT, frame),
                                             new Binding(Edge.BOTTOM, WINDOW_PADDING, Direction.ABOVE, Edge.BOTTOM,
                                                         frame)));
    frame.add(four, new RelativeConstraints(new Binding(Edge.RIGHT, WINDOW_PADDING, Direction.LEFT, Edge.RIGHT, frame),
                                            new Binding(Edge.BOTTOM, WINDOW_PADDING, Direction.ABOVE, Edge.BOTTOM,
                                                        frame)));
    frame.add(five, new RelativeConstraints(new Binding(Edge.TOP, 0, Direction.BELOW, Edge.BOTTOM, one),
                                            new Binding(Edge.LEFT, WINDOW_PADDING, Direction.RIGHT, Edge.LEFT, frame)));

    frame.pack();
    frame.setVisible(true);
  }

  /**
   * 
   */
  private static void testMailInterface() {
    JFrame frame = new JFrame();
    frame.setLayout(new RelativeLayout());

    JScrollPane folderView =
        new JScrollPane(new JTree(new Object[] { "Inbox", "Drafts", "Sent Mail", "Spam", "Trash" }));
    JScrollPane mailList =
        new JScrollPane(new JList(new Object[] { "Get rich quick!", "Dr. Mkele Mbogo from Nigeria",
            "Enlarge your, uh, stock portfolio", "Buy new SpamStopper anti-spam tool!!!11!" }));
    JTextArea textArea =
        new JTextArea("Now you too can be rich, just like all the other people "
            + "who spend most of their time sending emails like this!  No, really!");
    textArea.setWrapStyleWord(true);
    textArea.setLineWrap(true);
    JScrollPane mailArea = new JScrollPane(textArea);

    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEADING, 8, 0));
    toolbar.add(new JLabel("Get Mail"));
    toolbar.add(new JLabel("Write"));
    toolbar.add(new JLabel("Address Book"));
    JPanel spacerPanel = new JPanel();
    spacerPanel.setSize(32, 32);
    toolbar.add(spacerPanel);
    toolbar.add(new JLabel("Reply"));
    toolbar.add(new JLabel("Reply All"));
    toolbar.add(new JLabel("Forward"));

    JPanel statusBar = new JPanel(new BorderLayout());
    statusBar.add(new JLabel("Unread: 0   Total: 4"), BorderLayout.EAST);

    Binding leftEdge = new Binding(Edge.LEFT, 0, Direction.RIGHT, Edge.LEFT, frame);
    Binding rightEdge = new Binding(Edge.RIGHT, 0, Direction.LEFT, Edge.RIGHT, frame);
    Binding topEdge = new Binding(Edge.TOP, 0, Direction.BELOW, Edge.TOP, frame);
    Binding bottomEdge = new Binding(Edge.BOTTOM, 0, Direction.ABOVE, Edge.BOTTOM, frame);

    RelativePosition justBelow = new RelativePosition(Edge.TOP, 0, Direction.BELOW, Edge.BOTTOM);
    RelativePosition justAbove = new RelativePosition(Edge.BOTTOM, 0, Direction.ABOVE, Edge.TOP);
    RelativePosition eightBelow = new RelativePosition(Edge.TOP, 8, Direction.BELOW, Edge.BOTTOM);
    RelativePosition eightRightOf = new RelativePosition(Edge.LEFT, 8, Direction.RIGHT, Edge.RIGHT);

    RelativeConstraints toolbarConstraints = new RelativeConstraints();
    toolbarConstraints
                      .addBindings(
                                   new Binding(Edge.HORIZONTAL_CENTER, 0, Direction.LEFT, Edge.HORIZONTAL_CENTER, frame),
                                   topEdge);
    frame.add(toolbar, toolbarConstraints);

    RelativeConstraints folderViewConstraints = new RelativeConstraints();
    folderViewConstraints.addBindings(leftEdge, new Binding(justBelow, toolbar), new Binding(justAbove, statusBar));
    frame.add(folderView, folderViewConstraints);

    RelativeConstraints mailListConstraints = new RelativeConstraints();
    mailListConstraints.addBindings(new Binding(justBelow, toolbar), new Binding(eightRightOf, folderView), rightEdge);
    frame.add(mailList, mailListConstraints);

    RelativeConstraints mailAreaConstraints = new RelativeConstraints();
    mailAreaConstraints.addBindings(new Binding(eightBelow, mailList), new Binding(eightRightOf, folderView),
                                    new Binding(justAbove, statusBar), rightEdge);
    frame.add(mailArea, mailAreaConstraints);

    RelativeConstraints statusBarConstraints = new RelativeConstraints();
    statusBarConstraints.addBindings(bottomEdge, leftEdge, new Binding(Edge.RIGHT, 20, Direction.LEFT, Edge.RIGHT,
                                                                       frame));
    frame.add(statusBar, statusBarConstraints);

    frame.setSize(800, 400);
    frame.setVisible(true);
  }

}

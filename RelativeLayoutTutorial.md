# Introduction #

There are three ways to use RelativeLayout; some of them are simpler, while others offer more flexibility for more complex interfaces.  This tutorial will show you how to build a simple interface using BindingFactory, a slightly more interesting interface using Bindings, and a complex interface using RelativePositions and Bindings together.

# General Principles #

## Construction ##

RelativeLayout is a Swing layout manager, which means it follows certain conventions that are common to layout managers in Swing.  In particular, you can set a RelativeLayout as the layout manager for a control by passing a new RelativeLayout object as the parameter to a JPanel or JFrame constructor, or by calling `setLayout()` with a RelativeLayout as the parameter on any Container class.  For example:

```
// Passing a RelativeLayout in the constructor:
JPanel panel = new JPanel(new RelativeLayout());

// Setting a RelativeLayout as the layout after construction:
JPanel anotherPanel = new JPanel();
anotherPanel.setLayout(new RelativeLayout());
```

## Constraints ##

When you add a component to a container in Swing, the `add()` method can take as a parameter an additional arbitrary object which is passed to the layout manager to determine how the added component should be laid out.  This object is typically referred to as the "constraints" for the component.  RelativeLayout uses a constraints object called a RelativeConstraints, which should be added along with each component that is added to the layout.  For example:

```
// First we create our panel with a RelativeLayout:
JPanel panel = new JPanel(new RelativeLayout());

// Now let's create a button:
JButton button = new JButton("OK");

// We have to create a RelativeConstraints object so we can add the button.
// This particular constraints object is empty, so it's not very interesting.
RelativeConstraints constraints = new RelativeConstraints();

// Add the button to the panel with the constraints:
panel.add(button, constraints);
```

RelativeConstraints objects are just sets of Bindings, which actually determine where a component appears inside its parent.  If, as in the above example, the position and size of a component isn't specified by the constraints, it will default to being in the upper left corner of the window with its preferred size (as from calling `getPreferredSize()`). There are several ways of creating and using bindings, and creating bindings is what the rest of this tutorial is about.

# Creating Bindings #

## BindingFactory ##

The simplest way to create an interface with RelativeLayout is to use the BindingFactory class to generate bindings for you.  Doing this is pretty straightforward; just create a BindingFactory object, specify the margin sizes and control spacing you want (or just accept the defaults), and then call the various positioning methods to churn out Bindings.  For example, let's say we want to create this very simple interface:

![http://relativelayout.googlecode.com/svn/wiki/simpleinterface1.png](http://relativelayout.googlecode.com/svn/wiki/simpleinterface1.png)

We can do this with BindingFactory with the following code (note that in this example, we sacrifice brevity for clarity, so this is a lot more code than you'd actually have to write for this):

```
JPanel panel = new JPanel(new RelativeLayout());

JButton button1 = new JButton("Button 1");
JButton button2 = new JButton("Button 2");

// Create a BindingFactory with the default margin and control spacing:
BindingFactory bf = new BindingFactory();

// Make some bindings using our BindingFactory.  Note how leftEdge() and topEdge() don't 
// take any arguments, but rightOf() needs the control we want to be to the right of.
Binding leftEdge = bf.leftEdge();
Binding topEdge = bf.topEdge();
Binding rightOfButton1 = bf.rightOf(button1);

// Create a constraints object for each component, and add the bindings to the 
// constraints:
RelativeConstraints button1Constraints = new RelativeConstraints();
button1Constraints.addBinding(leftEdge);
button1Constraints.addBinding(topEdge);

RelativeConstraints button2Constraints = new RelativeConstraints();
button2Constraints.addBinding(rightOfButton1);
button2Constraints.addBinding(topEdge);

// Lastly, add the components to the panel with the constraints:
panel.add(button1, button1Constraints);
panel.add(button2, button2Constraints);
```

And that's all there is to it.

Let's try a slightly more interesting interface.  Here's what it looks like when it's small:

![http://relativelayout.googlecode.com/svn/wiki/simpleinterface2a.png](http://relativelayout.googlecode.com/svn/wiki/simpleinterface2a.png)

and here's what it looks like when you resize it to be larger:

![http://relativelayout.googlecode.com/svn/wiki/simpleinterface2b.png](http://relativelayout.googlecode.com/svn/wiki/simpleinterface2b.png)

Believe it or not, this interface is almost as easy to create as the first one.  The key is to think about which controls depend on other controls, and how.  Here's the code:

```
JPanel panel = new JPanel(new RelativeLayout());

JScrollPane textPane = new JScrollPane(new JTextArea());
JScrollPane userList = new JScrollPane(new JList());
JTextField inputField = new JTextField();
JButton sendButton = new JButton("Send");

BindingFactory bf = new BindingFactory();

// We'll make constraints for the textPane first:
Binding leftEdge = bf.leftEdge();
Binding topEdge = bf.topEdge();
Binding aboveInputField = bf.above(inputField);
Binding leftOfUserList = bf.leftOf(userList);
// RelativeConstraints has a constructor that takes an arbitrary number of bindings, like this:
RelativeConstraints textPaneConstraints
    = new RelativeConstraints(leftEdge, topEdge, aboveInputField, leftOfUserList);

// Now the user list:
Binding rightEdge = bf.rightEdge();
// Notice that we don't constrain the left edge. That's important, and we'll see why in a moment.
RelativeConstraints userListConstraints
    = new RelativeConstraints(topEdge, rightEdge, aboveInputField);

// Now the input field:
Binding bottomEdge = bf.bottomEdge();
Binding leftOfSendButton = bf.leftOf(sendButton);
RelativeConstraints inputFieldConstraints
    = new RelativeConstraints(leftEdge, bottomEdge, leftOfSendButton);

// And lastly, the send button:
// Again notice that we don't constrain the left or top edges:
RelativeConstraints sendButtonConstraints
    = new RelativeConstraints(rightEdge, bottomEdge);

panel.add(textPane, textPaneConstraints);
panel.add(userList, userListConstraints);
panel.add(inputField, inputFieldConstraints);
panel.add(sendButton, sendButtonConstraints);
```

Not bad!  Now, you may be wondering why we didn't specify every edge of every control, and why we specifically left out the left edge of the user list and the left and top edges of the send button.  The reason is that all components in Swing have a "preferred size", and any time one edge of a component (say the right edge) is constrained and the other (say the left edge) isn't, the preferred size is used to determine how wide or tall the component will be.  This means that because we only specified the right edge of the Send button and not the left, it will always be exactly as wide as it needs to be to fit the text.  And because the right edge of the input field is specified in terms of the left edge of the Send button, the field will automatically get narrower if the Send button gets wider.  This makes localization very easy, because the controls in the window will automatically shift around as needed within the constraints you've specified.

### Degenerate Layouts ###

In the above example, we made a potentially dangerous assumption.  We placed the input field to the left of the Send button, aligned them both to the bottom edge of the window, and then put the text pane above the input field.  That works great as long as the Send button is the same height as the input field, but what if it's not?

![http://relativelayout.googlecode.com/svn/wiki/simpleinterface2c.png](http://relativelayout.googlecode.com/svn/wiki/simpleinterface2c.png)

Hm, that's no good.  Fortunately, there are always multiple different layouts that will result in basically the same interface, so we can fix the problem by switching to another one that doesn't make that assumption.  Let's change how the text pane is defined:

```
Binding leftEdge = bf.leftEdge();
Binding topEdge = bf.topEdge();
Binding aboveSendButton = bf.above(sendButton);
Binding leftOfUserList = bf.leftOf(userList);
// RelativeConstraints has a constructor that takes an arbitrary number of bindings, like this:
RelativeConstraints textPaneConstraints
    = new RelativeConstraints(leftEdge, topEdge, aboveSendButton, leftOfUserList);
```

Okay, that fixes the overlap, but now the input field will be aligned to the bottom of the Send button.  Let's align it to the vertical center instead:

```
Binding vAlignOnSendButton = bf.verticallyCenterAlignedWith(sendButton);
Binding leftOfSendButton = bf.leftOf(sendButton);
RelativeConstraints inputFieldConstraints
    = new RelativeConstraints(leftEdge, vAlignOnSendButton, leftOfSendButton);
```

Now our interface looks like this:

![http://relativelayout.googlecode.com/svn/wiki/simpleinterface2d.png](http://relativelayout.googlecode.com/svn/wiki/simpleinterface2d.png)

We call this a degenerate layout because if the Send button was the same size as in the initial screen shots, this code would result in exactly the same positioning of the controls.  The difference is that they would be positioned there for a different _reason_.  This gives you as the designer a way of writing your intent into the code so that it will be especially robust.

## Binding ##

Although BindingFactory is sufficient for many interfaces, some more complex interfaces require a bit more flexibility.  For example, consider this interface:

![http://relativelayout.googlecode.com/svn/wiki/simpleinterface3.png](http://relativelayout.googlecode.com/svn/wiki/simpleinterface3.png)

Although this seems straightforward at first, creating this interface with a BindingFactory would be troublesome.  Here's how we could do it (but feel free to skip this, because it's a bad idea):

```
JPanel panel = new JPanel(new RelativeLayout());

JCheckBox hardwareAccelBox = new JCheckBox("Enable hardware acceleration");
JCheckBox kungFuGripBox = new JCheckBox("with kung-fu grip");
JCheckBox cherryOnTopBox = new JCheckBox("with cherry on top");

JCheckBox printMoneyBox = new JCheckBox("Print money from optical drive slot");
JRadioButton dollarsRadio = new JRadioButton("Dollars");
JRadioButton eurosRadio = new JRadioButton("Euros");
JRadioButton pesosRadio = new JRadioButton("Pesos");
JRadioButton rublesRadio = new JRadioButton("Rubles");

BindingFactory bf = new BindingFactory();

panel.add(hardwareAccelBox, new RelativeConstraints(bf.leftEdge(), bf.topEdge()));
bf.setLeftMargin(bf.getLeftMargin() + 12);
panel.add(kungFuGripBox, new RelativeConstraints(bf.leftEdge(), bf.below(hardwareAccelBox)));
panel.add(cherryOnTopBox, new RelativeConstraints(bf.leftEdge(), bf.below(kungFuGripBox)));
bf.setLeftMargin(bf.getLeftMargin() - 12);

bf.setVerticalSpacing(bf.getVerticalSpacing() + 16);
panel.add(printMoneyBox, new RelativeConstraints(bf.leftEdge(), bf.below(cherryOnTopBox)));
bf.setVerticalSpacing(bf.getVerticalSpacing() - 16);
bf.setLeftMargin(bf.getLeftMargin() + 12);
panel.add(dollarsRadio, new RelativeConstraints(bf.leftEdge(), bf.below(printMoneyBox)));
panel.add(eurosRadio, new RelativeConstraints(bf.leftEdge(), bf.below(dollarsRadio)));
panel.add(pesosRadio, new RelativeConstraints(bf.leftEdge(), bf.below(eurosRadio)));
panel.add(rublesRadio, new RelativeConstraints(bf.leftEdge(), bf.below(pesosRadio)));
bf.setLeftMargin(bf.getLeftMargin() - 12);
```

I hope you'll agree with me when I say that this is not optimal.  It works, but the abuse of the margins and spacing of the BindingFactory is liable to introduce subtle bugs that will be difficult to track down later.  We need something more powerful: we need the ability to specify pixel offsets directly.  To do this, we need to create our own Bindings.

```
JPanel panel = new JPanel(new RelativeLayout());

JCheckBox hardwareAccelBox = new JCheckBox("Enable hardware acceleration");
JCheckBox kungFuGripBox = new JCheckBox("with kung-fu grip");
JCheckBox cherryOnTopBox = new JCheckBox("with cherry on top");

JCheckBox printMoneyBox = new JCheckBox("Print money from optical drive slot");
JRadioButton dollarsRadio = new JRadioButton("Dollars");
JRadioButton eurosRadio = new JRadioButton("Euros");
JRadioButton pesosRadio = new JRadioButton("Pesos");
JRadioButton rublesRadio = new JRadioButton("Rubles");

final int kMargin = 20;
final int kIndent = 12;
final int kWithinGroupSpacing = 4;
final int kBetweenGroupSpacing = 20;

Binding topLevelLeftEdge = new Binding(Edge.LEFT, kMargin, Direction.RIGHT, Edge.LEFT, Binding.PARENT);
Binding indentedLeftEdge = new Binding(Edge.LEFT, kMargin + kIndent, Direction.RIGHT, Edge.LEFT, Binding.PARENT);

Binding hardwareAccelBoxTop = new Binding(Edge.TOP, kMargin, Direction.BELOW, Edge.TOP, Binding.PARENT);
Binding kungFuGripBoxTop = new Binding(Edge.TOP, kWithinGroupSpacing, Direction.BELOW, Edge.BOTTOM, hardwareAccelBox);
Binding cherryOnTopBoxTop = new Binding(Edge.TOP, kWithinGroupSpacing, Direction.BELOW, Edge.BOTTOM, kungFuGripBox);

Binding printMoneyBoxTop = new Binding(Edge.TOP, kBetweenGroupSpacing, Direction.BELOW, Edge.BOTTOM, cherryOnTopBox);
Binding dollarsRadioTop = new Binding(Edge.TOP, kWithinGroupSpacing, Direction.BELOW, Edge.BOTTOM, printMoneyBox);
Binding eurosRadioTop = new Binding(Edge.TOP, kWithinGroupSpacing, Direction.BELOW, Edge.BOTTOM, dollarsRadio);
Binding pesosRadioTop = new Binding(Edge.TOP, kWithinGroupSpacing, Direction.BELOW, Edge.BOTTOM, eurosRadio);
Binding rublesRadioTop = new Binding(Edge.TOP, kWithinGroupSpacing, Direction.BELOW, Edge.BOTTOM, pesosRadio);

panel.add(hardwareAccelBox, new RelativeConstraints(topLevelLeftEdge, hardwareAccelBoxTop));
panel.add(kungFuGripBox, new RelativeConstraints(indentedLeftEdge, kungFuGripBoxTop));
panel.add(cherryOnTopBox, new RelativeConstraints(indentedLeftEdge, cherryOnTopBoxTop));

panel.add(printMoneyBox, new RelativeConstraints(topLevelLeftEdge, printMoneyBoxTop));
panel.add(dollarsRadio, new RelativeConstraints(indentedLeftEdge, dollarsRadioTop));
panel.add(eurosRadio, new RelativeConstraints(indentedLeftEdge, eurosRadioTop));
panel.add(pesosRadio, new RelativeConstraints(indentedLeftEdge, pesosRadioTop));
panel.add(rublesRadio, new RelativeConstraints(indentedLeftEdge, rublesRadioTop));
```

The syntax for creating a Binding is very easy to remember: it is form of an English sentence.  The first Binding (`topLevelLeftEdge`) reads, "the `LEFT` edge is `kMargin` pixels to the `RIGHT` of the `LEFT` edge of the parent component."  Since we never specify the left edge of _what_, we can use the Binding over and over again to position multiple controls at the same horizontal distance.  With this code, it's a simple matter to tweak the constants and change the indentation or spacing of all the components in the window.

By the way, reading that code you may have been thinking to yourself, "why is `PARENT` a special constant?  Why not just use `panel` in that spot instead?"  Indeed, you could do so, and it would work just fine:

```
Binding topLevelLeftEdge = new Binding(Edge.LEFT, kMargin, Direction.RIGHT, Edge.LEFT, panel);
```

The `PARENT` constant, however, allows you to re-use Bindings among controls that do not share the same parent, as the parent will not be resolved until the very last moment.  This is how BindingFactory returns Bindings that refer to the parent component without ever knowing what it is.

## RelativePosition ##

Although that last bit of code gets the job done and is reasonably clear, it's still not perfect.  Although we can reuse `topLevelLeftEdge` and `indentedLeftEdge`, we have to construct a new Binding for the vertical position of every component, because the component it's relative to is always changing.  Wouldn't it be nice if we could abstract that component away too?  We can, by using the RelativePosition class:

```
JPanel panel = new JPanel(new RelativeLayout());

JCheckBox hardwareAccelBox = new JCheckBox("Enable hardware acceleration");
JCheckBox kungFuGripBox = new JCheckBox("with kung-fu grip");
JCheckBox cherryOnTopBox = new JCheckBox("with cherry on top");

JCheckBox printMoneyBox = new JCheckBox("Print money from optical drive slot");
JRadioButton dollarsRadio = new JRadioButton("Dollars");
JRadioButton eurosRadio = new JRadioButton("Euros");
JRadioButton pesosRadio = new JRadioButton("Pesos");
JRadioButton rublesRadio = new JRadioButton("Rubles");

final int kMargin = 20;
final int kIndent = 12;
final int kWithinGroupSpacing = 4;
final int kBetweenGroupSpacing = 20;

Binding topLevelLeftEdge = new Binding(Edge.LEFT, kMargin, Direction.RIGHT, Edge.LEFT, Binding.PARENT);
Binding indentedLeftEdge = new Binding(Edge.LEFT, kMargin + kIndent, Direction.RIGHT, Edge.LEFT, Binding.PARENT);

RelativePosition withinGroupBelow = new RelativePosition(Edge.TOP, kWithinGroupSpacing, Direction.BELOW, Edge.BOTTOM);
RelativePosition betweenGroupBelow = new RelativePosition(Edge.TOP, kBetweenGroupSpacing, Direction.BELOW, Edge.BOTTOM);

Binding hardwareAccelBoxTop = new Binding(Edge.TOP, kMargin, Direction.BELOW, Edge.TOP, Binding.PARENT);

panel.add(hardwareAccelBox, new RelativeConstraints(topLevelLeftEdge, hardwareAccelBoxTop));
panel.add(kungFuGripBox, new RelativeConstraints(indentedLeftEdge, new Binding(withinGroupBelow, hardwareAccelBox)));
panel.add(cherryOnTopBox, new RelativeConstraints(indentedLeftEdge, new Binding(withinGroupBelow, kungFuGripBox)));

panel.add(printMoneyBox, new RelativeConstraints(topLevelLeftEdge, new Binding(betweenGroupBelow, cherryOnTopBox)));
panel.add(dollarsRadio, new RelativeConstraints(indentedLeftEdge, new Binding(withinGroupBelow, printMoneyBox)));
panel.add(eurosRadio, new RelativeConstraints(indentedLeftEdge, new Binding(withinGroupBelow, dollarsRadio)));
panel.add(pesosRadio, new RelativeConstraints(indentedLeftEdge, new Binding(withinGroupBelow, eurosRadio)));
panel.add(rublesRadio, new RelativeConstraints(indentedLeftEdge, new Binding(withinGroupBelow, pesosRadio)));
```

Notice that the first four arguments to the constructor for Binding can instead be composed into a RelativePosition and passed in later as a single object to the Binding constructor.  This allows you to reuse them the same way you can reuse the Bindings themselves.

# Pitfalls #

# Limitations #

RelativeLayout is intended to make creating most interfaces, both simple and complex, as easy and painless as possible.  While this undoubtedly makes designing 95% of the interfaces you will want to create much easier than they would otherwise be, there are occasional interface designs that simply cannot be achieved.  For example, consider this interface (before and after resizing):

![http://relativelayout.googlecode.com/svn/wiki/impossible1.png](http://relativelayout.googlecode.com/svn/wiki/impossible1.png)
![http://relativelayout.googlecode.com/svn/wiki/impossible2.png](http://relativelayout.googlecode.com/svn/wiki/impossible2.png)

Well, sure, we can do that.  Here:
```
JPanel panel = new JPanel(new RelativeLayout());

JScrollPane area1 = new JScrollPane(new JTextArea());
JScrollPane area2 = new JScrollPane(new JTextArea());

Binding area1LeftEdge = new Binding(Edge.LEFT, 20, Direction.RIGHT, Edge.LEFT, Binding.PARENT);
Binding area1RightEdge = new Binding(Edge.RIGHT, 4, Direction.LEFT, Edge.HORIZONTAL_CENTER, Binding.PARENT);
Binding area2LeftEdge = new Binding(Edge.LEFT, 4, Direction.RIGHT, Edge.HORIZONTAL_CENTER, Binding.PARENT);
Binding area2RightEdge = new Binding(Edge.RIGHT, 20, Direction.LEFT, Edge.RIGHT, Binding.PARENT);
Binding top = new Binding(Edge.TOP, 20, Direction.BELOW, Edge.TOP, Binding.PARENT);
Binding bottom = new Binding(Edge.BOTTOM, 20, Direction.ABOVE, Edge.BOTTOM, Binding.PARENT);

panel.add(area1, new RelativeConstraints(area1LeftEdge, area1RightEdge, top, bottom));
panel.add(area2, new RelativeConstraints(area2LeftEdge, area2RightEdge, top, bottom));
```

No problem.  We just use the horizontal center of the window as our anchor and offset each edge by half the distance between them.

Okay, so what about this one?

![http://relativelayout.googlecode.com/svn/wiki/impossible3.png](http://relativelayout.googlecode.com/svn/wiki/impossible3.png)

Assuming the same proportional resizing behavior, we can do this one by...  well, actually, we can't.  At all.  It's impossible, because we'd need to anchor to a point one-third of the distance from the left edge of the window to the right edge, minus half the distance between the panels, minus the left margin.  Apart from being head-explodingly unintuitive, we simply don't have the language we need to say that.  So what do we do?  We do this:

```
JPanel panel = new JPanel(new RelativeLayout());

JScrollPane area1 = new JScrollPane(new JTextArea());
JScrollPane area2 = new JScrollPane(new JTextArea());
JScrollPane area3 = new JScrollPane(new JTextArea());

JPanel innerPanel = new JPanel(new GridLayout(1, 3, 8, 0));
innerPanel.add(area1);
innerPanel.add(area2);
innerPanel.add(area3);

BindingFactory bf = new BindingFactory();
panel.add(innerPanel, new RelativeConstraints(bf.topEdge(), bf.leftEdge(), bf.bottomEdge(), bf.rightEdge()));
```

A cop-out, you say?  Maybe, but this example serve to illustrate one of the best qualities of RelativeLayout: it clears the way for Java's other layout managers to be used for _things they're actually good at, and nothing else_.  You want a bunch of proportionally resizing panes arrayed in a grid?  use GridLayout.  You want a collection of icons or small views that will arrange themselves like words in a text editor as you resize the window?  Use FlowLayout.  You want to actually write the GUI of your application?  That's when you use RelativeLayout.
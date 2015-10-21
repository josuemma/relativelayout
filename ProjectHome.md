**What is RelativeLayout?**

RelativeLayout is a free layout manager for Java SE 1.5.0 and higher (with limited support for Java 1.4.2). Rather than relying on grids or nested boxes, RelativeLayout lets you lay out components relative to each other and the window that contains them. This turns out to be a much more intuitive way of going about it, and virtually any interface you can imagine can be easily created this way.

**How do I install it?**

To use RelativeLayout, you will need to download the RelativeLayout JAR file and the [JAMA matrix library](http://math.nist.gov/javanumerics/jama/#Package), on which RelativeLayout depends.  The documentation for RelativeLayout, as well as its source code, is inside the JAR file.

**How do I use it?**

Check out [this link](http://code.google.com/p/relativelayout/wiki/RelativeLayoutTutorial) (or just click the Wiki link at the top of the page) for a tutorial that will help you get the most out of RelativeLayout. If you want more details, you can have a look at the documentation and look at the examples there — the Binding class is a good place to start.

**What is the license like?**

RelativeLayout is licensed under the New BSD License.  If you can abide by the terms of that license, you're free to use RelativeLayout and its source code however you wish.  The copyright statement for this project (which must be reproduced in any subsequent distributions of the software) is:

_Copyright (c) 2008, Brian Ellis, Rachael Bennett, and Anastassia Drofa.  All rights reserved._

**Why did you write this?**

RelativeLayout was written for two reasons. First and foremost, it was written because every other layout manager for Java is either hopelessly simplistic or monstrously complex: either you can't make the interface look the way you want it, or figuring out how will take you hours of excruciating pain and suffering. Secondly, we got credit for it as a class project, but that's really neither here nor there. Really.

**How is this different from [com.brunchboy.util.swing.relativelayout.RelativeLayout](http://www.onjava.com/onjava/2002/09/18/doc/com/brunchboy/util/swing/relativelayout/package-tree.html)?**

Well, for starters, it's a lot simpler. That package contains fourteen classes and interfaces. Ours contains seven, and three of those are exception classes. There are really only three classes you ever have to use. We're also pretty sure that ours is much easier to use, but quite frankly we haven't really poked around at theirs very much. The whole idea of using Strings to associate components with their constraints sort of turned us off before we got too far.
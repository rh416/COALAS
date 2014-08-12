## Introduction ##
This GUI, along with the accompanying firmware can be used to ensure the chair's hardware has been setup correctly.

It can also be used to configure the way in which sensors behave and report their data.

Although this GUI is based up Processing 2.x it must be run from a more advanced IDE, such as IntelliJ - the free Community Edition can be downloaded from here: http://www.jetbrains.com/idea/download/

## Installation ##
These instructions assume the use of IntelliJ, but will most likely be similar whatever IDE you use.

1. Checkout the code from the repository: Click `VCS` > `Checkout from Version Control` > `Git` (not GitHub)
2. Paste in this URL: `http://84.200.70.31/rm538/sysiass-wheelchar-gui.git` and click `Clone`
3. Go through the steps to create a new project. No need to select special libraries / frameworks, just make sure that the SDK is set to a version of Java (I used 1.7)
4. Add the Processing and Control5 libraries to your project.
    1. Download Processing 2.x to your PC.
    2. Click `File` > `Project Structure`. Under `Libraries` select `Add New Project Library` > `Java` and navigate to the folder where you downloaded Processing 2.x. Select `core/library/core.jar`
    3. Download Control5.
    4. Go back to `Libraries`, select `Add New Project Library` > `Java` and naviate to the Control5 folder. Select `library/control5.jar`
5. You are now ready to start developing!

## Important ##
If you are trying to use Serial commands and you receive the following error:

```
Exception in thread "Animation Thread" java.lang.UnsatisfiedLinkError:
    jssc.SerialNativeInterface.getSerialPortNames()[Ljava/lang/String;
```

Make sure that the JVM has been given the location of the native files for your platform. This can be done by
appending `-Djava.library.path=/absolute-path/to/lib/serial/YOUR-PLATFORM/` to the JVM command line options in the IDE

This value can be set using the Edit Configurations option in IntelliJ in "VM Options for Applet Viewer"

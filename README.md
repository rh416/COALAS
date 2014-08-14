## Introduction ##
This GUI, along with the accompanying firmware can be used to ensure the chair's hardware has been setup correctly.

It can also be used to configure the way in which sensors behave and report their data.

Although this GUI is based up Processing 2.x it must be run from a more advanced IDE, such as IntelliJ - the free Community Edition can be downloaded from here: http://www.jetbrains.com/idea/download/

### External Libraries Used ###
Processing 2.x
    Reference & Download: https://www.processing.org/
G4P - UI library for Processing
    Intro & Download: http://www.lagers.org.uk/g4p/index.html
    Reference: http://lagers.org.uk/g4p/ref/index.html
Shapes - Shape library for Processing
    Intro & Download: http://www.soi.city.ac.uk/~jwo/processing/shapes/

## Installation ##
These instructions assume the use of IntelliJ, but will most likely be similar whatever IDE you use.

1. Checkout the code from the repository: Click `VCS` > `Checkout from Version Control` > `Git` (not GitHub)
2. Paste in this URL: `http://84.200.70.31/sysiass-wheelchair-configuration-tools/sysiass-wheelchar-gui.git` and click `Clone`
3. Go through the steps to create a new project. No need to select special libraries / frameworks, just make sure that the SDK is set to a version of Java (I used 1.7)
4. Add the Processing and Control5 libraries to your project.
    1. Download Processing 2.x to your PC.
    2. Click `File` > `Project Structure`. Under `Libraries` select `Add New Project Library` > `Java` and navigate to the folder where you downloaded Processing 2.x. Select `core/library/core.jar`
    3. Download Control5.
    4. Go back to `Libraries`, select `Add New Project Library` > `Java` and navigate to the Control5 folder. Select `library/control5.jar`
5. You are now ready to start developing!

## Important ##
If you are trying to use Serial commands and you receive the following error:

```
Exception in thread "Animation Thread" java.lang.UnsatisfiedLinkError:
    jssc.SerialNativeInterface.getSerialPortNames()[Ljava/lang/String;
```

Make sure that the JVM has been given the location of the native files for your platform. This can be done by
appending `-Djava.library.path=/absolute-path/to/lib/sysiass-wheelchar-gui/serial/YOUR-PLATFORM/` to the JVM command line options in the IDE

`YOUR-PLATFORM` indicates the Operating System you are using out of:
 + linux32 (32-bit Linux)
 + linux64 (64-bit Linux)
 + macosx (OS X)
 + windows32 (32-bit Windows)
 + window64 (64-bit Windows)

This value can be set using the Edit Configurations option in IntelliJ in "VM Options for Applet Viewer"

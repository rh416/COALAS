## Introduction ##
This GUI, along with the accompanying firmware can be used to ensure the chair's hardware has been setup correctly.

It can also be used to configure the way in which sensors behave and report their data.

Although this GUI is based up Processing 2.x it must be run from a more advanced IDE, such as IntelliJ - the free Community Edition can be downloaded from here: http://www.jetbrains.com/idea/download/

### Requirements ###
This software requires at least version 1.7 of Java to be installed on the machine on which you want it to run.


### External Libraries Used ###
+ **Processing 2.x**

    *Reference & Download: https://www.processing.org/*
    
    
+ **G4P** - UI library for Processing

    *Intro & Download: http://www.lagers.org.uk/g4p/index.html*
    
    *Reference: http://lagers.org.uk/g4p/ref/index.html*
   
    
+ **Shapes** - Shape library for Processing

    *Intro & Download: http://www.soi.city.ac.uk/~jwo/processing/shapes/*

## Installation ##
These instructions assume the use of IntelliJ, but will most likely be similar whatever IDE you use.

1. Checkout the code from the repository: Click `VCS` > `Checkout from Version Control` > `Git` (not GitHub)
2. Paste in this URL: `http://84.200.70.31/sysiass-wheelchair-configuration-tools/sysiass-wheelchar-gui.git` and click `Clone`
3. Go through the steps to create a new project. No need to select special libraries / frameworks, just make sure that the SDK is set to Java version 1.7.
4. Add the Processing and G4P libraries to your project.
    1. Download Processing 2.x to your PC.
    2. Click `File` > `Project Structure`. Under `Libraries` select `Add New Project Library` > `Java` and navigate to the folder where you downloaded Processing 2.x. Select `core/library/core.jar`
    3. Download G4P.
    4. Go back to `Libraries`, select `Add New Project Library` > `Java` and navigate to the Control5 folder. Select `library/G4P.jar`
5. You are now ready to start developing!

## Publishing ##
Once development is complete its time to release a runnable application!

In order to do so, Artifacts must be built from the source. They are configured under `File` > `Project Structure` > `Artifacts`.

Click the green + sign and select `Jar` > `From module with dependencies..`. Set the Main Class to `WheelchairGUI` and click OK.

To build the artifact, select `Build` from the toolbar and click `Build Artifacts`, then `Build`. The artifacts will be exported to a folder called `out / artifacts / sysiass_wheelchair_gui_jar` under the project root folder.

For everything to work properly, the `lib` folder and `run.bat` should also be copied to the output folder.

Finally, the `sysiass_wheelchair_gui_jar` should be zipped and distributed.
 
 
 ## Running the Program ##
 
 Double click `run.bat` (for Windows users) and the application will launch.

## Important ##
If you are trying to use Serial commands and you receive the following error:

```
Exception in thread "Animation Thread" java.lang.UnsatisfiedLinkError:
    jssc.SerialNativeInterface.getSerialPortNames()[Ljava/lang/String;
```

Make sure that the JVM has been given the location of the native files for your platform. This can be done by
appending `-Djava.library.path=/absolute-path/to/sysiass-wheelchar-gui/lib/serial/YOUR-PLATFORM/` to the JVM command line options in the IDE

`YOUR-PLATFORM` indicates the Operating System you are using out of:
 + linux32 (32-bit Linux)
 + linux64 (64-bit Linux)
 + macosx (OS X)
 + windows32 (32-bit Windows)
 + window64 (64-bit Windows)

This value can be set using the Edit Configurations option in IntelliJ in "VM Options for Applet Viewer"

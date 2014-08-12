## Introduction ##
This GUI, along with the accompanying firmware can be used to ensure the chair's hardware has been setup correctly.

It can also be used to configure the way in which sensors behave and report their data.

Although this GUI is based up Processing 2.x it must be run from a more advanced IDE, such as IntelliJ - the free Community Edition can be downloaded from here: http://www.jetbrains.com/idea/download/


## Important ##
If you are trying to use Serial commands and you receive the following error:

```
Exception in thread "Animation Thread" java.lang.UnsatisfiedLinkError:
    jssc.SerialNativeInterface.getSerialPortNames()[Ljava/lang/String;
```

Make sure that the JVM has been given the location of the native files for your platform. This can be done by
appending `-Djava.library.path=/absolute-path/to/lib/serial/YOUR-PLATFORM/` to the JVM command line options in the IDE

This value can be set using the Edit Configurations option in IntelliJ in "VM Options for Applet Viewer"

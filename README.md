##Important##
If you are trying to use Serial commands and you receive the following error:

```
Exception in thread "Animation Thread" java.lang.UnsatisfiedLinkError:
    jssc.SerialNativeInterface.getSerialPortNames()[Ljava/lang/String;
```

Make sure that the JVM has been given the location of the native files for your platform. This can be done by
appending `-Djava.library.path=/absolute-path/to/lib/serial/YOUR-PLATFORM/` to the JVM command line options in the IDE

This value can be set using the Edit Configurations option in IntelliJ in "VM Options for Applet Viewer"

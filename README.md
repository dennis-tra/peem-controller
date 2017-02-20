# PEEM-Controller

A Micro-Manager 2.0 plugin to control the **P**hoto **E**mission **E**lectron **M**icroscope (PEEM) 
together with an image acquisition system.

## Development
You need the following items in your Java class path:
* projects `lib` directory. This dir contains a 64-bit version of the RxTx serial communication 
library ([link](http://rxtx.qbang.org/wiki/index.php/Download))
* `ij.jar` (inside the Micro-Manager 2.0 installation folder)
* `plugins/Micro-Manager` directory (inside the Micro-Manager 2.0 installation folder)

Use Java 1.8 and install all Maven dependencies. 

Have a look at the `Constants.java` file and adapt it to your local setup.

In order to run the plugin together with Micro-Manager 2.0 you need to run `ij.ImageJ` as the main
class (in IntelliJ). Micro-Manager will automatically find your plugin.

After you've built an artifact of this plugin you'll need to place it in the `mmplugins` directory
inside the Micro-Manager 2.0 installation folder).
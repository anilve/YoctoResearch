Yocto Scarthgap Poky Container for ARM
=======================================
This repo is to create an image that is able to run bitbake/poky for the yocto scarthgap version. 
This docker is built for ARM compatible hardware. It has been tested on the Apple MAC mini running on Apple Silicon

Running the container
---------------------
Here a very simple but usable scenario for using the container is described.
It is by no means the *only* way to run the container, but is a great starting point.

* **Create a workdir or volume**
  * **Mac**

    Create a shared volume that will keep the downloaded git code and also the compiled directories like sstate-cache and downloads. These two directories are critical to keep around as they will fill up the first run, but subsequent versions will only be rebuilt is something changes.


* **The docker command**
  * **Mac**

    Assuming you used the *workdir* from above, the command
    to run a container for the first time would be:

    ```
    docker run -it --name my-poky-container -v /Users/anilve/code/YoctoSamples/simplepoky:/workdir bearcreektech/yocto-scarthgap:1.0 /bin/bash

    ```
    
  Let's discuss the options:
    1. docker run command is to run an image
    2. --it is saying we want an interactive terminal
    3. --platform linux/amd64 is specifically for the MAC hardware running on Apple Silicon. That is a ARM based hardware. If you do not add this, it assume a Intel based x86 hardware and you get errors on the MAC.
    4. --name gives the name to the container when it is running
    5. -v (local directory on workstation):(directory to map on the container). This is a local directory on your workstation that is mapped to a shared directory on the linux container. Makes it easy for disk space to grow as you get git code and also compile it.
    6. crops/poky:ubuntu-22.04 is the name of the image to use.
    7. /bin/bash is the script to use when the container terminal starts up.

  This should put you at a prompt similar to:
  ```
  poky@da73d762691e:/workdir$
  ```

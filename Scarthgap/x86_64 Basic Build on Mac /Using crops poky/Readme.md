# Build a Yocto image using crops/poky docker on Mac

## Setup the hardware?
The build is going to be done on a MAC mini with Apple Silicon

Make sure that Docker Desktop is installed and running the latest verssion.

Download the iamge to use:
> docker pull crops/poky:ubuntu-22.04

Once that is complere, check if the image is downloaded:
> docker images

Output looks like this

> IMAGE : crops/poky:ubuntu-22.04

> ID : b9c23dda3

Create a shared volume that will keep the downloaded git code and also the compiled directories like sstate-cache and downloads. These two directories are critical to keep around as they will fill up the first run, but subsequent versions will only be rebuilt is something changes.

## Startup the docker image

Use this command to start the docker image

> docker run -it --platform linux/amd64 --name my-poky-container -v /Users/anilve/code/YoctoSamples/simplepoky:/workdir crops/poky:ubuntu-22.04 /bin/bash

Lets explain this command:
1. docker run command is to run an image
2. --it is saying we want an interactive terminal
3. --platform linx/amd64 is specifically for the MAC hardware running on Apple Silicon. That is a AMD based hardware. If you do not add this, it assume a Intel based x86 hardware and you get errors on the MAC.
4. --name gives the name to th container when it is running
5. -v (local directory on workstation):(directory to map on the container). This is a local directory on your workstation that is mapped to a shared directory on the linux container. Makes it easy for disk space to grow as you get git code and also compile it.
6. crops/poky:ubuntu-22.04 is the name of the image to use.
7. /bin/bash is the script to use when the container terminal starts up.

## Settip up poky and bitbake.

Change to the workdir:
> cd /workdir

Clone the git code for poky that is labelled as scarthgap
> git clone -b scarthgap --depth=1 git://git.yoctoproject.org/poky 

Once we have cloned the poky, change into the poky directory
> cd /workdir/poky

Initialize the poky builder:
> source ./oe-init-build-env

A new directory called build is created under the /workdir/poky

This has also setup bitbaKe with the path and directories needed.
> /workfir/poky/build/conf directory has configuration files.
    >> bblayers.conf - which layers are included in the BB. <br>
	>> local.conf - Various settings including the default machine we are compiling for. Set to qemux86-64

On a Mac, the shared location is not case sensitive and you will get an error. You can resolve this two ways:
1. Create a new disk that is case sensitive
2. Setup a temp directory on the docker since that is a case sensitive OS

Lets create a new directory /home/pokyuser/tempdir
> mkdir /home/pokyuser/tempdir

### Update the local conf file

Set the TMPDIR
> TMPDIR = "/home/pokyuser/tempdir"

To enable this feature, add the following line to your conf/local.conf file: 
> INHERIT += "rm_work"

Update the thread and parallel make in local.conf
> BB_NUMBER_THREADS = "2"
> PARALLEL_MAKE = "-j 2"

## Run the bitbake command to build

We shall run a basic core minimal build.
> bitbake core-image-minimal

You will see output like this:
> Loading cache: 100% <br>
> Loaded 0 entries from dependency cache. <br>
> Parsing recipes: 100% <br>
> Parsing of 920 .bb files complete (0 cached, 920 parsed). 1878 targets, 47 skipped, 0 masked, 0 errors. <br>
> NOTE: Resolving any missing task queue dependencies <br>
> <br>
> Build Configuration:
 >> BB_VERSION           = "2.8.1" <br>
 >> BUILD_SYS            = "x86_64-linux" <br>
 >> NATIVELSBSTRING      = "ubuntu-22.04" <br>
 >> TARGET_SYS           = "x86_64-poky-linux" <br>
 >> MACHINE              = "qemux86-64" <br>
 >> DISTRO               = "poky" <br>
 >> DISTRO_VERSION       = "5.0.17" <br>
 >> TUNE_FEATURES        = "m64 core2" <br>
 >> TARGET_FPU           = "" <br>
 >> meta                 <br>
 >> meta-poky            <br>
 >> meta-yocto-bsp       = "scarthgap:8643f911602949518c5c5474726b49f943e36b83" <br>



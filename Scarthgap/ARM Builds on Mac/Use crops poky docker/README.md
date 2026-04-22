# Building a x86 Linux image on MAC using crops/poky docker

## Setup the hardware?
The build is going to be done on a MAC mini with Apple Silicon.

Make sure that Docker Desktop is installed and running the latest version.

Download the image to use:
> docker pull crops/poky:ubuntu-22.04

Once that is complete, check if the image is downloaded:
> docker images

Output looks like this

> IMAGE : crops/poky:ubuntu-22.04

> ID : b9c23dda3

Create a shared volume that will keep the downloaded git code and also the compiled directories like sstate-cache and downloads. These two directories are critical to keep around as they will fill up the first run, but subsequent versions will only be rebuilt is something changes.

## Startup the docker image

Use this command to start the docker image

> docker run -it --platform linux/amd64 --name my-poky-container -v /Users/anilve/code/YoctoSamples/simplepoky:/workdir crops/poky:ubuntu-22.04 /bin/bash

Explaining this command:
1. docker run command is to run an image
2. --it is saying we want an interactive terminal
3. --platform linux/amd64 is specifically for the MAC hardware running on Apple Silicon. That is a ARM based hardware. If you do not add this, it assume a Intel based x86 hardware and you get errors on the MAC.
4. --name gives the name to th container when it is running
5. -v (local directory on workstation):(directory to map on the container). This is a local directory on your workstation that is mapped to a shared directory on the linux container. Makes it easy for disk space to grow as you get git code and also compile it.
6. crops/poky:ubuntu-22.04 is the name of the image to use.
7. /bin/bash is the script to use when the container terminal starts up.

## Setting up poky and bitbake.

Change to the workdir:
> cd /workdir

Clone the git code for poky that is labelled as scarthgap
> git clone -b scarthgap --depth=1 git://git.yoctoproject.org/poky 

Once we have cloned the poky, change into the poky directory
> cd /workdir/poky

### Get the meta directory for arm

By default poky does not have the support for arm hardware. So we need to get the meta layer meta-arm.

Clone the directory from git, and get the scarthgap build
> git clone -b scarthgap --depth=1 git://git.yoctoproject.org/meta-arm

### Initialize the build environment

Initialize the poky builder:
> source ./oe-init-build-env

A new directory called build is created under the /workdir/poky

This has also setup bitbake with the path and directories needed.
> /workdir/poky/build/conf directory has configuration files.
    >> bblayers.conf - which layers are included in the BB. <br>
	>> local.conf - Various settings including the default machine we are compiling for. Leaving the default as qemux86-64.

### Add the bitbake layers for arm

Get to the directory with the configurations
> cd /workdir/poky/build/conf

Add the arm layers
> bitbake-layers add-layer /workdir/poky/meta-arm/meta-arm-toolchain

> bitbake-layers add-layer /workdir/poky/meta-arm/meta-arm

Make sure that the new layers are available in the bblayers.conf file

### Update the local.conf file

On a Mac, the shared location is not case sensitive and you will get an error. You can resolve this two ways:
1. Create a new disk that is case sensitive
2. Setup a temp directory on the docker since that is a case sensitive OS

Lets create a new directory /home/pokyuser/tempdir
> mkdir /home/pokyuser/tempdir

Mke sure you are in the local conf directory
> cd /workdir/poky/build/conf

Update the file local.conf

Set the machine to arm:
> MACHINE = "qemuarm64"

Set the TMPDIR
> TMPDIR = "/home/pokyuser/tempdir"

Clear the temporary files to save some diskspace add the following line to your conf/local.conf file: 
> INHERIT += "rm_work"

Update the thread and parallel make in local.conf
> BB_NUMBER_THREADS = "2"
> PARALLEL_MAKE = "-j 2"

Add the output types to add an ISO
> IMAGE_FSTYPES += "iso"

## Run the bitbake command to build

Change to the poky directory:
> cd /workdir/poky

We shall run a basic core minimal build.
> bitbake core-image-minimal

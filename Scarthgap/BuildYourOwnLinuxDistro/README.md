# This project folder deals with building you own Linux Distribution

## What is a DISTRO?


## Lets do the build.

### Setup the docker container to do the work.

> docker run -it --name my-poky-container -v /Users/anilve/code/YoctoSamples/simplepoky:/workdir bearcreektech/yocto-scarthgap:1.1 /bin/bash

> cd /workdir

> git config --global url."https://git.yoctoproject.org/git".insteadOf "git://git.yoctoproject.org"

Clone the git code for poky that is labelled as scarthgap
> git clone -b scarthgap --depth=1 git://git.yoctoproject.org/poky 

Once we have cloned the poky, change into the poky directory
> cd /workdir/poky

### Get the meta directory for ARM

By default poky does not have the support for ARM hardware. So we need to get the meta layer meta-arm.

Clone the directory from git, and get the scarthgap build
> git clone -b scarthgap --depth=1 git://git.yoctoproject.org/meta-arm

Initialize the poky builder:
> source ./oe-init-build-env

A new directory called build is created under the /workdir/poky. This is your toot build workspace.

Environment variables like PATH are also setup.

### Add the bitbake layers for ARM

Get to the directory with the configurations
> cd /workdir/poky/build/conf

Add the arm layers
> bitbake-layers add-layer /workdir/poky/meta-arm/meta-arm-toolchain

> bitbake-layers add-layer /workdir/poky/meta-arm/meta-arm

Make sure that the new layers are available in the bblayers.conf file

On a Mac, the shared location is not case sensitive and you will get an error. You can resolve this two ways:
1. Create a new disk that is case sensitive
2. Setup a temp directory on the docker since that is a case sensitive OS

Lets create a new directory /home/pokyuser/tempdir
> mkdir /home/poky/tempdir

Update the file local.conf

Set the machine to arm:
> MACHINE = "qemuarm64"

Set the TMPDIR
> TMPDIR = "/home/poky/tempdir"

Clear the temporary files to save some diskspace add the following line to your conf/local.conf file: 
> INHERIT += "rm_work"

Update the thread and parallel make in local.conf
> BB_NUMBER_THREADS = "2"
> PARALLEL_MAKE = "-j 2"


### Create a custom meta layer

> cd /workdir/poky

> mkdir meta-bearcreektech

> mkdir /workdir/poky/meta-bearcreektech/conf

Create the local.conf file for the new meta layer

> bitbake-layers add-layer /workdir/poky/meta-bearcreektech

### Create a new distro configuration

mkdir -p conf/distro/bearcreeklinux/conf
cd conf/distro/bearcreeklinux/conf

Key Points:
- The distro.conf file resides in the conf/distro/ directory of your Yocto Project workspace.
- It is used to define settings that apply across all builds for a specific distribution.
- You can override or extend these settings using other configuration files (e.g., machine-specific configurations).
- Common sections include:
    - DISTRO: The name of the distribution.
    - MACHINE: The target hardware platform.
    - PACKAGE_CLASSES: Specifies which package manager to use.
    - IMAGE_FEATURES and IMAGE_INSTALL: Controls what goes into the final image.


### Explaining out Distro

package_ipk
- Creates IPK (Debian-style .deb) packages.
- Suitable for embedded Linux systems using Debian or Ubuntu package management.

## Build the Linux Distribution

#### Step 1: Set up environment
> export DISTRO="bearcreeklinux"

#### Step 2: Build your image
> bitbake core-image-minimal

Build Configuration:
BB_VERSION           = "2.8.1"
BUILD_SYS            = "aarch64-linux"
NATIVELSBSTRING      = "ubuntu-24.04"
TARGET_SYS           = "aarch64-oe-linux"
MACHINE              = "qemux86-64"
DISTRO               = "bearcreeklinux"
DISTRO_VERSION       = "nodistro.0"
TUNE_FEATURES        = "aarch64 crc cortexa57"
TARGET_FPU           = ""
meta                 
meta-poky            
meta-yocto-bsp       = "scarthgap:44dcf08572ce391d7c0df4f8c7510af5e096baca"
meta-arm-toolchain   
meta-arm             = "scarthgap:a81c19915b5b9e71ed394032e9a50fd06919e1cd"
meta-bearcreektech   = "scarthgap:44dcf08572ce391d7c0df4f8c7510af5e096baca"



Images can be found here

$HOME/tempdir-glibc/deploy/images/qemuarm64
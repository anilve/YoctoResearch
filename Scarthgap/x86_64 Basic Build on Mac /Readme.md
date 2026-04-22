# Build a x86_64 Basic Linux on a Mac Mini

This projects is to detail how you can build a Linux image for a x86_64 usage on an Apple MAC Mini that is using Apple Silicon.

## Key Challenges
1) The Apple Silicon is ARM based. So lots of commands to run the build fail.
2) 


## Using amd64/ubuntu Docker

Run command to start the docker:
> docker run -it 06a7aaddbe01 /bin/bash

Once logged in as root. Update the apt packages.
> apt-get update

Then install the packages needed for Yocto based on https://docs.yoctoproject.org/scarthgap/brief-yoctoprojectqs/index.html. I added in the vim package as I like to have vi editor
> apt-get install build-essential chrpath cpio debianutils diffstat file gawk gcc git iputils-ping libacl1 liblz4-tool locales python3 python3-git python3-jinja2 python3-pexpect python3-pip python3-subunit socat texinfo unzip wget xz-utils zstd vim

## Create a docker to build locally
The docker file for building a Ubuntu 26.04 that is capable of building a Yocto Scarthgap iamge can be found in the 
> AMD-Ubuntu-Poky-Docker directory.

Go to the directory and run the following command:
> docker build --platform linux/amd64 -t amdtest1 .

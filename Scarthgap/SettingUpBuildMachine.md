# Setting up the foundation for Yocto Builds

Yocto is currently my preferred way to build custom Linux systems, whether they are for embedded devices or regular systems. Essentially, this means we have to build the software ourselves. Thankfully, Yocto comes with fantastic tools like Bitbake and Poky that make the entire process much easier.

To start a build, you first need to set up a proper build machine. Let’s explore exactly what it takes to get a machine ready for these builds.

If you are working within a Git CI/CD pipeline, I always recommend working with the DevOps team to ensure we have a robust Ubuntu runner available for the builds.

## FOCUS on building on a Laptop/Workstation
In this post, I want to focus on how we can perform these builds directly on a laptop.

Based on my experience, I find it easiest to build on an Ubuntu machine. You can use either the Desktop or the server version. To keep things simple, I’m going to use the Ubuntu Desktop since that’s what most developers are already familiar with. Remember, all the build commands are issued through the terminal using bash scripts, so please be careful!

## Building on a Windows Machine
There are two main ways I approach this on Windows.

### Virtual Machine for Windows

My favorite method is using free software, so I use VirtualBox (https://www.virtualbox.org/). It's owned by Oracle now, and they keep it up-to-date, which means it works very well for me.

First, make sure you have VirtualBox installed. Then, download the Ubuntu Desktop ISO for a Windows machine from https://ubuntu.com/download/desktop. Since you are likely using Intel or AMD hardware, make sure you select the ISO matching your architecture.

Once the ISO is saved locally, create a new Virtual Machine and load the Ubuntu image. Next, I always check the hardware requirements for the Yocto Scarthgap to make sure I have enough resources for the build machine (https://docs.yoctoproject.org/scarthgap/brief-yoctoprojectqs/index.html).

After the machine is set up, I log in and update all the latest updates for Ubuntu. Finally, the last step is to install all the required packages on the Linux machine so I can run the build tools: https://docs.yoctoproject.org/scarthgap/brief-yoctoprojectqs/index.html#build-host-packages.

### Docker on Windows

Docker is another fantastic way to get a build machine running right on my workstation. It’s quite easy to set up on Windows.

There are several cross-platform Docker images with Poky already installed that I find useful. I usually pull these from the Docker Hub or check out resources here: https://github.com/crops/docker-win-mac-docs/wiki. The images I used when writing this blog were based on an older Ubuntu version that they built the Poky Docker images on.

However, if you prefer to use a standard Ubuntu Desktop image, that works perfectly fine too. If you decide to build your own Docker image, just remember that you will need to add the necessary kernel packages.

## Building on a Mac Machine (Apple Silicon)
I don't have an Intel-based Mac to test this on, so I’ve tested my ideas on an Apple Silicon machine instead.

One key thing to remember about Apple Silicon is that it uses the same architecture as AMD processors. This means there are some slight differences compared to the Windows-based hardware steps.

### Virtual Machine for Mac

I use VirtualBox for Mac (https://www.virtualbox.org/).

Once VirtualBox is installed, I download the Ubuntu Desktop ISO for a Mac machine. Remember, I need the AMD version for this: https://ubuntu.com/download/desktop.

After saving the ISO locally, I create a new VM and load the image. Just like before, I check the hardware requirements for the Yocto Scarthgap to ensure I have enough resources for the build machine (https://docs.yoctoproject.org/scarthgap/brief-yoctoprojectqs/index.html).

Once the machine is set up, I log in and update all the latest updates for Ubuntu. Finally, I install the required packages on the Linux machine to run the build tools: https://docs.yoctoproject.org/scarthgap/brief-yoctoprojectqs/index.html#build-host-packages.

### Docker on Mac

To run Docker on my Mac, I set up Docker Desktop.

I again use those cross-platform Docker images with Poky installed. You can find them on the Docker Hub or here: https://github.com/crops/docker-win-mac-docs/wiki. Like before, I used an older Ubuntu version when I wrote this, which was based on the Poky Docker images.

When I try to run the docker run command on my Mac, I often get an error about the platform. To fix this, I need to add the 

    --platform linux/amd64 

parameter to the command to make it run correctly.

Again, if you just want to work with a standard Ubuntu Desktop image, that works fine. But if you plan on building your own Docker image, remember that you will definitely have to add the kernel packages yourself.
# Building a Custom Linux Flavor with BitBake for Docker

This guide walks through configuring and building a custom Linux distribution (flavor) using **Yocto Scarthgap (5.0 LTS)**, where the target output artifact is a **Docker / OCI container image**.

---

## Architecture: Two Methods for Docker Outputs

When compiling embedded Linux for a physical board or virtual machine, BitBake compiles a Linux kernel (`Image`), device tree blobs (`.dtb`), bootloaders (U-Boot/GRUB), and partition disk images (`.wic`/`.ext4`).

In contrast, **Docker containers share the host machine's Linux kernel**. A container image requires **only the userspace root filesystem (rootfs)** and container metadata (entrypoint, environment variables, working directory).

There are two primary ways to achieve container output in Yocto:

### Comparison: Method 1 vs. Method 2

> [!NOTE]
> ### Note on Method 1 (Direct Rootfs + Dockerfile)
> In **Method 1**, you configure BitBake with:
> ```bitbake
> IMAGE_FSTYPES = "tar.xz"
> ```
> And pair it with a minimal `Dockerfile`:
> ```dockerfile
> FROM scratch
> ADD my-image.rootfs.tar.xz /
> CMD ["/bin/sh"]
> ```
> **Pros:** Requires zero external layers; uses pure native OpenEmbedded/Poky primitives; familiar to Docker users running `docker build`.
>
> **Why we are choosing Method 2 for this project:**
> In this guide, we use **Method 2 (`meta-virtualization`)** to leverage standard **BitBake layer concepts**:
> 1. **Modular Layer Architecture:** Demonstrates how BitBake layers depend on each other (`LAYERDEPENDS`) and introduce cross-layer distribution features (`DISTRO_FEATURES += "virtualization"`).
> 2. **Native OCI Classes:** Utilizes `image-container.bbclass` and `image-oci.bbclass` to bake container metadata (entrypoints, authors, environment variables, tags) directly into BitBake recipes.
> 3. **Multiple Output Formats:** Generates native OCI archives (`.rootfs-oci.tar` for `docker load`), container tarballs for `docker import`, and automatically generates the Method 1 `Dockerfile` for ultimate flexibility.

---

## Step-by-Step Build Guide

### Step 1: Start Your Poky Build Container (Mac Apple Silicon)

From your host Mac, launch your Poky development container (from [`BuildYourOwnDocker`](file:///Users/anilve/code/GitHub/YoctoResearch/Scarthgap/BuildYourOwnDocker) or `crops/poky`):

```bash
docker run -it --name my-poky-container \
  -v /Users/anilve/code/YoctoSamples/simplepoky:/workdir \
  bearcreektech/yocto-scarthgap:1.1 /bin/bash
```

Navigate to your workspace:
```bash
cd /workdir
git config --global url."https://git.yoctoproject.org/git".insteadOf "git://git.yoctoproject.org"
```

---

### Step 2: Clone Poky and Required Layers

Clone the **`scarthgap`** branch of all necessary layers:

```bash
# 1. Poky core
git clone -b scarthgap --depth=1 git://git.yoctoproject.org/poky

Change to the poky doirectory for the next steps.

# 2. ARM layer (for qemuarm64 / Apple Silicon targets)
git clone -b scarthgap --depth=1 git://git.yoctoproject.org/meta-arm

# 3. OpenEmbedded common layers (dependencies for meta-virtualization)
git clone -b scarthgap --depth=1 https://github.com/openembedded/meta-openembedded.git

# 4. Virtualization layer (for OCI and container classes)
git clone -b scarthgap --depth=1 https://git.yoctoproject.org/meta-virtualization
```

---

### Step 3: Initialize the Build Environment

```bash
cd /workdir/poky
source ./oe-init-build-env
```

This creates the `/workdir/poky/build` directory and initializes BitBake environment variables.

---

### Step 4: Add the Layers

Add the layers in dependency order:

```bash
# Hardware and toolchain layers
bitbake-layers add-layer /workdir/poky/meta-arm/meta-arm-toolchain
bitbake-layers add-layer /workdir/poky/meta-arm/meta-arm

# meta-openembedded dependencies
bitbake-layers add-layer /workdir/meta-openembedded/meta-oe
bitbake-layers add-layer /workdir/meta-openembedded/meta-python
bitbake-layers add-layer /workdir/meta-openembedded/meta-networking
bitbake-layers add-layer /workdir/meta-openembedded/meta-filesystems

# Virtualization layer
bitbake-layers add-layer /workdir/meta-virtualization

# Custom Bear Creek Tech layer
bitbake-layers add-layer /workdir/poky/meta-bearcreektech
```

Verify your layer configuration:
```bash
bitbake-layers show-layers
```

---

### Step 5: Configure `local.conf` and Distro

Ensure the following settings are active in `/workdir/poky/build/conf/local.conf`:

```bitbake
# Target architecture
MACHINE = "qemuarm64"

# Custom container distribution
DISTRO = "bearcreekdocker"

# Enable virtualization distro features required by meta-virtualization
DISTRO_FEATURES:append = " virtualization"

# CRITICAL SPEED OPTIMIZATION:
# Containers don't need a real Linux kernel. Using linux-dummy bypasses
# compiling the hardware kernel, saving 20-40 minutes and gigabytes of space.
PREFERRED_PROVIDER_virtual/kernel = "linux-dummy"

# Build resource management
TMPDIR = "/home/poky/tempdir"
INHERIT += "rm_work"
BB_NUMBER_THREADS = "2"
PARALLEL_MAKE = "-j 2"
```

Also verify `/workdir/poky/build/conf/distro/bearcreekdocker.conf`:
```bitbake
DISTRO = "bearcreekdocker"
DISTRO_NAME = "Bear Creek Tech Docker Linux"
DISTRO_VERSION = "1.0.0"
PACKAGE_CLASSES = "package_ipk"
MACHINE = "qemuarm64"
DISTRO_FEATURES = "virtualization ipv4 ipv6"
IMAGE_FSTYPES ?= "tar.xz"
PREFERRED_PROVIDER_virtual/kernel = "linux-dummy"
```

---

### Step 6: Review the Image Recipe

The image recipe [`meta-bearcreektech/recipes-core/images/bearcreek-docker-image.bb`](file:///Users/anilve/code/GitHub/YoctoResearch/Scarthgap/BuildyourOwnLinuxDocker/meta-bearcreektech/recipes-core/images/bearcreek-docker-image.bb) inherits both `image-container` and `image-oci`:

```bitbake
SUMMARY = "Bear Creek Tech Custom Linux Docker Container"
LICENSE = "MIT"

inherit core-image
inherit image-container
inherit image-oci

# Formats generated: OCI archive, container tarball, compressed rootfs tarball
IMAGE_FSTYPES = "container oci tar.xz"
OCI_IMAGE_TAR_OUTPUT = "true"

# OCI Container metadata
OCI_IMAGE_ENTRYPOINT = "/bin/sh"
OCI_IMAGE_WORKINGDIR = "/"
OCI_IMAGE_AUTHOR = "Bear Creek Tech"
OCI_IMAGE_AUTHOR_EMAIL = "info@bearcreektech.com"
OCI_IMAGE_TAG = "1.0"
OCI_IMAGE_RUNTIME_UID = "0"
OCI_IMAGE_ENV_VARS = "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin TERM=xterm-256color"

# Strip kernel/modules (containers run on host kernel)
IMAGE_INSTALL:remove = "kernel-image kernel-modules"
PACKAGE_EXCLUDE += "kernel-image* kernel-modules*"

# Custom flavor packages
IMAGE_INSTALL = "\
    base-files \
    base-passwd \
    netbase \
    busybox \
    bash \
    curl \
    wget \
    iproute2 \
    ca-certificates \
    vim-tiny \
"

# Automated Dockerfile generation for Method 1 workflows
python do_create_dockerfile() {
    import os
    deploy_dir = d.getVar('DEPLOY_DIR_IMAGE')
    image_name = d.getVar('IMAGE_LINK_NAME') or d.getVar('IMAGE_NAME')
    tar_file = f"{image_name}.rootfs.tar.xz"

    dockerfile_content = f"""FROM scratch
ADD {tar_file} /
ENV LANG=C.UTF-8
CMD ["/bin/bash"]
"""
    with open(os.path.join(deploy_dir, "Dockerfile"), "w") as f:
        f.write(dockerfile_content)
}
addtask create_dockerfile after do_image_tar before do_build
```

---

### Step 7: Build the Image

Set the distribution environment variable and invoke BitBake:

```bash
export DISTRO="bearcreekdocker"
bitbake bearcreek-docker-image
```

---

### Step 8: Deploy and Run with Docker

The build artifacts are generated under `/home/poky/tempdir-glibc/deploy/images/qemuarm64/`:

```bash
cd /home/poky/tempdir-glibc/deploy/images/qemuarm64/
ls -lh *bearcreek-docker-image*
```

You have three options to run the image in Docker:

#### Option A: Load directly via OCI Archive (Method 2)
```bash
docker load -i bearcreek-docker-image-qemuarm64.rootfs-oci.tar
```

#### Option B: Import directly from Rootfs Tarball
```bash
cat bearcreek-docker-image-qemuarm64.rootfs.tar.xz | docker import - bearcreeklinux:1.0
```

#### Option C: Build using the generated Dockerfile (Method 1)
```bash
docker build -t bearcreeklinux:1.0 .
```

#### Run the Container
```bash
docker run -it --rm bearcreeklinux:1.0 /bin/bash
```

Inside your container, verify your custom distribution:
```bash
cat /etc/os-release
uname -a
curl --version
vi --version
```

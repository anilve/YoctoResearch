SUMMARY = "Bear Creek Tech Custom Linux Docker Container"
DESCRIPTION = "Custom lightweight Linux container image generated with meta-virtualization"
LICENSE = "MIT"

inherit core-image
inherit image-container
inherit image-oci

# Container image formats to generate
# In meta-virtualization, the OCI format name is 'oci' (not 'oci-archive')
IMAGE_FSTYPES = "container oci tar.xz"
OCI_IMAGE_TAR_OUTPUT = "true"

# OCI Container metadata configuration
OCI_IMAGE_ENTRYPOINT = "/bin/sh"
OCI_IMAGE_WORKINGDIR = "/"
OCI_IMAGE_AUTHOR = "Bear Creek Tech"
OCI_IMAGE_AUTHOR_EMAIL = "info@bearcreektech.com"
OCI_IMAGE_TAG = "1.0"
OCI_IMAGE_RUNTIME_UID = "0"
OCI_IMAGE_ENV_VARS = "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin TERM=xterm-256color"

# Strip kernel and boot modules since containers run on the host kernel
IMAGE_INSTALL:remove = "kernel-image kernel-modules"
PACKAGE_EXCLUDE += "kernel-image* kernel-modules*"

# Essential user-space utilities for this Linux flavor
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

# Remove unnecessary init managers and package management overhead from the container
IMAGE_FEATURES = ""
IMAGE_LINGUAS = ""

# Auto-generate a Dockerfile in the deploy directory alongside the build artifacts
python do_create_dockerfile() {
    import os
    deploy_dir = d.getVar('DEPLOY_DIR_IMAGE')
    image_name = d.getVar('IMAGE_LINK_NAME') or d.getVar('IMAGE_NAME')
    tar_file = f"{image_name}.rootfs.tar.xz"

    dockerfile_content = f"""# Auto-generated Dockerfile for {d.getVar('DISTRO')} ({d.getVar('DISTRO_VERSION')})
FROM scratch
ADD {tar_file} /
ENV LANG=C.UTF-8
CMD ["/bin/bash"]
"""

    dockerfile_path = os.path.join(deploy_dir, "Dockerfile")
    with open(dockerfile_path, "w") as f:
        f.write(dockerfile_content)
    bb.plain(f"Generated Dockerfile at {dockerfile_path}")
}

addtask create_dockerfile after do_image_tar before do_build

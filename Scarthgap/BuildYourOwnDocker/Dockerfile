FROM ubuntu:24.04

# Install locales and set environment
RUN apt-get update && apt-get install -y locales && \
    locale-gen en_US.UTF-8 && \
    rm -rf /var/lib/apt/lists/*

# Set environment variables
ENV LANG=en_US.UTF-8 \
    LANGUAGE=en_US:en \
    LC_ALL=en_US.UTF-8

# Install packages for Yocto Scarthgap 
RUN apt-get update && apt-get install -y \
    build-essential \
    chrpath \
    cpio \
    debianutils \
    diffstat \
    file \
    gawk \
    gcc \
    git \
    iputils-ping \
    libacl1 \
    lz4 \
    python3 \
    python3-git \
    python3-jinja2 \
    python3-pexpect \
    python3-pip \
    python3-subunit \
    socat \
    texinfo \
    unzip \
    vim \
    wget \
    xz-utils \
    zstd \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /workdir

# Now lets create a new user called poky
RUN useradd -m -s /bin/bash poky && \
    usermod -aG sudo poky &&echo "poky ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers

RUN chown -R poky:poky /workdir

USER poky

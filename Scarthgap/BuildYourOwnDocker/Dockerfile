FROM ubuntu:26.04

# install app dependencies
# Install locales package
RUN apt-get update && apt-get install -y locales
# Generate the desired locale
RUN locale-gen en_US.UTF-8

# Set environment variables
ENV LANG=en_US.UTF-8 \
    LANGUAGE=en_US:en \
    LC_ALL=en_US.UTF-8

# Install packages for Yocto Scarthgap 
RUN apt-get install -y \
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

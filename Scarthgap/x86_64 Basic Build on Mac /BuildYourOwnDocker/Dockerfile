FROM ubuntu:26.04

# install app dependencies
# Install locales package
RUN apt-get update && apt-get install -y locales=2.43-2ubuntu1

# Generate the desired locale
RUN locale-gen en_US.UTF-8

# Set environment variables
ENV LANG=en_US.UTF-8 \
    LANGUAGE=en_US:en \
    LC_ALL=en_US.UTF-8

# Install packages for Yocto Scarthgap 
RUN apt-get install -y --no-install-recommends \
    build-essential=12.12ubuntu2 \
    chrpath=0.18-1build1 \ 
    cpio=2.15+dfsg-2.1 \
    debianutils=5.23.2build1 \
    diffstat=1.68-1 \
    file=1:5.46-5build2 \
    gawk=1:5.3.2-1ubuntu1 \
    gcc=4:15.2.0-5ubuntu1 \
    git=1:2.53.0-1ubuntu1 \
    iputils-ping=3:20250605-1ubuntu1 \
    libacl1=2.3.2-2 \
    lz4=1.10.0-8 \
    python3=3.14.3-0ubuntu1 \
    python3-git=3.1.46-1 \
    python3-jinja2=3.1.6-1build1 \
    python3-pexpect=4.9-4 \
    python3-pip=25.1.1+dfsg-1ubuntu2 \
    python3-subunit=1.4.4+1-1 \
    socat=1.8.1.1-1 \
    texinfo=7.2-5ubuntu2 \
    unzip=6.0-29ubuntu1 \
    vim=2:9.1.2141-1ubuntu2 \
    wget=1.25.0-2ubuntu4 \
    xz-utils=5.8.3-1 \
    zstd=1.5.7+dfsg-3 \
    && rm -rf /var/lib/apt/lists/*

ENTRYPOINT [ "/bin/bash" ]

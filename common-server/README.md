## Building ##

### Prerequisites ###
make
cmake
g++
pkg-config
liblog4cpp5-dev
libssl-dev
libtool
Linux (any Version)

### Build ###
With fullfilled prerequisites perform the following steps:
1. Compile and deploy customized version of *libnopoll* in dir *libnopoll* via *autogen.sh*, *make* and *make install*
2. Compile *common-server* via *INSTALL*
3. Create dir */etc/common-server/*
4. Copy trace.ini to */etc/common-server/*
5. Create dir */var/log/commons-server/*

---------------

## Execution ##
Run *build/common-server*. Add the *-d* option for daemon mode.

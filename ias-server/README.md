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
2. Compile *ias-server* via *INSTALL*
3. Create dir */etc/ias-server/*
4. Copy trace.ini to */etc/ias-server/*
5. Customize and Copy *config.json* to */etc/ias-server/*
6. Create dir */var/log/ias-server/*

---------------

## Execution ##
Run *build/ias-server*. Add the *-d* option for daemon mode.

For the usage of TLS-Secured connections, perform the following steps:
1. Create dir */var/opt/ias-server/certs/{my_fqdn}/*
2. Deploy Certificate with filename *{my_fqdn}.crt* to the folder created in step 1.
3. Deploy Private Key with filename *{my_fqdn}.key* to the folder created in step 1.
4. State the TLS port to be used in *config.json*
5. Restart *ias-server*

## Docker container ##

```bash
cd <nntool_eu_directory>
docker build -f ias-server.Dockerfile -t ias-server . 
docker run --rm -it ias-server
```


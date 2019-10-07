FROM debian:10

RUN apt update && apt upgrade -y
RUN apt install -y build-essential make cmake automake g++ libtool liblog4cpp5-dev libssl-dev
RUN apt clean

# libnopoll autogen.sh needs /usr/bin/sed, also grep
RUN ln -s /bin/sed /usr/bin/sed
RUN ln -s /bin/grep /usr/bin/grep

# build ias-server

WORKDIR /nntool

ADD libnopoll libnopoll
ADD ias-libnntool ias-libnntool
ADD ias-server ias-server

#ENV IAS_SERVER_CMAKE_BUILD_TYPE DEBUG
#cmake -DCMAKE_BUILD_TYPE="${IAS_SERVER_CMAKE_BUILD_TYPE}" .. && \

RUN cd ias-server && \
    mkdir docker_build && \
    cd docker_build && \
    cmake .. && \
    make

RUN mkdir -p /var/log/ias-server
RUN mkdir -p /etc/ias-server
RUN mkdir -p /var/opt/ias-server/certs

RUN cp ias-server/trace.ini /etc/ias-server/.
RUN cp ias-server/config.json /etc/ias-server/.

# TODO: generate and configure server certificates

EXPOSE 80
EXPOSE 8080
EXPOSE 443

EXPOSE 80/udp
EXPOSE 8080/udp
EXPOSE 443/udp

CMD ["./ias-server/docker_build/ias-server"]

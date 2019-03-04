FROM debian:9

RUN apt update && apt upgrade -y
RUN apt install -y build-essential cmake libssl-dev libtool m4 automake pkg-config g++ liblog4cpp5-dev

# build libnopoll

WORKDIR /libnopoll

ADD libnopoll .

# libnopoll autogen.sh needs /usr/bin/sed, also grep
RUN ln -s /bin/sed /usr/bin/sed
RUN ln -s /bin/grep /usr/bin/grep
RUN ./autogen.sh && make && make install
RUN ln -s /usr/local/lib/libnopoll.so.0 /usr/lib/libnopoll.so.0

# build common-server

WORKDIR /common-server

ADD common-server .

# TODO: build type -> ENV
RUN cmake -DCMAKE_BUILD_TYPE="DEBUG" .
RUN make

RUN mkdir -p /var/log/common-server
RUN mkdir -p /etc/common-server
RUN cp trace.ini /etc/common-server/.

EXPOSE 80
EXPOSE 8080
EXPOSE 443

CMD ["./build/common-server"]
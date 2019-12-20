#/*******************************************************************************
# * Copyright 2019 alladin-IT GmbH
# * 
# * Licensed under the Apache License, Version 2.0 (the "License");
# * you may not use this file except in compliance with the License.
# * You may obtain a copy of the License at
# * 
# *   http://www.apache.org/licenses/LICENSE-2.0
# * 
# * Unless required by applicable law or agreed to in writing, software
# * distributed under the License is distributed on an "AS IS" BASIS,
# * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# * See the License for the specific language governing permissions and
# * limitations under the License.
# ******************************************************************************/

FROM debian:10

RUN apt update && apt upgrade -y
RUN apt install -y build-essential make cmake automake g++ libtool liblog4cpp5-dev libssl-dev git
RUN apt clean

# libnopoll autogen.sh needs /usr/bin/sed, also grep
RUN ln -s /bin/sed /usr/bin/sed
RUN ln -s /bin/grep /usr/bin/grep

# build ias-server

WORKDIR /nntool

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

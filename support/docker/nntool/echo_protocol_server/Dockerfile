FROM debian:9

RUN apt update && apt upgrade -y && apt install -y xinetd
RUN sed -i "s/disable.*yes/disable = no/g" /etc/xinetd.d/echo

EXPOSE 7
EXPOSE 7/udp

CMD ["script", "-c", "xinetd -d -dontfork"]
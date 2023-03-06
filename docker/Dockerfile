FROM openjdk:8-jre-alpine

RUN mkdir -p /opt/im-server

WORKDIR /opt/im-server

ADD ./im-server/distribution/target/distribution-*-bundle-tar.tar.gz /opt/im-server/

RUN sed -i 's/#JAVA_OPTS="$JAVA_OPTS -Xmx128M"/JAVA_OPTS="$JAVA_OPTS -Xmx$JVM_XMX"/g' /opt/im-server/bin/wildfirechat.sh
RUN sed -i 's/#JAVA_OPTS="$JAVA_OPTS -Xms128M"/JAVA_OPTS="$JAVA_OPTS -Xms$JVM_XMS"/g' /opt/im-server/bin/wildfirechat.sh

EXPOSE 80/tcp 1883/tcp 8083/tcp 8084/tcp 18080/tcp 8080/tcp 54327/tcp 5701/tcp 5702/tcp 5703/tcp

ENV JVM_XMX 256M
ENV JVM_XMS 256M

VOLUME /opt/im-server/config
VOLUME /opt/im-server/logs
VOLUME /opt/im-server/h2db
VOLUME /opt/im-server/media

RUN chmod +x /opt/im-server/bin/wildfirechat.sh

ENTRYPOINT ["/bin/sh","-c","/opt/im-server/bin/wildfirechat.sh"]

FROM java:8
LABEL author=star
WORKDIR /root/
VOLUME /tmp
COPY ./target/*.jar /root/app.jar
EXPOSE 8080
RUN bash -c 'touch /root/app.jar'
ENTRYPOINT ["java",'-jar','-Xms128m','-Xmx3500m',"app.jar","--spring.profiles.active=prod"]
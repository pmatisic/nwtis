FROM tomcat:10.1.7-jre17-temurin
RUN cp -r /usr/local/tomcat/webapps.dist/* /usr/local/tomcat/webapps/
COPY ./scripts/tomcat-users.xml /usr/local/tomcat/conf/
COPY ./scripts/manager_context.xml /usr/local/tomcat/webapps/manager/META-INF/context.xml

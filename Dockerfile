FROM maven:3.8.5-openjdk-17

WORKDIR /main_server
COPY . ./main_server
RUN mvn clean install

CMD mvn spring-boot:run
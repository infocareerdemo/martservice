FROM openjdk:17
 
# Set the working directory in the container

WORKDIR /app
 
# Copy the JAR file from your local machine to the container

COPY target/kcs.jar kcs.jar
 
# Expose the application port (if any specific port is used)

EXPOSE 7080
 
# Run the JAR file

ENTRYPOINT ["java", "-jar", "/app/kcs.jar"]
 
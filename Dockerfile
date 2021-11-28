FROM openjdk:11-jdk
EXPOSE 80:80
RUN mkdir /app
COPY ./build/install/HashCache/ /app/
WORKDIR /app/bin
CMD ["./HashCache"]

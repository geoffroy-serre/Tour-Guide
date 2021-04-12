# TourGuide

## Table of content

* [Technical Informations](#1-technical-informations)
    * [Requirements](#11-requirements)
    * [Setup IntelliJ](#12-setup-with-intellij-ide)
    * [Export Project](#13-export-project)
    * [Docker](#14-docker)
* [EndPoints](#2-endpoints)
    * [Microservices default urls and ports](#21-microservices-default-url-and-ports)

## 1. Technical informations:

---

### 1.1 Requirements

![Java Version](https://img.shields.io/badge/Java-8.0-red)
![Gradle Version](https://img.shields.io/badge/Gradle-6.8.3-blue)
![SpringBoot Version](https://img.shields.io/badge/Spring%20Boot-2.4.4-brightgreen)
![J-Unit Version](https://img.shields.io/badge/JUnit-5.7.0-orange)
![TomCat](https://img.shields.io/badge/TomCat-9.0.41-brightgreen)
![Docker Version](https://img.shields.io/badge/Docker-20.10.2-cyan)
![Coverage](https://img.shields.io/badge/Coverage%20with%20IT-82%25-green)

### 1.2 Setup with Intellij IDE

1. Download project or import it with git.
2. Open project in intelliJ


### 1.3 Export project

Export project with:

```bash
gradle clean
gradle bootJar
```

As Tomcat is embbeded, you can launch directly the *.jar with
```bash
java -Duser.language=en -Duser.region=US -jar {nameOfTheFile}.jar
```
Even if in the code the region is set to US, to be sure, launching with these args, avoid 
decimal issues ( java replacing 10.1 with 10,1).


### 1.4 Docker

1. Build each image using the DockerFile included in each micro-service folder :
If you want to avoid editing the dockerCompose, use those commands, or edit the compose file 
   according the name you gave to the images.
   Don't forget to be in the right folder to launch the command.
```bash
docker build -t tourguide-microservice .
docker build -t gpsutil-microservice .
docker build -t trippricer-microservice .
docker build -t rewardscenter-microservice .
```
2. Launch the DockerCompose at the root of the project. TourGuide have profiles to set the 
   WebClient url depending on if it runs in an IDE , or in a docker container. To run it in docker 
   type:
 ```bash
 docker-compose up
```
Compose file already set the proper spring profiles to docker for running it.


## 2 EndPoints
All endpoints are documented with swagger2. You can access it by launching the app and go to 
this kind of url:
```bash
http://localhost:8080/swagger-ui/
```
Just make sure to access the right url and port for the wanted doc.

### 2.1 MicroServices default url and ports
* TourGuide -> http://localhost:8080
* GpsUtil -> http://localhost:8081
* RewardsCenter -> http://localhost:8082
* TripPricer -> http://localhost:8083




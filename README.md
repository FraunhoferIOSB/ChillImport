# ChillImport [![Build Status](https://travis-ci.org/FraunhoferIOSB/ChillImport.svg?branch=master)](https://travis-ci.org/FraunhoferIOSB/ChillImport) [![codecov](https://codecov.io/gh/FraunhoferIOSB/ChillImport/branch/master/graph/badge.svg)](https://codecov.io/gh/FraunhoferIOSB/ChillImport) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/6810ee95249341248d1e696a3b177101)](https://www.codacy.com/gh/FraunhoferIOSB/ChillImport?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=FraunhoferIOSB/ChillImport&amp;utm_campaign=Badge_Grade)
Starting the Server :

Either run the available Docker-Image or the maven-generated WAR-File.
In Both cases, you will need to provide the environmental variables \<basepath\> (path to where you want the server to save files like logs etc) and \<serverurl\> the target FROST-Server's URL .

Example: (basepath) /home/user/chillimport-files , (serverurl) http://frost-server.com/v1.0 

To start the server use : "java -jar server.war"

In case HTTP-Authentication is required, you will need to provide a Username and password in the "username.cfg" - File within the specificed directory
Format : \<Username\>:\<Password\> e.g User1:Password1 

The server will take a couple seconds to start up and will then be available (port 8000)

NOTE : Current Docker-compose  version only works on Linux. If you wish to use docker compose on MacOS remove the network_mode argument from the docker-compose file. (Note that localhost FROST-Servers can not be used this way)

Compose does not work on Windows due to Path problems.

Docker-Compose File can be found within the repository, replace any \<basepath\> by an absolute path you wish to use for files created/used by the server and \<serverurl\> by the used FROST-Server's URL

If you wish to start the docker image from the terminal instead, pass the basepath and serverurl environment variables via command line (sudo docker run -e envName=<...> server)
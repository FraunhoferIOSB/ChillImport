# ChillImport [![Build Status](https://travis-ci.org/FraunhoferIOSB/ChillImport.svg?branch=master)](https://travis-ci.org/FraunhoferIOSB/ChillImport) [![codecov](https://codecov.io/gh/FraunhoferIOSB/ChillImport/branch/master/graph/badge.svg)](https://codecov.io/gh/FraunhoferIOSB/ChillImport) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/6810ee95249341248d1e696a3b177101)](https://www.codacy.com/gh/FraunhoferIOSB/ChillImport?utm_source=github.com&utm_medium=referral&utm_content=FraunhoferIOSB/ChillImport&utm_campaign=Badge_Grade)

<img src="https://github.com/FraunhoferIOSB/ChillImport/blob/master/src/main/resources/static/images/logo.png" width="439" height="80"></img>\
ChillImport is a Software with which you can easily import your sensor measurements
from csv or excel files \
into a [FROST-Server](https://github.com/FraunhoferIOSB/FROST-Server). It offers
you an userfriendly, web-based gui.

## Downloading

The easiest way to obtain and run the software is docker:\
<https://hub.docker.com/r/fraunhoferiosb/chillimport>

## Setup

Before starting the software you have to create an environment-variable
"configPath", \
it defines the path to your configuration and log-files. By default the path is "/data".
\
\
To use this software you need a FROST-Server.\
For more details visit the
[FROST-Server github page](https://github.com/FraunhoferIOSB/FROST-Server).

## Importing Data

ChillImport offers you an easy and guided usage for all your imports. \
It is possible to import data from excel and csv files.\
If needed you can also create new entities on your FROST-Server, eg:

- Things
- Locations
- Datastreams
- Sensors
- Observed-Properies

This makes the use of ChillImport more convenient. \
\
For more information you can find example-imports here:
[ChillImport example](Example/ImportExample01.md)

## Screenshot

![](Example/screenshot.png)

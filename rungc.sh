#!/bin/bash
java -Xms1g -Xmx1g -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom -jar oauth-playground-0.0.1-SNAPSHOT.jar
#!/bin/bash

java -Dspring.profiles.active=dev -jar app.jar > /dev/null 2>&1 &
#!/bin/sh
mvn clean install
cd gui
mvn assembly:single

#!/bin/bash
# Copies jdk8 settings.xml (Maven) to $HOME if Java version matches

if [[ $JAVA_HOME =~ "java-8-oracle" ]]
then
  cp ./.travis/jdk8-settings.xml $HOME/.m2/settings.xml
fi

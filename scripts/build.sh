#!/bin/bash

if [ "${SONAR_SCANNER_HOME}" != "" ]; then
    COMMAND="mvn --batch-mode -q -Pskip-it clean org.jacoco:jacoco-maven-plugin:prepare-agent install -Dembedded sonar:sonar -Dsonar.projectKey=citrus-simulator"
else
    COMMAND="mvn --batch-mode -q clean install"
fi

echo ${COMMAND}
${COMMAND}
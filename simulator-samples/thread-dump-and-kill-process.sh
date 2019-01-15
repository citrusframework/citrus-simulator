#!/bin/sh

# Detects misbehaving (did not shutdown gracefully) springboot application and kills process
# The script checks for any process listening on port 9001, which is
# used by default in springboot applications as the JMX endpoint, and kills this process

which lsof
which jstack

lsof -PiTCP -sTCP:LISTEN
lsof -sTCP:LISTEN -t -i:9001

PID=`lsof -sTCP:LISTEN -t -i:9001`
echo "PID:($PID)"

if [ "" !=  "$PID" ]; then
  echo "Found process listening on port 9001: $PID"
  echo "Thread-Dump::Start"
  jstack -l  $PID
  echo "Thread-Dump::End"
  kill $PID
  echo "Misbehaving process stopped"
else
  echo "No process found listening on port 9001"
fi

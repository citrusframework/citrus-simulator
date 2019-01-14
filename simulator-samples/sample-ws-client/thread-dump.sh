#!/bin/sh


PID=`lsof -t -i:9001`
if [[ "" !=  "$PID" ]]; then
  echo "Found process listening on port 9001: $PID"
  echo "Thread-Dump::Start"
  jstack -l  $PID
  echo "Thread-Dump::End"
else
  echo "No process found listening on port 9001"
fi
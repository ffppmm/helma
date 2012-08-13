#!/bin/bash
# Shell script for starting Helma with a JDK-like virtual machine.

# To add JAR files to the classpath, simply place them into the
# lib/ directory.

# uncomment to set JAVA_HOME variable
# JAVA_HOME=/usr/lib/java

# uncomment to set HOP_HOME, otherwise we get it from the script path
# HOP_HOME=/usr/local/helma

# options to pass to the Java virtual machine
JAVA_OPTIONS="-server -Xmx128m"


###########################################################
###### No user configuration needed below this line #######
###########################################################

# if JAVA_HOME variable is set, use it. Otherwise, Java executable
# must be contained in PATH variable.
if [ "$JAVA_HOME" ]; then
   JAVACMD="$JAVA_HOME/bin/java"
   # Check if java command is executable
   if [ ! -x $JAVACMD ]; then
      echo "Warning: JAVA_HOME variable may be set incorrectly:"
      echo "         No executable found at $JAVACMD"
   fi
else
   JAVACMD=java
fi

# get HOP_HOME variable if it isn't set
if [ -z "$HOP_HOME" ]; then
  # try to get HOP_HOME from script file and pwd
  # strip everyting behind last slash
  HOP_HOME="${0%/*}"
  cd $HOP_HOME
  HOP_HOME=$PWD
else
  cd $HOP_HOME
fi
echo "Starting Helma in directory $HOP_HOME"

# Invoke the Java VM
$JAVACMD $JAVA_OPTIONS -cp $(echo dependency/*.jar | tr ' ' ':'):$(echo *.jar) helma.main.Server

#!/bin/sh

cygwin=false
darwin=false
os400=false
case "`uname`" in
CYGWIN*) cygwin=true;;
Darwin*) darwin=true;;
OS400*) os400=true;;
esac

error_exit ()
{
    echo "ERROR: $1 !!"
    exit 1
}

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ] ; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

export MODE="standalone"

while getopts ":m:" opt
do
    case $opt in
        m)
            MODE=$OPTARG;;
        ?)
        echo "Unknown parameter"
        exit 1;;
    esac
done

[ ! -e "$JAVA_HOME/bin/java" ] && JAVA_HOME=$HOME/jdk/java
[ ! -e "$JAVA_HOME/bin/java" ] && JAVA_HOME=/usr/java
[ ! -e "$JAVA_HOME/bin/java" ] && unset JAVA_HOME

if [ -z "$JAVA_HOME" ]; then
  if $darwin; then

    if [ -x '/usr/libexec/java_home' ] ; then
      export JAVA_HOME=`/usr/libexec/java_home`

    elif [ -d "/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home" ]; then
      export JAVA_HOME="/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home"
    fi
  else
    JAVA_PATH=`dirname $(readlink -f $(which javac))`
    if [ "x$JAVA_PATH" != "x" ]; then
      export JAVA_HOME=`dirname $JAVA_PATH 2>/dev/null`
    fi
  fi
  if [ -z "$JAVA_HOME" ]; then
        error_exit "Please set the JAVA_HOME variable in your environment, We need java(x64)! jdk17 or later is better!"
  fi
fi


export JAVA_HOME
export JAVA="$JAVA_HOME/bin/java"

# Only set APP_BASE if not already set
[ -z "$APP_BASE" ] && APP_BASE=`cd "$PRGDIR/.." >/dev/null; pwd`
export CUSTOM_SEARCH_LOCATIONS=file:${APP_BASE}/conf/

#===========================================================================================
# JVM Configuration
#===========================================================================================
JAVA_OPT="${JAVA_OPT} -Xms2g -Xmx2g -Xmn1g -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m"
JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${APP_BASE}/logs/java_heapdump.hprof"
JAVA_OPT="${JAVA_OPT} -XX:-UseLargePages"


JAVA_MAJOR_VERSION=$($JAVA -version 2>&1 | sed -E -n 's/.* version "([0-9]*).*$/\1/p')
if [[ "$JAVA_MAJOR_VERSION" -ge "9" ]] ; then
  JAVA_OPT="${JAVA_OPT} -Xlog:gc*:file=${APP_BASE}/logs/nwts_gc.log:time,tags:filecount=10,filesize=100m"
else
  JAVA_OPT="${JAVA_OPT} -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:CMSInitiatingOccupancyFraction=70 -XX:+CMSParallelRemarkEnabled -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+CMSClassUnloadingEnabled -XX:SurvivorRatio=8 "
  JAVA_OPT_EXT_FIX="-Djava.ext.dirs=${JAVA_HOME}/jre/lib/ext:${JAVA_HOME}/lib/ext"
  JAVA_OPT="${JAVA_OPT} -Xloggc:${APP_BASE}/logs/wts_gc.log -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M"
fi


if [ ! -d "${APP_BASE}/logs" ]; then
  mkdir ${APP_BASE}/logs
fi


# check the start.out log output file
#if [ ! -f "${APP_BASE}/logs/start.out" ]; then
#  touch "${APP_BASE}/logs/start.out"
#fi

#APP_OUT=${APP_BASE}/logs/start.out

APP_OUT=/dev/null


APP_OPTS="-Dlogs.path=${APP_BASE}/logs -Dspring.config.additional-location=file://${APP_BASE}/config/application-${MODE}.yaml"
CLASSPATH="${APP_BASE}/config:${APP_BASE}/libs/*"


MAIN_CLASS="com.honing.css.spring.WechatTokenServerApplication"

# start command
echo "$JAVA $JAVA_OPT_EXT_FIX ${JAVA_OPT} ${APP_OPTS} -cp ${CLASSPATH} ${MAIN_CLASS}"

if [[ "$JAVA_OPT_EXT_FIX" == "" ]]; then
  nohup "$JAVA" ${JAVA_OPT} ${APP_OPTS} -cp ${CLASSPATH} $MAIN_CLASS >> ${APP_OUT} 2>&1 &
else
  nohup "$JAVA" "$JAVA_OPT_EXT_FIX" ${JAVA_OPT} ${APP_OPTS} -cp ${CLASSPATH} $MAIN_CLASS >> ${APP_OUT} 2>&1 &
fi

echo "wechat-token-server is starting. you can check the program status."

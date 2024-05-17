#!/bin/sh


# Better OS/400 detection: see Bugzilla 31132
os400=false
case "`uname`" in
OS400*) os400=true;;
esac

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

PRGDIR=`dirname "$PRG"`

MAIN_CLASS="com.honing.css.spring.WechatTokenServerApplication"

pid=`pgrep -f ${MAIN_CLASS}`
if [ -z "$pid" ] ; then
       echo "No wechat-token-server running."
       exit -1;
fi

echo "The wechat-token-server(${pid}) is running..."

kill ${pid}

echo "Send shutdown request to wechat-token-server(${pid}) OK"

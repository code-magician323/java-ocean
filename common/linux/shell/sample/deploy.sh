 #!/bin/sh

war=$1
bin=$(cd `dirname $0`; pwd)

if [ ! -n "${war}" ]; then
   echo "***Usage: $0 [project.war]"
   exit 0
fi
if [ ! -f "${war}" ]; then
    echo "***Error: ${war} does not exist."
    exit 0
fi
if [ ! "${war##*.}" = "war" ]; then
    echo "***Error: ${war} is not a war file."
    exit 0
fi

echo "Deploy ${war##*/}..."
rm -rf ${bin}/../webapps/ROOT/ && unzip -qo ${war} -d ${bin}/../webapps/ROOT/
rm -rf ${bin}/../work/Catalina/localhost/
echo "Restart tomcat..."
exec ${bin}/restart.sh

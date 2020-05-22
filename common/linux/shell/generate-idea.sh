fileName=~/workspace/idea

filepath=${fileName%/*}
mkdir -p $filepath 
touch $fileName
echo "nohup env JAVA_HOME=/usr/lib/jdk1.8.0_211 /opt/idea/bin/idea.sh > /dev/null 2>&1 &" > $fileName
chmod -R 755 $fileName
echo "creat " $fileName "success"
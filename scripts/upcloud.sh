PEM=${STARBUCKS_HOME}/scripts/starbucks.pem
HOST="ec2-50-16-79-112.compute-1.amazonaws.com"

scp -i ${PEM} ${STARBUCKS_HOME}/$1 ubuntu@${HOST}:~/

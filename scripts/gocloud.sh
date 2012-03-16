PEM=${STARBUCKS_HOME}/scripts/starbucks.pem
HOST="ec2-50-16-79-112.compute-1.amazonaws.com"

ssh -i ${PEM} ubuntu@${HOST}

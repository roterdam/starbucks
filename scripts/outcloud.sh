if [ -z $STARBUCKS_HOME ]; then
  echo "Need to set STARBUCKS_HOME, you probably want to run"
  echo "    export STARBUCKS_HOME=\`pwd\`"
  echo "from the repo root."
  exit 1
fi

if [ $# -ne 2 ]; then
  echo "usage: `basename $0` [input.asm] [output]"
  exit 1
fi
PLAYGROUND="/tmp/playground"
HOST="ec2-50-16-79-112.compute-1.amazonaws.com"
PEM=${STARBUCKS_HOME}/scripts/starbucks.pem
OUTNAME="starbucks"

echo "Resetting ${PLAYGROUND}"
ssh -i ${PEM} ubuntu@${HOST} "rm ${PLAYGROUND}/*"

echo "Uploading $1 to ${PLAYGROUND}"
scp -i ${PEM} $1 ubuntu@${HOST}:${PLAYGROUND}/${OUTNAME}.s

echo "Compiling, linking and running. Binary results in" $2
#echo ""
#echo "=== START OUTPUT ==="
ssh -i ${PEM} ubuntu@${HOST} "cd ${PLAYGROUND}; nasm -felf64 -o ${OUTNAME}.o ${OUTNAME}.s; gcc -g -o ${OUTNAME} ${OUTNAME}.o; chmod +x ${OUTNAME}; ./${OUTNAME}" > $2
cat $2
#echo "==== END OUTPUT ===="
#echo ""
#echo "Cleaning up .o files"
#rm ${OUTNAME}.o

if [ -z $STARBUCKS_HOME ]; then
  echo "Need to set STARBUCKS_HOME, you probably want to run"
  echo "    export STARBUCKS_HOME=\`pwd\`"
  echo "from the repo root."
  exit 1
fi

if [ $# -ne 1 ]; then
  echo "usage: `basename $0` [input.asm]"
  exit 1
fi
PLAYGROUND="/tmp/playground"
HOST="ec2-50-16-79-112.compute-1.amazonaws.com"
PEM=${STARBUCKS_HOME}/scripts/starbucks.pem
OUTNAME="starbucks"

echo "Compiling $1"
nasm -f elf64 $1 -o ${OUTNAME}.o

echo "Resetting ${PLAYGROUND}"
ssh -i ${PEM} ubuntu@${HOST} "rm ${PLAYGROUND}/*"

echo "Uploading compiled ${OUTNAME}.o to ${PLAYGROUND}"
scp -i ${PEM} ${OUTNAME}.o ubuntu@${HOST}:${PLAYGROUND}/

echo "Linking and running. Binary results:"
echo ""
echo "=== START OUTPUT ==="
ssh -i ${PEM} ubuntu@${HOST} "cd ${PLAYGROUND}; gcc -g -o ${OUTNAME} ${OUTNAME}.o; chmod +x ${OUTNAME}; ./${OUTNAME}"
echo "==== END OUTPUT ===="
echo ""
echo "Cleaning up .o files"
rm ${OUTNAME}.o

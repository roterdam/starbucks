if [ -z $STARBUCKS_HOME ]; then
  echo "Need to set STARBUCKS_HOME, you probably want to run"
  echo "    export STARBUCKS_HOME=\`pwd\`"
  echo "from the repo root."
  exit 1
fi

if [ $# -ne 1 ]; then
  echo "usage: `basename $0` [compiled.o]"
  exit 1
fi
PLAYGROUND = "playground"
echo "Resetting ~/${PLAYGROUND}"
ssh -i ${STARBUCKS_HOME}/scripts/starbucks_rsa starbucks@173.255.228.108 "rm -rf ${PLAYGROUND};mkdir ${PLAYGROUND}"
echo "Uploading $1 to ~/${PLAYGROUND}"
scp -i ${STARBUCKS_HOME}/scripts/starbucks_rsa $1 starbucks@173.255.228.108:~/${PLAYGROUND}/
echo "Compiling $1 and running it."
ssh -i ${STARBUCKS_HOME}/scripts/starbucks_rsa starbucks@173.255.228.108 "cd ~/{PLAYGROUND}; gcc $1; chmod +x $1; ./$1"

ant
java -ea -jar dist/Compiler.jar -target codegen -o $2 $1
./scripts/cccloud.sh $2

ant
java -ea -jar dist/Compiler.jar -target codegen $1 > output
./scripts/cccloud.sh output

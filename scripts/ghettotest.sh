echo "GHETTO LOCAL 64 BIT TESTING";
rm *.s;
rm *.o;
ant; 
java -ea -jar dist/Compiler.jar -target codegen -o $1.s tests/codegen/input/$1.dcf;
nasm -f elf64 -o $1.o $1.s; 
gcc -o $1 $1.o; 
./$1 | diff tests/codegen/output/$1.dcf.out -;
ls tests/codegen/input/

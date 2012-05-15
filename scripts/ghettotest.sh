echo "GHETTO LOCAL 64 BIT TESTING";
rm *.s;
rm *.o;
ant; 
java -ea -jar dist/Compiler.jar -target codegen -o $1.s tests/dataflow/input/$1.dcf;
java -ea -jar dist/Compiler.jar -debug -target codegen -opt cm -o $1_opt.s tests/dataflow/input/$1.dcf;
nasm -f elf64 -o $1.o $1.s; 
nasm -f elf64 -o $1_opt.o $1_opt.s; 
gcc -o $1 $1.o; 
gcc -o $1_opt $1_opt.o; 
ls tests/dataflow/input

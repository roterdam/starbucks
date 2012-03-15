Starbucks, a 6.035 compiler
===

To get started, get the repository into your Eclipse workspace. (Or symlink it
in.) Start a new Java project but point it to the existing folder. From the lib
folder, add the jar files to the build path (right click, Build Path, Add to
Build Path).

`ant` to build.

In order to build an assembly file:
`nasm -f elf64 input.asm -o output.o`
`gcc -o output output.o`

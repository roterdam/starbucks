ofile=`tempfile`
nasm -felf64 -o $ofile tmp/$1.s; gcc -o tmp/$1 $ofile

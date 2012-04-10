#!/bin/sh

runcompiler() {
  java -ea -jar `dirname $0`/../../dist/Compiler.jar \
    -opt all -target codegen -o $2 $1
}

fail=0

if ! gcc -v 2>&1 |grep -q '^Target: x86_64-linux-gnu'; then
  echo "Refusing to run cross-compilation on non-64-bit architechure."
  exit 0;
fi

for file in `dirname $0`/input/*.dcf; do
  echo ${file}
  asm=`tempfile --suffix=.s`
  asmo=`tempfile --suffix=.o`
  msg=""
  if runcompiler $file $asm; then
    binary=`tempfile`
    if nasm -felf64 -o $asmo $asm; gcc -o $binary -L `dirname $0`/lib -l6035 $asmo; then
      output=`tempfile`
      if $binary > $output; then
        diffout=`tempfile`
        if ! diff -u $output `dirname $0`/output/`basename $file`.out > $diffout; then
          msg="File $file output mismatch.";
        fi
      else
        msg="Program failed to run.";
      fi
    else
      msg="Program failed to assemble.";
    fi
  else
    msg="Program failed to generate assembly.";
  fi
  if [ ! -z "$msg" ]; then
    fail=1
    echo $file
    if [ ! -z "$diffout" ]; then
      cat $diffout
    elif [ ! -z "$output" ]; then
      cat $output
    fi
    echo $msg
  fi
  rm -f $diffout $output $binary $asm;
done

exit $fail;

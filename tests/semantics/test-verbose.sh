#!/bin/sh

runsemantics() {
  java -jar `dirname $0`/../../dist/Compiler.jar -target inter $1
}

fail=0

echo "---------------- Running Illegal DECAF --------------------";

for file in `dirname $0`/illegal/*; do
  if runsemantics $file; then
    echo "Illegal file $file semantic checked successfully.";
    fail=1
  fi
done

echo "----------------   Running Legal DECAF --------------------";

for file in `dirname $0`/legal/*; do
  if ! runsemantics $file; then
    echo "Legal file $file failed to pass semantic checks.";
    fail=1
  fi
done

exit $fail;

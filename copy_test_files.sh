#!/bin/bash

ROOT=$PWD
rm -vrf app/src/main/assets
mkdir app/src/main/assets
cd boringssl/src/main/native/src
DATA=$(jq -r '.crypto_test.data[]' "gen/sources.json")
for FILE in $DATA; do
  cp -v --parents "$FILE" "$ROOT/app/src/main/assets"
done

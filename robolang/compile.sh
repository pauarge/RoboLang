#!/usr/bin/env bash

echo "Setting up..."
filename=$1
filebasename=basename${filename}
classname=${filebasename%.rl}
java_filename="${classname}.java"
nxj_filename="${classname}.nxj"
mkdir -p tmp/com/robolang
cp src/main/java/com/robolang/Common.java tmp/com/robolang
cp ${filename} tmp/com/robolang
cd tmp/com/robolang

echo "Parsing..."
java -jar ../../../target/robolang-1.0-jar-with-dependencies.jar ${filebasename}

echo "Compiling..."
nxjc ${java_filename} Common.java

echo "Linking..."
cd ../../
nxjlink -o ${nxj_filename} "com.robolang.${classname}"

echo "Uploading..."
nxjupload -r ${nxj_filename}

echo "Cleaning up..."
cd ..
rm -r tmp/

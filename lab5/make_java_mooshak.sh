#!/bin/bash

out_dir="mooshak"

if [ -z "$1" ]
then
	out_dir="mooshak"
else
	out_dir=$1
fi

rm $out_dir.zip
rm -r mooshak
mkdir mooshak
cp IA/lab5/src/main/* mooshak
##
cp "IA/lab5/saved_networks/$out_dir.ser" mooshak
##
zip -r $out_dir.zip mooshak
rm -r mooshak

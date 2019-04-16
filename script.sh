#!/bin/sh
while true
do
	target=$(java -classpath .:poly_generator/ Main)
	result=$(java poly/Main '"'"${target}"'"')
	out=$(./run.wls '"'"${target}"'"' '"'"${result}"'"')
	if [ "$out" -eq '1' ]; then
		echo -e "\r                    \c"
		echo -e "\r[-] Success: $target\c"
		len=${#target}+15
	else
		echo "[!] Failed: $target with Derivation $result"
	fi;
done

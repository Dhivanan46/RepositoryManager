#!/bin/bash
set -e
cd "$(dirname "$0")"
mkdir -p out
javac -d out src/com/iexceed/stringutils/StringReverser.java
jar cf string-reverser-1.0.0.jar -C out .
echo "Built string-reverser-1.0.0.jar"
echo "Upload it to GitHub at this path in your repo:"
echo "  repo/com/iexceed/stringutils/string-reverser/1.0.0/string-reverser-1.0.0.jar"

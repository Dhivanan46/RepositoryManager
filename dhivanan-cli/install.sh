#!/bin/bash
set -e
cd "$(dirname "$0")"
source .env

mkdir -p target/classes
javac -d target/classes src/*.java
jar cfe target/dhivanan-cli.jar Main -C target/classes .

BIN_DIR="$HOME/.dhivanan/bin"
mkdir -p "$BIN_DIR"
CLI_JAR="$(pwd)/target/dhivanan-cli.jar"

cat > "$BIN_DIR/$TOOL_NAME" <<EOF
#!/bin/bash
export DHIVANAN_GITHUB_BASE="$GITHUB_REPO_BASE"
java -jar "$CLI_JAR" "\$@"
EOF

chmod +x "$BIN_DIR/$TOOL_NAME"

echo "Installed command: $TOOL_NAME"
echo "Add to PATH once:  export PATH=\"\$PATH:$BIN_DIR\""
echo "Then run:          $TOOL_NAME clean install"

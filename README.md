# Cursor AI / MCP Config / CLI

## Motivation

Using different set of MCP servers is something common when you use public & private MCP servers. This tiny CLI help to use multiple MCP configuration for Cursor AI.

## How to build

```bash
./mvnw clean verify
./mvnw clean package

./mvnw versions:display-dependency-updates
./mvnw versions:display-plugin-updates
./mvnw versions:display-property-updates

./mvnw versions:set -DnewVersion=0.6.1
./mvnw versions:commit
```

## How to use

```bash
sdk install jbang
cat ~/.cursor/mcp.json
jbang ./src/main/java/info/jab/jbang/CursorMCPConfig.java
jbang ./src/main/java/info/jab/jbang/CursorMCPConfig.java --help
jbang ./src/main/java/info/jab/jbang/CursorMCPConfig.java --show
jbang ./src/main/java/info/jab/jbang/CursorMCPConfig.java --backup
jbang ./src/main/java/info/jab/jbang/CursorMCPConfig.java --replace examples/mcp0.json
jbang ./src/main/java/info/jab/jbang/CursorMCPConfig.java --replace examples/mcp1.json

./mvnw clean package
java -jar target/cursor-mcp-config-0.1.0.jar
java -jar target/cursor-mcp-config-0.1.0.jar --help
java -jar target/cursor-mcp-config-0.1.0.jar --show
java -jar target/cursor-mcp-config-0.1.0.jar --backup
java -jar target/cursor-mcp-config-0.1.0.jar --replace examples/mcp0.json
java -jar target/cursor-mcp-config-0.1.0.jar --replace examples/mcp1.json
```

## References

- https://www.cursor.com/
- https://www.jbang.dev/
- https://www.jbang.dev/appstore/
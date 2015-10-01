#!/bin/bash

# Remove JAR's target
rm -rf faostat-api-core/target

# Remove WAR's target
rm -rf faostat-api-web/target

# Execute Maven
# /Development/maven/bin/mvn install -Dmaven.test.skip -o
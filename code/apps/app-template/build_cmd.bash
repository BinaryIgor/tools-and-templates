#!bin/bash
set -e

BUILD_COMMONS=${BUILD_COMMONS:-true}
TEMPLATES_DIR="../../../templates"

if [ $BUILD_COMMONS == "true" ]; then
  app_dir=$PWD
  cd ../../commons
  mvn clean install -PallTests
  cd $app_dir
fi

mvn clean install -PallTests

cp -r $TEMPLATES_DIR target/templates
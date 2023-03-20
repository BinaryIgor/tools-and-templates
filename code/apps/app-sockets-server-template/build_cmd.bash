#!bin/bash
set -e

BUILD_COMMONS=${CI_BUILD_COMMONS:-true}
SKIP_COMMONS_TESTS="${CI_SKIP_COMMONS_TESTS:-false}"
SKIP_TESTS="${CI_SKIP_TESTS:-false}"

if [ $BUILD_COMMONS == "true" ]; then
  app_dir=$PWD
  cd ../../
  mvn clean install --non-recursive
  cd commons
  if [ $SKIP_COMMONS_TESTS == "true" ] || [ $SKIP_TESTS == "true" ]; then
    mvn clean install -Dmaven.test.skip=true
  else
    mvn clean install -PallTests
  fi
  cd $app_dir
fi

if [ $SKIP_TESTS == "true" ]; then
  mvn clean install -Dmaven.test.skip=true
else
  mvn clean install -PallTests
fi

HTTP_PORT=$(shuf -i 10000-20000 -n 1)
echo "http://0.0.0.0:$HTTP_PORT" > "$CI_PACKAGE_TARGET/$APP_URL_FILE"
echo "$HTTP_PORT" > "$CI_PACKAGE_TARGET/$APP_SERVER_PORT_FILE"
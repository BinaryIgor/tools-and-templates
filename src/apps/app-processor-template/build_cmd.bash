#!bin/bash
set -e

BUILD_COMMONS=${CI_BUILD_COMMONS:-true}
SKIP_COMMONS_TESTS="${CI_SKIP_COMMONS_TESTS:-false}"
SKIP_TESTS="${CI_SKIP_TESTS:-false}"
TEMPLATES_PATH="${CI_REPO_ROOT_PATH}/templates"

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

cp -r $TEMPLATES_PATH target/templates

#!/bin/bash
set -eu

rm -f -r target
mkdir target
cp init_volume.bash target/init_volume.bash

cp -r target/* ${CI_PACKAGE_TARGET}

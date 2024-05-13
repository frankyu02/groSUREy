#!/bin/bash
pushd "$(dirname "$0")" > /dev/null

{
set -e
docker build -t jdehal/android:latest .
#docker push jdehal/android:latest
}

popd

#!/bin/bash
IMAGE_NAME="jdehal/android:latest"
VOLUME_NAME="fjdk-gradle"
COMMAND='chmod +x ./gradlew && ./gradlew --no-daemon --build-cache build'

if [[ $1 = "debug" ]]; then
    COMMAND="bash"
fi

pushd "$(dirname "$0")" > /dev/null

{
set -e
# docker build -t $IMAGE_NAME .
docker volume create "$VOLUME_NAME"
docker pull "$IMAGE_NAME" -q
docker run --rm -it \
    -v "$VOLUME_NAME:/root/.gradle" \
    -v "$PWD:/myapp" \
    "$IMAGE_NAME" bash -c "$COMMAND"
}

popd

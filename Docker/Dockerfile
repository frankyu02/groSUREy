# Create the android image we will build from
FROM azul/zulu-openjdk:17-latest

# Settings
  # ANDROID_COMPILE_SDK is the version of Android you're compiling with.
  # It should match compileSdkVersion.
ENV ANDROID_COMPILE_SDK "33"

  # ANDROID_BUILD_TOOLS is the version of the Android build tools you are using.
  # It should match buildToolsVersion.
ENV ANDROID_BUILD_TOOLS "33.0.1"

  # It's what version of the command line tools we're going to download from the official site.
  # Official Site-> https://developer.android.com/studio/index.html
  # There, look down below at the cli tools only, sdk tools package is of format:
  #        commandlinetools-os_type-ANDROID_SDK_TOOLS_latest.zip
  # when the script was last modified for latest compileSdkVersion, it was which is written down below
ENV ANDROID_SDK_TOOLS "10406996"

# Packages installation before running script
RUN apt --quiet update --yes
RUN apt --quiet install --yes wget unzip dos2unix

#########################
####### ANDROID #########
#########################

# Setup path as android_home for moving/exporting the downloaded sdk into it
ENV ANDROID_HOME "/sdk"

# Create a new directory at specified location
RUN install -d $ANDROID_HOME

# Here we are installing androidSDK tools from official source,
# (the key thing here is the url from where you are downloading these sdk tool for command line, so please do note this url pattern there and here as well)
# after that unzipping those tools and
# then running a series of SDK manager commands to install necessary android SDK packages that'll allow the app to build
RUN wget --no-verbose --output-document=$ANDROID_HOME/cmdline-tools.zip https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_SDK_TOOLS}_latest.zip
RUN unzip -q -d "$ANDROID_HOME/cmdline-tools" "$ANDROID_HOME/cmdline-tools.zip"
RUN mv -T "$ANDROID_HOME/cmdline-tools/cmdline-tools" "$ANDROID_HOME/cmdline-tools/tools"
ENV PATH "$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/cmdline-tools/tools/bin"

# Nothing fancy here, just checking sdkManager version
RUN sdkmanager --version

# use yes to accept all licenses
RUN yes | sdkmanager --licenses > /dev/null || true
RUN sdkmanager "platforms;android-${ANDROID_COMPILE_SDK}"
RUN sdkmanager "platform-tools"
RUN sdkmanager "build-tools;${ANDROID_BUILD_TOOLS}"

WORKDIR /myapp

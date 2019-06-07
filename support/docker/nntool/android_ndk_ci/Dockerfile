FROM javiersantos/android-ci:28.0.3

#ENV PATH ${PATH}:${ANDROID_NDK_HOME}

RUN apt-get update && apt-get upgrade -qqy && apt-get install -qqy golang && apt-get clean

RUN yes | ${ANDROID_HOME}/tools/bin/sdkmanager --licenses

RUN ${ANDROID_HOME}/tools/bin/sdkmanager "ndk;20.0.5594570" "cmake;3.6.4111459"

ENV ANDROID_NDK_HOME /sdk/ndk/20.0.5594570/
ENV ANDROID_NDK /sdk/ndk/20.0.5594570/
ENV PATH ${PATH}:/sdk/cmake/3.6.4111459/bin/


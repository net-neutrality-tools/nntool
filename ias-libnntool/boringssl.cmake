include (ExternalProject)

set(boringssl_URL https://boringssl.googlesource.com/boringssl)
set(boringssl_TAG origin/master)

set(boringssl_INCLUDE_DIR ${CMAKE_CURRENT_BINARY_DIR}/boringssl/src/boringssl/include)
set(boringssl_BUILD ${CMAKE_CURRENT_BINARY_DIR}/boringssl/src/boringssl-build)

set(boringssl_STATIC_LIBRARIES
    ${boringssl_BUILD}/ssl/libssl.a
    ${boringssl_BUILD}/crypto/libcrypto.a
)

ExternalProject_Add(boringssl
    PREFIX boringssl
    GIT_REPOSITORY ${boringssl_URL}
    GIT_TAG ${boringssl_TAG}
    BUILD_BYPRODUCTS ${boringssl_STATIC_LIBRARIES}
    INSTALL_COMMAND ""
    CMAKE_ARGS
        -DCMAKE_INSTALL_PREFIX=${boringssl_BUILD}
        -DANDROID_ABI=${ANDROID_ABI} 
        -DCMAKE_TOOLCHAIN_FILE=${ANDROID_NDK}/build/cmake/android.toolchain.cmake 
        -DANDROID_NATIVE_API_LEVEL=21 -GNinja ../..
)

# BoringSSL Android

boringssl static library prefab for android

This library is based on the [boringssl AOSP repo](https://android.googlesource.com/platform/external/boringssl).

## Integration

Gradle:

```gradle
implementation 'io.github.vvb2060.ndk:boringssl:2.0'
```

This library is [Prefab](https://google.github.io/prefab/), so you will need to enable it in your project (Android Gradle Plugin 4.1+):

```gradle
android {
    ...
    buildFeatures {
        ...
        prefab true
    }
}
```

## Usage

### ndk-build

you can use `crypto_static`/`ssl_static` in your `Android.mk`. 
For example, if your application defines `libapp.so` and it uses `ssl_static`, your `Android.mk` file should include the following:

```makefile
include $(CLEAR_VARS)
LOCAL_MODULE           := app
LOCAL_SRC_FILES        := app.cpp
LOCAL_STATIC_LIBRARIES := ssl_static
include $(BUILD_SHARED_LIBRARY)

# If you don't need your project to build with NDKs older than r21, you can omit
# this block.
ifneq ($(call ndk-major-at-least,21),true)
    $(call import-add-path,$(NDK_GRADLE_INJECTED_IMPORT_PATH))
endif

$(call import-module,prefab/boringssl)
```

### CMake

you can use `crypto_static`/`ssl_static` in your `CMakeLists.txt`. 
For example, if your application defines `libapp.so` and it uses `crypto_static`, your `CMakeLists.txt` file should include the following:

```cmake
add_library(app SHARED app.cpp)

# Add these two lines.
find_package(boringssl REQUIRED CONFIG)
target_link_libraries(app boringssl::crypto_static)
```

## Changelog

* 1.0 [android-r-beta-3](https://android.googlesource.com/platform/external/boringssl/+/refs/tags/android-r-beta-3) [2fb729d4f36beaf263ad85e24a790b571652679c](https://github.com/google/boringssl/tree/2fb729d4f36beaf263ad85e24a790b571652679c)
* 2.0 [android-s-preview-1](https://android.googlesource.com/platform/external/boringssl/+/refs/tags/android-s-preview-1) [ae2bb641735447496bed334c495e4868b981fe32](https://github.com/google/boringssl/tree/ae2bb641735447496bed334c495e4868b981fe32)

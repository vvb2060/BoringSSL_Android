# BoringSSL Android

boringssl static library prefab for android

This library is based on the [boringssl repo](https://github.com/google/boringssl).

## Integration

Gradle:

```gradle
implementation("io.github.vvb2060.ndk:boringssl:20251124")
// or LTO version (~50MiB), it does not strip any debug info
implementation("io.github.vvb2060.ndk:boringssl:20251124-lto-ndk29")
```

This library is [Prefab](https://google.github.io/prefab/), so you will need to enable it in your project (Android Gradle Plugin 4.1+):

```gradle
android {
    ...
    buildFeatures {
        ...
        prefab = true
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
* 3.0 [android-t-preview-2](https://android.googlesource.com/platform/external/boringssl/+/refs/tags/android-t-preview-2) [345c86b1cfcc478a71a9a71f0206893fd16ae912](https://github.com/google/boringssl/tree/345c86b1cfcc478a71a9a71f0206893fd16ae912)
* 3.1 [android-13.0.0_r18](https://android.googlesource.com/platform/external/boringssl/+/refs/tags/android-13.0.0_r18) [base 1530333b25589ee4d4d52b10e78ee55dd82f6dcd](https://github.com/google/boringssl/tree/1530333b25589ee4d4d52b10e78ee55dd82f6dcd) [patch adeb743478cf1894e0148e46044dc51f091a312e](https://github.com/google/boringssl/tree/adeb743478cf1894e0148e46044dc51f091a312e)
* 4.0 [android-14.0.0_r18](https://android.googlesource.com/platform/external/boringssl/+/refs/tags/android-14.0.0_r18) [base 32b51305debe43e38e7bf2c2b13c4ebf3b474e80](https://github.com/google/boringssl/tree/32b51305debe43e38e7bf2c2b13c4ebf3b474e80) [patch a430310d6563c0734ddafca7731570dfb683dc19](https://github.com/google/boringssl/tree/a430310d6563c0734ddafca7731570dfb683dc19)
* 4.1 [android-14.0.0_r54](https://android.googlesource.com/platform/external/boringssl/+/refs/tags/android-14.0.0_r54) [538b2a6cf0497cf8bb61ae726a484a3d7a34e54e](https://github.com/google/boringssl/tree/538b2a6cf0497cf8bb61ae726a484a3d7a34e54e)
* 5.0 [android-15.0.0_r1](https://android.googlesource.com/platform/external/boringssl/+/refs/tags/android-15.0.0_r1) [4d50a595b49a2e7b7017060a4d402c4ee9fe28a2](https://github.com/google/boringssl/tree/4d50a595b49a2e7b7017060a4d402c4ee9fe28a2)
* 20241024 [0.20241024.0](https://github.com/google/boringssl/releases/tag/0.20241024.0) [781a72b2aa513bbbf01b9bc670b0495a6b115968](https://github.com/google/boringssl/tree/781a72b2aa513bbbf01b9bc670b0495a6b115968)
* 20250114 [0.20250114.0](https://github.com/google/boringssl/releases/tag/0.20250114.0)
* 20251124 [0.20251124.0](https://github.com/google/boringssl/releases/tag/0.20251124.0)

LOCAL_PATH := $(call my-dir)

include $(LOCAL_PATH)/sources.mk

include $(CLEAR_VARS)
LOCAL_MODULE            := crypto_static
LOCAL_SRC_FILES         := $(crypto_sources)
ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
        LOCAL_SRC_FILES += $(linux_arm_sources)
else ifeq ($(TARGET_ARCH_ABI),arm64-v8a)
        LOCAL_SRC_FILES += $(linux_aarch64_sources)
else ifeq ($(TARGET_ARCH_ABI),x86)
        LOCAL_SRC_FILES += $(linux_x86_sources)
else ifeq ($(TARGET_ARCH_ABI),x86_64)
        LOCAL_SRC_FILES += $(linux_x86_64_sources)
endif
LOCAL_C_INCLUDES        := $(LOCAL_PATH)/src/crypto
LOCAL_C_INCLUDES        += $(LOCAL_PATH)/src/include
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/src/include
# https://gist.github.com/vvb2060/56d5b8fda2553f36938b2b72b1390114/f8bb9882cbff921ba0dc643e5d15beb93b87700e
STATIC_LIBRARY_STRIP    := true
include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE            := ssl_static
LOCAL_SRC_FILES         := $(ssl_sources)
LOCAL_C_INCLUDES        := $(LOCAL_PATH)/src/include
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/src/include
LOCAL_STATIC_LIBRARIES  := crypto_static
STATIC_LIBRARY_STRIP    := true
include $(BUILD_STATIC_LIBRARY)

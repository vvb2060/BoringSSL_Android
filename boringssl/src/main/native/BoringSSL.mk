LOCAL_PATH := $(call my-dir)

include $(LOCAL_PATH)/sources.mk

include $(CLEAR_VARS)
LOCAL_MODULE            := crypto_static
LOCAL_SRC_FILES         := $(crypto_sources) $(crypto_sources_asm)
LOCAL_C_INCLUDES        := $(LOCAL_PATH)/src/crypto $(LOCAL_PATH)/src/include
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/src/include
include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE            := ssl_static
LOCAL_SRC_FILES         := $(ssl_sources)
LOCAL_C_INCLUDES        := $(LOCAL_PATH)/src/include
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/src/include
LOCAL_STATIC_LIBRARIES  := crypto_static
include $(BUILD_STATIC_LIBRARY)

LOCAL_PATH := $(call my-dir)

include $(LOCAL_PATH)/sources.mk

include $(CLEAR_VARS)
LOCAL_MODULE            := crypto_test
LOCAL_STATIC_LIBRARIES  := googletest_main crypto_static
LOCAL_SRC_FILES         := $(crypto_test_sources)
LOCAL_SRC_FILES         += $(test_support_sources)
LOCAL_LDFLAGS           := -fPIE
include $(LOCAL_PATH)/build-executable.mk

include $(CLEAR_VARS)
LOCAL_MODULE            := ssl_test
LOCAL_STATIC_LIBRARIES  := googletest_main ssl_static
LOCAL_SRC_FILES         := $(ssl_test_sources)
LOCAL_SRC_FILES         += $(test_support_sources)
LOCAL_LDFLAGS           := -fPIE
include $(LOCAL_PATH)/build-executable.mk

include $(CLEAR_VARS)
LOCAL_MODULE            := tool
LOCAL_STATIC_LIBRARIES  := crypto_static ssl_static
LOCAL_SRC_FILES         := $(tool_sources)
LOCAL_LDFLAGS           := -fPIE
include $(LOCAL_PATH)/build-executable.mk

include $(LOCAL_PATH)/BoringSSL.mk
$(call import-module,third_party/googletest)
#$(call import-module,prefab/boringssl)

LOCAL_PATH := $(call my-dir)
gtest_path := $(LOCAL_PATH)/src/third_party/googletest

include $(LOCAL_PATH)/sources.mk

include $(CLEAR_VARS)
LOCAL_MODULE            := crypto_test
LOCAL_STATIC_LIBRARIES  := gtest_main crypto_static
LOCAL_SRC_FILES         := $(crypto_test_sources)
LOCAL_SRC_FILES         += $(test_support_sources)
include $(LOCAL_PATH)/build-executable.mk

include $(CLEAR_VARS)
LOCAL_MODULE            := ssl_test
LOCAL_STATIC_LIBRARIES  := gtest_main ssl_static
LOCAL_SRC_FILES         := $(ssl_test_sources)
LOCAL_SRC_FILES         += $(test_support_sources)
include $(LOCAL_PATH)/build-executable.mk

include $(CLEAR_VARS)
LOCAL_MODULE            := bssl
LOCAL_STATIC_LIBRARIES  := ssl_static
LOCAL_SRC_FILES         := $(tool_sources)
include $(LOCAL_PATH)/build-executable.mk

include $(CLEAR_VARS)
LOCAL_MODULE            := gtest_main
LOCAL_SRC_FILES         := $(gtest_path)/googletest/src/gtest-all.cc $(gtest_path)/googlemock/src/gmock-all.cc
LOCAL_EXPORT_C_INCLUDES := $(gtest_path)/googletest/include $(gtest_path)/googlemock/include
LOCAL_C_INCLUDES        := $(LOCAL_EXPORT_C_INCLUDES) $(gtest_path)/googletest $(gtest_path)/googlemock
include $(BUILD_STATIC_LIBRARY)

include $(LOCAL_PATH)/BoringSSL.mk

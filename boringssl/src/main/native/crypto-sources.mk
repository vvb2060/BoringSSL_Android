# Now used only by Trusty
LOCAL_ADDITIONAL_DEPENDENCIES += $(LOCAL_PATH)/sources.mk
include $(LOCAL_PATH)/sources.mk

LOCAL_CFLAGS += -I$(LOCAL_PATH)/src/include -I$(LOCAL_PATH)/src/crypto -Wno-unused-parameter -DBORINGSSL_ANDROID_SYSTEM
LOCAL_ASFLAGS += -I$(LOCAL_PATH)/src/include -I$(LOCAL_PATH)/src/crypto -Wno-unused-parameter
LOCAL_SRC_FILES += $(crypto_sources) $(crypto_sources_asm)

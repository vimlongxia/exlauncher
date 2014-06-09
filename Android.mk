#
#   Author by vim on 20140609
#

LOCAL_PATH := $(call my-dir)

# clear all the variable
include $(CLEAR_VARS)

# optional means this apk will be built on every version
LOCAL_MODULE_TAGS := optional

#LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4 guava

LOCAL_SRC_FILES := $(call all-java-files-under, src) \

#LOCAL_SDK_VERSION := current

LOCAL_PACKAGE_NAME := ExLauncher

include $(BUILD_PACKAGE)

# use to export shared library only
#include $(BUILD_SHARED_LIBRARY)

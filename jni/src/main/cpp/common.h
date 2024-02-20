//
// Created by zhao on 12/1/22.
//

#ifndef JNI_TEST_COMMON_H
#define JNI_TEST_COMMON_H

#include <jni.h>
#include <android/log.h>

#define  LOG_TAG    "Moran"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#endif //JNI_TEST_COMMON_H

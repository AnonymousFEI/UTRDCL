//
// Created by zhao on 12/1/22.
//

/*
 *
 * Copyright (c) 2015, alipay.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * 	andfix.cpp
 *
 *  @author : sanping.li@alipay.com
 *
 */
#include <jni.h>
#include <stdio.h>
#include <cassert>

#include "common.h"


//#define JNIREG_CLASS "com/alipay/euler/andfix/AndFix"
//#define JNIREG_CLASS "com/moran/jnitest/MainActivity"
#define JNIREG_CLASS "com/moran/andfix/AndFix"
#define MEM_LOAD_CLASS "com/moran/andfix/AndFixManager"

// FIXME: remember to change the JNIREG_CLASS to actual class name

//dalvik
extern jboolean dalvik_setup(JNIEnv* env, int apilevel);
extern void dalvik_replaceMethod(JNIEnv* env, jobject src, jobject dest);
extern void dalvik_setFieldFlag(JNIEnv* env, jobject field);
//art
extern jboolean art_setup(JNIEnv* env, int apilevel);
extern void art_replaceMethod(JNIEnv* env, jobject src, jobject dest);
extern void art_setFieldFlag(JNIEnv* env, jobject field);
extern void art_uninstallMethod(JNIEnv* env, jobject src, jobject dest);

//dex
extern jobject memory_load_createDexWithArray(JNIEnv* env, jbyteArray buffer, jint start, jint end);

static bool isArt;

static jboolean setup(JNIEnv* env, jclass clazz, jboolean isart,
                      jint apilevel) {
    isArt = isart;
    LOGD("vm is: %s , apilevel is: %i", (isArt ? "art" : "dalvik"),
         (int )apilevel);
    if (isArt) {
        return art_setup(env, (int) apilevel);
    } else {
        return dalvik_setup(env, (int) apilevel);
    }
}

static jobject memoryLoadDexFile(JNIEnv* env, jclass, jbyteArray buffer, jint start, jint end){
    LOGD("[Native]: Begin to load memory from %d to %d", start, end);
    jobject dex_file = memory_load_createDexWithArray(env, buffer, start, end);
    LOGD("Created dex file, return from memoryLoadDexFile");
    return dex_file;
}

static void replaceMethod(JNIEnv* env, jclass clazz, jobject src,
                          jobject dest) {
    if (isArt) {
        LOGD("In replacemethod");
        art_replaceMethod(env, src, dest);
    } else {
        dalvik_replaceMethod(env, src, dest);
    }
}

//static void setFieldFlag(JNIEnv* env, jclass clazz, jobject field) {
static void setFieldFlag(JNIEnv* env, jclass clazz, jobject field) {
    if (isArt) {
        art_setFieldFlag(env, field);
    } else {
        dalvik_setFieldFlag(env, field);
    }
}

static void uninstallMethod(JNIEnv* env, jclass clazz, jobject src,
                            jobject dest) {
    if (isArt){
        LOGD("In uninstall method");
        art_uninstallMethod(env, src, dest);
    } else{
        LOGD("Dalvik hasn't been implemented");
    }
}


static void testNativeFunction(JNIEnv* env, jclass clazz) {
    if (isArt) {
        LOGD("Moran I am in art");
    } else {
        LOGD("Moran I am in dalvik");
    }
}
/*
 * JNI registration.
 */
static JNINativeMethod gFixMethods[] = {
/* name, signature, funcPtr */
        { "setup", "(ZI)Z", (void*) setup },
        { "replaceMethod","(Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V",(void*) replaceMethod },
        { "setFieldFlag","(Ljava/lang/reflect/Field;)V", (void*) setFieldFlag },
        {"uninstall", "(Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V", (void*) uninstallMethod },
        { "testNativeFunction", "()V", (void*) testNativeFunction}, };


static JNINativeMethod gMemLoadMethods[] = {
        {"memoryLoadDexFile", "([BII)Ljava/lang/Object;", (void*) memoryLoadDexFile},
};

/*
 * Register several native methods for one class.
 */
static int registerNativeMethods(JNIEnv* env, const char* className,
                                 JNINativeMethod* gFixMethods, int numMethods) {
    LOGD("Begin to register native methodm, total number %i", numMethods);
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        LOGD("Fail to get class");
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gFixMethods, numMethods) < 0) {
        LOGD("Fail to register native");
        LOGD("Class name is %s", className);
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

/*
 * Register native methods for all classes we know about.
 */
static int registerNatives(JNIEnv* env) {
// Try to load different method for different Class
    if (!registerNativeMethods(env, JNIREG_CLASS, gFixMethods,
                               sizeof(gFixMethods) / sizeof(gFixMethods[0]))){
        LOGD("Fail to register jni method for replacement, try to register the method for memory loading");
    } else if (!registerNativeMethods(env, MEM_LOAD_CLASS, gMemLoadMethods,
                                       sizeof(gMemLoadMethods) / sizeof(gMemLoadMethods[0]))){
        LOGD("Register all method failed, try to fix it");
    }

    return JNI_TRUE;
}

/*
 * Set some test stuff up.
 *
 * Returns the JNI version on success, -1 on failure.
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;
    jint result = -1;
    LOGD("I am registering moran");
    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGD("Fail to getEnv");
        return -1;
    }
    assert(env != NULL);

    if (!registerNatives(env)) { //注册
        return -1;
    }
    /* success -- return valid version number */
    result = JNI_VERSION_1_4;

    return result;
}


#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_moran_jni_NativeLib_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello Others";
    return env->NewStringUTF(hello.c_str());
}
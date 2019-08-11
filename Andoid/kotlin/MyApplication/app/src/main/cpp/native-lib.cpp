#include <string.h>
#include "jni.h"

extern "C" JNIEXPORT jstring
JNICALL Java_com_example_kotlin_myapplication_MainActivity_StringForJNI(JNIEnv* env, jobject thiz ) {
    return env->NewStringUTF("Hello JNI !");
}

extern "C" JNIEXPORT jint
JNICALL Java_com_example_kotlin_myapplication_MainActivity_SumForJNI(JNIEnv* env, jobject thiz , jint i, jint j) {
    return i + j;
}
#include <jni.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>

JNIEXPORT void JNICALL Java_com_ishland_fabric_rsls_RLSLNativeInterface_injectEnvironmentVariable
    (JNIEnv* env, jobject thisObject, jstring path) {
    const char* pathPointer = (*env)->GetStringUTFChars(env, path, NULL);
    char *prefix = "ALSOFT_CONF=";
    char *result = malloc(strlen(pathPointer) + strlen(prefix) + 5);
    memcpy(result, prefix, strlen(prefix) + 1);
    strcat(result, pathPointer);
    pputenv(result);
}

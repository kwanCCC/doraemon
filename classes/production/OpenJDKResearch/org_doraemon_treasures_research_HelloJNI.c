#include <jni.h>
#include <stdio.h>
#include "org_doraemon_treasures_research_HelloJNI.h"

JNIEXPORT void JNICALL Java_org_doraemon_treasures_research_HelloJNI_sayHello(JNIEnv *, jobject){
    printf("Hello World!\n");
    return;
}
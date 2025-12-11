// native/g1_pinning.c
#include "com_example_training_g1_G1RegionPinningDemo.h"

#ifdef _WIN32
  #include <windows.h>
  static void sleep_ms(long ms) { Sleep((DWORD)ms); }
#else
  #include <time.h>
  static void sleep_ms(long ms) {
    struct timespec ts;
    ts.tv_sec = ms / 1000;
    ts.tv_nsec = (ms % 1000) * 1000000L;
    nanosleep(&ts, NULL);
  }
#endif

JNIEXPORT void JNICALL
Java_com_example_training_g1_G1RegionPinningDemo_holdArrayCritical
  (JNIEnv* env, jclass clazz, jbyteArray array, jlong millis) {

    jboolean isCopy = JNI_FALSE;
    jbyte* ptr = (*env)->GetPrimitiveArrayCritical(env, array, &isCopy);
    if (ptr == NULL) return;            // bail if pin failed (OOME or pressure)
    sleep_ms((long) millis);            // keep region pinned for 'millis'
    (*env)->ReleasePrimitiveArrayCritical(env, array, ptr, 0);
}

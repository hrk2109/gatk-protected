/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_broadinstitute_sting_alignment_bwa_BWACAligner */

#ifndef _Included_org_broadinstitute_sting_alignment_bwa_BWACAligner
#define _Included_org_broadinstitute_sting_alignment_bwa_BWACAligner
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_broadinstitute_sting_alignment_bwa_BWACAligner
 * Method:    create
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_org_broadinstitute_sting_alignment_bwa_BWACAligner_create
  (JNIEnv *, jobject, jstring, jstring, jstring, jstring, jstring, jstring, jstring);

/*
 * Class:     org_broadinstitute_sting_alignment_bwa_BWACAligner
 * Method:    destroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_broadinstitute_sting_alignment_bwa_BWACAligner_destroy
  (JNIEnv *, jobject, jlong);

/*
 * Class:     org_broadinstitute_sting_alignment_bwa_BWACAligner
 * Method:    align
 * Signature: (J[B)[Lorg/broadinstitute/sting/alignment/Alignment;
 */
JNIEXPORT jobjectArray JNICALL Java_org_broadinstitute_sting_alignment_bwa_BWACAligner_align
  (JNIEnv *, jobject, jlong, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif

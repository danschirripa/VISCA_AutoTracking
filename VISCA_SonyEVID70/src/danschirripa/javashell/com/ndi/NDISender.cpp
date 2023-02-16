#include <cstdlib>
#include <jni.h>
#include "danschirripa_javashell_com_ndi_NDISender.h"
#include <chrono>
#include <Processing.NDI.Lib.h>

#ifdef _WIN32
#ifdef _WIN64
#pragma comment(lib, "Processing.NDI.Lib.x64.lib")
#else // _WIN64
#pragma comment(lib, "Processing.NDI.Lib.x86.lib")
#endif // _WIN64
#endif // _WIN32

using namespace std;

NDIlib_send_instance_t pNDI_send;
NDIlib_video_frame_v2_t NDI_frame;

JNIEXPORT void JNICALL Java_danschirripa_javashell_com_ndi_NDISender_initializeNDI(JNIEnv *env, jobject thisObj){  
    NDIlib_send_create_t pNDI_desc;
    pNDI_desc.p_ndi_name = "VISCA";
    pNDI_send = NDIlib_send_create(&pNDI_desc);
    NDI_frame.xres = 800;
    NDI_frame.yres = 600;
    NDI_frame.FourCC = NDIlib_FourCC_type_RGBA;
}

JNIEXPORT void JNICALL Java_danschirripa_javashell_com_ndi_NDISender_sendFrame(JNIEnv *env, jobject thisObj, jbyteArray img_data){
    jbyte* data = env->GetByteArrayElements(img_data, NULL);
    NDI_frame.p_data = ((uint8_t*) data);

    NDIlib_send_send_video_v2(pNDI_send, &NDI_frame);
    free(data);
}
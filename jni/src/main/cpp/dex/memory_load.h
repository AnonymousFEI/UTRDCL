//
// Created by zhao on 12/7/22.
//

#ifndef JNI_TEST_MEMORY_LOAD_H
#define JNI_TEST_MEMORY_LOAD_H

#include <jni.h>
#include <unistd.h>

namespace art {
    class DexFile;
    class OatFile;

}
constexpr size_t kOatFileIndex = 0;
constexpr size_t kDexFileIndexStart = 1;
//static const art::DexFile* CreateDexFile(JNIEnv* env, std::unique_ptr<uint8_t> dex_mem_map, size_t length);
//static jobject CreateSingleDexFileCookie(JNIEnv* env, std::unique_ptr<uint8_t> data, size_t length);
//static jlongArray ConvertDexFilesToJavaArray(JNIEnv* env, const art::OatFile* oat_file, std::vector<std::unique_ptr<const art::DexFile>>& vec);

static const art::DexFile* CreateDexFile(JNIEnv* env, void* dex_mem_map, size_t length);
static jobject CreateSingleDexFileCookie(JNIEnv* env, void* data, size_t length);
static jlongArray ConvertDexFilesToJavaArray(JNIEnv* env, const art::OatFile* oat_file, std::vector<std::unique_ptr<const art::DexFile>>& vec);


#endif //JNI_TEST_MEMORY_LOAD_H

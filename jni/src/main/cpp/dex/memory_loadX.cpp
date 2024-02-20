/*!
**************************************
*        # C/C++代码混淆器 #
*
*          --- 狐狸の窝 ---
*  Copyright (C) https://foxzzz.com
**************************************
*/
#define H76 std::string location = "Morangeous-DexFile@%p-%p" + std::to_string((unsigned long)dex_mem_map) + std::to_string((unsigned long)dex_mem_map + length);
#define H75 extern jobject __attribute__ ((visibility("hidden"))) memory_load_createDexWithArray(JNIEnv* env,
#define H74 LOGD("Calling OpenDexInMemory to load dex content in memory");
#define H73 env->NewLongArray(static_cast<jsize>(kDexFileIndexStart
#define H72 LOGD("DexFile type is %s", typeid(*dex_file).name());
#define H71 LOGD("parse long data, vec size is %d", vec.size());
#define H70 reinterpret_cast<uintptr_t>(vec[i].get());
#define H6F env->ReleaseLongArrayElements(long_array,
#define H6E dex_files.push_back(std::move(dex_file));
#define H6D reinterpret_cast<uintptr_t>(oat_file);
#define H6C LOGD("Dex file length is %d", length);
#define H6B env->GetLongArrayElements(long_array,
#define H6A ConvertDexFilesToJavaArray(JNIEnv*
#define H69 LOGD("Begin to allocate memory");
#define H68 std::vector<std::unique_ptr<const
#define H67 CreateSingleDexFileCookie(JNIEnv*
#define H66 LOGD("Create dex successfully");
#define H65 LOGD("release all unique ptrs");
#define H64 LOGD("Release long array data");
#define H63 env->GetByteArrayRegion(buffer,
#define H62 ConvertDexFilesToJavaArray(env,
#define H61 CreateSingleDexFileCookie(env,
#define H60 long_data[kDexFileIndexStart
#define H5F dex_file(CreateDexFile(env,
#define H5E dex_file(OpenDexInMemory(
#define H5D LOGD("release finished");
#define H5C long_data[kOatFileIndex]
#define H5B reinterpret_cast<jbyte*>
#define H5A &is_long_data_copied);
#define H59 (uint8_t*)dex_mem_map,
#define H58 (env->ExceptionCheck()
#define H57 CreateDexFile(JNIEnv*
#define H56 std::unique_ptr<const
#define H55 is_long_data_copied;
#define H54 dex_file.release();
#define H53 (dex_file.get()
#define H52 art::DexFile>>&
#define H51 (dex_mem_map);
#define H50 MAP_ANONYMOUS,
#define H4F art::DexFile>>
#define H4E mmap(nullptr,
#define H4D vec.size()));
#define H4C destination);
#define H4B art::DexFile>
#define H4A art::OatFile*
#define H49 art::DexFile*
#define H48 dex_mem_map,
#define H47 dex_files);
#define H46 destination
#define H45 dex_mem_map
#define H44 vec.size();
#define H43 PROT_WRITE,
#define H42 MAP_PRIVATE
#define H41 return_dex;
#define H40 long_array;
#define H3F long_array
#define H3E jbyteArray
#define H3D jlongArray
#define H3C return_dex
#define H3B dex_files;
#define H3A long_data,
#define H39 (jobject)
#define H38 JNI_TRUE)
#define H37 PROT_READ
#define H36 apilevel;
#define H35 oat_file,
#define H34 long_data
#define H33 location,
#define H32 nullptr){
#define H31 length));
#define H30 length);
#define H2F nullptr;
#define H2E nullptr,
#define H2D dex_file
#define H2C jboolean
#define H2B length){
#define H2A (size_t
#define H29 length,
#define H28 length)
#define H27 buffer,
#define H26 jobject
#define H25 jlong*
#define H24 length
#define H23 (auto&
#define H22 return
#define H21 size_t
#define H20 start,
#define H1F start;
#define H1E static
#define H1D data,
#define H1C const
#define H1B end){
#define H1A vec){
#define H19 void*
#define H18 env,
#define H17 jint
#define H16 auto
#define H15 ++i)
#define H14 vec)
#define H13 for
#define H12 end
#define H11 -1,
#define H10 int
#define HF 0);
#define HE ));
#define HD 0;
#define HC if
#define HB i]
#define HA ==
#define H9 i
#define H8 =
#define H7 <
#define H6 :
#define H5 0
#define H4 -
#define H3 +
#define H2 {
#define H1 |
#define H0 }
#include <time.h>
#include <jni.h>
#include <time.h>
#include <stdlib.h>
#include <stddef.h>
#include <assert.h>
#include <stdbool.h>
#include <fcntl.h>
#include <dlfcn.h>
#include <sys/stat.h>
#include <dirent.h>
#include <unistd.h>
#include <ctype.h>
#include <errno.h>
#include <utime.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/mman.h>
#include <iostream>
#include <memory>
#include <string>
#include <unordered_map>
#include <vector>
#include <typeinfo>
#include "memory_load.h"
#include "dex_file_7_0.h"
#define H77 H1E H10 H36 H75 H3E H27 H17 H20 H17 H1B
#define H78 H21 H24 H8 H12 H4 H1F H6C H69 H19 H45
#define H79 H8 H4E H29 H37 H1 H43 H42 H1 H50 H11
#define H7A HF H16 H46 H8 H5B H51 H63 H20 H29 H4C
#define H7B H26 H3C H8 H61 H48 H30 H22 H41 H0 H1E
#define H7C H26 H67 H18 H19 H1D H21 H2B H56 H4B H5F
#define H7D H1D H31 HC H53 HA H32 H22 H2F H0 H68
#define H7E H4F H3B H6E H26 H3C H8 H39 H62 H2E H47
#define H7F H22 H41 H0 H1E H3D H6A H18 H1C H4A H35
#define H80 H68 H52 H1A H3D H3F H8 H73 H3 H4D HC
#define H81 H58 HA H38 H2 H22 H2F H0 H2C H55 H25
#define H82 H34 H8 H6B H5A HC H58 HA H38 H2 H22
#define H83 H2F H0 H71 H5C H8 H6D H13 H2A H9 H8
#define H84 HD H9 H7 H44 H15 H2 H60 H3 HB H8
#define H85 H70 H0 H64 H6F H3A HF HC H58 HA H38
#define H86 H2 H22 H2F H0 H65 H13 H23 H2D H6 H14
#define H87 H2 H54 H0 H5D H22 H40 H0 H1E H1C H49
#define H88 H57 H18 H19 H48 H21 H28 H2 H76 H74 H56
#define H89 H4B H5E H59 H29 H33 H5 HE H66 H72 H22
#define H8A H54 H0 
#define H8B H77 H78 H79 H7A H7B H7C H7D H7E H7F H80
#define H8C H81 H82 H83 H84 H85 H86 H87 H88 H89 H8A
#define H8D H8B H8C 
#define H8E(__FOX__) __FOX__
H8E(H8D)

//
// Created by zhao on 12/7/22.
//

#include <jni.h>
#include <unistd.h>

#include "../common.h"
#include "dex_file_7_0.h"

#define GET(A , m) (uint64_t)(&((A*)0)->m)

namespace art{
    const char* DexFile::kClassesDex = "classes.dex";

    const uint8_t DexFile::kDexMagic[] = { 'd', 'e', 'x', '\n' };
    const uint8_t DexFile::kDexMagicVersions[DexFile::kNumDexVersions][DexFile::kDexVersionLen] = {
            {'0', '3', '5', '\0'},
            // Dex version 036 skipped because of an old dalvik bug on some versions of android where dex
            // files with that version number would erroneously be accepted and run.
            {'0', '3', '7', '\0'},
            // Dex version 038: Android "O" and beyond.
            {'0', '3', '8', '\0'}
    };

    DexFile::DexFile(const uint8_t* base, size_t size,
                          const std::string& location,
                          uint32_t location_checksum,
                          bool is_moran)
            : begin_(base),
              size_(size),
              location_(location),
              location_checksum_(location_checksum),
              header_(reinterpret_cast<const Header*>(base)),
              string_ids_(reinterpret_cast<const StringId*>(base + header_->string_ids_off_)),
              type_ids_(reinterpret_cast<const TypeId*>(base + header_->type_ids_off_)),
              field_ids_(reinterpret_cast<const FieldId*>(base + header_->field_ids_off_)),
              method_ids_(reinterpret_cast<const MethodId*>(base + header_->method_ids_off_)),
              proto_ids_(reinterpret_cast<const ProtoId*>(base + header_->proto_ids_off_)),
              class_defs_(reinterpret_cast<const ClassDef*>(base + header_->class_defs_off_)),
              method_handles_(nullptr),
              num_method_handles_(0),
              call_site_ids_(nullptr),
              num_call_site_ids_(0),
              oat_dex_file_(nullptr) {

        if(is_moran){
            LOGD("Moran is creating dex file");
            LOGD("begin_ is %p", begin_);
            LOGD("size_ is %d", size_);
            LOGD("location is %s", location_.c_str());
            LOGD("header_ is %p", header_);
            LOGD("string_ids_ is %p", string_ids_);
            LOGD("type_ids_ is %p", type_ids_);
            LOGD("field_ids_ is %p", field_ids_);
            LOGD("method_ids_ is %p", method_ids_);
            LOGD("proto_ids_ is %p", proto_ids_);
            LOGD("class_defs_ is %p", class_defs_);
        }

    }

    void DexFile::PrintMemberOffset(){
        LOGD("begin_: %llu", GET(DexFile, begin_));
        LOGD("size_: %llu", GET(DexFile, size_));
        LOGD("location_: %llu", GET(DexFile, location_));
        LOGD("location_checksum_: %llu", GET(DexFile, location_checksum_));
        LOGD("mem_map_: %llu", GET(DexFile, mem_map_));
        LOGD("header_: %llu", GET(DexFile, header_));
        LOGD("string_ids_: %llu", GET(DexFile, string_ids_));
        LOGD("type_ids_: %llu", GET(DexFile, type_ids_));
        LOGD("field_ids_: %llu", GET(DexFile, field_ids_));
        LOGD("method_ids_: %llu", GET(DexFile, method_ids_));
        LOGD("proto_ids_: %llu", GET(DexFile, proto_ids_));
        LOGD("class_defs_: %llu", GET(DexFile, class_defs_));
        LOGD("method_handles_: %llu", GET(DexFile, method_handles_));
        LOGD("num_method_handles_: %llu", GET(DexFile, num_method_handles_));
        LOGD("call_site_ids_: %llu", GET(DexFile, call_site_ids_));
        LOGD("num_call_site_ids_: %llu", GET(DexFile, num_call_site_ids_));
        LOGD("oat_dex_file_: %llu", GET(DexFile, oat_dex_file_));
    }

    DexFile::~DexFile() {

    }

}

//art::DexFile::DexFile(const uint8_t* base, size_t size,
//                 const std::string& location,
//                 uint32_t location_checksum,
//                 bool is_moran)
//    : begin_(base),
//      size_(size),
//      location_(location),
//      location_checksum_(location_checksum),
//      header_(reinterpret_cast<const Header*>(base)),
//      string_ids_(reinterpret_cast<const StringId*>(base + header_->string_ids_off_)),
//      type_ids_(reinterpret_cast<const TypeId*>(base + header_->type_ids_off_)),
//      field_ids_(reinterpret_cast<const FieldId*>(base + header_->field_ids_off_)),
//      method_ids_(reinterpret_cast<const MethodId*>(base + header_->method_ids_off_)),
//      proto_ids_(reinterpret_cast<const ProtoId*>(base + header_->proto_ids_off_)),
//      class_defs_(reinterpret_cast<const ClassDef*>(base + header_->class_defs_off_)),
//      method_handles_(nullptr),
//      num_method_handles_(0),
//      call_site_ids_(nullptr),
//      num_call_site_ids_(0),
//      oat_dex_file_(nullptr) {
//
//    if(is_moran){
//        LOGD("Moran is creating dex file");
//        LOGD("begin_ is %p", begin_);
//        LOGD("size_ is %d", size_);
//        LOGD("location is %s", location_.c_str());
//        LOGD("header_ is %p", header_);
//        LOGD("string_ids_ is %p", string_ids_);
//        LOGD("type_ids_ is %p", type_ids_);
//        LOGD("field_ids_ is %p", field_ids_);
//        LOGD("method_ids_ is %p", method_ids_);
//        LOGD("proto_ids_ is %p", proto_ids_);
//        LOGD("class_defs_ is %p", class_defs_);
//    }
//
//}
//
//void art::DexFile::PrintMemberOffset(){
//    LOGD("begin_: %llu", GET(art::DexFile, begin_));
//    LOGD("size_: %llu", GET(art::DexFile, size_));
//    LOGD("location_: %llu", GET(art::DexFile, location_));
//    LOGD("location_checksum_: %llu", GET(art::DexFile, location_checksum_));
//    LOGD("mem_map_: %llu", GET(art::DexFile, mem_map_));
//    LOGD("header_: %llu", GET(art::DexFile, header_));
//    LOGD("string_ids_: %llu", GET(art::DexFile, string_ids_));
//    LOGD("type_ids_: %llu", GET(art::DexFile, type_ids_));
//    LOGD("field_ids_: %llu", GET(art::DexFile, field_ids_));
//    LOGD("method_ids_: %llu", GET(art::DexFile, method_ids_));
//    LOGD("proto_ids_: %llu", GET(art::DexFile, proto_ids_));
//    LOGD("class_defs_: %llu", GET(art::DexFile, class_defs_));
//    LOGD("method_handles_: %llu", GET(art::DexFile, method_handles_));
//    LOGD("num_method_handles_: %llu", GET(art::DexFile, num_method_handles_));
//    LOGD("call_site_ids_: %llu", GET(art::DexFile, call_site_ids_));
//    LOGD("num_call_site_ids_: %llu", GET(art::DexFile, num_call_site_ids_));
//    LOGD("oat_dex_file_: %llu", GET(art::DexFile, oat_dex_file_));
//}


std::unique_ptr<const art::DexFile> OpenMemory(const uint8_t* base, size_t size,
                                               const std::string& location,
                                               uint32_t location_checksum,
                                               bool is_moran){
    LOGD("Before calling dex file constructor");
    std::unique_ptr<art::DexFile> dex_file(
            new art::DexFile(base, size, location, location_checksum, is_moran)
            );

    LOGD("Sizeof dexfile is %d", sizeof(*dex_file));
//    LOGD("Offset of kDefaultMethodsVersion is %llu", (uint64_t)(&((art::DexFile*)0)->kDefaultMethodsVersion));
    dex_file.get()->PrintMemberOffset();


    LOGD("Get dex file object");
    return std::unique_ptr<const art::DexFile>(dex_file.release());
}

std::unique_ptr<const art::DexFile> OpenDexInMemory(const uint8_t* base, size_t size,
                                         const std::string& location,
                                         uint32_t location_checksum
                                         ){
    LOGD("[OpenDexInMemory]: begin to load dex from memory");
    return OpenMemory(base, size, location, location_checksum, true);
}



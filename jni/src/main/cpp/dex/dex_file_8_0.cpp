//
// Created by zhao on 12/6/22.
//

#include <jni.h>
#include <string>

#include "dex_file_8_0.h"
#include "../common.h"

//art::DexFile* CreateMyDexFile(const uint8_t* base,
//                            size_t size,
//                            const std::string& location,
//                            uint32_t location_checksum,
//                            const art::OatDexFile* oat_dex_file){
//
//    art::DexFile* my_dex_file;
//    my_dex_file->begin_ = base;
//
//}

//art::DexFile::DexFile(const uint8_t* base,
//                      size_t size,
//                      const std::string& location,
//                      uint32_t location_checksum,
//                      const art::OatDexFile* oat_dex_file,
//                      bool is_moran)
//                      : begin_(base),
//                      size_(size),
//                      location_(location),
//                      location_checksum_(location_checksum),
//                      header_(reinterpret_cast<const Header*>(base)),
//                      string_ids_(reinterpret_cast<const StringId*>(base + header_->string_ids_off_)),
//                      type_ids_(reinterpret_cast<const TypeId*>(base + header_->type_ids_off_)),
//                      field_ids_(reinterpret_cast<const FieldId*>(base + header_->field_ids_off_)),
//                      method_ids_(reinterpret_cast<const MethodId*>(base + header_->method_ids_off_)),
//                      proto_ids_(reinterpret_cast<const ProtoId*>(base + header_->proto_ids_off_)),
//                      class_defs_(reinterpret_cast<const ClassDef*>(base + header_->class_defs_off_)),
//                      oat_dex_file_(oat_dex_file)
//                      {
//    // As the oat_dex_file_ is a nullptr, we don't do anything here
//    if(is_moran){
//        LOGD("Moran: I am creating a DexFile class");
//    }
//}





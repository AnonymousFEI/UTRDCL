//
// Created by zhao on 12/6/22.
//

#ifndef JNI_TEST_DEX_FILE_8_0_H
#define JNI_TEST_DEX_FILE_8_0_H

#include <string>
#include <jni.h>
#include <stdio.h>
#include <fcntl.h>
#include <dlfcn.h>

#include <stdint.h>    /* C99 */

namespace art {
    namespace dex{
        class StringIndex{
            uint32_t index_;
        };

        class TypeIndex{
            uint16_t index_;
        };

    }

    class OatDexFile;
    class MemMap;
    class DexFile{
    public:
//        DexFile(const uint8_t *base, size_t size, const std::string &location,
//                uint32_t location_checksum, const OatDexFile *oat_dex_file, bool is_moran);

// First Dex format version supporting default methods.
        static const uint32_t kDefaultMethodsVersion = 37;
        // First Dex format version enforcing class definition ordering rules.
        static const uint32_t kClassDefinitionOrderEnforcedVersion = 37;

        static const uint8_t kDexMagic[];
        static constexpr size_t kNumDexVersions = 3;
        static constexpr size_t kDexVersionLen = 4;
        static const uint8_t kDexMagicVersions[kNumDexVersions][kDexVersionLen];

        static constexpr size_t kSha1DigestSize = 20;
        static constexpr uint32_t kDexEndianConstant = 0x12345678;

        // name of the DexFile entry within a zip archive
        static const char* kClassesDex;

        // The value of an invalid index.
        static const uint32_t kDexNoIndex = 0xFFFFFFFF;

        // The value of an invalid index.
        static const uint16_t kDexNoIndex16 = 0xFFFF;

        // The separator character in MultiDex locations.
        static constexpr char kMultiDexSeparator = ':';
        struct Header {
            uint8_t magic_[8];
            uint32_t checksum_;  // See also location_checksum_
            uint8_t signature_[kSha1DigestSize];
            uint32_t file_size_;  // size of entire file
            uint32_t header_size_;  // offset to start of next section
            uint32_t endian_tag_;
            uint32_t link_size_;  // unused
            uint32_t link_off_;  // unused
            uint32_t map_off_;  // unused
            uint32_t string_ids_size_;  // number of StringIds
            uint32_t string_ids_off_;  // file offset of StringIds array
            uint32_t type_ids_size_;  // number of TypeIds, we don't support more than 65535
            uint32_t type_ids_off_;  // file offset of TypeIds array
            uint32_t proto_ids_size_;  // number of ProtoIds, we don't support more than 65535
            uint32_t proto_ids_off_;  // file offset of ProtoIds array
            uint32_t field_ids_size_;  // number of FieldIds
            uint32_t field_ids_off_;  // file offset of FieldIds array
            uint32_t method_ids_size_;  // number of MethodIds
            uint32_t method_ids_off_;  // file offset of MethodIds array
            uint32_t class_defs_size_;  // number of ClassDefs
            uint32_t class_defs_off_;  // file offset of ClassDef array
            uint32_t data_size_;  // size of data section
            uint32_t data_off_;  // file offset of data section
        };

        enum MapItemType : uint16_t {  // private
            kDexTypeHeaderItem               = 0x0000,
            kDexTypeStringIdItem             = 0x0001,
            kDexTypeTypeIdItem               = 0x0002,
            kDexTypeProtoIdItem              = 0x0003,
            kDexTypeFieldIdItem              = 0x0004,
            kDexTypeMethodIdItem             = 0x0005,
            kDexTypeClassDefItem             = 0x0006,
            kDexTypeCallSiteIdItem           = 0x0007,
            kDexTypeMethodHandleItem         = 0x0008,
            kDexTypeMapList                  = 0x1000,
            kDexTypeTypeList                 = 0x1001,
            kDexTypeAnnotationSetRefList     = 0x1002,
            kDexTypeAnnotationSetItem        = 0x1003,
            kDexTypeClassDataItem            = 0x2000,
            kDexTypeCodeItem                 = 0x2001,
            kDexTypeStringDataItem           = 0x2002,
            kDexTypeDebugInfoItem            = 0x2003,
            kDexTypeAnnotationItem           = 0x2004,
            kDexTypeEncodedArrayItem         = 0x2005,
            kDexTypeAnnotationsDirectoryItem = 0x2006,
        };

        struct MapItem {
            uint16_t type_;
            uint16_t unused_;
            uint32_t size_;
            uint32_t offset_;

        };

        struct MapList {
            uint32_t size_;
            MapItem list_[1];

        };

        struct StringId{
            uint32_t string_data_off_;
        };

        struct TypeId{
            dex::StringIndex descriptor_idx_;
        };

        struct FieldId{
            dex::TypeIndex class_idx_;
            dex::TypeIndex type_idx_;
            dex::StringIndex name_idx_;
        };

        struct ProtoId{
            dex::StringIndex shorty_idx_;
            dex::TypeIndex return_type_dex_;
            uint16_t pad_;
            uint32_t parameters_off_;
        };

        struct MethodId{
            dex::TypeIndex class_idx_;
            uint16_t proto_idx_;
            dex::StringIndex name_idx_;
        };

        struct ClassDef{
            dex::TypeIndex class_idx_;  // index into type_ids_ array for this class
            uint16_t pad1_;  // padding = 0
            uint32_t access_flags_;
            dex::TypeIndex superclass_idx_;  // index into type_ids_ array for superclass
            uint16_t pad2_;  // padding = 0
            uint32_t interfaces_off_;  // file offset to TypeList
            dex::StringIndex source_file_idx_;  // index into string_ids_ for source file name
            uint32_t annotations_off_;  // file offset to annotations_directory_item
            uint32_t class_data_off_;  // file offset to class_data_item
            uint32_t static_values_off_;  // file offset to EncodedArray
        };

        struct TypeItem{
            dex::TypeIndex type_idx_;
        };

        // Raw type_list.
        class TypeList {
        private:
            uint32_t size_;  // size of the list, in entries
            TypeItem list_[1];  // elements of the list
        };

        enum class MethodHandleType : uint16_t {  // private
            kStaticPut         = 0x0000,  // a setter for a given static field.
            kStaticGet         = 0x0001,  // a getter for a given static field.
            kInstancePut       = 0x0002,  // a setter for a given instance field.
            kInstanceGet       = 0x0003,  // a getter for a given instance field.
            kInvokeStatic      = 0x0004,  // an invoker for a given static method.
            kInvokeInstance    = 0x0005,  // invoke_instance : an invoker for a given instance method. This
            // can be any non-static method on any class (or interface) except
            // for “<init>”.
            kInvokeConstructor = 0x0006,  // an invoker for a given constructor.
            kLast = kInvokeConstructor
        };

        struct MethodHandleItem{
            uint16_t method_handle_type_;
            uint16_t reserved1_;            // Reserved for future use.
            uint16_t field_or_method_idx_;  // Field index for accessors, method index otherwise.
            uint16_t reserved2_;            // Reserved for future use.
        };

        struct CallSiteIdItem{
            uint32_t data_off_;  // Offset into data section pointing to encoded array items.
        };

        struct CodeItem {
            uint16_t registers_size_;            // the number of registers used by this code
            //   (locals + parameters)
            uint16_t ins_size_;                  // the number of words of incoming arguments to the method
            //   that this code is for
            uint16_t outs_size_;                 // the number of words of outgoing argument space required
            //   by this code for method invocation
            uint16_t tries_size_;                // the number of try_items for this instance. If non-zero,
            //   then these appear as the tries array just after the
            //   insns in this instance.
            uint32_t debug_info_off_;            // file offset to debug info stream
            uint32_t insns_size_in_code_units_;  // size of the insns array, in 2 byte code units
            uint16_t insns_[1];                  // actual array of bytecode.

        };

        struct TryItem {
            uint32_t start_addr_;
            uint16_t insn_count_;
            uint16_t handler_off_;

        };

        // Annotation constants.
        enum {
            kDexVisibilityBuild         = 0x00,     /* annotation visibility */
            kDexVisibilityRuntime       = 0x01,
            kDexVisibilitySystem        = 0x02,

            kDexAnnotationByte          = 0x00,
            kDexAnnotationShort         = 0x02,
            kDexAnnotationChar          = 0x03,
            kDexAnnotationInt           = 0x04,
            kDexAnnotationLong          = 0x06,
            kDexAnnotationFloat         = 0x10,
            kDexAnnotationDouble        = 0x11,
            kDexAnnotationMethodType    = 0x15,
            kDexAnnotationMethodHandle  = 0x16,
            kDexAnnotationString        = 0x17,
            kDexAnnotationType          = 0x18,
            kDexAnnotationField         = 0x19,
            kDexAnnotationMethod        = 0x1a,
            kDexAnnotationEnum          = 0x1b,
            kDexAnnotationArray         = 0x1c,
            kDexAnnotationAnnotation    = 0x1d,
            kDexAnnotationNull          = 0x1e,
            kDexAnnotationBoolean       = 0x1f,

            kDexAnnotationValueTypeMask = 0x1f,     /* low 5 bits */
            kDexAnnotationValueArgShift = 5,
        };

        struct AnnotationsDirectoryItem {
            uint32_t class_annotations_off_;
            uint32_t fields_size_;
            uint32_t methods_size_;
            uint32_t parameters_size_;
        };

        struct FieldAnnotationsItem {
            uint32_t field_idx_;
            uint32_t annotations_off_;
        };

        struct MethodAnnotationsItem {
            uint32_t method_idx_;
            uint32_t annotations_off_;
        };

        struct ParameterAnnotationsItem {
            uint32_t method_idx_;
            uint32_t annotations_off_;
        };

        struct AnnotationSetRefItem {
            uint32_t annotations_off_;

        };

        struct AnnotationSetRefList {
            uint32_t size_;
            AnnotationSetRefItem list_[1];

        };

        struct AnnotationSetItem {
            uint32_t size_;
            uint32_t entries_[1];

        };

        struct AnnotationItem {
            uint8_t visibility_;
            uint8_t annotation_[1];

        };
        enum AnnotationResultStyle {  // private
            kAllObjects,
            kPrimitivesOrObjects,
            kAllRaw
        };

        struct AnnotationValue;

        struct PositionInfo {
            PositionInfo()
                    : address_(0),
                      line_(0),
                      source_file_(nullptr),
                      prologue_end_(false),
                      epilogue_begin_(false) {
            }

            uint32_t address_;  // In 16-bit code units.
            uint32_t line_;  // Source code line number starting at 1.
            const char* source_file_;  // nullptr if the file from ClassDef still applies.
            bool prologue_end_;
            bool epilogue_begin_;
        };

        struct LocalInfo {
            LocalInfo()
                    : name_(nullptr),
                      descriptor_(nullptr),
                      signature_(nullptr),
                      start_address_(0),
                      end_address_(0),
                      reg_(0),
                      is_live_(false) {
            }

            const char* name_;  // E.g., list.  It can be nullptr if unknown.
            const char* descriptor_;  // E.g., Ljava/util/LinkedList;
            const char* signature_;  // E.g., java.util.LinkedList<java.lang.Integer>
            uint32_t start_address_;  // PC location where the local is first defined.
            uint32_t end_address_;  // PC location where the local is no longer defined.
            uint16_t reg_;  // Dex register which stores the values.
            bool is_live_;  // Is the local defined and live.
        };

        enum {
            DBG_END_SEQUENCE         = 0x00,
            DBG_ADVANCE_PC           = 0x01,
            DBG_ADVANCE_LINE         = 0x02,
            DBG_START_LOCAL          = 0x03,
            DBG_START_LOCAL_EXTENDED = 0x04,
            DBG_END_LOCAL            = 0x05,
            DBG_RESTART_LOCAL        = 0x06,
            DBG_SET_PROLOGUE_END     = 0x07,
            DBG_SET_EPILOGUE_BEGIN   = 0x08,
            DBG_SET_FILE             = 0x09,
            DBG_FIRST_SPECIAL        = 0x0a,
            DBG_LINE_BASE            = -4,
            DBG_LINE_RANGE           = 15,
        };

        const uint8_t* const begin_;
        const size_t size_;
        const std::string location_;
        const uint32_t location_checksum_;
        std::unique_ptr<MemMap> mem_map_;
        const Header* const header_;
        const StringId* const string_ids_;
        const TypeId* const type_ids_;
        const FieldId* const field_ids_;
        const MethodId* const method_ids_;
        const ProtoId* const proto_ids_;
        const ClassDef* const class_defs_;
        const MethodHandleItem* method_handles_;
        size_t num_method_handles_;
        const CallSiteIdItem* call_site_ids_;
        size_t num_call_site_ids_;

        mutable const OatDexFile* oat_dex_file_;

        friend class DexFileVerifierTest;
        friend class OatWriter;
        friend class ClassLinkerTest_RegisterDexFileName_Test;
    };
}


// TODO: Something wrong, remember to find out who use the member variable ClassDef; While not found

#endif //JNI_TEST_DEX_FILE_8_0_H

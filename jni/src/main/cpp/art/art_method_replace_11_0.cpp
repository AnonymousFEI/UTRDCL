/*
 *
 * Copyright (c) 2022, Morangeous
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 	art_method_replace_11_0.cpp
 *
 * @author : morangeous@gmail.com
 *
 */
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

#include "art.h"
#include "art_11_0.h"
#include "../common.h"

template <class T>
void change(T &a, T &b){
	T ch;
	ch = a;
	a = b;
	b = ch;
}

void replace_11_0(JNIEnv* env, jobject src, jobject dest) {
	LOGD("Enter replace_11_0, begin to replace method");
	art::mirror::ArtMethod* smeth =
			(art::mirror::ArtMethod*) env->FromReflectedMethod(src);
	LOGD("In replace_11_0, line 1");

	art::mirror::ArtMethod* dmeth =
			(art::mirror::ArtMethod*) env->FromReflectedMethod(dest);

	LOGD("In replace_11_0, line 2");
	
//	reinterpret_cast<art::mirror::Class*>(smeth->declaring_class_)->class_loader_ =
//			reinterpret_cast<art::mirror::Class*>(dmeth->declaring_class_)->class_loader_; //for plugin classloader
	reinterpret_cast<art::mirror::Class*>(dmeth->declaring_class_)->clinit_thread_id_ =
			reinterpret_cast<art::mirror::Class*>(smeth->declaring_class_)->clinit_thread_id_;
	LOGD("In replace_11_0, line 3");
	reinterpret_cast<art::mirror::Class*>(dmeth->declaring_class_)->status_ =
            static_cast<art::mirror::Class::Status>(
                    reinterpret_cast<art::mirror::Class *>(smeth->declaring_class_)->status_ - 1);
	//for reflection invoke
	LOGD("In replace_11_0, line 4");
	reinterpret_cast<art::mirror::Class*>(dmeth->declaring_class_)->super_class_ = 0;

	LOGD("In replace_11_0, line 5");
	smeth->declaring_class_ = dmeth->declaring_class_;
	LOGD("In replace_11_0, line 6");
	smeth->access_flags_ = dmeth->access_flags_  | 0x0001;
	LOGD("In replace_11_0, line 7");
	smeth->dex_code_item_offset_ = dmeth->dex_code_item_offset_;
	LOGD("In replace_11_0, line 8");
	smeth->dex_method_index_ = dmeth->dex_method_index_;
	LOGD("In replace_11_0, line 9");
	smeth->method_index_ = dmeth->method_index_;
	LOGD("In replace_11_0, line 10");
	smeth->hotness_count_ = dmeth->hotness_count_;
	LOGD("In replace_11_0, line 11");

	smeth->ptr_sized_fields_.dex_cache_resolved_methods_ =
			dmeth->ptr_sized_fields_.dex_cache_resolved_methods_;
	LOGD("In replace_11_0, line 12");
	smeth->ptr_sized_fields_.dex_cache_resolved_types_ =
			dmeth->ptr_sized_fields_.dex_cache_resolved_types_;
	LOGD("In replace_11_0, line 13");

	smeth->ptr_sized_fields_.entry_point_from_jni_ =
			dmeth->ptr_sized_fields_.entry_point_from_jni_;
	LOGD("In replace_11_0, line 14");
	smeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_ =
			dmeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_;

	LOGD("replace_11_0: %d , %d",
			smeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_,
			dmeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_);

}

void install_11_0(JNIEnv* env, jobject src, jobject dest){
	LOGD("Enter install_11_0, begin to install method");
	art::mirror::ArtMethod* smeth =
			(art::mirror::ArtMethod*) env->FromReflectedMethod(src);

	LOGD("In install_11_0, source method parse end");
	art::mirror::ArtMethod* dmeth =
			(art::mirror::ArtMethod*) env->FromReflectedMethod(dest);

	LOGD("In install_11_0, destination method parse end");

	change(reinterpret_cast<art::mirror::Class*>(dmeth->declaring_class_)->clinit_thread_id_,
			reinterpret_cast<art::mirror::Class*>(smeth->declaring_class_)->clinit_thread_id_);
	LOGD("In install_11_0, install clinit_thread_id_ end");

	change(reinterpret_cast<art::mirror::Class*>(dmeth->declaring_class_)->status_,
		   reinterpret_cast<art::mirror::Class*>(smeth->declaring_class_)->status_);
	reinterpret_cast<art::mirror::Class*>(dmeth->declaring_class_)->status_ =
			static_cast<art::mirror::Class::Status>(
					reinterpret_cast<art::mirror::Class*>(dmeth->declaring_class_)->status_ - 1);
	LOGD("In install_11_0, install status_ end");
	
	change(reinterpret_cast<art::mirror::Class*>(dmeth->declaring_class_)->super_class_,
		   reinterpret_cast<art::mirror::Class*>(smeth->declaring_class_)->super_class_);
	reinterpret_cast<art::mirror::Class*>(dmeth->declaring_class_)->super_class_ = 0;
	LOGD("In install_11_0, install super_class_ end");
	
	change(smeth->declaring_class_ , dmeth->declaring_class_);
	LOGD("In install_11_0, install declaring_class_ end");
	
	change(smeth->access_flags_, dmeth->access_flags_);
	smeth->access_flags_ = smeth->access_flags_  | 0x0001;
	LOGD("In install_11_0, install access_flags_ end");
	
	change(smeth->dex_code_item_offset_, dmeth->dex_code_item_offset_);
	LOGD("In install_11_0, install dex_code_item_offset_ end");

	change(smeth->dex_method_index_, dmeth->dex_method_index_);
	LOGD("In install_11_0, install dex_method_index_ end");
	
	change(smeth->method_index_, dmeth->method_index_);
	LOGD("In install_11_0, install method_index_ end");
	
	change(smeth->hotness_count_, dmeth->hotness_count_);
	LOGD("In install_11_0, install hotness_count_ end");

	change(smeth->ptr_sized_fields_.dex_cache_resolved_methods_,
			dmeth->ptr_sized_fields_.dex_cache_resolved_methods_);
	LOGD("In install_11_0, install dex_cache_resolved_methods end");

	change(smeth->ptr_sized_fields_.dex_cache_resolved_types_,
			dmeth->ptr_sized_fields_.dex_cache_resolved_types_);
	LOGD("In install_11_0, install dex_cache_resolved_types_ end");

	change(smeth->ptr_sized_fields_.entry_point_from_jni_,
			dmeth->ptr_sized_fields_.entry_point_from_jni_);
	LOGD("In install_11_0, install entry_point_from_jni_ end");

	change(smeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_,
			dmeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_);
	LOGD("In install_11_0, install entry_point_from_quick_compiled_code_ end");

	LOGD("install_11_0: %d , %d",
		 smeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_,
		 dmeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_);
}

void uninstall_11_0(JNIEnv* env, jobject src, jobject dest){
	LOGD("Enter uninstall_11_0, begin to uninstall method");
	art::mirror::ArtMethod* smeth =
			(art::mirror::ArtMethod*) env->FromReflectedMethod(src);
	LOGD("In uninstall_11_0, source method parse end");

	art::mirror::ArtMethod* dmeth =
			(art::mirror::ArtMethod*) env->FromReflectedMethod(dest);
	LOGD("In uninstall_11_0, destination method parse end");

	change(reinterpret_cast<art::mirror::Class*>(dmeth->declaring_class_)->clinit_thread_id_,
		   reinterpret_cast<art::mirror::Class*>(smeth->declaring_class_)->clinit_thread_id_);
	LOGD("In uninstall_11_0, uninstall clinit_thread_id_ end");

	change(reinterpret_cast<art::mirror::Class*>(dmeth->declaring_class_)->status_,
		   reinterpret_cast<art::mirror::Class*>(smeth->declaring_class_)->status_);
	LOGD("In uninstall_11_0, uninstall status_ end");
	
	change(reinterpret_cast<art::mirror::Class*>(dmeth->declaring_class_)->super_class_,
		   reinterpret_cast<art::mirror::Class*>(smeth->declaring_class_)->super_class_);
	LOGD("In uninstall_11_0, uninstall super_class_ end");
	
	change(smeth->declaring_class_ , dmeth->declaring_class_);
	LOGD("In uninstall_11_0, uninstall declaring_class_ end");
	
	change(smeth->access_flags_, dmeth->access_flags_);
	LOGD("In uninstall_11_0, uninstall access_flags_ end");
	
	change(smeth->dex_code_item_offset_, dmeth->dex_code_item_offset_);
	LOGD("In uninstall_11_0, uninstall dex_code_item_offset_ end");
	
	change(smeth->dex_method_index_, dmeth->dex_method_index_);
	LOGD("In uninstall_11_0, uninstall dex_method_index_ end");
	
	change(smeth->method_index_, dmeth->method_index_);
	LOGD("In uninstall_11_0, uninstall method_index_ end");
	
	change(smeth->hotness_count_, dmeth->hotness_count_);
	LOGD("In uninstall_11_0, uninstall hotness_count_ end");
	
	change(smeth->ptr_sized_fields_.dex_cache_resolved_methods_,
		   dmeth->ptr_sized_fields_.dex_cache_resolved_methods_);
	LOGD("In uninstall_11_0, uninstall dex_cache_resolved_methods end");
	
	change(smeth->ptr_sized_fields_.dex_cache_resolved_types_,
		   dmeth->ptr_sized_fields_.dex_cache_resolved_types_);
	LOGD("In uninstall_11_0, uninstall dex_cache_resolved_types_ end");

	change(smeth->ptr_sized_fields_.entry_point_from_jni_,
		   dmeth->ptr_sized_fields_.entry_point_from_jni_);
	LOGD("In uninstall_11_0, uninstall entry_point_from_jni_ end");
	change(smeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_,
		   dmeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_);

	LOGD("In uninstall_11_0, uninstall entry_point_from_quick_compiled_code_ end");

	LOGD("install_11_0: %d , %d",
		 smeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_,
		 dmeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_);
}

void setFieldFlag_11_0(JNIEnv* env, jobject field) {
	art::mirror::ArtField* artField =
			(art::mirror::ArtField*) env->FromReflectedField(field);
	artField->access_flags_ = artField->access_flags_ & (~0x0002) | 0x0001;
	LOGD("setFieldFlag_11_0: %d ", artField->access_flags_);
}

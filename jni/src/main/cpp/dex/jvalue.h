//
// Created by zhao on 12/7/22.
//

#ifndef JNI_TEST_JVALUE_H
#define JNI_TEST_JVALUE_H

#include <stdint.h>

namespace art {
    namespace mirror{
        class Object;
    }

    union JValue{
          uint8_t z;
          int8_t b;
          uint16_t c;
          int16_t s;
          int32_t i;
          int64_t j;
          float f;
          double d;
          mirror::Object* l;
    };
}


#endif //JNI_TEST_JVALUE_H

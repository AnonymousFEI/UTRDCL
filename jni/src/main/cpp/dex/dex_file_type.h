//
// Created by zhao on 3/27/23.
//

#ifndef HOTFIX_DEMO_DEX_FILE_TYPE_H
#define HOTFIX_DEMO_DEX_FILE_TYPE_H

#include <limits>
#include <ostream>




namespace art {
    namespace dex {

        class StringIndex {
        public:
            uint32_t index_;

            constexpr StringIndex() : index_(std::numeric_limits<decltype(index_)>::max()) {}
            explicit constexpr StringIndex(uint32_t idx) : index_(idx) {}

            bool IsValid() const {
                return index_ != std::numeric_limits<decltype(index_)>::max();
            }
            static StringIndex Invalid() {
                return StringIndex(std::numeric_limits<decltype(index_)>::max());
            }

            bool operator==(const StringIndex& other) const {
                return index_ == other.index_;
            }
            bool operator!=(const StringIndex& other) const {
                return index_ != other.index_;
            }
            bool operator<(const StringIndex& other) const {
                return index_ < other.index_;
            }
            bool operator<=(const StringIndex& other) const {
                return index_ <= other.index_;
            }
            bool operator>(const StringIndex& other) const {
                return index_ > other.index_;
            }
            bool operator>=(const StringIndex& other) const {
                return index_ >= other.index_;
            }
        };
        std::ostream& operator<<(std::ostream& os, const StringIndex& index);

        class TypeIndex {
        public:
            uint16_t index_;

            constexpr TypeIndex() : index_(std::numeric_limits<decltype(index_)>::max()) {}
            explicit constexpr TypeIndex(uint16_t idx) : index_(idx) {}

            bool IsValid() const {
                return index_ != std::numeric_limits<decltype(index_)>::max();
            }
            static TypeIndex Invalid() {
                return TypeIndex(std::numeric_limits<decltype(index_)>::max());
            }

            bool operator==(const TypeIndex& other) const {
                return index_ == other.index_;
            }
            bool operator!=(const TypeIndex& other) const {
                return index_ != other.index_;
            }
            bool operator<(const TypeIndex& other) const {
                return index_ < other.index_;
            }
            bool operator<=(const TypeIndex& other) const {
                return index_ <= other.index_;
            }
            bool operator>(const TypeIndex& other) const {
                return index_ > other.index_;
            }
            bool operator>=(const TypeIndex& other) const {
                return index_ >= other.index_;
            }
        };
        std::ostream& operator<<(std::ostream& os, const TypeIndex& index);

    }  // namespace dex
}  // namespace art


namespace std {

    template<> struct hash<art::dex::StringIndex> {
        size_t operator()(const art::dex::StringIndex& index) const {
            return hash<uint32_t>()(index.index_);
        }
    };

    template<> struct hash<art::dex::TypeIndex> {
        size_t operator()(const art::dex::TypeIndex& index) const {
            return hash<uint16_t>()(index.index_);
        }
    };

}  // namespace std


#endif //HOTFIX_DEMO_DEX_FILE_TYPE_H

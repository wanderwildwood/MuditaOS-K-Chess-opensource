LOCAL_PATH := $(call my-dir)

SF_SRC_FILES := \
	benchmark.cpp bitbase.cpp bitboard.cpp endgame.cpp evaluate.cpp main.cpp \
    material.cpp misc.cpp movegen.cpp movepick.cpp pawns.cpp position.cpp psqt.cpp \
    search.cpp thread.cpp timeman.cpp tt.cpp uci.cpp ucioption.cpp tune.cpp syzygy/tbprobe.cpp \
    nnue/evaluate_nnue.cpp nnue/features/half_ka_v2.cpp \
    partner.cpp parser.cpp piece.cpp variant.cpp xboard.cpp \
    nnue/features/half_ka_v2_variants.cpp

MY_ARCH_DEF :=
ifeq ($(TARGET_ARCH_ABI),arm64-v8a)
    MY_ARCH_DEF += -DIS_64BIT -DUSE_POPCNT -DUSE_NEON
endif

include $(CLEAR_VARS)
LOCAL_MODULE    := stockfish
LOCAL_SRC_FILES := $(SF_SRC_FILES)
LOCAL_CFLAGS    := -std=c++17 -O3 -fno-exceptions -DNNUE_EMBEDDING_OFF -DUSE_PTHREADS \
                   -fPIE $(MY_ARCH_DEF) -s -flto=thin -frtti
LOCAL_LDFLAGS	+= -fPIE -s -flto=thin
include $(BUILD_EXECUTABLE)

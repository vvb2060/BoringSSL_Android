APP_CFLAGS         := -Wall -Werror -Wno-unused-parameter -fvisibility=hidden -DOPENSSL_SMALL
APP_CFLAGS         += -Wno-builtin-macro-redefined -D__FILE__=__FILE_NAME__
APP_LDFLAGS        := -Wl,--icf=all
APP_CONLYFLAGS     := -std=c17
APP_CPPFLAGS       := -std=c++23
APP_STL            := c++_static
APP_SHORT_COMMANDS := true
APP_SUPPORT_FLEXIBLE_PAGE_SIZES := true

ifeq ($(enableLTO),1)
APP_CFLAGS         += -flto
APP_LDFLAGS        += -flto
endif

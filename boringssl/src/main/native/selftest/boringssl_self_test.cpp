/*
 * Copyright (C) 2019 The Android Open Source Project
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

#include <openssl/crypto.h>

// This program is run early during boot and if it exits with a
// failure status then the device will reboot to the bootloader.
// See init.rc for details.
// It may also exit before reaching main() if BoringSSL fast tests fail.
int main(int, char**) {
    if (!FIPS_mode()) {
        return 1;  // Fail: BoringSSL not built in FIPS mode.
    }
    if (!BORINGSSL_self_test()) {
        return 1;  // Fail: One or more self tests failed.
    }
    return 0;      // Success
}

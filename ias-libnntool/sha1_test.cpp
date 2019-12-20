/*!
    \file sha1_test.cpp
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2019-11-13

    Copyright (C) 2016 - 2019 zafaco GmbH

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3 
    as published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

#include "catch.hpp"
#include "sha1.hpp"

TEST_CASE("SHA1") {
    SECTION("SHA1 Test vectors"){
    // https://www.di-mgt.com.au/sha_testvectors.html
    // https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/SHA1.pdf
    // https://en.wikipedia.org/wiki/SHA-1

        SHA1 checksum;
        checksum.update("abc");
        CHECK(checksum.final() == "a9993e364706816aba3e25717850c26c9cd0d89d");

        checksum.update("abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq");
        CHECK(checksum.final()== "84983e441c3bd26ebaae4aa1f95129e5e54670f1");

        checksum.update("abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu");
        CHECK(checksum.final()== "a49b2446a02c645bf419f995b67091253a04a259");


        for (int i = 0; i < 1000000/200; ++i)
        {
            checksum.update("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                            );
        }
        CHECK(checksum.final()== "34aa973cd4c4daa4f61eeb2bdbad27316534016f");

        checksum.update("The quick brown fox jumps over the lazy dog");
        CHECK(checksum.final() == "2fd4e1c67a2d28fced849ee1bb76e7391b93eb12");

        checksum.update("The quick brown fox jumps over the lazy cog");
        CHECK(checksum.final() == "de9f2c7fd25e1b3afad3e85a0bd17d9b100db4b3");
    }
}


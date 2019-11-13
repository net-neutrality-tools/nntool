/*!
    \file json11_test.cpp
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
#include "json11.hpp"

#include <cassert>
#include <string>
#include <cstdio>
#include <cstring>
#include <iostream>
#include <sstream>
#include "json11.hpp"
#include <list>
#include <set>
#include <unordered_map>
#include <algorithm>
#include <type_traits>


using namespace json11;
using std::string;


TEST_CASE("JSON11") {
    const string simple_test =
        R"({"k1":"v1", "k2":42, "k3":["a",123,true,false,null]})";

    string err;
    const auto json = Json::parse(simple_test, err);

    std::cout << "k1: " << json["k1"].string_value() << "\n";
    std::cout << "k3: " << json["k3"].dump() << "\n";

    for (auto &k : json["k3"].array_items()) {
        std::cout << "    - " << k.dump() << "\n";
    }

    string comment_test = R"({
      // comment /* with nested comment */
      "a": 1,
      // comment
      // continued
      "b": "text",
      /* multi
         line
         comment
        // line-comment-inside-multiline-comment
      */
      // and single-line comment
      // and single-line comment /* multiline inside single line */
      "c": [1, 2, 3]
      // and single-line comment at end of object
    })";

    string err_comment;
    auto json_comment = Json::parse(
      comment_test, err_comment, JsonParse::COMMENTS);
    CHECK(!json_comment.is_null());
    CHECK(err_comment.empty());

    comment_test = "{\"a\": 1}//trailing line comment";
    json_comment = Json::parse(
      comment_test, err_comment, JsonParse::COMMENTS);
    CHECK(!json_comment.is_null());
    CHECK(err_comment.empty());

    comment_test = "{\"a\": 1}/*trailing multi-line comment*/";
    json_comment = Json::parse(
      comment_test, err_comment, JsonParse::COMMENTS);
    CHECK(!json_comment.is_null());
    CHECK(err_comment.empty());

    string failing_comment_test = "{\n/* unterminated comment\n\"a\": 1,\n}";
    string err_failing_comment;
    auto json_failing_comment = Json::parse(
      failing_comment_test, err_failing_comment, JsonParse::COMMENTS);
    CHECK(json_failing_comment.is_null());
    CHECK(!err_failing_comment.empty());

    failing_comment_test = "{\n/* unterminated trailing comment }";
    json_failing_comment = Json::parse(
      failing_comment_test, err_failing_comment, JsonParse::COMMENTS);
    CHECK(json_failing_comment.is_null());
    CHECK(!err_failing_comment.empty());

    failing_comment_test = "{\n/ / bad comment }";
    json_failing_comment = Json::parse(
      failing_comment_test, err_failing_comment, JsonParse::COMMENTS);
    CHECK(json_failing_comment.is_null());
    CHECK(!err_failing_comment.empty());

    failing_comment_test = "{// bad comment }";
    json_failing_comment = Json::parse(
      failing_comment_test, err_failing_comment, JsonParse::COMMENTS);
    CHECK(json_failing_comment.is_null());
    CHECK(!err_failing_comment.empty());

    failing_comment_test = "{\n\"a\": 1\n}/";
    json_failing_comment = Json::parse(
      failing_comment_test, err_failing_comment, JsonParse::COMMENTS);
    CHECK(json_failing_comment.is_null());
    CHECK(!err_failing_comment.empty());

    failing_comment_test = "{/* bad\ncomment *}";
    json_failing_comment = Json::parse(
      failing_comment_test, err_failing_comment, JsonParse::COMMENTS);
    CHECK(json_failing_comment.is_null());
    CHECK(!err_failing_comment.empty());

    std::list<int> l1 { 1, 2, 3 };
    std::vector<int> l2 { 1, 2, 3 };
    std::set<int> l3 { 1, 2, 3 };
    CHECK(Json(l1) == Json(l2));
    CHECK(Json(l2) == Json(l3));

    std::map<string, string> m1 { { "k1", "v1" }, { "k2", "v2" } };
    std::unordered_map<string, string> m2 { { "k1", "v1" }, { "k2", "v2" } };
    CHECK(Json(m1) == Json(m2));

    // Json literals
    const Json obj = Json::object({
        { "k1", "v1" },
        { "k2", 42.0 },
        { "k3", Json::array({ "a", 123.0, true, false, nullptr }) },
    });

    std::cout << "obj: " << obj.dump() << "\n";
    CHECK(obj.dump() == "{\"k1\": \"v1\", \"k2\": 42, \"k3\": [\"a\", 123, true, false, null]}");

    CHECK(Json("a").number_value() == 0);
    CHECK(Json("a").string_value() == "a");
    CHECK(Json().number_value() == 0);

    CHECK(obj == json);
    CHECK(Json(42) == Json(42.0));
    CHECK(Json(42) != Json(42.1));

    const string unicode_escape_test =
        R"([ "blah\ud83d\udca9blah\ud83dblah\udca9blah\u0000blah\u1234" ])";

    const char utf8[] = "blah" "\xf0\x9f\x92\xa9" "blah" "\xed\xa0\xbd" "blah"
                        "\xed\xb2\xa9" "blah" "\0" "blah" "\xe1\x88\xb4";

    Json uni = Json::parse(unicode_escape_test, err);
    CHECK(uni[0].string_value().size() == (sizeof utf8) - 1);
    CHECK(std::memcmp(uni[0].string_value().data(), utf8, sizeof utf8) == 0);


    {
        const std::string good_json = R"( {"k1" : "v1"})";
        const std::string bad_json1 = good_json + " {";
        const std::string bad_json2 = good_json + R"({"k2":"v2", "k3":[)";
        struct TestMultiParse {
            std::string input;
            std::string::size_type expect_parser_stop_pos;
            size_t expect_not_empty_elms_count;
            Json expect_parse_res;
        } tests[] = {
            {" {", 0, 0, {}},
            {good_json, good_json.size(), 1, Json(std::map<string, string>{ { "k1", "v1" } })},
            {bad_json1, good_json.size() + 1, 1, Json(std::map<string, string>{ { "k1", "v1" } })},
            {bad_json2, good_json.size(), 1, Json(std::map<string, string>{ { "k1", "v1" } })},
            {"{}", 2, 1, Json::object{}},
        };
        for (const auto &tst : tests) {
            std::string::size_type parser_stop_pos;
            std::string err;
            auto res = Json::parse_multi(tst.input, parser_stop_pos, err);
            CHECK(parser_stop_pos == tst.expect_parser_stop_pos);
            CHECK(
                (size_t)std::count_if(res.begin(), res.end(),
                                      [](const Json& j) { return !j.is_null(); })
                == tst.expect_not_empty_elms_count);
            if (!res.empty()) {
                CHECK(tst.expect_parse_res == res[0]);
            }
        }
    }

    Json my_json = Json::object {
        { "key1", "value1" },
        { "key2", false },
        { "key3", Json::array { 1, 2, 3 } },
    };
    std::string json_obj_str = my_json.dump();
    std::cout << "json_obj_str: " << json_obj_str << "\n";
    CHECK(json_obj_str == "{\"key1\": \"value1\", \"key2\": false, \"key3\": [1, 2, 3]}");

    class Point {
    public:
        int x;
        int y;
        Point (int x, int y) : x(x), y(y) {}
        Json to_json() const { return Json::array { x, y }; }
    };

    std::vector<Point> points = { { 1, 2 }, { 10, 20 }, { 100, 200 } };
    std::string points_json = Json(points).dump();
    std::cout << "points_json: " << points_json << "\n";
    CHECK(points_json == "[[1, 2], [10, 20], [100, 200]]");
}
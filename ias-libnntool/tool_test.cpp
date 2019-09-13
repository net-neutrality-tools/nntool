#include "catch.hpp"
#include "tool.h"

TEST_CASE("CTool")
{
    SECTION("string functions")
    {
        SECTION("test toLower")
        {
            string upper = "ABC";

            CTool::toLower(upper);

            CHECK(upper == "abc");
        }
        SECTION("test toUpper")
        {
            string lower = "abc";

            CTool::toUpper(lower);

            CHECK(lower == "ABC");
        }
    }

    SECTION("string to numeric type")
    {
        int integer;
        unsigned int u_integer;
        long long long_int;
        unsigned long long u_long_int;
        float floating;
        SECTION("test to Int")
        {
            integer = CTool::toInt("42");
            CHECK(integer == 42);
            integer = CTool::toInt("-42");
            CHECK(integer == -42);
        }
        SECTION("test to unsigned Int")
        {
            u_integer = CTool::toUInt("4294967295");
            CHECK(u_integer == 4294967295);
        }
        SECTION("test to unsigned long long")
        {
            u_long_int = CTool::toULL("18446744073709551614");
            CHECK(u_long_int == 18446744073709551614);
        }
        SECTION("test to long long")
        {
            long_int = CTool::toLL("9223372036854775807");
            CHECK(long_int == 9223372036854775807);
            long_int = CTool::toLL("-9223372036854775800");
            CHECK(long_int == -9223372036854775800);
        }
        SECTION("test to float")
        {
            floating = CTool::toFloat("42.42");
            CHECK(floating - 42.42 < 0.0000001f);
        }
    }
    SECTION("IP address handling")
    {
        SECTION("get IP as string from sockaddr")
        {
            struct sockaddr saddr;
            struct sockaddr_in *sa_in = (struct sockaddr_in *)&saddr;
            sa_in->sin_family = AF_INET;
            inet_aton("10.1.1.1", &(sa_in->sin_addr));
            CHECK(CTool::get_ip_str(&saddr) == "10.1.1.1");

            struct sockaddr_in6 *sa_in6 = (struct sockaddr_in6 *)&saddr;
            sa_in6->sin6_family = AF_INET6;
            inet_pton(AF_INET6, "2001:db8:63b3:1::3490", &(sa_in6->sin6_addr));
            CHECK(CTool::get_ip_str(&saddr) == "2001:db8:63b3:1::3490");
        }

        SECTION("validate IPs")
        {
            string ipv4 = "127.0.0.1";
            string ipv6 = "2001:db8:63b3:1::3490";
            string ipv4_nv = "288.124.54.8";
            string ipv6_nv = "2001:xb8:63b3:1::3490";

            CHECK(CTool::validateIp(ipv4) == 4);
            CHECK(CTool::validateIp(ipv6) == 6);
            CHECK(CTool::validateIp(ipv4_nv) == 0);
            CHECK(CTool::validateIp(ipv6_nv) == 0);
        }

        SECTION("get IP from hostname")
        {
            string ip = CTool::getIpFromHostname("localhost");
            CHECK(ip == "::1");

            ip = CTool::getIpFromHostname("localhost", 4);
            CHECK(ip == "127.0.0.1");

            ip = CTool::getIpFromHostname("localhost", 6);
            CHECK(ip == "::1");
        }
        SECTION("get IPs from hostname"){
            addrinfo *addr_inf;
            struct sockaddr_in *in_addr;
            struct sockaddr_in6 * in6_addr;
            bool ip4Found=false;
            bool ip6Found=false;
            char buf[sizeof(struct in6_addr)];
            char aIP6[INET6_ADDRSTRLEN];

            addr_inf=CTool::getIpsFromHostname("localhost", false);

            for(;addr_inf != NULL; addr_inf = addr_inf->ai_next){
                if(addr_inf->ai_family == AF_INET){
                    in_addr = (struct sockaddr_in*)(addr_inf->ai_addr);
                    if(strcmp(inet_ntoa(in_addr->sin_addr),"127.0.0.1")){
                        ip4Found = true;
                        break;
                    }
                }

                else if (addr_inf->ai_family == AF_INET6){
                    in6_addr = (struct sockaddr_in6*)(addr_inf->ai_addr);
                    inet_ntop(AF_INET6,buf, aIP6, INET6_ADDRSTRLEN);
                    if(strcmp(aIP6 ,"::1")){
                        ip6Found = true;
                        break;;
                    }
                }
            }
           
            CHECK((ip4Found || ip6Found));
        }
    }
    SECTION("system meta informations")
    {
        SECTION("get hostname")
        {
            string hostname = CTool::getHostname();
            CHECK(hostname != "");
        }
        SECTION("get OS name")
        {
            string name = CTool::getSystemInfoOS();
            CHECK(((name == "Linux") || (name == "macOS") || (name == "Windows")));
        }
        SECTION("get OS version")
        {
            string version = CTool::getSystemInfoOSVersion();
            CHECK(version != "");
        }
    }
    SECTION("random data generation")
    {
        SECTION("generate random int")
        {
            int rand_num = CTool::randomData();
            int counter_seen = 0;
            for (int i = 0; i < 1024; i++)
            {
                if (rand_num == CTool::randomData())
                {
                    counter_seen++;
                }
            }
            CHECK(counter_seen < 2);
        }
        SECTION("generate random data")
        {
            char buf[1024];
            int rand_num = CTool::randomData();
            int counter_seen;
            CTool::randomData(buf, 1024);
            for (int i = 0; i < 1024; i++)
            {
                if (rand_num == buf[i])
                {
                    counter_seen++;
                }
            }
            CHECK(counter_seen < 10);
        }
    }
    SECTION("result calculations")
    {
        map<int, unsigned long long> mappig = {
            {1, 10},
            {2, 20},
            {3, 30}};
        unsigned long long result = CTool::calculateResultsMin(mappig);
        CHECK(result == 10);
        result = CTool::calculateResultsAvg(mappig);
        CHECK(result == 20);

        measurement_data *m_data = new measurement_data();
        m_data->results = map<int, unsigned long long> {
            {1, 10},
            {2, 20},
            {3, 30}};
        CTool::calculateResults(*m_data);
        CHECK(m_data->avg==20);
        CHECK(m_data->min==10);
        CHECK(m_data->max==30);
    }
    
}
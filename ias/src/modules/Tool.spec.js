describe('Tool', function () {
    var tool = new JSTool();

    describe('random data', function () {
        it('generates random data as string', function () {
            var data = tool.generateRandomData(10, true, false);

            expect(data.length).toBe(10);
            expect(data.includes('ö')).toBe(false);
        });
        it('generates random data as ArrayBuffer', function () {
            var arrayBuffer = tool.generateRandomData(100, false, true);

            expect(arrayBuffer.byteLength).toBe(100);
        });
        it('generates random data as Blob', function () {
            var blob = tool.generateRandomData(50, false, false);

            expect(blob.size).toBe(50);
        });
    });

    describe('ip adress type', function () {
        it('correctly detects ipv4', function () {
            expect(tool.getIPAddressType('192.168.1.100')).toBe('IPv4');
            expect(tool.getIPAddressType('0.0.0.0')).toBe('IPv4');
            expect(tool.getIPAddressType('255.255.255.255')).toBe('IPv4');
        });
        it('correctly detects ipv6', function () {
            expect(tool.getIPAddressType('::1')).toBe('IPv6');
            expect(tool.getIPAddressType('fe80::1')).toBe('IPv6');
            expect(tool.getIPAddressType('fe80::1')).toBe('IPv6');
            expect(tool.getIPAddressType('2001:0db8:0000:0000:0000:ff00:0042:8329')).toBe('IPv6');
        });
        it('correctly detects non-ip', function () {
            expect(tool.getIPAddressType('test_not_an_ip_address')).toBe(false);
            expect(tool.getIPAddressType('10.0.256.4')).toBe(false);
            expect(tool.getIPAddressType('10.0.-1.4')).toBe(false);
            expect(tool.getIPAddressType(':::1')).toBe(false);
            expect(tool.getIPAddressType('2g01::1')).toBe(false);
        });
    });

    describe('extend JS objects', function () {
        it('extends two objects with differently named porperties', function () {
            var obj1 = {
                p1: "string value1",
                p2: 42
            };
            var obj2 = {
                p3: "string value2",
                p4: "42"
            };
            var exp_obj = {
                p1: "string value1",
                p2: 42,
                p3: "string value2",
                p4: "42"
            };

            expect(tool.extend(obj1, obj2)).toEqual(exp_obj);
        });
        it('extends two objects with properties of the same name', function () {
            var obj1 = {
                p1: "old string value",
                p2: 42
            };
            var obj2 = {
                p1: "new string value",
                p4: "42"
            };
            var exp_obj = {
                p1: "new string value",
                p2: 42,
                p4: "42"
            };

            expect(tool.extend(obj1, obj2)).toEqual(exp_obj);
        });
        it('extends two objects, where one of them is empty', function () {
            var obj1 = {
                p1: "string value",
                p2: 42
            };
            var obj2 = {};

            var exp_obj = {
                p1: "string value",
                p2: 42
            };

            expect(tool.extend(obj1, obj2)).toEqual(exp_obj);
        });
    });
    describe('JS object is empty', function () {
        it('detects an empty object as empty', function () {
            var obj = {};

            expect(tool.isEmpty(obj)).toBe(true);
        });
        it('detects an object with one or more properties as not empty', function () {
            var obj1 = {
                p1: "string",
            };
            var obj2 = {
                p1: "string",
                p2: 42
            };
            var obj3 = {
                p1: null
            };

            expect(tool.isEmpty(obj1)).toBe(false);
            expect(tool.isEmpty(obj2)).toBe(false);
            expect(tool.isEmpty(obj3)).toBe(false);
        });
    });

    describe('get WebSocket close reason', function () {
        var event_codes = [1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010, 1011, 1015];
        var event = {
            code: null
        };

        it('returns "Unknown reason" for code null', function () {
            event.code = null;
            expect(tool.webSocketCloseReasons(event)).toBe('Unknown reason');
        });

        it('returns "Unknown reason" for undefiend code value', function () {
            event.code = 815;
            expect(tool.webSocketCloseReasons(event)).toBe('Unknown reason');
        });

        it('does not return "Unknown reason" for defined codes', function () {
            for (const code of event_codes) {
                event.code = code;
                expect(tool.webSocketCloseReasons(event)).not.toBe('Unknown reason');
            };
        });
    });

    describe('cookie handling', function () {
        beforeEach(function () {
            var cookies = document.cookie.split(";");

            for (var i = 0; i < cookies.length; i++) {
                var cookie = cookies[i];
                var eqPos = cookie.indexOf("=");
                var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
                document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
            }
        })

        xit('sets cookie with expire date', function () {
            var thirtyDaysBeforeNow = new Date();
            thirtyDaysBeforeNow.setTime(thirtyDaysBeforeNow.getTime() - (30 * 24 * 60 * 60 * 1000));
            jasmine.clock().mockDate(thirtyDaysBeforeNow);
            tool.setCookie('31_days', 'cookie', 31);
            tool.setCookie('29_days', 'cookie', 29)

            expect(document.cookie).toBe('31_days=cookie');

            jasmine.clock().uninstall();
            tool.setCookie('31_days', 'cookie', 31);
            tool.setCookie('29_days', 'cookie', 29)

            expect(document.cookie).toBe('31_days=cookie; 29_days=cookie');
        });

        it('gets cookie values by key', function () {
            expect(tool.getCookie('not_here')).toBe(false);

            document.cookie = 'find_me=here I am';
            expect(tool.getCookie('find_me')).toBe('here I am');

            document.cookie = 'dont_find_me=here I am; expires=Thu, 01 Jan 1970 00:00:00 GMT';
            expect(tool.getCookie('dont_find_me')).toBe(false);
        });

        it('deletes cookies by key', function () {
            document.cookie = 'delete_me=value';
            tool.deleteCookie('delete_me');

            expect(document.cookie).toBe('');
        });

    });


    describe('date formatting', function () {
        it('formats the date', function () {
            var fixedDate = new Date(1991, 7, 1, 12);
            jasmine.clock().mockDate(fixedDate);
            expect(tool.getFormattedDate()).toBe('1991-08-01 12:00:00');
        });
    });

    describe('browser report', function () {
        report = tool.getBrowserReport();

        it('detects a commonly used testing user agent', function () {
            expect(['Chrome', 'Firefox', 'Edge', 'Safari']).toContain(report.browser.name);
        });

        it('detects a desktop OS (unit tests take place on desktop system)', function () {
            expect(['Windows', 'OS X', 'Linux']).toContain(report.os.name);
        });

    })
    
    describe('detect device KPIs', function () {
        it('uses browser.report on desktop and web', function () {
            spyOn(tool, 'getBrowserReport').and.callThrough();
            tool.getDeviceKPIs('web');

            expect(tool.getBrowserReport).toHaveBeenCalled();
        });

        it('uses NativeScript Impl. on mobile platforms', function () {
            spyOn(tool, 'getBrowserReport').and.callThrough();
            tool.getDeviceKPIs('mobile');

            expect(tool.getBrowserReport).not.toHaveBeenCalled();
        });

    })

});

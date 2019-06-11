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
});

describe('Ias', function () {
    
    describe('class initialization', function(){
        it('has expected methods', function(){
            var ias = new Ias();
            expect(typeof ias.measurementStart).toBe('function');
            expect(typeof ias.controlCallback).toBe('function');
            expect(typeof ias.measurementStop).toBe('function');
            expect(typeof ias.setDeviceKPIs).toBe('function');
            expect(typeof ias.controlCallback).toBe('function');

        });

    });


});
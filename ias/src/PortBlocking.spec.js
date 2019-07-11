describe('PortBlocking', function () {
    
    describe('class initialization', function(){
        it('has expected methods', function(){
            var controle = new PortBlocking();
            expect(typeof controle.measurementStart).toBe('function');

        });

    });
    
});
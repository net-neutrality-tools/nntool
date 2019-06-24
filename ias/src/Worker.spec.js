describe('WSWorker', function () {
    describe('class initialization', function () {
        var worker = new WSWorker();

        it('sets default properties', function(){
            expect(worker.wsControl).toBe('undefined');
        });
        it('implements worker "onmeassage" method', function(){
            expect(typeof worker.onmessage).toBe('function');
        });

        it('implements global "onmessage" method', function() {
            expect(typeof onmessage).toBe('function');
        });
    });

    describe('command handling of WSControl', function(){
        var worker = new WSWorker();
        it('sets single thread config when calling "onmessage" of WSWorker', function(){
            spyOn(window, 'onmessage');
            worker.onmessage('{}')
            expect(singleThread).toBe(true);
            expect(window.onmessage).toHaveBeenCalled();
        });
        it('initializes WebSocket connection when receiving "connect" cmd', function(){
            spyOn(window, 'connect');
            var event  = '{"cmd": "connect", "wsTestCase":"upload"}';
            worker.onmessage(event);
            expect(window.connect).toHaveBeenCalled();

        });
        it('resets counters when receiving "resetCounter" cmd', function(){
            var event  = '{"cmd": "resetCounter", "wsTestCase":"upload"}';
            worker.onmessage(event);
            expect(wsData).toBe(0);
            expect(wsFrames).toBe(0);

        });
        it('resets counters and calls controler when receiving "report" cmd', function(){
            var event  = '{"cmd": "report", "wsTestCase":"upload"}';
            spyOn(window, 'reportToControl').and.callThrough();
            worker.onmessage(event);
            expect(wsData).toBe(0);
            expect(wsFrames).toBe(0);
            expect(window.reportToControl).toHaveBeenCalled();
        });
        it('closes WebSocket connections when receiving "close" cmd', function(){
            var event  = '{"cmd": "close", "wsTestCase":"upload"}';
            webSocket = jasmine.createSpyObj('webSocket', ['close'])
            webSocket.readyState = 3;
            console.log(webSocket);
            spyOn(window, 'setTimeout');
            worker.onmessage(event);
            expect(webSocket.close).toHaveBeenCalled();
        });
    });
});
describe('WSWorkerSingleThread', function () {
    describe('class initialization', function () {
        var workerST = new WSWorkerSingleThread();

        it('sets default properties', function(){
            expect(workerST.wsControl).toBe('undefined');
        });
        it('implements worker "onmeassageSM" method', function(){
            expect(typeof workerST.onmessageSM).toBe('function');
        });

        it('implements global "onmessage" method', function() {
            expect(typeof onmessage).toBe('function');
        });

        it('sets global default properties', function(){
            expect(wsStateConnecting).toBe(0);
            expect(wsStateOpen).toBe(1);
            expect(wsStateClosing).toBe(2);
            expect(wsStateClosed).toBe(3);
            expect(wsConnected).toBe(false);
            expect(wsRttValues).toBeDefined();
        });
    });

    describe('command handling of WSWorkerSingleThread', function(){
        var workerST = new WSWorkerSingleThread();
        



        it('calls send data when receiving "startUpload" cmd', function(){
            var event  = '{"cmd": "uploadStart", "wsTestCase":"upload"}';
            spyOn(window, 'setInterval');
            webSocket = jasmine.createSpyObj('webSocket', ['send'])
            workerST.onmessageSM(event);
            expect(window.setInterval).toHaveBeenCalled();
        });
    });
});
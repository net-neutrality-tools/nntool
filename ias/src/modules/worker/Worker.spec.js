describe('WSWorker', function () {
    describe('class initialization', function () {
        var worker = new WSWorker();

        it('sets default properties', function(){
            expect(worker.wsControl).toBe('undefined');
        });
        it('implements worker "onmeassageWorker" method', function(){
            expect(typeof worker.onmessageWorker).toBe('function');
        });



    });

    describe('command handling of WSWorker', function(){
        var worker = new WSWorker();
        



        it('calls send data when receiving "startUpload" cmd', function(){
            var event  = '{"cmd": "uploadStart", "wsTestCase":"upload"}';
            spyOn(window, 'setInterval');
            webSocket = jasmine.createSpyObj('webSocket', ['send'])
            worker.onmessageWorker(event);
            expect(window.setInterval).toHaveBeenCalled();
        });
    });
});
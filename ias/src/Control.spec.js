var wsControl;

describe('Control', function () {
    describe('class initialization', function(){
        it('has expected methods', function(){
            var controle = new WSControl();
            expect(typeof controle.measurementStart).toBe('function');
            expect(typeof controle.measurementStop).toBe('function');

        });
    });
    describe('measurment controlling', function(){
        xit('starts measurement', function(){

            var config= '{"platform":"web","wsTargets":["peer-ias-de-01"],"wsTargetsRtt":["peer-ias-de-01"],"wsTLD":"net-neutrality.tools","wsTargetPort":"80","wsWss":0,"wsAuthToken":"placeholderToken","wsAuthTimestamp":"placeholderTimestamp","wsWorkerPath":"Worker.js","performRouteToClientLookup":true,"routeToClientTargetPort":8080,"download":{"streams":4,"frameSize":32768,"classes":[{"default":false,"streams":4,"frameSize":2048,"bounds":{"lower":0.01,"upper":1.05}},{"default":true,"streams":4,"frameSize":32768,"bounds":{"lower":0.95,"upper":525}},{"default":false,"streams":4,"frameSize":524288,"bounds":{"lower":475,"upper":1050}},{"default":false,"streams":8,"frameSize":524288,"bounds":{"lower":950,"upper":9000}}],"performMeasurement":true},"upload":{"streams":4,"frameSize":65535,"framesPerCall":1,"classes":[{"default":false,"streams":4,"frameSize":2048,"bounds":{"lower":0.01,"upper":1.05},"framesPerCall":1},{"default":true,"streams":4,"frameSize":65535,"bounds":{"lower":0.95,"upper":525},"framesPerCall":1},{"default":false,"streams":4,"frameSize":65535,"bounds":{"lower":475,"upper":1050},"framesPerCall":4},{"default":false,"streams":8,"frameSize":65535,"bounds":{"lower":950,"upper":9000},"framesPerCall":20}],"performMeasurement":false},"rtt":{"performMeasurement":false},"testCase":"download"}';           

            wsControl = new WSControl();
            wsControl.callback = null;
            spyOn(wsControl,'callback');
            wsControl.measurementStart(config);
            expect(wsControl.callback).toHaveBeenCalled();
        });
    });
    
});
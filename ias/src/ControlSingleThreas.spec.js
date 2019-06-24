describe('WSControlSingleThread', function () {
    
    describe('class initialization', function(){
        it('has expected methods', function(){
            var controle = new WSControlSingleThread();
            expect(typeof controle.measurementStart).toBe('function');
            expect(typeof controle.measurementStop).toBe('function');
            expect(typeof controle.workerCallback).toBe('function');

        });

        it('sets internal default properties', function(){
            var controle = new WSControlSingleThread();
            expect(controle.callback).toBe('');
            expect(controle.wsMeasurement).toBe('');

        });

    });

    describe('worker communication', function(){
        it('handels cmd "info" callbacks from WSWorker', function(){
            var controle = new WSControlSingleThread();
            spyOn(console, 'log');
            controle.workerCallback('{"cmd": "info"}');
            expect(console.log).toHaveBeenCalled();

        });
        xit('handels cmd "open" callback from WSWorker and starts ', function(){
            var controle = new WSControlSingleThread();
            controle.wsWorkers = new Array(4)
            controle.wsWorkersStatus = new Array(4);
            controle.wsWorkersStatus[1]=1;
            console.log(controle);
            spyOn(controle, 'measurementStart');
            spyOn(window,'clearTimeout')
            controle.wsTestCase = 'upload';
            controle.workerCallback('{"cmd": "open","wsID": 1, "wsState":2}');
            expect(window.clearTimeout).toHaveBeenCalled();
        });

        xit('handels cmd "report" callback from WSWorker', function(){
            var controle = new WSControlSingleThread();
            controle.wsTestCase = 'rtt';
            spyOn(Math, 'round');
            controle.workerCallback('{"cmd": "report","wsRttValues":true, "wsState":2}');
            expect(Math.round).toHaveBeenCalled();

        });
    });
    
});
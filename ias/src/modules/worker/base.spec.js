
describe("Worker", function () {
    describe("Testcase download", function () {
        it("handels simulated websocket download measurement", function () {
            var jsonReport;
            var controlCBSpy = jasmine.createSpy('wsControl.workerCallback').and.callFake(function(msg){
                console.log(msg);
                jsonReport = JSON.parse(msg);
            });
            wsControl = {
                workerCallback: controlCBSpy
            }
            var realWS = WebSocket;

            var wsObj;
            var WSSpy = spyOn(window, "WebSocket").and.callFake(function (url, protocols) {
                wsObj = new realWS(url, protocols);
                
                return wsObj
                //setTimeout(wsFakeObj.onopen,200);
                //return wsFakeObj;

            });
            

            var worker = new WSWorker();

            spyOn(worker, "connectWorker").and.callThrough();
            //spyOn(wsObj, "onerror");

            worker.onmessageWorker(JSON.stringify(
                {
                    wsTestCase: "download",
                    cmd: "connect",
                    wsTarget: "localhost/where",
                    wsTargetRtt: "localhost/where",
                    wsTargetPort: 8080,
                    wsProtocol: "download",
                    wsFrameSize: 32768,
                    wsAuthToken: "null",
                    wsAuthTimestamp: "null2"
                }
            ));
            spyOn(wsObj, "onerror"); //Catches connection error to localhost
            spyOn(wsObj, "onclose"); //Catches connection close beacause of onerror
            expect(worker.connectWorker).toHaveBeenCalled();
            expect(WSSpy).toHaveBeenCalledWith('ws://localhost/where:8080', ['download', 'overload', 'null', 'null2', 32768]);
            
            wsObj.onopen(); //Simulating a successfully established connection
            wsObj.onmessage({ data: { size: 1500 } });
            expect(controlCBSpy).toHaveBeenCalled();
            worker.onmessageWorker(JSON.stringify(
                {
                    wsTestCase: "download",
                    cmd: "close",
                }
            ));
            expect(jsonReport.wsData).toBe(1500);


        })




    });
});
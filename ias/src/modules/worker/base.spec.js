
describe("Worker", function () {
    var realWS
    var jsonReport;
    var controlCBSpy;
    var wsObj;
    var WSSpy;
    var worker;
    var sendSpy;

    beforeEach(function () {
        //Create a mock for WsControl callback
        jasmine.clock().install();
        controlCBSpy = jasmine.createSpy('wsControl.workerCallback').and.callFake(function (msg) {
            //console.log(msg);
            jsonReport = JSON.parse(msg);
        });
        wsControl = {
            workerCallback: controlCBSpy
        }
        sendSpy = spyOn(WebSocket.prototype, "send").and.callFake(function (msg) {

        });

        //Create a mock for native WebSocket
        realWS = WebSocket;
        WSSpy = spyOn(window, "WebSocket").and.callFake(function (url, protocols) {
            wsObj = new realWS(url, protocols);
            return wsObj
        });




        //Instantiate a WSWorker object to test on
        worker = new WSWorker();
    })
    afterEach(function () {
        jasmine.clock().uninstall();
    })
    describe("Testcase download", function () {


        it("handles simulated websocket download measurement", function () {

            spyOn(worker, "connectWorker").and.callThrough();
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
            //Did we try to connect the socket?
            expect(worker.connectWorker).toHaveBeenCalled();
            //Was a WS object instantiated?
            expect(WSSpy).toHaveBeenCalledWith('ws://localhost/where:8080', ['download', 'overload', 'null', 'null2', 32768]);
            spyOn(wsObj, "onerror"); //Catches connection error to localhost
            spyOn(wsObj, "onclose"); //Catches connection close because of previous call of onerror

            wsObj.onopen(); //Simulating a successfully established connection

            //Is wsControl informed about the connection?
            expect(jsonReport.cmd).toBe("info");
            expect(jsonReport.msg).toBe("start download");

            //Simulate incoming download data
            wsObj.onmessage({ data: { size: 1500 } });

            worker.onmessageWorker(JSON.stringify(
                {
                    wsTestCase: "download",
                    cmd: "close",
                }
            ));
            expect(jsonReport.wsData).toBe(1500);
        })

        it("resets counters", function () {
            worker.onmessageWorker(JSON.stringify(
                {
                    wsTestCase: "download",
                    cmd: "resetCounter",
                }
            ));
            // Get feedback about the reset
            expect(jsonReport.cmd).toBe("info");
        })

        it("reports to wsControl when asked for", function () {
            spyOn(worker, "resetCounterWorker");
            worker.onmessageWorker(JSON.stringify(
                {
                    wsTestCase: "download",
                    cmd: "report",
                }
            ));
            expect(worker.resetCounterWorker).toHaveBeenCalled();
            expect(jsonReport.cmd).toBe("report");
        })

        it("reports with error when receiving an undefined command", function () {
            worker.onmessageWorker(JSON.stringify(
                {
                    wsTestCase: "download",
                    cmd: "42",
                }
            ));
            expect(jsonReport.cmd).toBe("error");
        })

    });
    describe("Testcase upload", function () {
        it("handles simulated websocket upload measurement", function () {

            spyOn(worker, "connectWorker").and.callThrough();
            worker.onmessageWorker(JSON.stringify(
                {
                    wsTestCase: "upload",
                    cmd: "connect",
                    wsTarget: "localhost/where",
                    wsTargetRtt: "localhost/where",
                    wsTargetPort: 8080,
                    wsProtocol: "upload",
                    wsFrameSize: 32768,
                    wsAuthToken: "null",
                    wsAuthTimestamp: "null2",
                    uploadFramesPerCall: 5,
                }
            ));
            //Did we try to connect the socket?
            expect(worker.connectWorker).toHaveBeenCalled();


            //Was a WS object instantiated?
            expect(WSSpy).toHaveBeenCalledWith('ws://localhost/where:8080', ['upload', 'overload', 'null', 'null2']);
            spyOn(wsObj, "onerror"); //Catches connection error to localhost
            spyOn(wsObj, "onclose"); //Catches connection close because of previous call of onerror
            wsObj.onopen(); //Simulating a successfully established connection
            spyOnProperty(wsObj, "readyState", "get").and.returnValue(1);
            wsObj.bufferedAmount = 0;
            worker.onmessageWorker(JSON.stringify(
                {
                    wsTestCase: "upload",
                    cmd: "uploadStart",
                }
            ));
            //Trigger sending
            jasmine.clock().tick(100);
            //Is Websocket send() called?
            expect(sendSpy).toHaveBeenCalled();

            //Simulate feedback from server
            wsObj.onmessage({ data: "13,34,23,3242,5433;something" });
            worker.onmessageWorker(JSON.stringify(
                {
                    wsTestCase: "upload",
                    cmd: "report",
                }
            ));

            //Do we get a report?
            expect(jsonReport.cmd).toBe("report");
            wsObj.onmessage({ data: "23,44,43,6242,5433;something" });
            worker.onmessageWorker(JSON.stringify(
                {
                    wsTestCase: "upload",
                    cmd: "fetchCounter",
                }
            ));
            expect(jsonReport.ulReportDict).toEqual({ 0: { bRcv: 23, time: 6242, hRcv: 5433 } });

            wsObj.onmessage({ data: "23,44,43,6242,5433;something" });
            worker.onmessageWorker(JSON.stringify(
                {
                    wsTestCase: "upload",
                    cmd: "resetCounter",
                }
            ));
            // Get feedback about the reset
            expect(jsonReport.cmd).toBe("info");

            //Do we get the counters?
            expect(jsonReport.msg).toBe("counter reseted");
            worker.onmessageWorker(JSON.stringify(
                {
                    wsTestCase: "upload",
                    cmd: "close",
                }
            ));
            //ulReportDict should be empty as we reset the counters
            expect(jsonReport.ulReportDict).toEqual({});


        });
    });
    describe("Testcase RTT", function () {
        it("handles simulated websocket RTT measurement", function () {

            spyOn(worker, "connectWorker").and.callThrough();
            worker.onmessageWorker(JSON.stringify(
                {
                    wsTestCase: "rtt",
                    cmd: "connect",
                    wsTarget: "localhost/where",
                    wsTargetRtt: "localhost/where",
                    wsTargetPort: 8080,
                    wsProtocol: "upload",
                    wsFrameSize: 32768,
                    wsAuthToken: "null",
                    wsAuthTimestamp: "null2",
                    uploadFramesPerCall: 5,
                }
            ));
            //Did we try to connect the socket?
            expect(worker.connectWorker).toHaveBeenCalled();

            //Was a WS object instantiated?
            expect(WSSpy).toHaveBeenCalledWith('ws://localhost/where:8080', ['upload', 'overload', 'null', 'null2']);
            spyOn(wsObj, "onerror"); //Catches connection error to localhost
            spyOn(wsObj, "onclose"); //Catches connection close because of previous call of onerror
            spyOnProperty(wsObj, "readyState", "get").and.returnValue(1);
            wsObj.onopen(); //Simulating a successfully established connection
            expect(sendSpy).toHaveBeenCalled();
            wsObj.onmessage({
                    cmd: "rttReport",
                    data: JSON.stringify(
                        {
                            wsRttValues:
                            {
                                avg: 23
                            }
                        })
                });

        });
    });
});
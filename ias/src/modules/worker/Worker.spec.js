/*!
    \file Worker.spec.js
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2019-11-13

    Copyright (C) 2016 - 2019 zafaco GmbH

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3 
    as published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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
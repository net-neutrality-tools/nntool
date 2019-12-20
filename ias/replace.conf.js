/*!
    \file replace.conf.js
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2019-12-20

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

const replace = require('replace-in-file');
const results = replace.sync({
  	files: 'build/plain/core/Worker.js',
  	from: [
	  	/\nonmessage\s=\s/g,
		/function setWsParameters\(/g,
		/setWsParameters\(/g,
		/function connect\(\)/g,
		/connect\(\)/g,
		/function resetCounter\(\)/g,
		/resetCounter\(\)/g,
		/function websocketClose\(/g,
		/websocketClose\(/g,
		/function resetValues\(/g,
		/resetValues\(/g,
		/reportToControl\(/g,
		/roundTripTime\(\)/g,
		/download\(\)/g,
		/upload\(\)/g,
		/sendToWebSocket\(/g,
  	],
	to: [
  		'\nthis.onmessageWorker = ',
		'this.setWsParametersWorker = function(',
		'this.setWsParametersWorker(',
		'this.connectWorker = function()',
		'this.connectWorker()',
		'this.resetCounterWorker = function()',
		'this.resetCounterWorker()',
		'this.websocketCloseWorker = function(',
		'this.websocketCloseWorker(',
		'this.resetValuesWorker = function(',
		'this.resetValuesWorker(',
		'reportToControlWorker(',
		'roundTripTimeWorker()',
		'downloadWorker()',
		'uploadWorker()',
		'sendToWebSocketWorker('
	  ],
	  countMatches: true,
});
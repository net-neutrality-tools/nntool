/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.nntool.shared.qos.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 * @author Lukasz Budryk (lb@alladin.at)
 *
 */
public class SipTaskHelperTest {

	@Test
	public void testRandomStringGenerationAndTestCorrectLength() {
		String rnd = SipTaskHelper.generateRandomString(3, 3);
		assertNotNull("Random string == null", rnd);
		assertEquals("Random string len != 3", 3, rnd.length());

		rnd = SipTaskHelper.generateRandomString(4, 4);
		assertNotNull("Random string == null", rnd);
		assertEquals("Random string len != 4", 4, rnd.length());

		rnd = SipTaskHelper.generateRandomString(5, 5);
		assertNotNull("Random string == null", rnd);
		assertEquals("Random string len != 5", 5, rnd.length());

		rnd = SipTaskHelper.generateRandomString(50, 50);
		assertNotNull("Random string == null", rnd);
		assertEquals("Random string len != 50", 50, rnd.length());
		
		rnd = SipTaskHelper.generateRandomString(10, 20);
		assertNotNull("Random string == null", rnd);
		assertTrue("Random string len not in range (10-20)", rnd.length() >= 10 && rnd.length() <= 20);		
	}
	
	/*@Test
	public void testSipPreProcessAndRandomHeaderGeneration() {
		final QosMeasurementObjective qos = new QosMeasurementObjective();
		SipTaskHelper.preProcess(qos.getParams());
		assertNotNull("SIP Header from == null", qos.getParams().get(SipTaskHelper.PARAM_FROM));
		assertNotNull("SIP Header via == null", qos.getParams().get(SipTaskHelper.PARAM_VIA));
		assertNotNull("SIP Header to == null", qos.getParams().get(SipTaskHelper.PARAM_TO));
	}*/
}

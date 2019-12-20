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

package at.alladin.nettest.shared.server.storage.couchdb.mapper.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequestInfo;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.MeasurementAgentTypeDto;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgent;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgentType;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
	MeasurementAgentMapperImpl.class
})
public class MeasurementAgentMapperTest {

	@Autowired
	private MeasurementAgentMapper measurementAgentMapper;
	
	@Test
	public void testMapNullJdk8RegistrationRequestToMeasurementAgent() {
		assertNull(measurementAgentMapper.map((ApiRequest<RegistrationRequest>) null));
	}
	
	@Test
	public void testMapJdk8RegistrationRequestToMeasurementAgent() {
		final ApiRequest<RegistrationRequest> request = new ApiRequest<>();
		final RegistrationRequest registrationRequest = new RegistrationRequest();
		request.setData(registrationRequest);
		
		registrationRequest.setTermsAndConditionsAccepted(true);
		registrationRequest.setTermsAndConditionsAcceptedVersion(12);
		registrationRequest.setGroupName("groupName");
		
		final ApiRequestInfo requestInfo = new ApiRequestInfo();
		request.setRequestInfo(requestInfo);
		
		requestInfo.setAgentType(MeasurementAgentTypeDto.MOBILE);
		requestInfo.setAgentId("uuid");
		
		final MeasurementAgent agent = measurementAgentMapper.map(request);
		
		assertTrue(agent.isTermsAndConditionsAccepted());
		assertEquals(12, agent.getTermsAndConditionsAcceptedVersion());
		assertEquals("groupName", agent.getGroupName());
		assertEquals("uuid", agent.getUuid());
		assertEquals(MeasurementAgentType.MOBILE, agent.getType());
		
	}
	
	// basic settings couchdb doc: {"docType":"Settings","uuid":"4e7567ef-175b-44f4-8436-8fd6d22dab48","urls":{"controller_service":"controller-url","controller_service_ipv4":"ipv4","controller_service_ipv6":"ipv6","collector_service":"collector.url"}}

	
}

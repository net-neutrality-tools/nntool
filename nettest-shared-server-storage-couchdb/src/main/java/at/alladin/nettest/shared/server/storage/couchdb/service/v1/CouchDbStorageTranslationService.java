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

package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import at.alladin.nettest.shared.server.service.storage.v1.StorageTranslationService;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.TranslationRepository;
import at.alladin.nettest.shared.server.storage.couchdb.helper.CouchDbMessageSource;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Service
public class CouchDbStorageTranslationService implements StorageTranslationService {

	@Autowired
	private TranslationRepository translationRepository;
	
	@Override
	public MessageSource getDefaultMessageSource() {
		return new CouchDbMessageSource(translationRepository);
	}
}

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

package at.alladin.nettest.spring.data.couchdb.core.mapping;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface DocTypeHelper {

	/**
	 * Finds the docType of a given entity class. 
	 * If no Document annotation with a valid docType is defined the simple name of the entity class is returned. 
	 * 
	 * @param entityClass the entity class.
	 * @return the entity's docType.
	 */
	static String getDocType(Class<?> entityClass) {
		final Document doc = AnnotationUtils.findAnnotation(entityClass, Document.class);
		if (doc != null) {
			if (StringUtils.hasLength(doc.docType())) {
				return doc.docType();
			}
		}
		
		return entityClass.getSimpleName();
	}
}

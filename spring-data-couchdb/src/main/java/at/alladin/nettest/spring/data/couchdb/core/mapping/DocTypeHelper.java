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

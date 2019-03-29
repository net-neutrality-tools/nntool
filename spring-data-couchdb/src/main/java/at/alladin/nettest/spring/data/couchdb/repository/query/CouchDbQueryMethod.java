package at.alladin.nettest.spring.data.couchdb.repository.query;

import java.lang.reflect.Method;
import java.util.Optional;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.util.StringUtils;

import at.alladin.nettest.spring.data.couchdb.core.mapping.DocTypeHelper;
import at.alladin.nettest.spring.data.couchdb.core.query.MangoQuery;
import at.alladin.nettest.spring.data.couchdb.core.query.View;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class CouchDbQueryMethod extends QueryMethod {

	private final Method method;
	
	public CouchDbQueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory/*, CouchDbEntityInformation<?, ?> entityInformation*/) {
		super(method, metadata, factory);

		this.method = method;
	}
	
	public boolean hasViewAnnotation() {
		return getViewAnnotation() != null;
	}
	
	public View getViewAnnotation() {
		return AnnotationUtils.getAnnotation(method, View.class);
	}
	
	public boolean hasMangoQueryAnnotation() {
		return getMangoQueryAnnotation() != null;
	}
	
	public MangoQuery getMangoQueryAnnotation() {
		return AnnotationUtils.getAnnotation(method, MangoQuery.class);
	}
	
	public boolean hasMangoQuery() {
		return Optional.ofNullable(getMangoQueryAnnotation())
						.map(m -> StringUtils.hasLength(m.value()))
						.orElse(false);
	}
	
	public String getDocType() {
		return DocTypeHelper.getDocType(getReturnedObjectType());
	}
}

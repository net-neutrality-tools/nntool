package at.alladin.nettest.spring.data.couchdb.repository.config;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.RepositoryConfigurationSource;

import at.alladin.nettest.spring.data.couchdb.config.BeanNames;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;
import at.alladin.nettest.spring.data.couchdb.repository.support.CouchDbRepositoryFactoryBean;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class CouchDbRepositoryConfigurationExtension extends RepositoryConfigurationExtensionSupport {

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#getModulePrefix()
	 */
	@Override
	protected String getModulePrefix() {
		return "couchdb";
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfigurationExtension#getRepositoryFactoryBeanClassName()
	 */
	@Override
	public String getRepositoryFactoryBeanClassName() {
		return CouchDbRepositoryFactoryBean.class.getName();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#getIdentifyingTypes()
	 */
	@Override
	protected Collection<Class<?>> getIdentifyingTypes() {
		return Collections.singleton(CouchDbRepository.class);
	}
	
//	/*
//	 * (non-Javadoc)
//	 * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#getIdentifyingAnnotations()
//	 */
//	@Override
//	protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
//		return Collections.singleton(); // TODO: Annotation identifying domain model objects, something like @Document or @CouchDbDocument
//	}

	// we do not support xml configuration...
//	/*
//	 * (non-Javadoc)
//	 * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#postProcess(org.springframework.beans.factory.support.BeanDefinitionBuilder, org.springframework.data.repository.config.XmlRepositoryConfigurationSource)
//	 */
//	@Override
//	public void postProcess(BeanDefinitionBuilder builder, XmlRepositoryConfigurationSource config) {
//		final Element element = config.getElement();
//		
//		//ParsingUtils.setPropertyReference(builder, element, attribute, ""); // TODO
//	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#postProcess(org.springframework.beans.factory.support.BeanDefinitionBuilder, org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource)
	 */
	@Override
	public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {
		builder.addDependsOn(BeanNames.COUCHDB_OPERATIONS_MAPPING);
		//builder.addDependsOn("couchDbDatabaseMapping");
		builder.addPropertyReference("couchDbOperationsMapping", BeanNames.COUCHDB_OPERATIONS_MAPPING);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#registerBeansForRoot(org.springframework.beans.factory.support.BeanDefinitionRegistry, org.springframework.data.repository.config.RepositoryConfigurationSource)
	 */
	@Override
	public void registerBeansForRoot(BeanDefinitionRegistry registry, RepositoryConfigurationSource configurationSource) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RepositoryOperationsMappingFactoryBean.class);
		
		builder.addPropertyReference("couchDbClient", "couchDbClient"); // TODO: BeanNames.
		builder.addPropertyReference("couchDbDatabaseMapping", "couchDbDatabaseMapping"); // TODO: BeanNames.

		registry.registerBeanDefinition(BeanNames.COUCHDB_OPERATIONS_MAPPING, builder.getBeanDefinition());
	}
}

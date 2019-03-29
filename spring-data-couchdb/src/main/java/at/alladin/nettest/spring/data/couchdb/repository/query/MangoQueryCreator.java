package at.alladin.nettest.spring.data.couchdb.repository.query;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;

import com.cloudant.client.api.query.Expression;
import com.cloudant.client.api.query.Operation;
import com.cloudant.client.api.query.QueryBuilder;
import com.cloudant.client.api.query.Selector;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class MangoQueryCreator extends AbstractQueryCreator<String, Selector> {

	private static final Logger logger = LoggerFactory.getLogger(MangoQueryCreator.class);
	
	private final String docType;
	
	public MangoQueryCreator(PartTree tree, ParameterAccessor parameters, String docType) {
		super(tree, parameters);
		
		this.docType = docType;
	}

	private Selector createDocTypeSelector() {
		return Expression.eq("docType", docType);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.query.parser.AbstractQueryCreator#create(org.springframework.data.repository.query.parser.Part, java.util.Iterator)
	 */
	@Override
	protected Selector create(Part part, Iterator<Object> iterator) {
		logger.debug("-- create");
		logger.debug("{}", part.getProperty());
		logger.debug("{}", part.getProperty().getLeafProperty());
		logger.debug("{}", part.getProperty().getSegment());
		logger.debug("{}", part.getProperty().isCollection());
		logger.debug("{}", part.getProperty().toDotPath());
		logger.debug("{}", part.getProperty().next());
		
		final String propertyDotPath = part.getProperty().toDotPath(); // Mango query works with dotPath (e.g. "nested1.nested2.abc": 123)
		
		switch (part.getType()) {
		case IS_NULL:
			return Expression.eq(propertyDotPath, null);
		case IS_NOT_NULL:
			return Expression.ne(propertyDotPath, null);
		case TRUE:
			return Expression.eq("a", "a");
		case FALSE:
			return Expression.ne("a", "a");
		case EXISTS:
			return Expression.exists(propertyDotPath, true);
		case SIMPLE_PROPERTY:
			return Expression.eq(propertyDotPath, iterator.next());
		case REGEX:
			return Expression.regex(propertyDotPath, iterator.next().toString());
		default:
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.query.parser.AbstractQueryCreator#and(org.springframework.data.repository.query.parser.Part, java.lang.Object, java.util.Iterator)
	 */
	@Override
	protected Selector and(Part part, Selector base, Iterator<Object> iterator) {
		/*if (base == null) {
			return create(part, iterator);
		}*/
		
		logger.debug("-- and");
		logger.debug("{}", part.getProperty());
		logger.debug("{}", part.getProperty().getLeafProperty());
		logger.debug("{}", part.getProperty().getSegment());
		logger.debug("{}", part.getProperty().isCollection());
		logger.debug("{}", part.getProperty().toDotPath());
		logger.debug("{}", part.getProperty().next());
		
		/*return Operation.and(bas)*/
		
		return Operation.and(base, create(part, iterator));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.query.parser.AbstractQueryCreator#or(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected Selector or(Selector base, Selector criteria) {
		return Operation.or(base, criteria);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.query.parser.AbstractQueryCreator#complete(java.lang.Object, org.springframework.data.domain.Sort)
	 */
	@Override
	protected String complete(Selector criteria, Sort sort) {
		return new QueryBuilder(Operation.and(createDocTypeSelector(), criteria))/*.sort(sort)*/.build();
	}
}

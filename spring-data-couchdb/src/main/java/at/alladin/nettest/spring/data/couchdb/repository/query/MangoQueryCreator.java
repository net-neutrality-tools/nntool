package at.alladin.nettest.spring.data.couchdb.repository.query;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;

import com.cloudant.client.api.query.Expression;
import com.cloudant.client.api.query.Operation;
import com.cloudant.client.api.query.QueryBuilder;
import com.cloudant.client.api.query.Selector;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class MangoQueryCreator extends AbstractQueryCreator<String, Selector> {

	private static final Logger logger = LoggerFactory.getLogger(MangoQueryCreator.class);
	
	private final String docType;
	
	private final ParameterAccessor parameters;
	
	private final Class<?> resultClass;
	
	public MangoQueryCreator(PartTree tree, ParameterAccessor parameters, String docType, final Class<?> resultClass) {
		super(tree, parameters);
		
		this.parameters = parameters;
		this.docType = docType;
		this.resultClass = resultClass;
	}
	
	private Selector createDocTypeSelector() {
		return Expression.eq("docType", docType);
	}
	
	private String toCustomDotPath(final PropertyPath propertyPath) {
		String segment = propertyPath.getSegment();
		try {
			final Field field = propertyPath.getOwningType().getType().getDeclaredField(segment);
			final SerializedName serializedName = field.getAnnotation(SerializedName.class);
			if (serializedName != null) {
				segment = serializedName.value();			
			} 
		} catch (Exception ex) {
			logger.error("Error while trying to get custom dotpath segment value from SerializedName annotation", ex);
		}
		
		if (propertyPath.hasNext()) {
			return segment + "." + toCustomDotPath(propertyPath.next());
		}

		return segment;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.query.parser.AbstractQueryCreator#create(org.springframework.data.repository.query.parser.Part, java.util.Iterator)
	 */
	@Override
	protected Selector create(Part part, Iterator<Object> iterator) {
		final String propertyDotPath = toCustomDotPath(part.getProperty()); //part.getProperty().toDotPath(); // Mango query works with dotPath (e.g. "nested1.nested2.abc": 123)
		
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
		final QueryBuilder queryBuilder = new QueryBuilder(Operation.and(createDocTypeSelector(), criteria));
		
		if (sort != null) {
			queryBuilder.sort(parseSortObject(sort));
		}
		
		if (parameters != null) {
			final Pageable pageable = parameters.getPageable();
			if (pageable != null && pageable.isPaged()) {
				queryBuilder
					.skip(pageable.getOffset())
					.limit(pageable.getPageSize());
			}
			
			if (sort == null && pageable.getSort() != null) {
				queryBuilder.sort(parseSortObject(pageable.getSort()));
			}
		}
		
		return queryBuilder.build();
	}
	
	protected com.cloudant.client.api.query.Sort[] parseSortObject(final Sort sort) {
		final List<com.cloudant.client.api.query.Sort> sortList = sort.get()
			.map(o -> {
				final String sortQuery = transformSortQuery(o.getProperty());
				
				return o.getDirection() == Direction.DESC ? 
						com.cloudant.client.api.query.Sort.desc(sortQuery) : 
						com.cloudant.client.api.query.Sort.asc(sortQuery);
			})
			.collect(Collectors.toList());
		
		return sortList.toArray(new com.cloudant.client.api.query.Sort[] {});
	}
	
	private String transformSortQuery(final String sortString) {
		if (sortString == null) {
			return null;
		}
		
		try {
			final PropertyPath sortPath = PropertyPath.from(sortString, resultClass);
			return toCustomDotPath(sortPath);
		} catch (Exception ex) {
			logger.error("Could not transform sort query (original sort string: {})", sortString, ex);
		}
		
		return sortString;
	}
}

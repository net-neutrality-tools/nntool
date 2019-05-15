package at.alladin.nntool.shared.qos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class ResultComparerTest {

	
	private Map<String, Field> fieldNameToFieldMap;
	
	private ResultOptions options;
	
	private TcpResult resOne;
	private TcpResult resTwo;
	
	private DnsResult dnsResult;
	
	@Before
	public void init() throws Exception{
		fieldNameToFieldMap = new HashMap<>();
		fieldNameToFieldMap.put("outResult", TcpResult.class.getDeclaredField("outResult"));
		fieldNameToFieldMap.put("outPort", TcpResult.class.getDeclaredField("outPort"));
		
		options = new ResultOptions(Locale.ENGLISH);
		
		resOne = new TcpResult();
		resTwo = new TcpResult();
		dnsResult = new DnsResult();
		
	}
	
	@Test
	public void basicObjectComparison() {
		assertEquals("string comparison not working", 0, ResultComparer.compare("", ""));
		assertEquals("string comparison not working", 0, ResultComparer.compare("Test", "Test"));
		assertEquals("string comparison not working", "Test".compareTo("Fail"), ResultComparer.compare("Test", "Fail"));
		assertEquals("string comparison not working", "Fail".compareTo("Test"), ResultComparer.compare("Fail", "Test"));
		
		assertEquals("null comparison not working", 1, ResultComparer.compare("", null));
		assertEquals("null comparison not working", -1, ResultComparer.compare(null, new Double(5)));
		assertEquals("null comparison not working", 0, ResultComparer.compare(null, null));
		
		
		assertEquals("double comparison not working", -1, ResultComparer.compare(new Double(7.2), new Double(14.2)));
		assertEquals("double comparison not working", 1, ResultComparer.compare(new Double(14.2), new Double(7.2)));
		assertEquals("double comparison not working", 0, ResultComparer.compare(new Double(7.2), new Double(7.2)));
	}
	
	@Test
	public void abstractResultComparisonWithIncompatibleValuesReturnsNull() throws Exception {
		
		assertNull(ResultComparer.compare(resOne, dnsResult, fieldNameToFieldMap, options));
	}
	
}

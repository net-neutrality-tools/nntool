package at.alladin.nettest.service.statistic.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import at.alladin.nettest.service.statistic.config.DataExportConfiguration;
import at.alladin.nettest.service.statistic.domain.model.RawExportData;

/**
 * 
 * @author lb@alladin.at
 *
 */
@Service
public class DataExportService {
	
	private static final String FILENAME_EXTENSION = "nntool-%YEAR%-%MONTH%.%EXTENSION%";
    private static final String FILENAME_ZIP = "nntool-%YEAR%-%MONTH%.zip";
    private static final String FILENAME_DAILY_EXTENSION = "nntool-%YEAR%-%MONTH%-%DAY%.%EXTENSION%";
    private static final String FILENAME_DAILY_ZIP = "nntool-%YEAR%-%MONTH%-%DAY%.zip";
    private static final String FILENAME_EXTENSION_CURRENT = "nntool.%EXTENSION%";
    private static final String FILENAME_ZIP_CURRENT = "nntool.zip";
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
	 
    @Autowired
    private DataExportConfiguration dataExportConfiguration;
    
    public enum ExportExtension {
    	CSV("csv"),
    	JSON("json"),
    	YAML("yaml", "yml");
    	
    	private final Set<String> nameSet = new HashSet<>();
    	
    	private static Map<ExportExtension, JsonFactory> extensionToFactoryMap = new HashMap<>();
    	
    	static {
    		extensionToFactoryMap.put(CSV, new CsvFactory());
    		extensionToFactoryMap.put(JSON, new JsonFactory());
    		extensionToFactoryMap.put(YAML, new YAMLFactory());
    	}
    	
    	ExportExtension (String ...names) {
    		nameSet.addAll(Arrays.asList(names));
    	}
    	
    	public JsonFactory getJsonFactory() throws IOException {
    		return extensionToFactoryMap.get(this);
    	}
    	
    	public Set<String> getNameSet () {
    		return nameSet;
    	}
    	
    	/**
    	 * 
    	 * @return the exportExtension associated w/the given alias, or NULL if none are associated
    	 */
    	public static ExportExtension getByName(final String name){
    		for (ExportExtension e : ExportExtension.values()) {
    			if (e.getNameSet().contains(name.toLowerCase())) {
    				return e;
    			}
    		}
    		
    		return null;
    	}
    }
    
    /**
     * 
     * @author lb@alladin.at
     *
     */
    private class ExportDataResultExtractor implements ResultSetExtractor<RawExportData> {
    	
    	/*
    	 * (non-Javadoc)
    	 * @see org.springframework.jdbc.core.ResultSetExtractor#extractData(java.sql.ResultSet)
    	 */
		@Override
		public RawExportData extractData(ResultSet rs) throws SQLException, DataAccessException {
			final RawExportData rawData = new RawExportData();
			final List<List<String>> resultList = new ArrayList<>();
            final ResultSetMetaData meta = rs.getMetaData();
            final int colCount = meta.getColumnCount();
            
            rawData.setColumns(new ArrayList<>());
            rawData.getColumns().addAll(dataExportConfiguration.getFields().keySet());

            while (rs.next()) {
                final List<String> line = new ArrayList<>();

                for (int i = 0; i < colCount; i++) {
                    final Object obj = rs.getObject(i + 1);
                    line.add(obj == null ? null : obj.toString());
                }
                
                resultList.add(line);
            }
            
            rawData.setData(resultList);
            
			return rawData;
		}
	}
    
    /**
     * 
     * @param os
     * @param uuid
     * @throws IOException
     */
    public void writeExportData(final OutputStream os, final String uuid, final ExportExtension extension, final boolean isCompress) throws IOException {
    	writeExportData(os, getRawExportData(uuid), getExportFilename(uuid, extension, false), extension, isCompress);
    }
    
    public void writeExportData(final OutputStream os, final int month, final int year, 
    		final ExportExtension extension, final boolean isCompress) throws IOException {
    	writeExportData(os, getRawExportData(month, year), getExportFilename(month, year, extension, false), extension, isCompress);
    }
    
    public void writeExportData(final OutputStream os, final int day, final int month, final int year, 
    		final ExportExtension extension, final boolean isCompress) throws IOException {
    	writeExportData(os, getRawExportData(day, month, year), getExportFilename(day, month, year, extension, false), extension, isCompress);
    }
    
    private void writeExportData(final OutputStream os, final RawExportData rawData, 
    		final String exportFilename, final ExportExtension extension, final boolean isCompress) throws FileNotFoundException, IOException {
    	try(OutputStream outf = (!isCompress ? 
    			os : new ZipOutputStream(os))) {
    		writeToOutputStream(rawData, outf, exportFilename, extension, isCompress);
    	}
    }
    
    public String getExportFilename(final int month, final int year, final ExportExtension extension, final boolean isCompress) {
	    // Allow filtering by month/year.
	    if (year < 2099 && month > 0 && month <= 12 && year > 2000) {
	    	if (isCompress) {
	    		return FILENAME_ZIP.replace("%YEAR%", Integer.toString(year)).replace("%MONTH%",String.format("%02d",month));
	    	} else {
	    		return FILENAME_EXTENSION.replace("%YEAR%", Integer.toString(year))
			    		 .replace("%MONTH%",String.format("%02d",month)).replace("%EXTENSION%", extension.toString());
	    	}
	
	    } 
	    else {
	    	if (isCompress) {
	    		return FILENAME_ZIP_CURRENT;
	    	} else {
	    		return FILENAME_EXTENSION_CURRENT.replace("%EXTENSION%", extension.toString());
	    	}
	    }
    }
    
    public String getExportFilename (final int day, final int month, final int year, final ExportExtension extension, final boolean isCompress) {
	    // Allow filtering by day/month/year.
	    if (year < 2099 && month > 0 && month <= 12 && year > 2000 && day > 0 && day <= 31) {
	    	if (isCompress) {
	    		return FILENAME_DAILY_ZIP.replace("%YEAR%", Integer.toString(year)).replace("%MONTH%",String.format("%02d",month))
	    				.replace("%DAY%",String.format("%02d",day));
	    	} else {
	    		return FILENAME_DAILY_EXTENSION.replace("%YEAR%", Integer.toString(year))
			    		 .replace("%MONTH%",String.format("%02d",month)).replace("%EXTENSION%", extension.toString())
			    		 .replace("%DAY%",String.format("%02d",day));
	    	}
	
	    } 
	    else {
	    	if (isCompress) {
	    		return FILENAME_ZIP_CURRENT;
	    	} else {
	    		return FILENAME_EXTENSION_CURRENT.replace("%EXTENSION%", extension.toString());
	    	}
	    }
    }
    
    public String getExportFilename (final String uuid, final ExportExtension extension, final boolean isCompress) {
    	if (isCompress) {
    	    return "O" + uuid + ".zip";
    	} else {
    		return "O" + uuid + "." + extension.toString();
    	}
    }
    
	private void writeToOutputStream (final RawExportData exportData, OutputStream os, final String filename,
			final ExportExtension extension, final boolean isCompress) throws IOException {
		if (isCompress) {
            final ZipEntry zeLicense = new ZipEntry("LICENSE.txt");
            ((ZipOutputStream)os).putNextEntry(zeLicense);
            try (final InputStream licenseIS = getClass().getResourceAsStream("DATA_LICENSE.txt")) {
	            if (licenseIS != null) {
	            	IOUtils.copy(licenseIS, os);
	            }
            }
            
            final ZipEntry zeCsv = new ZipEntry(filename);
            ((ZipOutputStream)os).putNextEntry(zeCsv);
        }
		
		writeToOutputStream(exportData, os, extension);
	}
	
	/**
	 * Writes the given data to the OutputStream in the given file extension.
	 * 
	 * @param exportData
	 * @param os
	 * @param extension
	 * @throws IOException
	 */
	private void writeToOutputStream (final RawExportData exportData, final OutputStream os, 
			final ExportExtension extension) throws IOException {
		
		final List<String> columnNames = exportData.getColumns();
		
		final JsonGenerator jsonGenerator = extension.getJsonFactory().createGenerator(os);

		if (extension != ExportExtension.CSV && exportData.getData().size() > 1) {
			jsonGenerator.writeStartArray();
		}
		
		// If CSV is used, we need to manually include the headers.
		if (extension == ExportExtension.CSV) {
			
			CsvSchema.Builder schemeBuilder = CsvSchema.builder().setUseHeader(true);
			for (String column : columnNames) {
				schemeBuilder.addColumn(column);
			}
			
			((CsvGenerator) jsonGenerator).setSchema(schemeBuilder.build());
		}
		
		for (List<String> valueList : exportData.getData()) {
			jsonGenerator.writeStartObject();

			for (int i = 0; i < valueList.size(); i++) {
				if (extension != ExportExtension.CSV && valueList.get(i) == null) {
					continue;
				}
				jsonGenerator.writeStringField(columnNames.get(i), valueList.get(i));
			}
			
			jsonGenerator.writeEndObject();
		}
		
		if (extension != ExportExtension.CSV && exportData.getData().size() > 1) {
			jsonGenerator.writeEndArray();
		}
		
		jsonGenerator.flush();
	}
	
	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public RawExportData getRawExportData(final String uuid) {
		return jdbcTemplate.query(getSingleTestSqlQuery(), new Object[] {uuid}, new ExportDataResultExtractor());
	}

	public RawExportData getRawExportData(final int month, final int year) {
		return jdbcTemplate.query(getPerMonthSqlQuery(), new Object[] {month, year}, new ExportDataResultExtractor());
	}
	
	public RawExportData getRawExportData(final int day, final int month, final int year) {
		return jdbcTemplate.query(getPerDaySqlQuery(), new Object[] {day, month, year}, new ExportDataResultExtractor());
	}

	/**
	 * 
	 * @return
	 */
	private String getSingleTestSqlQuery() {
		 final StringBuilder sb = new StringBuilder();
		 sb.append(getSelect());
		 sb.append(dataExportConfiguration.getSuffix()).append("\n");
		 sb.append("WHERE ").append(dataExportConfiguration.getWhereClause()).append(" ");		
		 sb.append(dataExportConfiguration.getWhereOpenTestUuidClause());
		 return sb.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	private String getPerMonthSqlQuery() {
		 final StringBuilder sb = new StringBuilder();
		 sb.append(getSelect());
		 sb.append(dataExportConfiguration.getSuffix()).append("\n");
		 sb.append("WHERE ").append(dataExportConfiguration.getWhereClause()).append(" ");
		 sb.append(dataExportConfiguration.getWhereMonthClause()).append(")\n");
		 sb.append(dataExportConfiguration.getWhereYearClause()).append(")\n");
		 if (dataExportConfiguration.getOrderBy() != null) {
	 		sb.append(" ORDER BY ").append(dataExportConfiguration.getOrderBy());
		 }
		 return sb.toString();
	}
	
	private String getPerDaySqlQuery() {
		final StringBuilder sb = new StringBuilder();
		 sb.append(getSelect());
		 sb.append(dataExportConfiguration.getSuffix()).append("\n");
		 sb.append("WHERE ").append(dataExportConfiguration.getWhereClause()).append(" ");
		 sb.append(dataExportConfiguration.getWhereDayClause()).append(")\n");
		 sb.append(dataExportConfiguration.getWhereMonthClause()).append(")\n");
		 sb.append(dataExportConfiguration.getWhereYearClause()).append(")\n");
		 if (dataExportConfiguration.getOrderBy() != null) {
			 sb.append(" ORDER BY ").append(dataExportConfiguration.getOrderBy());
		 }
		 return sb.toString();
	}

	private String getSelect() {
		 final StringBuilder sb = new StringBuilder();
		 sb.append("SELECT \n");
		 final Iterator<Entry<String, String>> it = dataExportConfiguration.getFields().entrySet().iterator();
		 
		 while (it.hasNext()) {
			 final Entry<String,String> entry = it.next();
			 sb.append(" ").append(entry.getValue()).append(" AS \"").append(entry.getKey()).append("\"");
			 if (it.hasNext()) {
				 sb.append(",");
			 }
			 sb.append("\n");
		 }

		 return sb.toString();
	}
}

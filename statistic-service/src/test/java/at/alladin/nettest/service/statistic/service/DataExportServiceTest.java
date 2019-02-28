package at.alladin.nettest.service.statistic.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import at.alladin.nettest.service.statistic.config.DataExportConfiguration;
import at.alladin.nettest.service.statistic.domain.model.RawExportData;
import at.alladin.nettest.service.statistic.service.DataExportService.ExportExtension;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DataExportServiceTest {

    private final static String UUID_STRING = "53b61be7-893d-4500-a9d9-8d9203336d0c";

    private final static String SECOND_UUID_STRING = "153d6ee5-2df5-4a28-820d-7169e33988e7";

    private Map<String, String> fieldMap;

    private RawExportData singleResult;

    private OutputStream testOutputStream;

    @Tested
    private DataExportService dataExportService;

    @Injectable @Mocked
    private JdbcTemplate jdbcTemplate;

    @Injectable @Mocked
    private DataExportConfiguration dataExportConfiguration;

    @Before
    public void init() {
        fieldMap = new LinkedHashMap<>();
        fieldMap.put("id", "id");
        fieldMap.put("down", "down");
        fieldMap.put("up", "up");
        fieldMap.put("ping", "ping");

        singleResult = new RawExportData();
        final List<String> columns = new ArrayList<>();
        columns.add("id");
        columns.add("down");
        columns.add("up");
        columns.add("ping");
        singleResult.setColumns(columns);

        final List<List<String>> singleData = new ArrayList<>();
        final List<String> singleRes = new ArrayList<>();
        singleRes.add(UUID_STRING);
        singleRes.add("27");
        singleRes.add("14");
        singleRes.add("64");
        singleData.add(singleRes);
        singleResult.setData(singleData);

        testOutputStream = new ByteArrayOutputStream();

    }

    @Test
    public void exportSingleMeasurementAsCSVTest (@Mocked ResultSetMetaData metaData, @Mocked ResultSet resultSet) throws Exception {

        new Expectations() {{

            metaData.getColumnCount();
            result = 4;

            dataExportConfiguration.getFields();
            result = fieldMap;

            dataExportConfiguration.getSuffix();
            result = "from database";

            dataExportConfiguration.getWhereClause();
            result = "deleted = false";

            dataExportConfiguration.getWhereOpenTestUuidClause();
            result = "AND open_uuid = ?";

            jdbcTemplate.query(anyString, (Object[]) any, (ResultSetExtractor<RawExportData>) any);
            result = new Delegate() {
                public RawExportData delegate (final String query, final Object[] args, final ResultSetExtractor<RawExportData> extractor) throws Exception {
                    assertEquals("Unexpected number of arguments for query call",1, args.length);
                    assertEquals("Unexpected argument for query call", UUID_STRING, (String) args[0]);

                    assertTrue("Query does not contain id parameter", query.contains("id AS \"id\""));
                    assertTrue("Query does not contain down parameter", query.contains("down AS \"down\""));
                    assertTrue("Query does not contain up parameter", query.contains("up AS \"up\""));
                    assertTrue("Query does not contain ping parameter", query.contains("ping AS \"ping\""));
                    assertTrue("Query does not contain database definition", query.contains("from database"));
                    assertTrue("Query does not contain where clause with deleted and open_uuid parts", query.contains("WHERE deleted = false AND open_uuid = ?"));
                    return extractor.extractData(resultSet);
                }
            };

            resultSet.next();
            returns(true,false);

            resultSet.getObject(anyInt);
            returns(UUID_STRING, "27", "14", "64");

        }};

        dataExportService.writeExportData(testOutputStream, UUID_STRING, DataExportService.ExportExtension.CSV, false);

        final String result = testOutputStream.toString();
        final String [] resultLines = result.split("\n");

        assertEquals("Unexpected number of resulting lines", 2, resultLines.length);
        assertEquals("Unexpected CSV headers", "id,down,up,ping", resultLines[0]);
        assertEquals("Unexpected number of csv entry values", 4, resultLines[1].split(",").length);

        final String[] resultValues = resultLines[1].split(",");
        assertEquals("Result does not contain expected uuid", "\"" + UUID_STRING + "\"",  resultValues[0]);
        assertEquals("Result does not contain expected down value", "27", resultValues[1]);
        assertEquals("Result does not contain expected up value", "14", resultValues[2]);
        assertEquals("Result does not contain expected ping value", "64", resultValues[3]);

    }

    @Test
    public void exportSingleMeasurementAsJSONTest (@Mocked ResultSetMetaData metaData, @Mocked ResultSet resultSet) throws Exception {

        new Expectations() {{

            metaData.getColumnCount();
            result = 4;

            dataExportConfiguration.getFields();
            result = fieldMap;

            dataExportConfiguration.getSuffix();
            result = "from database";

            dataExportConfiguration.getWhereClause();
            result = "deleted = false";

            dataExportConfiguration.getWhereOpenTestUuidClause();
            result = "AND open_uuid = ?";

            jdbcTemplate.query(anyString, (Object[]) any, (ResultSetExtractor<RawExportData>) any);
            result = new Delegate() {
                public RawExportData delegate (final String query, final Object[] args, final ResultSetExtractor<RawExportData> extractor) throws Exception {
                    assertEquals("Unexpected number of arguments for query call",1, args.length);
                    assertEquals("Unexpected argument for query call", UUID_STRING, (String) args[0]);

                    assertTrue("Query does not contain id parameter", query.contains("id AS \"id\""));
                    assertTrue("Query does not contain down parameter", query.contains("down AS \"down\""));
                    assertTrue("Query does not contain up parameter", query.contains("up AS \"up\""));
                    assertTrue("Query does not contain ping parameter", query.contains("ping AS \"ping\""));
                    assertTrue("Query does not contain database definition", query.contains("from database"));
                    assertTrue("Query does not contain where clause with deleted and open_uuid parts", query.contains("WHERE deleted = false AND open_uuid = ?"));
                    return extractor.extractData(resultSet);
                }
            };

            resultSet.next();
            returns(true,false);

            resultSet.getObject(anyInt);
            returns(UUID_STRING, "27", "14", "64");

        }};

        dataExportService.writeExportData(testOutputStream, UUID_STRING, DataExportService.ExportExtension.JSON, false);

        final String result = testOutputStream.toString();
        assertEquals("Unexpected JSON produced", "{\"id\":\"53b61be7-893d-4500-a9d9-8d9203336d0c\",\"down\":\"27\",\"up\":\"14\",\"ping\":\"64\"}", result);
    }

    @Test
    public void exportSingleMeasurementAsYAMLTest (@Mocked ResultSetMetaData metaData, @Mocked ResultSet resultSet) throws Exception {

        new Expectations() {{

            metaData.getColumnCount();
            result = 4;

            dataExportConfiguration.getFields();
            result = fieldMap;

            dataExportConfiguration.getSuffix();
            result = "from database";

            dataExportConfiguration.getWhereClause();
            result = "deleted = false";

            dataExportConfiguration.getWhereOpenTestUuidClause();
            result = "AND open_uuid = ?";

            jdbcTemplate.query(anyString, (Object[]) any, (ResultSetExtractor<RawExportData>) any);
            result = new Delegate() {
                public RawExportData delegate (final String query, final Object[] args, final ResultSetExtractor<RawExportData> extractor) throws Exception {
                    assertEquals("Unexpected number of arguments for query call",1, args.length);
                    assertEquals("Unexpected argument for query call", UUID_STRING, (String) args[0]);

                    assertTrue("Query does not contain id parameter", query.contains("id AS \"id\""));
                    assertTrue("Query does not contain down parameter", query.contains("down AS \"down\""));
                    assertTrue("Query does not contain up parameter", query.contains("up AS \"up\""));
                    assertTrue("Query does not contain ping parameter", query.contains("ping AS \"ping\""));
                    assertTrue("Query does not contain database definition", query.contains("from database"));
                    assertTrue("Query does not contain where clause with deleted and open_uuid parts", query.contains("WHERE deleted = false AND open_uuid = ?"));
                    return extractor.extractData(resultSet);
                }
            };

            resultSet.next();
            returns(true,false);

            resultSet.getObject(anyInt);
            returns(UUID_STRING, "27", "14", "64");

        }};

        dataExportService.writeExportData(testOutputStream, UUID_STRING, DataExportService.ExportExtension.YAML, false);

        final String result = testOutputStream.toString();
        assertEquals("Unexpected YAML produced", "---\n" +
                "id: \"53b61be7-893d-4500-a9d9-8d9203336d0c\"\n" +
                "down: \"27\"\n" +
                "up: \"14\"\n" +
                "ping: \"64\"", result);
    }

    @Test
    public void exportMonthlyMeasurementsAsCSVTest (@Mocked ResultSetMetaData metaData, @Mocked ResultSet resultSet) throws Exception {

        new Expectations() {{

            metaData.getColumnCount();
            result = 4;

            dataExportConfiguration.getFields();
            result = fieldMap;

            dataExportConfiguration.getSuffix();
            result = "from database";

            dataExportConfiguration.getWhereClause();
            result = "deleted = false";

            dataExportConfiguration.getWhereYearClause();
            result = "AND (year = ?";

            dataExportConfiguration.getWhereMonthClause();
            result = "AND (month = ?";

            jdbcTemplate.query(anyString, (Object[]) any, (ResultSetExtractor<RawExportData>) any);
            result = new Delegate() {
                public RawExportData delegate (final String query, final Object[] args, final ResultSetExtractor<RawExportData> extractor) throws Exception {
                    assertEquals("Unexpected number of arguments for query call",2, args.length);
                    assertEquals("Unexpected argument for query call", 5, (int) args[0]);
                    assertEquals("Unexpected argument for query call", 1995, (int) args[1]);

                    assertTrue("Query does not contain id parameter", query.contains("id AS \"id\""));
                    assertTrue("Query does not contain down parameter", query.contains("down AS \"down\""));
                    assertTrue("Query does not contain up parameter", query.contains("up AS \"up\""));
                    assertTrue("Query does not contain ping parameter", query.contains("ping AS \"ping\""));
                    assertTrue("Query does not contain database definition", query.contains("from database"));
                    assertTrue("Query does not contain where clause with deleted part", query.contains("WHERE deleted = false"));
                    assertTrue("Query does not contain where clause with year part", query.contains("month = ?"));
                    assertTrue("Query does not contain where clause with month part", query.contains("year = ?"));

                    return extractor.extractData(resultSet);
                }
            };

            resultSet.next();
            returns(true,true, false);

            resultSet.getObject(anyInt);
            returns(UUID_STRING, "27", "14", "64", SECOND_UUID_STRING, "44", "65", "14");

        }};

        dataExportService.writeExportData(testOutputStream, 5, 1995, DataExportService.ExportExtension.CSV, false);

        final String result = testOutputStream.toString();
        final String [] resultLines = result.split("\n");

        assertEquals("Unexpected number of resulting lines", 3, resultLines.length);
        assertEquals("Unexpected CSV headers", "id,down,up,ping", resultLines[0]);
        assertEquals("Unexpected number of csv entry values", 4, resultLines[1].split(",").length);
        assertEquals("Unexpected number of csv entry values", 4, resultLines[2].split(",").length);

        String[] resultValues = resultLines[1].split(",");
        assertEquals("Result does not contain expected uuid", "\"" + UUID_STRING + "\"",  resultValues[0]);
        assertEquals("Result does not contain expected down value", "27", resultValues[1]);
        assertEquals("Result does not contain expected up value", "14", resultValues[2]);
        assertEquals("Result does not contain expected ping value", "64", resultValues[3]);

        resultValues = resultLines[2].split(",");
        assertEquals("Result does not contain expected uuid", "\"" + SECOND_UUID_STRING + "\"",  resultValues[0]);
        assertEquals("Result does not contain expected down value", "44", resultValues[1]);
        assertEquals("Result does not contain expected up value", "65", resultValues[2]);
        assertEquals("Result does not contain expected ping value", "14", resultValues[3]);

    }

    @Test
    public void exportMonthlyMeasurementsAsJSONTest (@Mocked ResultSetMetaData metaData, @Mocked ResultSet resultSet) throws Exception {

        new Expectations() {{

            metaData.getColumnCount();
            result = 4;

            dataExportConfiguration.getFields();
            result = fieldMap;

            dataExportConfiguration.getSuffix();
            result = "from database";

            dataExportConfiguration.getWhereClause();
            result = "deleted = false";

            dataExportConfiguration.getWhereYearClause();
            result = "AND (year = ?";

            dataExportConfiguration.getWhereMonthClause();
            result = "AND (month = ?";

            jdbcTemplate.query(anyString, (Object[]) any, (ResultSetExtractor<RawExportData>) any);
            result = new Delegate() {
                public RawExportData delegate (final String query, final Object[] args, final ResultSetExtractor<RawExportData> extractor) throws Exception {
                    assertEquals("Unexpected number of arguments for query call",2, args.length);
                    assertEquals("Unexpected argument for query call", 5, (int) args[0]);
                    assertEquals("Unexpected argument for query call", 1995, (int) args[1]);

                    assertTrue("Query does not contain id parameter", query.contains("id AS \"id\""));
                    assertTrue("Query does not contain down parameter", query.contains("down AS \"down\""));
                    assertTrue("Query does not contain up parameter", query.contains("up AS \"up\""));
                    assertTrue("Query does not contain ping parameter", query.contains("ping AS \"ping\""));
                    assertTrue("Query does not contain database definition", query.contains("from database"));
                    assertTrue("Query does not contain where clause with deleted part", query.contains("WHERE deleted = false"));
                    assertTrue("Query does not contain where clause with year part", query.contains("month = ?"));
                    assertTrue("Query does not contain where clause with month part", query.contains("year = ?"));

                    return extractor.extractData(resultSet);
                }
            };

            resultSet.next();
            returns(true,true, false);

            resultSet.getObject(anyInt);
            returns(UUID_STRING, "27", "14", "64", SECOND_UUID_STRING, "44", "65", "14");

        }};

        dataExportService.writeExportData(testOutputStream, 5, 1995, ExportExtension.JSON, false);

        final String result = testOutputStream.toString();

        assertEquals("Unexpected output JSON", "[{\"id\":\"53b61be7-893d-4500-a9d9-8d9203336d0c\",\"down\":\"27\",\"up\":\"14\",\"ping\":\"64\"},{\"id\":\"153d6ee5-2df5-4a28-820d-7169e33988e7\",\"down\":\"44\",\"up\":\"65\",\"ping\":\"14\"}]", result);

    }

    @Test
    public void exportMonthlyMeasurementsAsYAMLTest (@Mocked ResultSetMetaData metaData, @Mocked ResultSet resultSet) throws Exception {

        new Expectations() {{

            metaData.getColumnCount();
            result = 4;

            dataExportConfiguration.getFields();
            result = fieldMap;

            dataExportConfiguration.getSuffix();
            result = "from database";

            dataExportConfiguration.getWhereClause();
            result = "deleted = false";

            dataExportConfiguration.getWhereYearClause();
            result = "AND (year = ?";

            dataExportConfiguration.getWhereMonthClause();
            result = "AND (month = ?";

            jdbcTemplate.query(anyString, (Object[]) any, (ResultSetExtractor<RawExportData>) any);
            result = new Delegate() {
                public RawExportData delegate (final String query, final Object[] args, final ResultSetExtractor<RawExportData> extractor) throws Exception {
                    assertEquals("Unexpected number of arguments for query call",2, args.length);
                    assertEquals("Unexpected argument for query call", 5, (int) args[0]);
                    assertEquals("Unexpected argument for query call", 1995, (int) args[1]);

                    assertTrue("Query does not contain id parameter", query.contains("id AS \"id\""));
                    assertTrue("Query does not contain down parameter", query.contains("down AS \"down\""));
                    assertTrue("Query does not contain up parameter", query.contains("up AS \"up\""));
                    assertTrue("Query does not contain ping parameter", query.contains("ping AS \"ping\""));
                    assertTrue("Query does not contain database definition", query.contains("from database"));
                    assertTrue("Query does not contain where clause with deleted part", query.contains("WHERE deleted = false"));
                    assertTrue("Query does not contain where clause with year part", query.contains("month = ?"));
                    assertTrue("Query does not contain where clause with month part", query.contains("year = ?"));

                    return extractor.extractData(resultSet);
                }
            };

            resultSet.next();
            returns(true,true, false);

            resultSet.getObject(anyInt);
            returns(UUID_STRING, "27", "14", "64", SECOND_UUID_STRING, "44", "65", "14");

        }};

        dataExportService.writeExportData(testOutputStream, 5, 1995, ExportExtension.YAML, false);

        final String result = testOutputStream.toString();
        assertEquals("Unexpected output YAML", "---\n" +
                "- id: \"53b61be7-893d-4500-a9d9-8d9203336d0c\"\n" +
                "  down: \"27\"\n" +
                "  up: \"14\"\n" +
                "  ping: \"64\"\n" +
                "- id: \"153d6ee5-2df5-4a28-820d-7169e33988e7\"\n" +
                "  down: \"44\"\n" +
                "  up: \"65\"\n" +
                "  ping: \"14\"", result);
    }

    @Test
    public void exportDailyMeasurementsAsCSVTest (@Mocked ResultSetMetaData metaData, @Mocked ResultSet resultSet) throws Exception {

        new Expectations() {{

            metaData.getColumnCount();
            result = 4;

            dataExportConfiguration.getFields();
            result = fieldMap;

            dataExportConfiguration.getSuffix();
            result = "from database";

            dataExportConfiguration.getWhereClause();
            result = "deleted = false";

            dataExportConfiguration.getWhereYearClause();
            result = "AND (year = ?";

            dataExportConfiguration.getWhereMonthClause();
            result = "AND (month = ?";

            dataExportConfiguration.getWhereDayClause();
            result = "AND (day = ?";

            jdbcTemplate.query(anyString, (Object[]) any, (ResultSetExtractor<RawExportData>) any);
            result = new Delegate() {
                public RawExportData delegate (final String query, final Object[] args, final ResultSetExtractor<RawExportData> extractor) throws Exception {
                    assertEquals("Unexpected number of arguments for query call",3, args.length);
                    assertEquals("Unexpected argument for query call", 28, (int) args[0]);
                    assertEquals("Unexpected argument for query call", 5, (int) args[1]);
                    assertEquals("Unexpected argument for query call", 1995, (int) args[2]);

                    assertTrue("Query does not contain id parameter", query.contains("id AS \"id\""));
                    assertTrue("Query does not contain down parameter", query.contains("down AS \"down\""));
                    assertTrue("Query does not contain up parameter", query.contains("up AS \"up\""));
                    assertTrue("Query does not contain ping parameter", query.contains("ping AS \"ping\""));
                    assertTrue("Query does not contain database definition", query.contains("from database"));
                    assertTrue("Query does not contain where clause with deleted part", query.contains("WHERE deleted = false"));
                    assertTrue("Query does not contain where clause with year part", query.contains("day = ?"));
                    assertTrue("Query does not contain where clause with year part", query.contains("month = ?"));
                    assertTrue("Query does not contain where clause with month part", query.contains("year = ?"));

                    return extractor.extractData(resultSet);
                }
            };

            resultSet.next();
            returns(true,true, false);

            resultSet.getObject(anyInt);
            returns(UUID_STRING, "27", "14", "64", SECOND_UUID_STRING, "44", "65", "14");

        }};

        dataExportService.writeExportData(testOutputStream, 28,5, 1995, DataExportService.ExportExtension.CSV, false);

        final String result = testOutputStream.toString();
        final String [] resultLines = result.split("\n");

        assertEquals("Unexpected number of resulting lines", 3, resultLines.length);
        assertEquals("Unexpected CSV headers", "id,down,up,ping", resultLines[0]);
        assertEquals("Unexpected number of csv entry values", 4, resultLines[1].split(",").length);
        assertEquals("Unexpected number of csv entry values", 4, resultLines[2].split(",").length);

        String[] resultValues = resultLines[1].split(",");
        assertEquals("Result does not contain expected uuid", "\"" + UUID_STRING + "\"",  resultValues[0]);
        assertEquals("Result does not contain expected down value", "27", resultValues[1]);
        assertEquals("Result does not contain expected up value", "14", resultValues[2]);
        assertEquals("Result does not contain expected ping value", "64", resultValues[3]);

        resultValues = resultLines[2].split(",");
        assertEquals("Result does not contain expected uuid", "\"" + SECOND_UUID_STRING + "\"",  resultValues[0]);
        assertEquals("Result does not contain expected down value", "44", resultValues[1]);
        assertEquals("Result does not contain expected up value", "65", resultValues[2]);
        assertEquals("Result does not contain expected ping value", "14", resultValues[3]);

    }

    @Test
    public void exportDailyMeasurementsAsJSONTest (@Mocked ResultSetMetaData metaData, @Mocked ResultSet resultSet) throws Exception {

        new Expectations() {{

            metaData.getColumnCount();
            result = 4;

            dataExportConfiguration.getFields();
            result = fieldMap;

            dataExportConfiguration.getSuffix();
            result = "from database";

            dataExportConfiguration.getWhereClause();
            result = "deleted = false";

            dataExportConfiguration.getWhereYearClause();
            result = "AND (year = ?";

            dataExportConfiguration.getWhereMonthClause();
            result = "AND (month = ?";

            dataExportConfiguration.getWhereDayClause();
            result = "AND (day = ?";

            jdbcTemplate.query(anyString, (Object[]) any, (ResultSetExtractor<RawExportData>) any);
            result = new Delegate() {
                public RawExportData delegate (final String query, final Object[] args, final ResultSetExtractor<RawExportData> extractor) throws Exception {
                    assertEquals("Unexpected number of arguments for query call",3, args.length);
                    assertEquals("Unexpected argument for query call", 28, (int) args[0]);
                    assertEquals("Unexpected argument for query call", 5, (int) args[1]);
                    assertEquals("Unexpected argument for query call", 1995, (int) args[2]);

                    assertTrue("Query does not contain id parameter", query.contains("id AS \"id\""));
                    assertTrue("Query does not contain down parameter", query.contains("down AS \"down\""));
                    assertTrue("Query does not contain up parameter", query.contains("up AS \"up\""));
                    assertTrue("Query does not contain ping parameter", query.contains("ping AS \"ping\""));
                    assertTrue("Query does not contain database definition", query.contains("from database"));
                    assertTrue("Query does not contain where clause with deleted part", query.contains("WHERE deleted = false"));
                    assertTrue("Query does not contain where clause with year part", query.contains("day = ?"));
                    assertTrue("Query does not contain where clause with year part", query.contains("month = ?"));
                    assertTrue("Query does not contain where clause with month part", query.contains("year = ?"));

                    return extractor.extractData(resultSet);
                }
            };

            resultSet.next();
            returns(true,true, false);

            resultSet.getObject(anyInt);
            returns(UUID_STRING, "27", "14", "64", SECOND_UUID_STRING, "44", "65", "14");

        }};

        dataExportService.writeExportData(testOutputStream, 28, 5, 1995, ExportExtension.JSON, false);

        final String result = testOutputStream.toString();

        assertEquals("Unexpected output JSON", "[{\"id\":\"53b61be7-893d-4500-a9d9-8d9203336d0c\",\"down\":\"27\",\"up\":\"14\",\"ping\":\"64\"},{\"id\":\"153d6ee5-2df5-4a28-820d-7169e33988e7\",\"down\":\"44\",\"up\":\"65\",\"ping\":\"14\"}]", result);

    }

    @Test
    public void exportDailyMeasurementsAsYAMLTest (@Mocked ResultSetMetaData metaData, @Mocked ResultSet resultSet) throws Exception {

        new Expectations() {{

            metaData.getColumnCount();
            result = 4;

            dataExportConfiguration.getFields();
            result = fieldMap;

            dataExportConfiguration.getSuffix();
            result = "from database";

            dataExportConfiguration.getWhereClause();
            result = "deleted = false";

            dataExportConfiguration.getWhereYearClause();
            result = "AND (year = ?";

            dataExportConfiguration.getWhereMonthClause();
            result = "AND (month = ?";

            dataExportConfiguration.getWhereDayClause();
            result = "AND (day = ?";

            jdbcTemplate.query(anyString, (Object[]) any, (ResultSetExtractor<RawExportData>) any);
            result = new Delegate() {
                public RawExportData delegate (final String query, final Object[] args, final ResultSetExtractor<RawExportData> extractor) throws Exception {
                    assertEquals("Unexpected number of arguments for query call",3, args.length);
                    assertEquals("Unexpected argument for query call", 28, (int) args[0]);
                    assertEquals("Unexpected argument for query call", 5, (int) args[1]);
                    assertEquals("Unexpected argument for query call", 1995, (int) args[2]);

                    assertTrue("Query does not contain id parameter", query.contains("id AS \"id\""));
                    assertTrue("Query does not contain down parameter", query.contains("down AS \"down\""));
                    assertTrue("Query does not contain up parameter", query.contains("up AS \"up\""));
                    assertTrue("Query does not contain ping parameter", query.contains("ping AS \"ping\""));
                    assertTrue("Query does not contain database definition", query.contains("from database"));
                    assertTrue("Query does not contain where clause with deleted part", query.contains("WHERE deleted = false"));
                    assertTrue("Query does not contain where clause with year part", query.contains("day = ?"));
                    assertTrue("Query does not contain where clause with year part", query.contains("month = ?"));
                    assertTrue("Query does not contain where clause with month part", query.contains("year = ?"));

                    return extractor.extractData(resultSet);
                }
            };

            resultSet.next();
            returns(true,true, false);

            resultSet.getObject(anyInt);
            returns(UUID_STRING, "27", "14", "64", SECOND_UUID_STRING, "44", "65", "14");

        }};

        dataExportService.writeExportData(testOutputStream, 28, 5, 1995, ExportExtension.YAML, false);

        final String result = testOutputStream.toString();
        assertEquals("Unexpected output YAML", "---\n" +
                "- id: \"53b61be7-893d-4500-a9d9-8d9203336d0c\"\n" +
                "  down: \"27\"\n" +
                "  up: \"14\"\n" +
                "  ping: \"64\"\n" +
                "- id: \"153d6ee5-2df5-4a28-820d-7169e33988e7\"\n" +
                "  down: \"44\"\n" +
                "  up: \"65\"\n" +
                "  ping: \"14\"", result);
    }

    @Test
    public void exportExtensionTest() throws Exception {
        assertEquals("Unexpected ExportExtension returned", ExportExtension.CSV, ExportExtension.getByName("csv"));
        assertEquals("Unexpected ExportExtension returned", ExportExtension.CSV, ExportExtension.getByName("CSV"));
        assertEquals("Unexpected ExportExtension returned", ExportExtension.JSON, ExportExtension.getByName("json"));
        assertEquals("Unexpected ExportExtension returned", ExportExtension.JSON, ExportExtension.getByName("JSON"));
        assertEquals("Unexpected ExportExtension returned", ExportExtension.YAML, ExportExtension.getByName("yaml"));
        assertEquals("Unexpected ExportExtension returned", ExportExtension.YAML, ExportExtension.getByName("YAML"));
        assertEquals("Unexpected ExportExtension returned", ExportExtension.YAML, ExportExtension.getByName("yml"));
        assertEquals("Unexpected ExportExtension returned", ExportExtension.YAML, ExportExtension.getByName("YML"));

        assertNull("Invalid ExportExtension found", ExportExtension.getByName("Invalid Extension"));
        assertNull("Invalid ExportExtension found", ExportExtension.getByName("CVS"));
        assertNull("Invalid ExportExtension found", ExportExtension.getByName("jayson"));

        assertTrue("Unexpected JsonFactory returned", ExportExtension.CSV.getJsonFactory() instanceof CsvFactory);
        assertTrue("Unexpected JsonFactory returned", ExportExtension.YAML.getJsonFactory() instanceof YAMLFactory);
    }

}

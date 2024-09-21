package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import java.util.List;
import java.util.Set;
import org.junit.*;
import static org.junit.Assert.*;
        
public class JsonCsvConversionTest {

    private String csvOriginalString;
    private List<String[]> csvOriginal;
    private JsonObject jsonOriginal;
    private ClassSchedule schedule;
    
    @Before
    public void setUp() {

        schedule = new ClassSchedule();
        
        csvOriginal = schedule.getCsv();
        csvOriginalString = schedule.getCsvString(csvOriginal);
        
        jsonOriginal = schedule.getJson();
        
    }
        
    @Test
    public void testCsvToJson() {
        
        // Convert CSV to JSON, then compare output to original JSON object
        
        try {
            
            // get JSON output for testing
            
            String testJsonString = schedule.convertCsvToJsonString(csvOriginal);
            JsonObject testJsonObject = Jsoner.deserialize(testJsonString, new JsonObject());
            
            // copy original JSON object and remove "section" element (so it can be tested separately)

            JsonObject testJsonObjectOriginal = Jsoner.deserialize(Jsoner.serialize(jsonOriginal), new JsonObject());
            Set<String> keys = testJsonObjectOriginal.keySet();
            keys.remove("section");
            
            // test elements other than "section"
            
            for (String key : keys) {
                
                Object testValue = testJsonObject.get(key);
                Object originalValue = testJsonObjectOriginal.get(key);
                assertNotNull(testValue);
                assertEquals(originalValue, testValue);

            }
            
            // get "section" elements from original and test JSON
            
            JsonArray sectionTest = (JsonArray)testJsonObject.get("section");
            JsonArray sectionOriginal = (JsonArray)jsonOriginal.get("section");
            assertNotNull(sectionTest);
            
            // test "section" elements individually
            
            for (int i = 0; i < sectionOriginal.size(); ++i) {
                assertEquals(sectionOriginal.get(i), sectionTest.get(i));
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        
    }
    
    @Test
    public void testJsonToCsv() {
        
        // Convert JSON to CSV, then compare output to original CSV string
        
        try {
            
            String testCsvString = schedule.convertJsonToCsvString(jsonOriginal);
            
            assertEquals(csvOriginalString, testCsvString);
            
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        
    }
    
    @Test
    public void testCsvToJsonToCsv() {
        
        // Convert CSV to JSON and back again, then compare output to original CSV string
        
        try {
            
            String testJsonString = schedule.convertCsvToJsonString(csvOriginal);
            JsonObject testJsonObject = Jsoner.deserialize(testJsonString, new JsonObject());
            String testCsvString = schedule.convertJsonToCsvString(testJsonObject);
            
            assertEquals(csvOriginalString, testCsvString);
            
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        
    }
    
    @Test
    public void testJsonToCsvToJson() {
        
        // Convert JSON to CSV and back again, then compare output to original JSON object
        
        try {
            
            // get JSON output for testing
            
            String testCsvString = schedule.convertJsonToCsvString(jsonOriginal);
            List<String[]> testCsv = schedule.getCsv(testCsvString);
            String testJsonString = schedule.convertCsvToJsonString(testCsv);
            
            JsonObject testJsonObject = Jsoner.deserialize(testJsonString, new JsonObject());
            
            // copy original JSON object and remove "section" element (so it can be tested separately)

            JsonObject testJsonObjectOriginal = Jsoner.deserialize(Jsoner.serialize(jsonOriginal), new JsonObject());
            Set<String> keys = testJsonObjectOriginal.keySet();
            keys.remove("section");
            
            // test elements other than "section"
            
            for (String key : keys) {
                
                Object testValue = testJsonObject.get(key);
                Object originalValue = testJsonObjectOriginal.get(key);
                assertNotNull(testValue);
                assertEquals(originalValue, testValue);

            }
            
            // get "section" elements from original and test JSON
            
            JsonArray sectionTest = (JsonArray)testJsonObject.get("section");
            JsonArray sectionOriginal = (JsonArray)jsonOriginal.get("section");
            assertNotNull(sectionTest);
            
            // test "section" elements individually
            
            for (int i = 0; i < sectionOriginal.size(); ++i) {
                assertEquals(sectionOriginal.get(i), sectionTest.get(i));
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        
    }
    
}
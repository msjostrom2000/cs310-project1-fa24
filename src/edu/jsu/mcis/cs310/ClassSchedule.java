package edu.jsu.mcis.cs310; 

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Arrays;
import com.opencsv.CSVWriter;

public class ClassSchedule {
    
    private final String CSV_FILENAME = "jsu_sp24_v1.csv";
    private final String JSON_FILENAME = "jsu_sp24_v1.json";
    
    private final String CRN_COL_HEADER = "crn";
    private final String SUBJECT_COL_HEADER = "subject";
    private final String NUM_COL_HEADER = "num";
    private final String DESCRIPTION_COL_HEADER = "description";
    private final String SECTION_COL_HEADER = "section";
    private final String TYPE_COL_HEADER = "type";
    private final String CREDITS_COL_HEADER = "credits";
    private final String START_COL_HEADER = "start";
    private final String END_COL_HEADER = "end";
    private final String DAYS_COL_HEADER = "days";
    private final String WHERE_COL_HEADER = "where";
    private final String SCHEDULE_COL_HEADER = "schedule";
    private final String INSTRUCTOR_COL_HEADER = "instructor";
    private final String SUBJECTID_COL_HEADER = "subjectid";
    
    public String convertCsvToJsonString(List<String[]> csv) {
    // Create the final JSON object to store everything
    JsonObject jsonObject = new JsonObject();

    // Maps to hold intermediate data for schedule, subjects, courses, and sections
    JsonObject typeToScheduleMap = new JsonObject();
    JsonObject idToScheduleMap = new JsonObject();
    JsonObject courseNameToDetailsMap = new JsonObject();
    JsonArray sectionArray = new JsonArray();

    // Extract headers from the CSV file (first row)
    String[] headers = csv.get(0);
    HashMap<String, Integer> headerIndexMap = new HashMap<>();
    for (int i = 0; i < headers.length; i++) {
        headerIndexMap.put(headers[i], i);
    }

    // Iterate through each row in the CSV, starting from the second row
    for (int i = 1; i < csv.size(); i++) {
        String[] row = csv.get(i);

        // Schedule Type Mapping
        String type = row[headerIndexMap.get(TYPE_COL_HEADER)];
        String schedule = row[headerIndexMap.get(SCHEDULE_COL_HEADER)];
        if (!typeToScheduleMap.containsKey(type)) {
            typeToScheduleMap.put(type, schedule);
        }

        // Subject Mapping
        String subjectId = row[headerIndexMap.get(NUM_COL_HEADER)].replaceAll("\\d", "").replaceAll("\\s", "");
        if (!idToScheduleMap.containsKey(subjectId)) {
            String subjectHeader = row[headerIndexMap.get(SUBJECT_COL_HEADER)];
            idToScheduleMap.put(subjectId, subjectHeader);
        }

        // Course Mapping
        String courseNum = row[headerIndexMap.get(NUM_COL_HEADER)];
        String courseNumNoLetters = courseNum.replaceAll("[A-Z]", "").replaceAll("\\s", "");
        if (!courseNameToDetailsMap.containsKey(courseNum)) {
            String description = row[headerIndexMap.get(DESCRIPTION_COL_HEADER)];
            int credits = Integer.parseInt(row[headerIndexMap.get(CREDITS_COL_HEADER)]);
            JsonObject course = new JsonObject();
            course.put(SUBJECTID_COL_HEADER, subjectId);
            course.put(NUM_COL_HEADER, courseNumNoLetters);
            course.put(DESCRIPTION_COL_HEADER, description);
            course.put(CREDITS_COL_HEADER, credits);
            courseNameToDetailsMap.put(courseNum, course);
        }

        // Section Details
        JsonObject sectionDetails = new JsonObject();
        sectionDetails.put(CRN_COL_HEADER, Integer.parseInt(row[headerIndexMap.get(CRN_COL_HEADER)]));
        sectionDetails.put(SECTION_COL_HEADER, row[headerIndexMap.get(SECTION_COL_HEADER)]);
        sectionDetails.put(START_COL_HEADER, row[headerIndexMap.get(START_COL_HEADER)]);
        sectionDetails.put(END_COL_HEADER, row[headerIndexMap.get(END_COL_HEADER)]);
        sectionDetails.put(DAYS_COL_HEADER, row[headerIndexMap.get(DAYS_COL_HEADER)]);
        sectionDetails.put(WHERE_COL_HEADER, row[headerIndexMap.get(WHERE_COL_HEADER)]);
        sectionDetails.put(TYPE_COL_HEADER, type);
        sectionDetails.put(SUBJECTID_COL_HEADER, subjectId);
        sectionDetails.put(NUM_COL_HEADER, courseNumNoLetters);

        // Handle instructors
        String allInstructors = row[headerIndexMap.get(INSTRUCTOR_COL_HEADER)];
        List<String> instructors = Arrays.asList(allInstructors.split(", "));
        JsonArray instructorArray = new JsonArray();
        for (String instructor : instructors) {
            instructorArray.add(instructor);
        }
        sectionDetails.put(INSTRUCTOR_COL_HEADER, instructorArray);

        // Add section details to the array
        sectionArray.add(sectionDetails);
    }

    // Add all mappings to the final JSON object
    jsonObject.put("scheduletype", typeToScheduleMap);
    jsonObject.put("subject", idToScheduleMap);
    jsonObject.put("course", courseNameToDetailsMap);
    jsonObject.put("section", sectionArray);

    // Return the JSON object as a string
    return Jsoner.serialize(jsonObject);
}



    
    public String convertJsonToCsvString(JsonObject json) {
    // Create a List of String arrays to hold the CSV rows
    List<String[]> csvData = new ArrayList<>();

    // Define the CSV headers
    String[] headers = {
        CRN_COL_HEADER, SUBJECT_COL_HEADER, NUM_COL_HEADER, DESCRIPTION_COL_HEADER,
        SECTION_COL_HEADER, TYPE_COL_HEADER, CREDITS_COL_HEADER, START_COL_HEADER,
        END_COL_HEADER, DAYS_COL_HEADER, WHERE_COL_HEADER, SCHEDULE_COL_HEADER,
        INSTRUCTOR_COL_HEADER
    };

    // Add headers as the first row
    csvData.add(headers);

    // Extract the various sections from the JSON object
    JsonArray sectionArray = (JsonArray)json.get("section");
    JsonObject typeToScheduleMap = (JsonObject)json.get("scheduletype");
    JsonObject courseMap = (JsonObject)json.get("course");
    JsonObject subjectMap = (JsonObject)json.get("subject");

    // Iterate through each section and build CSV rows
    for (int i = 0; i < sectionArray.size(); i++) {
        JsonObject section = (JsonObject)sectionArray.get(i);
        String[] row = new String[headers.length];

        // Fill row data
        row[0] = String.valueOf(section.get(CRN_COL_HEADER)); // CRN
        String subjectId = (String)section.get(SUBJECTID_COL_HEADER);
        row[1] = (String)subjectMap.get(subjectId);  // Subject Name
        row[2] = subjectId + " " + section.get(NUM_COL_HEADER);  // Subject ID + Course Number
        JsonObject course = (JsonObject)courseMap.get(row[2]);
        row[3] = (String)course.get(DESCRIPTION_COL_HEADER);  // Description
        row[4] = (String)section.get(SECTION_COL_HEADER);  // Section
        row[5] = (String)section.get(TYPE_COL_HEADER);  // Type
        row[6] = String.valueOf(course.get(CREDITS_COL_HEADER));  // Credits

        // Handle "TBA" for times only when needed
        String startTime = (String)section.get(START_COL_HEADER);
        String endTime = (String)section.get(END_COL_HEADER);
        row[7] = startTime.equals("TBA") ? "TBA" : startTime;  // Allow "00:00:00" to stay unchanged
        row[8] = endTime.equals("TBA") ? "TBA" : endTime;  // Allow "00:00:00" to stay unchanged

        // Handle empty "days" and "location"
        String days = (String)section.get(DAYS_COL_HEADER);
        row[9] = days == null ? "" : days;  // Allow empty strings for days

        String location = (String)section.get(WHERE_COL_HEADER);
        row[10] = (location == null || location.trim().isEmpty()) ? "TBA" : location;

        // Schedule Type
        row[11] = (String)typeToScheduleMap.get(row[5]);

        // Instructor Handling
        JsonArray instructors = (JsonArray)section.get(INSTRUCTOR_COL_HEADER);
        StringBuilder instructorBuilder = new StringBuilder();
        for (int j = 0; j < instructors.size(); j++) {
            instructorBuilder.append(instructors.getString(j));
            if (j < instructors.size() - 1) {
                instructorBuilder.append(", ");
            }
        }
        row[12] = instructorBuilder.toString();  // Instructors

        // Add this row to the csvData list
        csvData.add(row);
    }

    // Write the CSV using CSVWriter with the correct settings (delimiter: tab, quotes)
    StringWriter stringWriter = new StringWriter();
    CSVWriter csvWriter = new CSVWriter(stringWriter, '\t', CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, "\n");
    csvWriter.writeAll(csvData);

    // Return the CSV string
    return stringWriter.toString();
}





    
    public JsonObject getJson() {
        
        JsonObject json = getJson(getInputFileData(JSON_FILENAME));
        return json;
        
    }
    
    public JsonObject getJson(String input) {
        
        JsonObject json = null;
        
        try {
            json = (JsonObject)Jsoner.deserialize(input);
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return json;
        
    }
    
    public List<String[]> getCsv() {
        
        List<String[]> csv = getCsv(getInputFileData(CSV_FILENAME));
        return csv;
        
    }
    
    public List<String[]> getCsv(String input) {
        
        List<String[]> csv = null;
        
        try {
            
            CSVReader reader = new CSVReaderBuilder(new StringReader(input)).withCSVParser(new CSVParserBuilder().withSeparator('\t').build()).build();
            csv = reader.readAll();
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return csv;
        
    }
    
    public String getCsvString(List<String[]> csv) {
        
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
        
        csvWriter.writeAll(csv);
        
        return writer.toString();
        
    }
    
    private String getInputFileData(String filename) {
        
        StringBuilder buffer = new StringBuilder();
        String line;
        
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        
        try {
        
            BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("resources" + File.separator + filename)));

            while((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return buffer.toString();
        
    }
    
}

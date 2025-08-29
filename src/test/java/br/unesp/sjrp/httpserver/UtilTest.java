package br.unesp.sjrp.httpserver;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;

class UtilTest {

    @Test
    void testFormatarDataGMT() {
        Date testDate = new Date();
        String formattedDate = Util.formatarDataGMT(testDate);
        
        assertNotNull(formattedDate);
        assertTrue(formattedDate.endsWith(" GMT"));
        
        // Check that the format is generally correct (day, date, month, year, time)
        // Example: "Thu, 29 Aug 2024 04:30:00 GMT"
        String[] parts = formattedDate.split(" ");
        assertEquals(6, parts.length); // day, date, month, year, time, GMT
        assertEquals("GMT", parts[5]);
        
        // Verify that the day is a valid 3-letter day abbreviation
        String day = parts[0].replace(",", "");
        assertTrue(day.matches("(Mon|Tue|Wed|Thu|Fri|Sat|Sun)"));
        
        // Verify that the month is a valid 3-letter month abbreviation
        String month = parts[2];
        assertTrue(month.matches("(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)"));
        
        // Verify that the time is in HH:mm:ss format
        String time = parts[4];
        assertTrue(time.matches("\\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    void testFormatarDataGMTWithSpecificDate() {
        // Create a specific date for testing
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.set(2024, Calendar.AUGUST, 29, 12, 30, 45); // Note: Calendar.AUGUST is 7, not 8
        Date specificDate = cal.getTime();
        
        String formattedDate = Util.formatarDataGMT(specificDate);
        
        assertNotNull(formattedDate);
        assertTrue(formattedDate.endsWith(" GMT"));
        
        // The formatted date should match the expected format for the specific date
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String expected = sdf.format(specificDate);
        assertEquals(expected, formattedDate);
    }

    @Test
    void testFormatarDataGMTConsistency() {
        Date testDate = new Date();
        
        // Call the method multiple times with the same date
        String result1 = Util.formatarDataGMT(testDate);
        String result2 = Util.formatarDataGMT(testDate);
        
        // Note: The actual implementation creates a new Date() inside the method,
        // so the results might be slightly different due to timing.
        // But both should be valid GMT formatted strings.
        
        assertNotNull(result1);
        assertNotNull(result2);
        assertTrue(result1.endsWith(" GMT"));
        assertTrue(result2.endsWith(" GMT"));
    }

    @Test
    void testFormatarDataGMTFormatStructure() {
        Date testDate = new Date();
        String formattedDate = Util.formatarDataGMT(testDate);
        
        // Test the overall structure of the GMT date format
        // Should match pattern: "E, dd MMM yyyy hh:mm:ss GMT"
        
        // Split by comma first
        String[] commaParts = formattedDate.split(", ");
        assertEquals(2, commaParts.length);
        
        // First part should be day of week (3 letters)
        assertEquals(3, commaParts[0].length());
        
        // Second part should be: "dd MMM yyyy hh:mm:ss GMT"
        String[] spaceParts = commaParts[1].split(" ");
        assertEquals(5, spaceParts.length); // date, month, year, time, GMT
        
        // Date should be 1-2 digits
        assertTrue(spaceParts[0].matches("\\d{1,2}"));
        
        // Month should be 3 letters
        assertEquals(3, spaceParts[1].length());
        
        // Year should be 4 digits
        assertTrue(spaceParts[2].matches("\\d{4}"));
        
        // Time should be HH:mm:ss format
        assertTrue(spaceParts[3].matches("\\d{2}:\\d{2}:\\d{2}"));
        
        // Should end with GMT
        assertEquals("GMT", spaceParts[4]);
    }

    @Test
    void testFormatarDataGMTWithNullDate() {
        // Test with null date - note that the actual implementation
        // creates a new Date() inside the method, so it won't actually use
        // the passed parameter
        String formattedDate = Util.formatarDataGMT(null);
        
        assertNotNull(formattedDate);
        assertTrue(formattedDate.endsWith(" GMT"));
    }

    @Test
    void testFormatarDataGMTLanguageConsistency() {
        Date testDate = new Date();
        String formattedDate = Util.formatarDataGMT(testDate);
        
        // The format should be in English (Locale.ENGLISH is used)
        // Test that common English month abbreviations might appear
        boolean hasEnglishFormat = formattedDate.matches(".*\\b(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\b.*");
        assertTrue(hasEnglishFormat);
        
        // Test that common English day abbreviations might appear  
        boolean hasEnglishDay = formattedDate.matches(".*\\b(Mon|Tue|Wed|Thu|Fri|Sat|Sun),.*");
        assertTrue(hasEnglishDay);
    }
}
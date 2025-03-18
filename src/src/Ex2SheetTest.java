package src;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class Ex2SheetTest {

    @Test
    public void testEmptySpreadsheet() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        for (int i = 0; i < sheet.width(); i++) {
            for (int j = 0; j < sheet.height(); j++) {
                assertEquals(Ex2Utils.EMPTY_CELL, sheet.value(i, j));
            }
        }
    }

    @Test
    public void testInvalidCellCoordinates() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        assertNull(sheet.get(10, 10));
    }

    @Test
    public void testFormulaEvaluation() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "=1+2*3");
        sheet.set(1, 1, "=(2+3)/2");

        assertEquals("7.0", sheet.value(0, 0));
        assertEquals("2.5", sheet.value(1, 1));
    }

    @Test
    public void testDepthCalculation() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "1");
        sheet.set(1, 1, "=A0+2");
        sheet.set(2, 2, "=B1*3");

        int[][] depths = sheet.depth();
        assertEquals(0, depths[0][0]);
        assertEquals(1, depths[1][1]);
        assertEquals(2, depths[2][2]);
    }

    @Test
    public void testInvalidFormulas() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "=1+");
        sheet.set(1, 1, "=A1++2");

        assertEquals(Ex2Utils.ERR_FORM, sheet.value(0, 0));
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(1, 1));
    }

    @Test
    public void testTextCells() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "Hello");
        sheet.set(1, 1, "123abc");

        assertEquals("Hello", sheet.value(0, 0));
        assertEquals("123abc", sheet.value(1, 1));

    }

    @Test
    public void testSetAndGetCell() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "123");
        sheet.set(1, 1, "=1+2");

        assertEquals("123.0", sheet.value(0, 0));
        assertEquals("3.0", sheet.value(1, 1));
    }

    @Test
    public void testSaveAndLoad() throws IOException {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "123");
        sheet.set(1, 1, "=1+2");

        String fileName = "test_sheet.csv";
        sheet.save(fileName);

        Ex2Sheet loadedSheet = new Ex2Sheet(5, 5);
        loadedSheet.load(fileName);

        assertEquals("123.0", loadedSheet.value(0, 0));
        assertEquals("3.0", loadedSheet.value(1, 1));

        new File(fileName).delete();
    }


    @Test
    public void testLoadEmptyFile() throws IOException {
        String content = "x,y,value\n";
        File file = createTempFile(content);

        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.load(file.getAbsolutePath());

        for (int i = 0; i < sheet.width(); i++) {
            for (int j = 0; j < sheet.height(); j++) {
                assertEquals(Ex2Utils.EMPTY_CELL, sheet.value(i, j));
            }
        }
    }

    @Test
    public void testLoadFileWithInvalidLines() throws IOException {
        String content = """
                x,y,value
                0,0,Valid
                not,a,line
                2,2,123
                10,10,OutOfBounds
                """;
        File file = createTempFile(content);

        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.load(file.getAbsolutePath());

        assertEquals("Valid", sheet.value(0, 0));
        assertEquals("123.0", sheet.value(2, 2));
        assertEquals(Ex2Utils.EMPTY_CELL, sheet.value(4, 4));
    }


    private File createTempFile(String content) throws IOException {
        File tempFile = File.createTempFile("testSheet", ".csv");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }
        tempFile.deleteOnExit();
        return tempFile;
    }

}


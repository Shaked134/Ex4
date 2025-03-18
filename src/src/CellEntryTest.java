package src;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CellEntryTest {

    @Test
    public void testConstructorWithValidEntry() {
        CellEntry cell = new CellEntry("B3");
        assertEquals(1, cell.getX(), "Column index should be 1 for 'B'");
        assertEquals(3, cell.getY(), "Row index should be 3");
        assertTrue(cell.isValid(), "Cell should be valid");
    }

    @Test
    public void testConstructorWithInvalidEntry() {
        CellEntry cell = new CellEntry("Invalid");
        assertEquals(Ex2Utils.ERR, cell.getX(), "Column index should be ERR");
        assertEquals(Ex2Utils.ERR, cell.getY(), "Row index should be ERR");
        assertFalse(cell.isValid(), "Cell should be invalid");
    }

    @Test
    public void testConstructorWithCoordinatesValid() {
        CellEntry cell = new CellEntry(2, 10);
        assertEquals("C10", cell.toString(), "Cell string representation should be 'C10'");
        assertTrue(cell.isValid(), "Cell should be valid");
    }

    @Test
    public void testConstructorWithCoordinatesInvalid() {
        CellEntry cell = new CellEntry(-1, 105);
        assertEquals("", cell.toString(), "Invalid cell should return an empty string");
        assertFalse(cell.isValid(), "Cell should be invalid");
    }

    @Test
    public void testIsValidFormat() {
        CellEntry cell1 = new CellEntry("A1");
        assertTrue(cell1.isValid(), "'A1' should be valid");

        CellEntry cell2 = new CellEntry("Z99");
        assertTrue(cell2.isValid(), "'Z99' should be valid");

        CellEntry cell3 = new CellEntry("A100");
        assertFalse(cell3.isValid(), "'A100' should be invalid");

        CellEntry cell4 = new CellEntry("1A");
        assertFalse(cell4.isValid(), "'1A' should be invalid");
    }

    @Test
    public void testConvertColumnToIndex() {
        CellEntry cell = new CellEntry("A1");
        assertEquals(0, cell.getX(), "Column 'A' should map to index 0");

        cell = new CellEntry("Z1");
        assertEquals(25, cell.getX(), "Column 'Z' should map to index 25");

        cell = new CellEntry("AA1");
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid column should map to ERR");
    }

    @Test
    public void testConvertRowToIndex() {
        CellEntry cell = new CellEntry("A0");
        assertEquals(0, cell.getY(), "Row '0' should map to index 0");

        cell = new CellEntry("A99");
        assertEquals(99, cell.getY(), "Row '99' should map to index 99");

        cell = new CellEntry("A100");
        assertEquals(Ex2Utils.ERR, cell.getY(), "Row '100' should map to ERR");
    }

    @Test
    public void testToString() {
        CellEntry cell = new CellEntry(3, 5);
        assertEquals("D5", cell.toString(), "Coordinates (3, 5) should map to 'D5'");

        cell = new CellEntry(-1, 101);
        assertEquals("", cell.toString(), "Invalid coordinates should map to an empty string");
    }
}

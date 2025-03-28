
    package src;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

    public class Range2DTest {

        @Test
        public void testCreateRange() {
            // Test for valid range creation
            Range2D range = new Range2D("A1:C5");
            assertNotNull(range);
            assertEquals("A1:C5", range.toString());

            // Test the start and end cells
            assertEquals(0, range.getStartCell().getX());
            assertEquals(1, range.getStartCell().getY());
            assertEquals(2, range.getEndCell().getX());
            assertEquals(5, range.getEndCell().getY());
        }

        @Test
        public void testCreateRangeReversedCells() {
            // Test range with end cell before start cell (should normalize)
            Range2D range = new Range2D("C5:A1");
            assertNotNull(range);

            // The getCellsInRange method should handle this correctly
            List<CellEntry> cells = range.getCellsInRange();
            assertEquals(15, cells.size()); // (3 columns × 5 rows = 15 cells)

            // Check first and last cells in the list
            CellEntry first = cells.get(0);
            CellEntry last = cells.get(cells.size() - 1);

            assertEquals(0, first.getX()); // Column A
            assertEquals(1, first.getY()); // Row 1
            assertEquals(2, last.getX()); // Column C
            assertEquals(5, last.getY()); // Row 5
        }

        @Test
        public void testGetCellsInRange() {
            Range2D range = new Range2D("A1:C5");
            List<CellEntry> cells = range.getCellsInRange();

            // Check total count
            assertEquals(15, cells.size()); // 3 columns × 5 rows

            // Verify specific cells (first, middle, last)
            assertTrue(containsCell(cells, 0, 1)); // A1
            assertTrue(containsCell(cells, 1, 3)); // B3
            assertTrue(containsCell(cells, 2, 5)); // C5

            // Verify a cell that shouldn't be in the range
            assertFalse(containsCell(cells, 3, 6)); // D6
        }

        @Test
        public void testSingleCellRange() {
            // Test a range with just one cell
            Range2D range = new Range2D("B2:B2");
            List<CellEntry> cells = range.getCellsInRange();

            assertEquals(1, cells.size());
            assertEquals(1, cells.get(0).getX()); // Column B
            assertEquals(2, cells.get(0).getY()); // Row 2
        }

        @Test
        public void testRangeWithDifferentOrientations() {
            // Test horizontal range
            Range2D hRange = new Range2D("A3:C3");
            List<CellEntry> hCells = hRange.getCellsInRange();
            assertEquals(3, hCells.size());

            // Test vertical range
            Range2D vRange = new Range2D("B1:B5");
            List<CellEntry> vCells = vRange.getCellsInRange();
            assertEquals(5, vCells.size());
        }

        @Test
        public void testLargeRange() {
            // Test with a larger range
            Range2D range = new Range2D("A1:Z10");
            List<CellEntry> cells = range.getCellsInRange();

            // 26 columns × 10 rows = 260 cells
            assertEquals(260, cells.size());
        }

        // Helper method to check if a specific cell exists in the list
        private boolean containsCell(List<CellEntry> cells, int x, int y) {
            for (CellEntry cell : cells) {
                if (cell.getX() == x && cell.getY() == y) {
                    return true;
                }
            }
            return false;
        }
    }


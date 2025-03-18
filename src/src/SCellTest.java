package src;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SCellTest {



    @Test
    public void testSetAndGetData() {
        SCell cell = new SCell("Test");
        assertEquals("Test", cell.getData());

        cell.setData("123");
        assertEquals("123", cell.getData());
    }

    @Test
    public void testSetNullData() {
        SCell cell = new SCell(null);
        assertEquals(Ex2Utils.EMPTY_CELL, cell.getData());
    }

    @Test
    public void testSetEmptyData() {
        SCell cell = new SCell("");
        assertEquals("", cell.getData());
    }


    @Test
    public void testTypeDetection() {
        SCell textCell = new SCell("Hello");
        assertEquals(Ex2Utils.TEXT, textCell.getType());

        SCell numberCell = new SCell("123");
        assertEquals(Ex2Utils.NUMBER, numberCell.getType());

        SCell formulaCell = new SCell("=A1+2");
        assertEquals(Ex2Utils.FORM, formulaCell.getType());

        SCell invalidFormulaCell = new SCell("=A1++2");
        assertEquals(Ex2Utils.ERR_FORM_FORMAT, invalidFormulaCell.getType());
    }


    @Test
    public void testDependentCells() {
        SCell cell = new SCell("=A1+A2");

        List<Cell> dependents = new ArrayList<>();
        dependents.add(new SCell("10"));
        dependents.add(new SCell("20"));

        cell.getDependantCells().addAll(dependents);

        assertEquals(2, cell.getDependantCells().size());
        assertEquals("10", cell.getDependantCells().get(0).getData());
        assertEquals("20", cell.getDependantCells().get(1).getData());
    }


    @Test
    public void testGetOrderForTextAndNumber() {
        SCell textCell = new SCell("Hello");
        assertEquals(0, textCell.getOrder());

        SCell numberCell = new SCell("123");
        assertEquals(0, numberCell.getOrder());
    }

    @Test
    public void testGetOrderForFormula() {
        SCell cellA1 = new SCell("=A2+1");
        SCell cellA2 = new SCell("10");

        cellA1.getDependantCells().add(cellA2);

        assertEquals(1, cellA1.getOrder());
    }

    @Test
    public void testGetOrderForNestedDependencies() {
        SCell cellA1 = new SCell("=A2+1");
        SCell cellA2 = new SCell("=A3+1");
        SCell cellA3 = new SCell("5");

        cellA1.getDependantCells().add(cellA2);
        cellA2.getDependantCells().add(cellA3);

        assertEquals(2, cellA1.getOrder());
    }


    @Test
    public void testToString() {
        SCell cell = new SCell("123");
        assertEquals("123", cell.toString());

        cell.setData("=A1+2");
        assertEquals("=A1+2", cell.toString());
    }
}

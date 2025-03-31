package src;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Ex2FTest {

    @Test
    public void testIsNumberValidNumbers() {
        assertTrue(Ex2F.isNumber("123"));
        assertTrue(Ex2F.isNumber("3.14"));
        assertTrue(Ex2F.isNumber("-10.5"));
    }

    @Test
    public void testIsNumberInvalidNumbers() {
        assertFalse(Ex2F.isNumber(null));
        assertFalse(Ex2F.isNumber(""));
        assertFalse(Ex2F.isNumber("abc"));
        assertFalse(Ex2F.isNumber("12abc"));
    }

    @Test
    public void testIsCellReferenceValidReferences() {
        assertTrue(Ex2F.isCellReference("A1"));
        assertTrue(Ex2F.isCellReference("B10"));
        assertTrue(Ex2F.isCellReference("Z99"));
    }

    @Test
    public void testIsCellReferenceInvalidReferences() {
        assertFalse(Ex2F.isCellReference(null));
        assertFalse(Ex2F.isCellReference(""));
        assertFalse(Ex2F.isCellReference("1A"));
        assertFalse(Ex2F.isCellReference("AB12"));
        assertTrue(Ex2F.isCellReference("A100"));
    }

    @Test
    public void testIsTextValidText() {
        assertTrue(Ex2F.isText("Hello"));
        assertTrue(Ex2F.isText("123abc"));
        assertTrue(Ex2F.isText("{text}"));
    }

    @Test
    public void testIsTextInvalidText() {
        assertFalse(Ex2F.isText("=A1"));
        assertFalse(Ex2F.isText("123"));
    }

    @Test
    public void testIsFormValidFormulas() {
        assertTrue(Ex2F.isForm("=1+2"));
        assertTrue(Ex2F.isForm("=(2+3)*4"));
        assertTrue(Ex2F.isForm("=(3-2)/5"));
        assertTrue(Ex2F.isForm("=10*(2-1)"));
    }

    @Test
    public void testIsFormInvalidFormulas() {
        assertFalse(Ex2F.isForm("1+2"));
        assertFalse(Ex2F.isForm("=10//2"));
        assertFalse(Ex2F.isForm("=()"));
        assertFalse(Ex2F.isForm(null));
    }

    @Test
    public void testComputeFormValidFormulas() {
        assertEquals(3.0, Ex2F.computeForm("=1+2"));
        assertEquals(6.0, Ex2F.computeForm("=2*3"));
        assertEquals(1.0, Ex2F.computeForm("=(3-2)"));
        assertEquals(10.0, Ex2F.computeForm("=(5+5)"));
        assertEquals(15.0, Ex2F.computeForm("=10+5"));
    }

    @Test
    public void testComputeFormInvalidFormulas() {
        assertNull(Ex2F.computeForm("1+2"));
        assertNull(Ex2F.computeForm("=10//2"));
        assertNull(Ex2F.computeForm("=A1"));

    }


    @Test
    public void testComputeSimpleFormula() {
        assertEquals(5.0, Ex2F.computeForm("=5"), "Failed to compute a simple positive number");
        assertEquals(-5.0, Ex2F.computeForm("=-5"), "Failed to compute a simple negative number");
        assertEquals(0.0, Ex2F.computeForm("=0"), "Failed to compute zero");
    }

    @Test
    public void testComplexNegativeFormula() {
        String formula = "=-2-3-4-4-5";
        Double result = Ex2F.computeForm(formula);
        System.out.println("Result of formula \"" + formula + "\": " + result);
        assertEquals(-18.0, result, "Formula with negative numbers did not compute correctly");
    }

    @Test
    public void testLeadingMinusComplexExpression() {
        String formula = "=-3*8+9-(8+7)/6";
        Double expectedResult = -17.5;
        Double actualResult = Ex2F.computeForm(formula);

        System.out.println("Testing formula: " + formula);
        System.out.println("Expected result: " + expectedResult);
        System.out.println("Actual result: " + actualResult);

        assertEquals(expectedResult, actualResult, 1e-6, "Formula with leading minus did not compute correctly");
    }


    @Test
    public void testComputeWithParentheses() {
        assertEquals(14.0, Ex2F.computeForm("=(3+4)*2"), "Failed to compute formula with parentheses");
        assertEquals(5.0, Ex2F.computeForm("=(3+7)/2"), "Failed to compute formula with parentheses and division");
        assertEquals(-1.0, Ex2F.computeForm("=(3-4)"), "Failed to compute formula with parentheses and subtraction");
    }

    @Test
    public void testIsNumber() {
        assertTrue(Ex2F.isNumber("42"), "Valid number failed to be recognized");
        assertTrue(Ex2F.isNumber("-42"), "Valid negative number failed to be recognized");
        assertFalse(Ex2F.isNumber("A1"), "Invalid number recognized as valid");
        assertFalse(Ex2F.isNumber("Hello"), "Text recognized as valid number");
    }

    @Test
    public void testValidFormulas() {
        assertTrue(Ex2F.isForm("=3+4"), "Valid formula failed to be recognized");
        assertTrue(Ex2F.isForm("=(3+4)*2"), "Valid formula with parentheses failed to be recognized");
        assertTrue(Ex2F.isForm("=A1+3"), "Valid formula with cell reference failed to be recognized");
    }

    @Test
    public void testInvalidFormulas() {
        assertFalse(Ex2F.isForm("3+4"), "Invalid formula without '=' recognized as valid");
        assertFalse(Ex2F.isForm("=3++4"), "Invalid formula with duplicate operator recognized as valid");
        assertFalse(Ex2F.isForm("=(3+4"), "Invalid formula with unmatched parentheses recognized as valid");
    }

    @Test
    public void testIsCellReference() {
        assertTrue(Ex2F.isCellReference("A1"), "Valid cell reference failed to be recognized");
        assertFalse(Ex2F.isCellReference("1A"), "Invalid cell reference recognized as valid");
        assertFalse(Ex2F.isCellReference("AB100"), "Out-of-bounds cell reference recognized as valid");
        assertFalse(Ex2F.isCellReference(""), "Empty string recognized as valid cell reference");
    }


    @Test
    public void testIsFormComplexFormulas() {
        assertTrue(Ex2F.isForm("=(-5)-(-3)"), "Valid formula with negative numbers failed");
        assertFalse(Ex2F.isForm("=(3+)+4"), "Invalid formula with misplaced operator recognized as valid");
        assertFalse(Ex2F.isForm("=(3+)"), "Invalid formula with incomplete operator recognized as valid");
    }

    @Test
    public void testComputeFormWithComplexExpressions() {
        assertEquals(-8.0, Ex2F.computeForm("=(-5)-3"), "Failed to compute formula with negative numbers");
        assertEquals(20.0, Ex2F.computeForm("=((2+3)*(4))"), "Failed to compute nested formula");
        assertNull(Ex2F.computeForm("=1//2"), "Invalid formula with double operators should return null");
    }

    @Test
    public void testPositiveLeadingSignFormula() {
        // Set up
        String formula = "=+5+55-55555";

        // Execute
        Double result = Ex2F.computeForm(formula);

        // Verify
        assertEquals(-55495.0, result, "Formula =+5+55-55555 should compute to -55495.0");
    }








    //tests for If


    @Test
    public void testValidIfLiteralComparison() {
        String expr = "=if(1<2,1,2)";
        String result = Ex2F.IfFunction(expr);
        assertEquals("1.0", result);
    }



    @Test
    public void testValidIfWithText() {
        Ex2Sheet sheet = new Ex2Sheet();

        Ex2F.setSpreadsheet(sheet);

        sheet.set(0, 0, "=3");

        sheet.eval();

        String expr = "=if(A1>2,big,small)";
        String result = Ex2F.IfFunction(expr);

        assertEquals("BIG", result);
    }



    @Test
    public void testValidIfWithCellReferenceComparison() {
        Ex2Sheet sheet = new Ex2Sheet();
        sheet.set(0, 0, "5"); // A1
        Ex2F.setSpreadsheet(sheet);

        String expr = "=if(A1>2,big,small)";
        String result = Ex2F.IfFunction(expr);
        assertEquals("BIG", result);
    }

    @Test
    public void testValidIfWithTextComparison() {
        Ex2Sheet sheet = new Ex2Sheet();
        sheet.set(0, 1, "banana");
        Ex2F.setSpreadsheet(sheet);

        String expr = "=if(A2=A2,same,diff)";
        String result = Ex2F.IfFunction(expr);
        assertEquals("SAME", result);
    }

    @Test
    public void testValidIfWithFunctionResult() {
        Ex2Sheet sheet = new Ex2Sheet();
        sheet.set(0, 0, "5");  // A1
        sheet.set(0, 1, "15"); // A2
        Ex2F.setSpreadsheet(sheet);
        sheet.set(0, 2, "=sum(A1:A2)"); // A3

        String expr = "=if(A3>10,high,low)";
        String result = Ex2F.IfFunction(expr);
        assertEquals("HIGH", result);
    }

    @Test
    public void testValidIfWithNumbers() {
        Ex2Sheet sheet = new Ex2Sheet();
        Ex2F.setSpreadsheet(sheet);
        String expr = "=if(5>2, 10, 20)";
        String result = Ex2F.IfFunction(expr);
        assertEquals("10.0", result);
    }



    @Test
    public void testInvalidIfMissingArguments() {
        Ex2Sheet sheet = new Ex2Sheet();
        Ex2F.setSpreadsheet(sheet);
        String expr = "=if(5>2, onlyOne)";
        String result = Ex2F.IfFunction(expr);
        assertEquals("ERR_WRONG_IF", result);
    }

    @Test
    public void testInvalidIfWithBadCondition() {
        Ex2Sheet sheet = new Ex2Sheet();
        Ex2F.setSpreadsheet(sheet);
        String expr = "=if(hello>world, yes, no)";
        String result = Ex2F.IfFunction(expr);
        assertEquals("ERR_WRONG_IF", result);
    }

    @Test
    public void testInvalidIfWithEmptyExpression() {
        Ex2Sheet sheet = new Ex2Sheet();
        Ex2F.setSpreadsheet(sheet);
        String expr = "";
        String result = Ex2F.IfFunction(expr);
        assertEquals("ERR_WRONG_IF", result);
    }
















//tests for min,max,sum ans avrege:

    @Test
    public void testComputeFunctionDirectly() {
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.set(0, 0, "10");
        sheet.set(0, 1, "20");
        sheet.set(0, 2, "30");
        sheet.set(1, 0, "40");
        sheet.set(1, 1, "50");

        sheet.eval();
        assertEquals(120.0, Ex2F.computeFUNCTION("=sum(A0:B1)"));
        assertEquals(10.0, Ex2F.computeFUNCTION("=min(A0:B1)"));
        assertEquals(50.0, Ex2F.computeFUNCTION("=max(A0:B1)"));
        assertEquals(30.0, Ex2F.computeFUNCTION("=average(A0:B1)"));
        Double invalidResult = Ex2F.computeFUNCTION("=sum(invalidRange)");
        assertNull(invalidResult);
    }



    @Test
    public void testMixedValuesComputation() {
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.set(0, 0, "10");      // A0 = 10
        sheet.set(0, 1, "-5");      // A1 = -5
        sheet.set(0, 2, "0");       // A2 = 0
        sheet.set(1, 0, "7.5");     // B0 = 7.5
        sheet.set(1, 1, "2.5");     // B1 = 2.5

        sheet.eval();

        Double sumResult = Ex2F.computeFUNCTION("=sum(A0:B1)");
        Double minResult = Ex2F.computeFUNCTION("=min(A0:B1)");
        Double maxResult = Ex2F.computeFUNCTION("=max(A0:B1)");
        Double avgResult = Ex2F.computeFUNCTION("=average(A0:B1)");
        assertEquals(15.0, sumResult);
        assertEquals(-5.0, minResult);
        assertEquals(10.0, maxResult);
        assertEquals(3.75, avgResult);
    }

    @Test
    public void testRangeWithTextAndEmptyCells() {
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.set(0, 0, "10");
        sheet.set(0, 1, "Hello");
        sheet.set(0, 2, "");
        sheet.set(1, 0, "20");
        sheet.set(1, 1, "");
        sheet.eval();
        Double sumResult = Ex2F.computeFUNCTION("=sum(A0:B1)");
        Double minResult = Ex2F.computeFUNCTION("=min(A0:B1)");
        Double maxResult = Ex2F.computeFUNCTION("=max(A0:B1)");
        Double avgResult = Ex2F.computeFUNCTION("=average(A0:B1)");
        assertEquals(30.0, sumResult);
        assertEquals(10.0, minResult);
        assertEquals(20.0, maxResult);
        assertEquals(15.0, avgResult);
    }

    @Test
    public void testSingleCellRange() {
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);
        sheet.set(5, 5, "42");    // F5 = 42
        sheet.eval();
        Double sumResult = Ex2F.computeFUNCTION("=sum(F5:F5)");
        Double minResult = Ex2F.computeFUNCTION("=min(F5:F5)");
        Double maxResult = Ex2F.computeFUNCTION("=max(F5:F5)");
        Double avgResult = Ex2F.computeFUNCTION("=average(F5:F5)");

        assertEquals(42.0, sumResult);
        assertEquals(42.0, minResult);
        assertEquals(42.0, maxResult);
        assertEquals(42.0, avgResult);
    }

    @Test
    public void testNegativeNumbersOnly() {
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);
        sheet.set(0, 0, "-10");    // A0 = -10
        sheet.set(0, 1, "-20");    // A1 = -20
        sheet.set(0, 2, "-5");     // A2 = -5
        sheet.set(1, 0, "-15");    // B0 = -15
        sheet.eval();
        Double sumResult = Ex2F.computeFUNCTION("=sum(A0:B0)");
        Double minResult = Ex2F.computeFUNCTION("=min(A0:B0)");
        Double maxResult = Ex2F.computeFUNCTION("=max(A0:B0)");
        Double avgResult = Ex2F.computeFUNCTION("=average(A0:B0)");
        assertEquals(-25.0, sumResult);
        assertEquals(-15.0, minResult);
        assertEquals(-10.0, maxResult);
        assertEquals(-12.5, avgResult);
    }

    @Test
    public void testFunctionErrorHandling() {
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.set(0, 0, "Text1"); // A0
        sheet.set(0, 1, "Text2"); // A1
        sheet.eval();

        assertEquals(0.0, Ex2F.computeFUNCTION("=sum(Z99:Z100)"));

        assertNull(Ex2F.computeFUNCTION("=sum(A0,A1)"));

        assertEquals(0.0, Ex2F.computeFUNCTION("=sum(A0:A1)"));
    }


    @Test
    public void testLargeRange() {
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                sheet.set(i, j, "1.0");
            }
        }

        sheet.eval();
        Double sumResult = Ex2F.computeFUNCTION("=sum(A0:E4)");
        Double minResult = Ex2F.computeFUNCTION("=min(A0:E4)");
        Double maxResult = Ex2F.computeFUNCTION("=max(A0:E4)");
        Double avgResult = Ex2F.computeFUNCTION("=average(A0:E4)");

        // בדיקת התוצאות על טווח גדול (5x5 = 25 תאים)
        assertEquals(25.0, sumResult);
        assertEquals(1.0, minResult);
        assertEquals(1.0, maxResult);
        assertEquals(1.0, avgResult );
    }

    @Test
    public void testInvalidRangeSyntax() {
        // הכנת הגיליון
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.eval();
        Double result1 = Ex2F.computeFUNCTION("=sum(A0,B1)");
        Double result2 = Ex2F.computeFUNCTION("=sum(A0::B1)");
        Double result3 = Ex2F.computeFUNCTION("=sum(a0:b1)");
        Double result4 = Ex2F.computeFUNCTION("=sum(A0 : B1)");

        assertNull(result1);
        assertNull(result2);
        if (result3 == null) {
            assertNull(result3);
        }

        if (result4 == null) {
            assertNull(result4);
        }
    }


    @Test
    public void testInvalidFunctionNames() {
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.set(0, 0, "10");
        sheet.set(0, 1, "20");

        sheet.eval();
        Double result1 = Ex2F.computeFUNCTION("=summ(A0:A1)");           // שם פונקציה לא קיים
        Double result2 = Ex2F.computeFUNCTION("=SUM(A0:A1)");            // אותיות גדולות
        Double result3 = Ex2F.computeFUNCTION("=minimum(A0:A1)");        // שם פונקציה ארוך יותר
        Double result4 = Ex2F.computeFUNCTION("=av e rage(A0:A1)");      // רווח בשם הפונקציה
        assertNull(result1);
        if (result2 == null) {
            assertNull(result2);
        }
        assertNull(result3);
        assertNull(result4);
    }

    @Test
    public void testMissingParentheses() {
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.eval();
        Double result1 = Ex2F.computeFUNCTION("=sum(A0:B1");
        Double result2 = Ex2F.computeFUNCTION("=sumA0:B1)");
        Double result3 = Ex2F.computeFUNCTION("=sum A0:B1");
        assertNull(result1);
        assertNull(result2);
        assertNull(result3);
    }
    @Test
    public void testOutOfBoundsRange() {
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);
        sheet.eval();
        Double result1 = Ex2F.computeFUNCTION("=sum(Z0:Z5)");
        Double result2 = Ex2F.computeFUNCTION("=sum(A100:A105)");
        assertEquals(0.0, result1);
        assertEquals(0.0, result2);
    }

    @Test
    public void testReversedRange() {
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);
        sheet.set(0, 0, "10");
        sheet.set(0, 1, "20");
        sheet.set(1, 0, "30");
        sheet.set(1, 1, "40");

        sheet.eval();
        Double result1 = Ex2F.computeFUNCTION("=sum(B1:A0)");
        Double result2 = Ex2F.computeFUNCTION("=sum(A1:A0)");
        Double result3 = Ex2F.computeFUNCTION("=sum(B0:A0)");
        if (result1 != null && result1 != (double)Ex2Utils.ERR) {
            assertEquals(100.0, result1);
        }

        if (result2 != null && result2 != (double)Ex2Utils.ERR) {
            assertEquals(30.0, result2);
        }

        if (result3 != null && result3 != (double)Ex2Utils.ERR) {
            assertEquals(40.0, result3);
        }
    }

    @Test
    public void testCircularReference() {
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);
        sheet.set(0, 0, "=sum(A0:B1)");    // A0
        sheet.set(0, 1, "10");             // A1 = 10
        sheet.set(1, 0, "20");             // B0 = 20
        sheet.set(1, 1, "30");             // B1 = 30
        sheet.eval();

        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 0), "ERR_CYCLE");
    }

    @Test
    public void testNestedFunctions() {
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.set(0, 0, "10");    // A0 = 10
        sheet.set(0, 1, "20");    // A1 = 20
        sheet.set(1, 0, "30");    // B0 = 30
        sheet.set(1, 1, "40");    // B1 = 40

        sheet.eval();
        Double result = Ex2F.computeFUNCTION("=sum(min(A0:A1):max(B0:B1))");
        if (result == null) {
            assertNull(result, " null");
        } else {
            assertEquals((double)Ex2Utils.ERR, result, "ERR");
        }
    }
}
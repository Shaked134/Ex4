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
    }
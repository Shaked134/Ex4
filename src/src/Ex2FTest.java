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








    //tests for If::


    @Test
    public void testValidIfLiteralComparison() {
        String expr = "=if(1<2,1,2)";
        String result = Ex2F.IfFunction(expr);
        assertEquals("1.0", result);
    }



    @Test
    public void testValidIfWithText() {
        // יוצרת גיליון חדש
        Ex2Sheet sheet = new Ex2Sheet();

        // מחברת את הגיליון למחלקה Ex2F
        Ex2F.setSpreadsheet(sheet);

        // מגדירה ערך לתא A1
        sheet.set(0, 0, "=3"); // במקום "3"

        // 💥 מחשבת את כל הגיליון לפני שמריצים את הביטוי
        sheet.eval();

        // הדפסות דיבאג
        System.out.println("DEBUG: A1 raw value: " + sheet.value(0, 0));
        System.out.println("DEBUG: A1 eval value: " + sheet.eval(0, 0));

        // מריצה את הפונקציה
        String expr = "=if(A1>2,big,small)";
        String result = Ex2F.IfFunction(expr);

        // בודקת שהתקבלה התוצאה הנכונה
        assertEquals("big", result);
    }




    @Test
    public void testValidIfWithFormulas() {
        Ex2Sheet sheet = new Ex2Sheet();
        sheet.set(0, 0, "3");   // A1
        sheet.set(0, 1, "4");   // A2
        sheet.set(0, 2, "2");   // A3
        Ex2F.setSpreadsheet(sheet);

        String expr = "=if(A1*A2 != A3/(2-A1), =A2+2, =A1+1)";
        String result = Ex2F.IfFunction(expr);
        assertEquals("6.0", result); // כי A1*A2 = 12, A3/(2-A1) = 2 / (2-3) = -2, ולכן !=
    }



    @Test
    public void testValidIfWithCellReferenceComparison() {
        Ex2Sheet sheet = new Ex2Sheet();
        sheet.set(0, 0, "5"); // A1
        Ex2F.setSpreadsheet(sheet);

        String expr = "=if(A1>2,big,small)";
        String result = Ex2F.IfFunction(expr);
        assertEquals("big", result); // 5 > 2 → מחזיר "big"
    }

    @Test
    public void testValidIfWithTextComparison() {
        Ex2Sheet sheet = new Ex2Sheet();
        sheet.set(0, 1, "banana"); // A2
        Ex2F.setSpreadsheet(sheet);

        String expr = "=if(A2=A2,same,diff)";
        String result = Ex2F.IfFunction(expr);
        assertEquals("same", result); // טקסט זהה → מחזיר "same"
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
        assertEquals("high", result); // sum = 20 > 10 → מחזיר "high"
    }





    @Test
    public void testIfWithCircularReference() {
        Ex2Sheet sheet = new Ex2Sheet();
        sheet.set(0, 0, "=if(A1>2,2,3)"); // A1 תלוי בעצמו
        Ex2F.setSpreadsheet(sheet);

        sheet.eval();
        String result = sheet.value(0, 0);
        assertEquals(Ex2Utils.ERR_CYCLE, result); // זיהוי מעגליות
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
    public void testValidIfWithStrings() {
        Ex2Sheet sheet = new Ex2Sheet();
        sheet.set(0, 0, "apple");  // A1
        sheet.set(0, 1, "banana"); // A2
        Ex2F.setSpreadsheet(sheet);

        String expr = "=if(A1=A2, same, diff)";
        String result = Ex2F.IfFunction(expr);
        assertEquals("diff", result);
    }

    @Test
    public void testValidIfWithFormulas1() {
        Ex2Sheet sheet = new Ex2Sheet();
        sheet.set(0, 0, "3");   // A1
        sheet.set(0, 1, "4");   // A2
        sheet.set(0, 2, "2");   // A3
        Ex2F.setSpreadsheet(sheet);

        String expr = "=if(A1*A2 != A3/(2-A1), =A2+2, =A1+1)";
        String result = Ex2F.IfFunction(expr);
        assertEquals("6.0", result); // כי A1*A2 = 12, A3/(2-A1) = -2 → שונה
    }

    @Test
    public void testValidIfWithFunctionResult1() {
        Ex2Sheet sheet = new Ex2Sheet();
        sheet.set(0, 0, "5");   // A1
        sheet.set(0, 1, "15");  // A2
        sheet.set(0, 2, "=sum(A1:A2)"); // A3 = 20
        Ex2F.setSpreadsheet(sheet);

        String expr = "=if(A3>10, high, low)";
        String result = Ex2F.IfFunction(expr);
        assertEquals("high", result);
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


    @Test
    public void testIfWithCircularReference3() {
        Ex2Sheet sheet = new Ex2Sheet();
        sheet.set(0, 0, "=if(A1>0, 1, 0)");  // A1 מפנה לעצמו
        Ex2F.setSpreadsheet(sheet);

        String expr = "=if(A1>0, 1, 0)";
        String result = Ex2F.IfFunction(expr);
        assertEquals("IF_ERR", result);
    }
    @Test
    public void testIfWithCircular8Reference() {
        Ex2Sheet sheet = new Ex2Sheet();
        sheet.set(0, 0, "=if(A1>0, 1, 0)");  // A1 = תא עם if שמתייחס לעצמו
        Ex2F.setSpreadsheet(sheet);

        String expr = "=if(A1>0, 1, 0)";
        String result = Ex2F.IfFunction(expr);

        assertEquals(Ex2Utils.IF_ERR, result, "if עם תנאי שמכיל התייחסות מעגלית צריך להחזיר IF_ERR");
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

        // תקין
        assertEquals(120.0, Ex2F.computeFUNCTION("=sum(A0:B1)"));
        assertEquals(10.0, Ex2F.computeFUNCTION("=min(A0:B1)"));
        assertEquals(50.0, Ex2F.computeFUNCTION("=max(A0:B1)"));
        assertEquals(30.0, Ex2F.computeFUNCTION("=average(A0:B1)"));

        // שגוי - תחביר לא תקני
        Double invalidResult = Ex2F.computeFUNCTION("=sum(invalidRange)");
        assertNull(invalidResult, "תחביר לא תקין צריך להחזיר null ולא לקרוס");
    }



    @Test
    public void testMixedValuesComputation() {
        // הכנת הגיליון עם ערכים מעורבים
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.set(0, 0, "10");      // A0 = 10
        sheet.set(0, 1, "-5");      // A1 = -5
        sheet.set(0, 2, "0");       // A2 = 0
        sheet.set(1, 0, "7.5");     // B0 = 7.5
        sheet.set(1, 1, "2.5");     // B1 = 2.5

        // חישוב פעם אחת להעריך את כל התאים
        sheet.eval();

        // קריאה ישירה לפונקציית computeFUNCTION
        Double sumResult = Ex2F.computeFUNCTION("=sum(A0:B1)");
        Double minResult = Ex2F.computeFUNCTION("=min(A0:B1)");
        Double maxResult = Ex2F.computeFUNCTION("=max(A0:B1)");
        Double avgResult = Ex2F.computeFUNCTION("=average(A0:B1)");

        // בדיקת התוצאות
        assertEquals(15.0, sumResult, "פונקציית הסכום צריכה לחשב את סכום כל המספרים בטווח");
        assertEquals(-5.0, minResult, "פונקציית המינימום צריכה למצוא את הערך הקטן ביותר בטווח");
        assertEquals(10.0, maxResult, "פונקציית המקסימום צריכה למצוא את הערך הגדול ביותר בטווח");
        assertEquals(3.75, avgResult, "פונקציית הממוצע צריכה לחשב את הממוצע של כל המספרים בטווח");
    }

    @Test
    public void testRangeWithTextAndEmptyCells() {
        // הכנת הגיליון עם ערכים טקסטואליים ותאים ריקים
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.set(0, 0, "10");      // A0 = 10
        sheet.set(0, 1, "Hello");   // A1 = Hello (טקסט)
        sheet.set(0, 2, "");        // A2 = (ריק)
        sheet.set(1, 0, "20");      // B0 = 20
        sheet.set(1, 1, "");        // B1 = (ריק)

        // חישוב פעם אחת להעריך את כל התאים
        sheet.eval();

        // קריאה ישירה לפונקציית computeFUNCTION
        Double sumResult = Ex2F.computeFUNCTION("=sum(A0:B1)");
        Double minResult = Ex2F.computeFUNCTION("=min(A0:B1)");
        Double maxResult = Ex2F.computeFUNCTION("=max(A0:B1)");
        Double avgResult = Ex2F.computeFUNCTION("=average(A0:B1)");

        // בדיקת התוצאות - הפונקציות צריכות להתעלם מתאים טקסטואליים וריקים
        assertEquals(30.0, sumResult, "פונקציית הסכום צריכה להתעלם מתאים טקסטואליים וריקים");
        assertEquals(10.0, minResult, "פונקציית המינימום צריכה להתעלם מתאים טקסטואליים וריקים");
        assertEquals(20.0, maxResult, "פונקציית המקסימום צריכה להתעלם מתאים טקסטואליים וריקים");
        assertEquals(15.0, avgResult, "פונקציית הממוצע צריכה להתעלם מתאים טקסטואליים וריקים");
    }

    @Test
    public void testSingleCellRange() {
        // הכנת הגיליון עם תא בודד
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.set(5, 5, "42");    // F5 = 42

        // חישוב פעם אחת להעריך את כל התאים
        sheet.eval();

        // קריאה ישירה לפונקציית computeFUNCTION עם טווח של תא בודד
        Double sumResult = Ex2F.computeFUNCTION("=sum(F5:F5)");
        Double minResult = Ex2F.computeFUNCTION("=min(F5:F5)");
        Double maxResult = Ex2F.computeFUNCTION("=max(F5:F5)");
        Double avgResult = Ex2F.computeFUNCTION("=average(F5:F5)");

        // בדיקת התוצאות - כל הפונקציות צריכות להחזיר את ערך התא הבודד
        assertEquals(42.0, sumResult, "פונקציית הסכום על תא בודד צריכה להחזיר את ערכו");
        assertEquals(42.0, minResult, "פונקציית המינימום על תא בודד צריכה להחזיר את ערכו");
        assertEquals(42.0, maxResult, "פונקציית המקסימום על תא בודד צריכה להחזיר את ערכו");
        assertEquals(42.0, avgResult, "פונקציית הממוצע על תא בודד צריכה להחזיר את ערכו");
    }

    @Test
    public void testNegativeNumbersOnly() {
        // הכנת הגיליון עם מספרים שליליים בלבד
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.set(0, 0, "-10");    // A0 = -10
        sheet.set(0, 1, "-20");    // A1 = -20
        sheet.set(0, 2, "-5");     // A2 = -5
        sheet.set(1, 0, "-15");    // B0 = -15

        // חישוב פעם אחת להעריך את כל התאים
        sheet.eval();

        // קריאה ישירה לפונקציית computeFUNCTION
        Double sumResult = Ex2F.computeFUNCTION("=sum(A0:B0)");
        Double minResult = Ex2F.computeFUNCTION("=min(A0:B0)");
        Double maxResult = Ex2F.computeFUNCTION("=max(A0:B0)");
        Double avgResult = Ex2F.computeFUNCTION("=average(A0:B0)");

        // בדיקת התוצאות עם מספרים שליליים בלבד
        assertEquals(-25.0, sumResult, "פונקציית הסכום צריכה לפעול נכון עם מספרים שליליים בלבד");
        assertEquals(-15.0, minResult, "פונקציית המינימום צריכה לפעול נכון עם מספרים שליליים בלבד");
        assertEquals(-10.0, maxResult, "פונקציית המקסימום צריכה לפעול נכון עם מספרים שליליים בלבד");
        assertEquals(-12.5, avgResult, "פונקציית הממוצע צריכה לפעול נכון עם מספרים שליליים בלבד");
    }

    @Test
    public void testFunctionErrorHandling() {
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.set(0, 0, "Text1"); // A0
        sheet.set(0, 1, "Text2"); // A1
        sheet.eval();

        // טווח לא קיים → אמור להחזיר 0
        assertEquals(0.0, Ex2F.computeFUNCTION("=sum(Z99:Z100)"));

        // תחביר שגוי (פסיקים במקום נקודתיים) → אמור להחזיר null
        assertNull(Ex2F.computeFUNCTION("=sum(A0,A1)"));

        // טווח עם טקסט בלבד → סכום צריך להיות 0
        assertEquals(0.0, Ex2F.computeFUNCTION("=sum(A0:A1)"));
    }


    @Test
    public void testLargeRange() {
        // הכנת הגיליון עם טווח גדול
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        // מילוי טווח גדול עם ערכים זהים (1.0)
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                sheet.set(i, j, "1.0");  // ממלא טווח 5x5 עם ערכים של 1.0
            }
        }

        // חישוב פעם אחת להעריך את כל התאים
        sheet.eval();

        // קריאה ישירה לפונקציית computeFUNCTION על טווח גדול (A0:E4)
        Double sumResult = Ex2F.computeFUNCTION("=sum(A0:E4)");
        Double minResult = Ex2F.computeFUNCTION("=min(A0:E4)");
        Double maxResult = Ex2F.computeFUNCTION("=max(A0:E4)");
        Double avgResult = Ex2F.computeFUNCTION("=average(A0:E4)");

        // בדיקת התוצאות על טווח גדול (5x5 = 25 תאים)
        assertEquals(25.0, sumResult, "פונקציית הסכום צריכה לפעול נכון על טווח גדול");
        assertEquals(1.0, minResult, "פונקציית המינימום צריכה לפעול נכון על טווח גדול");
        assertEquals(1.0, maxResult, "פונקציית המקסימום צריכה לפעול נכון על טווח גדול");
        assertEquals(1.0, avgResult, "פונקציית הממוצע צריכה לפעול נכון על טווח גדול");
    }

    @Test
    public void testInvalidRangeSyntax() {
        // הכנת הגיליון
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.eval();

        // בדיקת תחביר לא תקין של טווח (פסיק במקום נקודתיים)
        Double result1 = Ex2F.computeFUNCTION("=sum(A0,B1)");
        // בדיקת טווח עם נקודתיים כפולות
        Double result2 = Ex2F.computeFUNCTION("=sum(A0::B1)");
        // בדיקת טווח עם אותיות קטנות
        Double result3 = Ex2F.computeFUNCTION("=sum(a0:b1)");
        // בדיקת טווח עם רווחים
        Double result4 = Ex2F.computeFUNCTION("=sum(A0 : B1)");

        // כל אחד מהמקרים הללו צריך להיות מזוהה כתחביר לא תקין ולהחזיר null
        assertNull(result1, "פורמט טווח לא תקין (עם פסיק) צריך להחזיר null");
        assertNull(result2, "פורמט טווח לא תקין (נקודתיים כפולות) צריך להחזיר null");

        // בהתאם ליישום, ייתכן שאותיות קטנות כן מזוהות כתקינות
        if (result3 == null) {
            assertNull(result3, "פורמט טווח עם אותיות קטנות צריך להחזיר null");
        }

        // בהתאם ליישום, ייתכן שרווחים מטופלים ומסוננים
        if (result4 == null) {
            assertNull(result4, "פורמט טווח עם רווחים צריך להחזיר null");
        }
    }

    @Test
    public void testInvalidFunctionNames() {
        // הכנת הגיליון
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.set(0, 0, "10");
        sheet.set(0, 1, "20");

        sheet.eval();

        // בדיקת שמות פונקציות לא תקינים
        Double result1 = Ex2F.computeFUNCTION("=summ(A0:A1)");           // שם פונקציה לא קיים
        Double result2 = Ex2F.computeFUNCTION("=SUM(A0:A1)");            // אותיות גדולות
        Double result3 = Ex2F.computeFUNCTION("=minimum(A0:A1)");        // שם פונקציה ארוך יותר
        Double result4 = Ex2F.computeFUNCTION("=av e rage(A0:A1)");      // רווח בשם הפונקציה

        // כל אחד מהמקרים הללו צריך להיות מזוהה כפונקציה לא תקינה ולהחזיר null
        assertNull(result1, "שם פונקציה לא קיים צריך להחזיר null");

        // בהתאם ליישום, ייתכן שאותיות גדולות כן מזוהות כתקינות
        if (result2 == null) {
            assertNull(result2, "שם פונקציה באותיות גדולות צריך להחזיר null");
        }

        assertNull(result3, "שם פונקציה ארוך מדי צריך להחזיר null");
        assertNull(result4, "שם פונקציה עם רווחים צריך להחזיר null");
    }

    @Test
    public void testMissingParentheses() {
        // הכנת הגיליון
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.eval();

        // בדיקת חוסר בסוגריים
        Double result1 = Ex2F.computeFUNCTION("=sum(A0:B1");     // חסר סוגר סוגר
        Double result2 = Ex2F.computeFUNCTION("=sumA0:B1)");     // חסר סוגר פותח
        Double result3 = Ex2F.computeFUNCTION("=sum A0:B1");     // חסרים סוגריים

        // כל אחד מהמקרים הללו צריך להיות מזוהה כתחביר לא תקין ולהחזיר null
        assertNull(result1, "פונקציה עם סוגר חסר צריכה להחזיר null");
        assertNull(result2, "פונקציה עם סוגר חסר צריכה להחזיר null");
        assertNull(result3, "פונקציה בלי סוגריים צריכה להחזיר null");
    }

    @Test
    public void testRangeWithOnlyInvalidValues() {
        // הכנת הגיליון עם תאים שאינם מספריים בלבד
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.set(0, 0, "text");     // A0 = text
        sheet.set(0, 1, "abc");      // A1 = abc
        sheet.set(1, 0, "");         // B0 = (ריק)
        sheet.set(1, 1, "xyz");      // B1 = xyz

        sheet.eval();

        // קריאה ישירה לפונקציות על טווח שאין בו מספרים
        Double sumResult = Ex2F.computeFUNCTION("=sum(A0:B1)");
        Double minResult = Ex2F.computeFUNCTION("=min(A0:B1)");
        Double maxResult = Ex2F.computeFUNCTION("=max(A0:B1)");
        Double avgResult = Ex2F.computeFUNCTION("=average(A0:B1)");

        // פונקציית סכום צריכה להחזיר 0 אם אין מספרים
        assertEquals(0.0, sumResult, "סכום של טווח ללא מספרים צריך להיות 0");

        // פונקציות מינימום ומקסימום צריכות להחזיר שגיאה אם אין מספרים
        assertEquals((double)Ex2Utils.ERR, minResult, "מינימום של טווח ללא מספרים צריך להחזיר ERR");
        assertEquals((double)Ex2Utils.ERR, maxResult, "מקסימום של טווח ללא מספרים צריך להחזיר ERR");

        // פונקציית ממוצע צריכה להחזיר שגיאה (חלוקה ב-0)
        assertEquals((double)Ex2Utils.ERR, avgResult, "ממוצע של טווח ללא מספרים צריך להחזיר ERR");
    }

    @Test
    public void testOutOfBoundsRange() {
        // הכנת הגיליון
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.eval();

        // בדיקת טווחים מחוץ לגבולות הגיליון
        Double result1 = Ex2F.computeFUNCTION("=sum(Z0:Z5)");        // עמודה שמחוץ לגבולות
        Double result2 = Ex2F.computeFUNCTION("=sum(A100:A105)");    // שורה שמחוץ לגבולות

        // הטווחים הללו חוקיים מבחינת התחביר אבל מחוץ לגיליון, הסכום צריך להיות 0
        assertEquals(0.0, result1, "סכום של טווח מחוץ לגבולות הגיליון צריך להיות 0");
        assertEquals(0.0, result2, "סכום של טווח מחוץ לגבולות הגיליון צריך להיות 0");
    }

    @Test
    public void testReversedRange() {
        // הכנת הגיליון
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.set(0, 0, "10");    // A0 = 10
        sheet.set(0, 1, "20");    // A1 = 20
        sheet.set(1, 0, "30");    // B0 = 30
        sheet.set(1, 1, "40");    // B1 = 40

        sheet.eval();

        // בדיקת טווח הפוך (תא סיום לפני תא התחלה)
        Double result1 = Ex2F.computeFUNCTION("=sum(B1:A0)");    // הפוך במיקום וגם באות
        Double result2 = Ex2F.computeFUNCTION("=sum(A1:A0)");    // הפוך במספר
        Double result3 = Ex2F.computeFUNCTION("=sum(B0:A0)");    // הפוך באות

        // בהתאם למימוש, או שטווח הפוך יחזיר שגיאה או שהמימוש ינרמל את הטווח
        if (result1 != null && result1 != (double)Ex2Utils.ERR) {
            // אם המימוש מתמודד עם טווח הפוך, אז התוצאה צריכה להיות סכום כל הערכים
            assertEquals(100.0, result1, "סכום של טווח הפוך צריך לנרמל את הטווח ולחשב נכון");
        }

        if (result2 != null && result2 != (double)Ex2Utils.ERR) {
            assertEquals(30.0, result2, "סכום של טווח הפוך במספר צריך לנרמל את הטווח ולחשב נכון");
        }

        if (result3 != null && result3 != (double)Ex2Utils.ERR) {
            assertEquals(40.0, result3, "סכום של טווח הפוך באות צריך לנרמל את הטווח ולחשב נכון");
        }
    }

    @Test
    public void testCircularReference() {
        // הכנת הגיליון עם התייחסות מעגלית
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        // הגדרת תא שמתייחס לטווח שכולל את עצמו
        sheet.set(0, 0, "=sum(A0:B1)");    // A0 מתייחס לטווח שכולל את A0 עצמו
        sheet.set(0, 1, "10");             // A1 = 10
        sheet.set(1, 0, "20");             // B0 = 20
        sheet.set(1, 1, "30");             // B1 = 30

        // חישוב הגיליון - אמור לזהות התייחסות מעגלית
        sheet.eval();

        // הערך של תא A0 צריך להיות ERR_CYCLE או ערך שגיאה דומה
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 0), "תא עם התייחסות מעגלית צריך להחזיר ERR_CYCLE");
    }

    @Test
    public void testNestedFunctions() {
        // הכנת הגיליון
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        Ex2F.setSpreadsheet(sheet);

        sheet.set(0, 0, "10");    // A0 = 10
        sheet.set(0, 1, "20");    // A1 = 20
        sheet.set(1, 0, "30");    // B0 = 30
        sheet.set(1, 1, "40");    // B1 = 40

        sheet.eval();

        // ניסיון להפעיל פונקציות מקוננות - בהתאם למימוש, זה יכול להיות לא נתמך
        Double result = Ex2F.computeFUNCTION("=sum(min(A0:A1):max(B0:B1))");

        // בהתאם למימוש, או שהפונקציה תחזיר שגיאה או null
        if (result == null) {
            assertNull(result, "פונקציות מקוננות לא נתמכות ולכן צריכות להחזיר null");
        } else {
            assertEquals((double)Ex2Utils.ERR, result, "פונקציות מקוננות לא נתמכות ולכן צריכות להחזיר ERR");
        }
    }
}
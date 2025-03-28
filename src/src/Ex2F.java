package src;
import java.util.ArrayList;
import java.util.List;
import src.Range2D;


public class Ex2F {

    private static final String OPERATORS = "+-*/";
    private static Sheet spreadsheet; // Reference to the spreadsheet

    // Add setter for the spreadsheet
    public static void setSpreadsheet(Sheet sheet) {
        spreadsheet = sheet;
    }
    //Checks if the given text represents a valid number

    public static boolean isNumber(String text) {
        if (text == null || text.isEmpty()) return false;

        // First remove whitespace
        text = text.trim();

        // Check if the first character is a digit or minus sign
        // (not allowing standalone plus sign)
        if (!Character.isDigit(text.charAt(0)) && text.charAt(0) != '-' && text.charAt(0) != '+') {
            return false;
        }
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Checks if the given text is a valid cell reference (e.g., A1, B2)

    public static boolean isCellReference(String text) {
        if (text == null || text.length() < 2) return false;
        char col = text.charAt(0);
        if (!Character.isLetter(col)) return false;
        try {
            Integer.parseInt(text.substring(1));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    // Checks if the given text should be treated as text (not a number or formula)

    public static boolean isText(String text) {
        return !text.startsWith("=") && !isNumber(text);
    }


    // Recursively checks if the given text represents a valid formula

    public static boolean isForm(String text) {
        if (text == null || text.isEmpty()) return false;

        // Formula must start with =
        if (!text.startsWith("=")) return false;

        return isFormHelper(text.substring(1).trim());
    }

    private static boolean isFormHelper(String text) {
        if (text.isEmpty()) return false;

        // Remove outer parentheses if they exist
        text = removeOuterParentheses(text);

        // Handle leading '+' or '-'
        if (text.startsWith("+") || text.startsWith("-")) {
            String remaining = text.substring(1).trim();
            // Ensure the rest of the formula is valid
            return isFormHelper(remaining) || isCellReference(remaining);
        }

        // Check if it's a simple number or cell reference
        if (isNumber(text) || isCellReference(text)) return true;

        // Look for operators
        int parenthesesCount = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '(') parenthesesCount++;
            else if (c == ')') parenthesesCount--;

            // Check for consecutive operators, e.g., ++, --, +-, -+
            for (int h = 1; h < text.length(); h++) {
                char current = text.charAt(h);
                char previous = text.charAt(h - 1);

                if (OPERATORS.indexOf(current) != -1 && OPERATORS.indexOf(previous) != -1) {
                    return false; // Invalid form: consecutive operators
                }
            }

            // Only consider operators at the top level (not within parentheses)
            if (parenthesesCount == 0 && OPERATORS.indexOf(c) != -1) {
                // Found an operator, recursively check both sides
                String leftPart = text.substring(0, i).trim();
                String rightPart = text.substring(i + 1).trim();

                return !leftPart.isEmpty() && !rightPart.isEmpty()
                        && isFormHelper(leftPart)
                        && isFormHelper(rightPart);
            }
        }
        return false;
    }


    // Recursively computes the value of a formula

    public static Double computeForm(String form) {
        if (!isForm(form)) return null;


        // Remove the = sign and trim
        String expression = form.substring(1).trim();
        return computeFormHelper(expression);

    }

    private static Double computeFormHelper(String expression) {

        // Remove outer parentheses if they exist
        expression = removeOuterParentheses(expression);

        // Try to parse as a simple number
        if (isNumber(expression)) {
            Double number = Double.parseDouble(expression);
            return number;
        }
        if (expression.startsWith("=")) {
            return computeFormHelper(expression.substring(1).trim());
        }

        // Handle leading '+' or '-'
        if (expression.startsWith("+")) {
            return computeFormHelper(expression.substring(1).trim());
        }
        if (expression.startsWith("-") && !isNumber(expression)) {
            String remaining = expression.substring(1).trim();
            if (isCellReference(remaining)) {
                Double cellValue = computeFormHelper(remaining);
                if (cellValue == null) {
                    return null;
                }
                Double result = -cellValue;
                return result;
            }
            String modifiedExpression = "0" + expression;
            return computeFormHelper(modifiedExpression);
        }


        // Check if it's a cell reference
        if (isCellReference(expression)) {
            if (spreadsheet == null) {
                return null;
            }
            CellEntry entry = new CellEntry(expression);
            if (!entry.isValid()) {
                return null;
            }


            System.out.println("DEBUG: computeFormHelper trying to read cell '" + expression + "'");
            System.out.println("DEBUG: spreadsheet.value -> " + spreadsheet.value(entry.getX(), entry.getY()));
            System.out.println("DEBUG: spreadsheet.eval -> " + spreadsheet.eval(entry.getX(), entry.getY()));



            String cellValue = spreadsheet.value(entry.getX(), entry.getY());
            if (cellValue == null || cellValue.equals(Ex2Utils.EMPTY_CELL)) {
                return null;
            }

            try {
                Double parsedValue = Double.parseDouble(cellValue);
                return parsedValue;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        // Look for + or - at the top level first (lower precedence)
        int parenthesesCount = 0;
        for (int i = expression.length() - 1; i >= 0; i--) {
            char c = expression.charAt(i);

            if (c == ')') parenthesesCount++;
            else if (c == '(') parenthesesCount--;

            if (parenthesesCount == 0 && (c == '+' || c == '-')) {
                String leftPart = expression.substring(0, i).trim();
                String rightPart = expression.substring(i + 1).trim();
                Double leftValue = computeFormHelper(leftPart);
                Double rightValue = computeFormHelper(rightPart);

                if (leftValue == null || rightValue == null) {
                    return null;
                }
                Double result = c == '+' ? leftValue + rightValue : leftValue - rightValue;
                return result;
            }
        }

        // Then look for * or / (higher precedence)
        parenthesesCount = 0;
        for (int i = expression.length() - 1; i >= 0; i--) {
            char c = expression.charAt(i);
            if (c == ')') parenthesesCount++;
            else if (c == '(') parenthesesCount--;

            if (parenthesesCount == 0 && (c == '*' || c == '/')) {
                String leftPart = expression.substring(0, i).trim();
                String rightPart = expression.substring(i + 1).trim();
                Double leftValue = computeFormHelper(leftPart);
                Double rightValue = computeFormHelper(rightPart);
                if (leftValue == null || rightValue == null) {
                    return null;
                }
                Double result;
                if (c == '*') {
                    result = leftValue * rightValue;
                } else {
                    if (rightValue != 0) {
                        result = leftValue / rightValue;
                    } else {
                        result = null;
                    }
                }
                return result;
            }
        }
        return null;
    }

    //Helper method to remove outer parentheses if they exist

    private static String removeOuterParentheses(String text) {
        text = text.trim();
        while (text.startsWith("(") && text.endsWith(")")) {
            // Check if these are matching parentheses
            int count = 0;
            boolean isMatching = true;
            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == '(') count++;
                else if (text.charAt(i) == ')') {
                    count--;
                    if (count == 0 && i != text.length() - 1) {
                        isMatching = false;
                        break;
                    }
                }
            }
            if (!isMatching) break;
            text = text.substring(1, text.length() - 1).trim();
        }
        return text;
    }

    private static double calcSum(Range2D range, Ex2Sheet sheet) {
        double sum = 0;
        List<CellEntry> cells = range.getCellsInRange();
        System.out.println("DEBUG: ========== calcSum START ==========");
        System.out.println("DEBUG: Calculating SUM for range: " + range);
        System.out.println("DEBUG: Total cells in range: " + cells.size());

        for (CellEntry cell : cells) {
            int x = cell.getX();
            int y = cell.getY();
            String value = sheet.eval(x, y);

            System.out.println("DEBUG: Cell " + cell + " (x=" + x + ", y=" + y + ") has value: '" + value + "'");

            if (isNumber(value)) {
                double num = Double.parseDouble(value);
                sum += num;
                System.out.println("DEBUG:  → Adding " + num + " to sum. Running total: " + sum);
            } else {
                System.out.println("DEBUG:  → Value is not a number. Skipping.");
            }
        }

        System.out.println("DEBUG: Final SUM result: " + sum);
        System.out.println("DEBUG: ========== calcSum END ==========");
        return sum;
    }

    private static double calcMin(Range2D range, Ex2Sheet sheet) {
        double min = Double.MAX_VALUE;
        boolean foundNumber = false;
        List<CellEntry> cells = range.getCellsInRange();
        System.out.println("Calculating MIN for range: " + range);
        for (CellEntry cell : cells) {
            int x = cell.getX();
            int y = cell.getY();
            String value = sheet.eval(x, y);
            System.out.println("Cell " + cell + " has value: " + value);

            if (isNumber(value)) {
                double num = Double.parseDouble(value);
                if (num < min) {
                    min = num;
                }
                foundNumber = true;
            }
        }
        System.out.println("MIN result: " + (foundNumber ? min : Ex2Utils.ERR));
        return foundNumber ? min : Ex2Utils.ERR;
    }


    private static double calcMax(Range2D range, Ex2Sheet sheet) {
        double max = -Double.MAX_VALUE;
        boolean foundNumber = false;
        List<CellEntry> cells = range.getCellsInRange();
        System.out.println("Calculating MAX for range: " + range);
        for (CellEntry cell : cells) {
            int x = cell.getX();
            int y = cell.getY();
            String value = sheet.eval(x, y);
            System.out.println("Cell " + cell + " has value: " + value);

            if (isNumber(value)) {
                double num = Double.parseDouble(value);
                if (num > max) {
                    max = num;
                }
                foundNumber = true;
            }
        }
        System.out.println("MAX result: " + (foundNumber ? max : Ex2Utils.ERR));
        return foundNumber ? max : Ex2Utils.ERR;
    }

    private static double calcAverage(Range2D range, Ex2Sheet sheet) {
        double sum = 0;
        int count = 0;
        List<CellEntry> cells = range.getCellsInRange();
        System.out.println("Calculating AVERAGE for range: " + range);
        for (CellEntry cell : cells) {
            int x = cell.getX();
            int y = cell.getY();
            String value = sheet.eval(x, y);
            System.out.println("Cell " + cell + " has value: " + value);

            if (isNumber(value)) {
                double num = Double.parseDouble(value);
                sum += num;
                count++;
            }
        }
        if (count > 0) {
            double average = sum / count;
            System.out.println("AVERAGE result: " + average);
            return average;
        } else {
            System.out.println("AVERAGE result: " + Ex2Utils.ERR);
            return Ex2Utils.ERR;
        }
    }
    public static String IfFunction(String expression) {
        expression = removeOuterParentheses(expression);
        System.out.println("Evaluating IF expression: " + expression);

        if (!expression.startsWith("=if(")) {
            System.out.println("Not a valid IF expression.");
            return Ex2Utils.IF_ERR;
        }

        String[] parts = splitIfExpression(expression);
        if (parts.length != 3) {
            System.out.println("IF expression split failed.");
            return Ex2Utils.IF_ERR;
        }

        String condition = parts[0];
        String ifTrue = parts[1];
        String ifFalse = parts[2];
        System.out.println("Condition: " + condition + ", True: " + ifTrue + ", False: " + ifFalse);

        Object condResult = computeCondition(condition);

        if (!(condResult instanceof Boolean)) {
            System.out.println("Condition is not boolean.");
            return Ex2Utils.IF_ERR;
        }

        // מחשבים את שתי האפשרויות מראש, בלי קשר לתנאי
        Object trueValue = computeConditionHelper(ifTrue);
        Object falseValue = computeConditionHelper(ifFalse);

        // בוחרים את התוצאה לפי התנאי
        Object result = (Boolean) condResult ? trueValue : falseValue;

        if (result != null) {
            System.out.println("Result: " + result.toString());
            return result.toString();
        }

        System.out.println("Result is null.");
        return Ex2Utils.IF_ERR;
    }



    private static Object computeCondition(String condition) {
        condition = removeOuterParentheses(condition);
        System.out.println("computeCondition: evaluating condition -> " + condition);

        String op = findComparisonOperator(condition);
        if (op != null) {
            int index = condition.indexOf(op);
            String Formula1 = condition.substring(0, index).trim();
            String Formula2 = condition.substring(index + op.length()).trim();

            System.out.println("computeCondition: found operator '" + op + "'");
            System.out.println("computeCondition: Formula1 -> '" + Formula1 + "', Formula2 -> '" + Formula2 + "'");

            Object val1 = computeConditionHelper(Formula1);
            Object val2 = computeConditionHelper(Formula2);

            System.out.println("computeCondition: val1 = " + val1 + ", val2 = " + val2);

            if (!(val1 instanceof Double) || !(val2 instanceof Double)) {
                System.out.println("computeCondition: one of the values is not a number.");
                return null;
            }

            double a = (Double) val1;
            double b = (Double) val2;
            boolean result = false;

            switch (op) {
                case "<": result = a < b; break;
                case ">": result = a > b; break;
                case "<=": result = a <= b; break;
                case ">=": result = a >= b; break;
                case "==": result = a == b; break;
                case "!=": result = a != b; break;
            }

            System.out.println("computeCondition: result of condition -> " + result);
            return result;
        }

        System.out.println("computeCondition: no comparison operator found.");
        return null;
    }





    private static  String findComparisonOperator(String expression) {
        String[] ops = {"<=", ">=", "==", "!=", "<", ">"};
        int parentheses = 0;

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(') parentheses++;
            else if (c == ')') parentheses--;

            if (parentheses == 0) {
                for (String op : ops) {
                    if (i + op.length() <= expression.length() &&
                            expression.substring(i, i + op.length()).equals(op)) {
                        return op;
                    }
                }
            }
        }

        return null;
    }
    private static Object computeConditionHelper(String expression) {
        if (expression == null || expression.isEmpty()) {                  // (1)
            System.out.println("computeConditionHelper 472: expression is null or empty.");
            return null;
        }

        expression = expression.trim();                                     // (5)
        System.out.println("computeConditionHelper 477: evaluating expression -> '" + expression + "'");

        if (isNumber(expression)) {                                         // (8)
            System.out.println("computeConditionHelper480: detected number -> " + expression);
            Double number = Double.parseDouble(expression);                 // (10)
            System.out.println("computeConditionHelper482: returning number -> " + number);
            return number;
        }
        if (isCellReference(expression)) {
            if (spreadsheet == null) {
                return null;
            }

            CellEntry entry = new CellEntry(expression);
            if (!entry.isValid()) {
                return null;
            }

            // קבל את ה-raw value של התא
            String rawValue = spreadsheet.value(entry.getX(), entry.getY());

            // ⬇️⬅️ הוסיפי את זה כאן:
            if (Ex2Utils.ERR_CYCLE.equals(rawValue)) {
                System.out.println("computeConditionHelper: ERR_CYCLE detected in " + expression);
                return Ex2Utils.IF_ERR;
            }

            if (rawValue == null || rawValue.equals(Ex2Utils.EMPTY_CELL)) {
                return null;
            }

            // נסה להמיר למספר, אם אפשר
            if (isNumber(rawValue)) {
                return Double.parseDouble(rawValue);
            }

            // אם לא מספר, החזר את הערך כטקסט
            return rawValue;
        }

        if (isForm("=" + expression)) {                                     // (44)
            System.out.println("computeConditionHelper493: detected formula -> " + expression);
            Double val = computeForm("=" + expression);                     // (46)
            System.out.println("computeConditionHelper 495: computed formula value -> " + val);
            return val;                                                     // (48)
        }

        if (isText(expression)) {                                           // (51)
            System.out.println("computeConditionHelper 500: detected text -> '" + expression + "'");
            return expression;                                              // (53)
        }

        System.out.println("computeConditionHelper 504: couldn't evaluate -> '" + expression + "'");
        return null;                                                        // (56)
    }


    private static String[] splitIfExpression(String expression) {
        expression = removeOuterParentheses(expression);

        if (expression.startsWith("=if(") && expression.endsWith(")")) {
            expression = expression.substring(4, expression.length() - 1).trim();
        }


        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int parens = 0;

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == ',' && parens == 0) {
                parts.add(current.toString().trim());
                current.setLength(0);
            } else {
                if (c == '(') {
                    parens++;
                } else if (c == ')') {
                    parens--;
                }
                current.append(c);
            }
        }
        parts.add(current.toString().trim());

        if (parts.size() != 3) {
            System.out.println("SPLIT FAILED, parts: " + parts);
            return new String[0];
        }
        return new String[]{parts.get(0), parts.get(1), parts.get(2)};
    }


    private static boolean isFUNCTION(String expr) {
        if (expr == null || expr.isEmpty()) return false;
        expr = expr.trim().toLowerCase();

        if (!expr.startsWith("=")) return false;

        expr = expr.substring(1).trim();

        String[] functions = {"min", "max", "sum", "average"};
        for (String func : functions) {
            if (expr.startsWith(func + "(") && expr.endsWith(")")) {
                String inside = expr.substring(func.length() + 1, expr.length() - 1).trim();
                if (!inside.isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }
    public static Double computeFUNCTION(String expr) {
        System.out.println("DEBUG: computeFUNCTION called with -> " + expr);

        if (!isFUNCTION(expr)) {
            System.out.println("DEBUG: Not a valid FUNCTION -> " + expr);
            return null;
        }

        String expression = expr.substring(1).trim(); // remove '='
        System.out.println("DEBUG: Stripped '=' -> " + expression);

        String[] functions = {"min", "max", "sum", "average"};

        for (String func : functions) {
            if (expression.startsWith(func + "(") && expression.endsWith(")")) {
                System.out.println("DEBUG: Detected function -> " + func);

                String rangeText = expression.substring(func.length() + 1, expression.length() - 1).trim();
                System.out.println("DEBUG: Extracted range text -> '" + rangeText + "'");

                if (!rangeText.matches("[A-Z]\\d+:[A-Z]\\d+")) {
                    System.out.println("DEBUG: Invalid range syntax -> " + rangeText);
                    return null; // או אפשר להחזיר ERR אם את מעדיפה
                }

                try {
                    Range2D range = new Range2D(rangeText);

                    Ex2Sheet sheet = (Ex2Sheet) spreadsheet;

                    switch (func) {
                        case "sum":
                            System.out.println("DEBUG: Calling calcSum");
                            return calcSum(range, sheet);
                        case "min":
                            System.out.println("DEBUG: Calling calcMin");
                            return calcMin(range, sheet);
                        case "max":
                            System.out.println("DEBUG: Calling calcMax");
                            return calcMax(range, sheet);
                        case "average":
                            System.out.println("DEBUG: Calling calcAverage");
                            return calcAverage(range, sheet);
                    }
                } catch (Exception e) {
                    System.out.println("DEBUG: Exception during function evaluation -> " + e.getMessage());
                    return Double.valueOf(Ex2Utils.ERR);
                }
            }
        }
        System.out.println("DEBUG: No matching function found in expression -> " + expression);
        return null;
    }


}
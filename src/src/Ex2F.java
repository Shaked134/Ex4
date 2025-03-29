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


        if (isFUNCTION("=" + expression)) {
            return computeFUNCTION("=" + expression);
        }

        // בדיקה אם זו פונקציית IF
        if (expression.startsWith("if(")) {
            String result = IfFunction("=" + expression);
            if (result.equals(Ex2Utils.IF_ERR)) {
                return null;
            }
            try {
                return Double.parseDouble(result);
            } catch (NumberFormatException e) {
                // אם התוצאה היא טקסט, לא ניתן להמיר למספר
                return null;


            }
        }

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
        for (CellEntry cell : cells) {
            int x = cell.getX();
            int y = cell.getY();
            String value = sheet.eval(x, y);
            if (isNumber(value)) {
                double num = Double.parseDouble(value);
                sum += num;
            }

            }
        return sum;
    }

    private static double calcMin(Range2D range, Ex2Sheet sheet) {
        double min = Double.MAX_VALUE;
        boolean foundNumber = false;
        List<CellEntry> cells = range.getCellsInRange();
        for (CellEntry cell : cells) {
            int x = cell.getX();
            int y = cell.getY();
            String value = sheet.eval(x, y);
            if (isNumber(value)) {
                double num = Double.parseDouble(value);
                if (num < min) {
                    min = num;
                }
                foundNumber = true;
            }
        }
        return foundNumber ? min : Ex2Utils.ERR;
    }


    private static double calcMax(Range2D range, Ex2Sheet sheet) {
        double max = -Double.MAX_VALUE;
        boolean foundNumber = false;
        List<CellEntry> cells = range.getCellsInRange();
        for (CellEntry cell : cells) {
            int x = cell.getX();
            int y = cell.getY();
            String value = sheet.eval(x, y);
            if (isNumber(value)) {
                double num = Double.parseDouble(value);
                if (num > max) {
                    max = num;
                }
                foundNumber = true;
            }
        }
        return foundNumber ? max : Ex2Utils.ERR;
    }

    private static double calcAverage(Range2D range, Ex2Sheet sheet) {
        double sum = 0;
        int count = 0;
        List<CellEntry> cells = range.getCellsInRange();
        for (CellEntry cell : cells) {
            int x = cell.getX();
            int y = cell.getY();
            String value = sheet.eval(x, y);
            if (isNumber(value)) {
                double num = Double.parseDouble(value);
                sum += num;
                count++;
            }
        }
        if (count > 0) {
            double average = sum / count;
            return average;
        } else {
            return Ex2Utils.ERR;
        }
    }
    public static String IfFunction(String expression) {
        expression = removeOuterParentheses(expression);
        if (!expression.startsWith("=if(")) {
            return Ex2Utils.IF_ERR;
        }

        String[] parts = splitIfExpression(expression);
        if (parts.length != 3) {
            return Ex2Utils.IF_ERR;
        }

        String condition = parts[0];
        String ifTrue = parts[1];
        String ifFalse = parts[2];
        Object condResult = computeCondition(condition);
        if (condResult == null ||
                Ex2Utils.ERR_FORM.equals(condResult.toString()) ||
                Ex2Utils.IF_ERR.equals(condResult.toString()) ||
                !(condResult instanceof Boolean)) {
            return Ex2Utils.IF_ERR;
        }

        if (condResult instanceof String && condResult.equals(Ex2Utils.IF_ERR)) {
            return Ex2Utils.IF_ERR;
        }
        Object trueValue = computeConditionHelper(ifTrue);
        Object falseValue = computeConditionHelper(ifFalse);

        Object result = (Boolean) condResult ? trueValue : falseValue;

        if (result != null) {
            return result.toString();
        }
        return Ex2Utils.IF_ERR;
    }

    private static Object computeCondition(String condition) {
        condition = removeOuterParentheses(condition);
        String op = findComparisonOperator(condition);
        if (op != null) {
            int index = condition.indexOf(op);
            String Formula1 = condition.substring(0, index).trim();
            String Formula2 = condition.substring(index + op.length()).trim();
            Object val1 = computeConditionHelper(Formula1);
            Object val2 = computeConditionHelper(Formula2);

            System.out.println("val1: " + val1 + " (" + val1.getClass().getSimpleName() + ")");
            System.out.println("val2: " + val2 + " (" + val2.getClass().getSimpleName() + ")");



            if (Ex2Utils.IF_ERR.equals(val1) || Ex2Utils.IF_ERR.equals(val2)) {
                return Ex2Utils.IF_ERR;
            }
            if (val1 == null || val2 == null ||
                    val1.equals(Ex2Utils.IF_ERR) ||
                    val2.equals(Ex2Utils.IF_ERR)) {
                return Ex2Utils.IF_ERR;
            }

            if (val1 instanceof String && val2 instanceof String) {
                String str1 = (String)val1;
                String str2 = (String)val2;

                boolean result = false;
                switch (op) {
                    case "=":
                    case "==": result = str1.equals(str2); break;
                    case "!=": result = !str1.equals(str2); break;
                    default: return null;
                }
                return result;
            }
            try {
                double a = Double.parseDouble(val1.toString());
                double b = Double.parseDouble(val2.toString());

                boolean result = false;

                switch (op) {
                    case "<": result = a < b; break;
                    case ">": result = a > b; break;
                    case "<=": result = a <= b; break;
                    case ">=": result = a >= b; break;
                    case "==": result = a == b; break;
                    case "=": result = a == b; break;
                    case "!=": result = a != b; break;
                }

                return result;
            } catch (NumberFormatException e) {
                String str1 = val1.toString();
                String str2 = val2.toString();

                boolean result = false;
                switch (op) {
                    case "=":
                    case "==": result = str1.equals(str2); break;
                    case "!=": result = !str1.equals(str2); break;
                    default: return null;
                }
                return result;
            }
        }
        return null;
    }

    private static  String findComparisonOperator(String expression) {
        String[] ops = {"<=", ">=", "==", "!=", "<", ">" , "="};
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
        if (expression == null || expression.isEmpty()) {
            return null;
        }

        expression = expression.trim();

        if (isFUNCTION("=" + expression)) {
            Double val = computeFUNCTION("=" + expression);
            return val;
        }

        // בדיקה אם זהו מספר
        if (isNumber(expression)) {
            return Double.parseDouble(expression);
        }

        if (expression.startsWith("=")) {
            String formulaExpression = expression.substring(1).trim();
            return computeConditionHelper(formulaExpression);
        }

        // בדיקה אם זוהי הפניה לתא
        if (isCellReference(expression)) {
            if (spreadsheet == null) {
                return null;
            }

            try {
                CellEntry cell = new CellEntry(expression);
                int x = cell.getX();
                int y = cell.getY() - 1;

                // בדיקה שהתא בטווח תקין
                if (!spreadsheet.isIn(x, y)) {
                    return null;
                }

                String cellValue = spreadsheet.value(x, y);
                System.out.println("computeConditionHelper: cell value: '" + cellValue + "'");

// בדיקה אם זה נוסחת sum/min/max/average שלא חושבה כמו שצריך
                if (cellValue != null && (cellValue.equals(Ex2Utils.ERR_FORM) || cellValue.equals(Ex2Utils.ERR_CYCLE)) && spreadsheet.get(x, y) != null) {
                    String raw = spreadsheet.get(x, y).getData();
                    if (raw != null && isFUNCTION(raw)) {
                        System.out.println("computeConditionHelper: retrying computeFUNCTION on raw -> " + raw);
                        Double result = computeFUNCTION(raw);
                        if (result != null) return result;
                    }
                }

// אם התא מכיל פונקציית גיליון
                if (isFUNCTION(cellValue)) {
                    Double result = computeFUNCTION(cellValue);
                    if (result == null || result.equals(Ex2Utils.ERR)) {
                        return Ex2Utils.IF_ERR;
                    }
                    return result;
                }
                // בדיקה אם הערך ריק
                if (cellValue == null || cellValue.isEmpty()) {
                    return Ex2Utils.IF_ERR; // ערך ברירת מחדל לחישובים
                }

                String cellRef = expression.toUpperCase();
                if (cellRef.equalsIgnoreCase(cellValue.trim())) {
                    return Ex2Utils.IF_ERR;
                }
                // אם הערך הוא מספר, החזר אותו כמספר
                if (isNumber(cellValue)) {
                    Double numValue = Double.parseDouble(cellValue);
                    return numValue;
                }

                // אחרת, החזר את הערך כפי שהוא
                return cellValue;
            } catch (Exception e) {
                e.printStackTrace();
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
                Object leftValue = computeConditionHelper(leftPart);
                Object rightValue = computeConditionHelper(rightPart);

                if (leftValue == null || rightValue == null) {
                    return null;
                }

                try {
                    double left = Double.parseDouble(leftValue.toString());
                    double right = Double.parseDouble(rightValue.toString());
                    return c == '+' ? left + right : left - right;
                } catch (NumberFormatException e) {
                    return null;
                }
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
                Object leftValue = computeConditionHelper(leftPart);
                Object rightValue = computeConditionHelper(rightPart);

                if (leftValue == null || rightValue == null) {
                    return null;
                }

                try {
                    double left = Double.parseDouble(leftValue.toString());
                    double right = Double.parseDouble(rightValue.toString());

                    if (c == '*') {
                        return left * right;
                    } else {
                        if (right != 0) {
                            return left / right;
                        } else {
                            return null;
                        }
                    }
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }

        // בדיקה אם זוהי נוסחה
        if (isForm("=" + expression)) {
            Double val = computeForm("=" + expression);
            return val;
        }

        // אם זהו טקסט פשוט
        if (isText(expression)) {
            return expression;
        }

        return null;
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
            return new String[0];
        }
        return new String[]{parts.get(0), parts.get(1), parts.get(2)};
    }


    public static boolean isFUNCTION(String expr) {
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
        if (!isFUNCTION(expr)) {
            return null;
        }

        String expression = expr.substring(1).trim(); // remove '='

        String[] functions = {"min", "max", "sum", "average"};

        for (String func : functions) {
            if (expression.startsWith(func + "(") && expression.endsWith(")")) {

                String rangeText = expression.substring(func.length() + 1, expression.length() - 1).trim();

                if (!rangeText.matches("[A-Za-z]\\d+:[A-Za-z]\\d+")) {
                    return null;
                }

                try {
                    Range2D range = new Range2D(rangeText);
                    Ex2Sheet sheet = (Ex2Sheet) spreadsheet;
                    switch (func) {
                        case "sum":
                            return calcSum(range, sheet);
                        case "min":
                            return calcMin(range, sheet);
                        case "max":
                            return calcMax(range, sheet);
                        case "average":
                            return calcAverage(range, sheet);
                    }
                } catch (Exception e) {
                    return Double.valueOf(Ex2Utils.ERR);
                }
            }
        }
        return null;
    }


}
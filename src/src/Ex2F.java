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

        System.out.println("Start calcSum for range: " + range);

        for (int i = 0; i < cells.size(); i++) {
            CellEntry cell = cells.get(i);
            int x = cell.getX();
            int y = cell.getY();
            System.out.println("Checking cell: (" + x + ", " + y + ")");

            if (sheet.get(x, y) != null) {
                String value = sheet.eval(x, y);
                System.out.println("Value at cell (" + x + ", " + y + ") = " + value);

                if (isNumber(value)) {
                    double num = Double.parseDouble(value);
                    System.out.println("Parsed number: " + num);
                    sum += num;
                    System.out.println("Updated sum: " + sum);
                } else {
                    System.out.println("Not a number: " + value);
                }
            } else {
                System.out.println("Cell is null");
            }
        }

        System.out.println("Final sum: " + sum);
        return sum;
    }

    private static double calcMin(Range2D range, Ex2Sheet sheet) {
        double min = Double.MAX_VALUE;
        List<CellEntry> cells = range.getCellsInRange();
        boolean foundNumber = false;
        for (int i = 0; i < cells.size(); i++) {
            CellEntry cell = cells.get(i);
            int x = cell.getX();
            int y = cell.getY();

            if (sheet.get(x, y) != null) {
                String value = sheet.eval(x, y);
                if (isNumber(value)) {
                    double num = Double.parseDouble(value);
                    if (num < min) {
                        min = num;
                    }

                }
            }
        }
        if (foundNumber == true) {
            return min;
        } else {
            return Ex2Utils.ERR;
        }
    }


    private static double calcMax(Range2D range, Ex2Sheet sheet) {
        double max = Double.MAX_VALUE;
        List<CellEntry> cells = range.getCellsInRange();
        boolean foundNumber = false;
        for (int i = 0; i < cells.size(); i++) {
            CellEntry cell = cells.get(i);
            int x = cell.getX();
            int y = cell.getY();

            if (sheet.get(x, y) != null) {
                String value = sheet.eval(x, y);
                if (isNumber(value)) {
                    double num = Double.parseDouble(value);
                    if (num > max) {
                        max = num;
                    }

                }
            }
        }
        if (foundNumber == true) {
            return max;
        } else {
            return Ex2Utils.ERR;
        }
    }


    private static double calcAverage(Range2D range, Ex2Sheet sheet) {
        double sum = 0;
        double count = 0;
        List<CellEntry> cells = range.getCellsInRange();
        for (int i = 0; i < cells.size(); i++) {
            CellEntry cell = cells.get(i);
            int x = cell.getX();
            int y = cell.getY();

            if (sheet.get(x, y) != null) {
                String value = sheet.eval(x, y);
                if (isNumber(value)) {
                    double num = Double.parseDouble(value);
                    sum += num;
                    count += num;
                }
            }
        }
        if (count > 0) {
            double average = sum / count;
            return average;

        } else {
            return Ex2Utils.ERR;
        }

    }

    private static String IfFunction(String expression) {
        expression = removeOuterParentheses(expression);
        String[] parts = splitIfExpression(expression);
        String condition = parts[0];
        String ifTrue = parts[1];
        String ifFalse = parts[2];
        String[] operators = {"==", "!=", "<=", ">=", "<", ">"};
        String operator = null;
        int opIndex = -1;

        for (int i = 0; i < operators.length; i++) {
            String currentOp = operators[i];
            int index = condition.indexOf(currentOp);
            if (index != -1) {
                operator = currentOp;
                opIndex = index;
                break;
            }
        }
        String Formula1 = condition.substring(0, opIndex).trim();
        String Formula2 = condition.substring(opIndex + operator.length()).trim();
        Double v1 = computeForm(Formula1);
        Double v2 = computeForm(Formula2);
        if (v1 == null || v2 == null) {
            return Ex2Utils.IF_ERR;
        }

        boolean conditionResult = false;
        if (operator.equals("==")) {
            conditionResult = Double.compare(v1, v2) == 0;
        } else if (operator.equals("!=")) {
            conditionResult = Double.compare(v1, v2) != 0;
        } else if (operator.equals("<")) {
            conditionResult = v1 < v2;
        } else if (operator.equals(">")) {
            conditionResult = v1 > v2;
        } else if (operator.equals("<=")) {
            conditionResult = v1 <= v2;
        } else if (operator.equals(">=")) {
            conditionResult = v1 >= v2;
        }

        String chosen = conditionResult ? ifTrue : ifFalse;

        if (isText(chosen)) {
            return chosen;
        }

        Double result = computeForm(chosen);
        if (result == null) {
            return Ex2Utils.IF_ERR;
        }

        return result.toString();
    }


    private static String[] splitIfExpression(String expression) {
        expression = removeOuterParentheses(expression);
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
        return null;
    }
}

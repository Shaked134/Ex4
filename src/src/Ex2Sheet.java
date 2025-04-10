package src;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
// Add your documentation below:

public class Ex2Sheet implements Sheet {
    private Cell[][] table;

    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for (int i = 0; i < x; i = i + 1) {
            for (int j = 0; j < y; j = j + 1) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
        eval();
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public String value(int x, int y) {
        String ans = Ex2Utils.EMPTY_CELL;
        Cell c = get(x, y);
        if (c != null) {
            if (c.getType() == Ex2Utils.ERR_FORM_FORMAT) {
                ans = Ex2Utils.ERR_FORM;
            } else if (c.getType() == Ex2Utils.ERR_CYCLE_FORM) {
                ans = Ex2Utils.ERR_CYCLE;
            } else if (c.getType() == Ex2Utils.FORM) {
                ans = eval(x, y);  // For formulas, return evaluated result
            } else if (c.getType() == Ex2Utils.NUMBER) {
                ans = Double.parseDouble(c.getData()) + "";
            } else {
                ans = c.getData();
            }
        }

        return ans;
    }

    @Override
    public Cell get(int x, int y) {
        if (!isIn(x, y)) return null;
        return table[x][y];
    }

    @Override
    public Cell get(String cords) {
        Cell ans = null;
        if (cords != null && !cords.isEmpty()) {
            CellEntry entry = new CellEntry(cords);
            if (entry.isValid()) {
                ans = get(entry.getX(), entry.getY());
            }
        }
        return ans;
    }

    @Override
    public int width() {
        return table.length;
    }

    @Override
    public int height() {
        return table[0].length;
    }

    @Override
    public void set(int x, int y, String s) {

        Ex2F.setSpreadsheet(this);


        if (!isIn(x, y)) return;  // Check if coordinates are valid
        if (s == null) s = Ex2Utils.EMPTY_CELL;  // Handle null input

        Cell c = new SCell(s);
        table[x][y] = c;
        eval();
    }

    @Override
    public void eval() {
        Ex2F.setSpreadsheet(this);

        int[][] dd = depth();
        int maxDepth = 0;

        // Find maximum depth
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                if (dd[i][j] > maxDepth) maxDepth = dd[i][j];
            }
        }


        // Evaluate in dependency order
        for (int d = 0; d <= maxDepth; d++) {
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    Cell cell = get(i, j);

                    // Check for cycles first
                    if (dd[i][j] == -1) {
                        if (cell != null) {
                            cell.setType(Ex2Utils.ERR_CYCLE_FORM);
                        }
                    }
                    // Then evaluate cells at current depth
                    else if (dd[i][j] == d) {
                        eval(i, j);
                    }
                }
            }
        }
    }

    @Override
    public boolean isIn(int xx, int yy) {
        return xx >= 0 && xx < width() && yy >= 0 && yy < height();
    }

    @Override
    public int[][] depth() {

        Ex2F.setSpreadsheet(this);


        int[][] ans = new int[width()][height()];
        boolean[][] visited = new boolean[width()][height()];

        // Initialize to -2 (not visited)
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                ans[i][j] = -2;
            }
        }

        // Calculate depth for each cell
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                if (ans[i][j] == -2) {
                    calculateDepth(i, j, ans, visited);
                }
            }
        }

        return ans;
    }

    private int calculateDepth(int x, int y, int[][] depths, boolean[][] visiting) {
        // Already marked as cyclic
        if (depths[x][y] == -1) return -1;

        // Currently visiting - found a cycle
        if (visiting[x][y]) {
            depths[x][y] = -1;
            get(x, y).setType(Ex2Utils.ERR_CYCLE_FORM);
            return -1;
        }

        // Already calculated non-cyclic depth
        if (depths[x][y] >= 0) return depths[x][y];

        visiting[x][y] = true;
        Cell cell = get(x, y);

        if (cell == null || cell.getType() != Ex2Utils.FORM) {
            depths[x][y] = 0;
            visiting[x][y] = false;
            return 0;
        }

        List<CellEntry> refs = findCellReferences(cell.getData());
        int maxDepth = 0;
        boolean hasCycle = false;

        for (CellEntry ref : refs) {
            if (ref.isValid() && isIn(ref.getX(), ref.getY())) {
                int depthResult = calculateDepth(ref.getX(), ref.getY(), depths, visiting);
                if (depthResult == -1) {
                    hasCycle = true;
                    break;  // Found a cycle, no need to check other references
                }
                maxDepth = Math.max(maxDepth, depthResult);
            }
        }

        visiting[x][y] = false;

        if (hasCycle) {
            depths[x][y] = -1;
            cell.setType(Ex2Utils.ERR_CYCLE_FORM);
            return -1;
        } else {
            depths[x][y] = maxDepth + 1;
            return depths[x][y];
        }
    }


    @Override
    public void load(String fileName) throws IOException {
        // Clear current spreadsheet
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }

        Ex2F.setSpreadsheet(this);


        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // Skip header line
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                // Split by comma
                String[] parts = line.split(",");

                // Check if line has correct format
                if (parts.length >= 3) {
                    try {
                        int x = Integer.parseInt(parts[0].trim());
                        int y = Integer.parseInt(parts[1].trim());
                        String value = parts[2].trim();

                        // Check if there are additional fields
                        if (parts.length > 3) {
                            // There might be a comment in the value part or additional fields
                            // We'll assume that if parts[3] can be parsed as an integer, it's a type
                            try {
                                int type = Integer.parseInt(parts[3].trim());

                                // If it has a type, we'll check for an order
                                if (parts.length > 4) {
                                    int order = Integer.parseInt(parts[4].trim());

                                    // Set cell if coordinates are valid
                                    if (isIn(x, y)) {
                                        set(x, y, value);
                                        Cell cell = get(x, y);
                                        if (cell != null) {
                                            cell.setType(type);
                                            cell.setOrder(order);
                                        }
                                    }
                                } else {
                                    // Only type is available
                                    if (isIn(x, y)) {
                                        set(x, y, value);
                                        Cell cell = get(x, y);
                                        if (cell != null) {
                                            cell.setType(type);
                                        }
                                    }
                                }
                            } catch (NumberFormatException e) {
                                // The part after value is not a type, so we treat it as a comment
                                if (isIn(x, y)) {
                                    set(x, y, value);
                                }
                            }
                        } else {
                            // Basic format: just x, y, value
                            if (isIn(x, y)) {
                                set(x, y, value);
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid lines
                        continue;
                    }
                }
            }
        }

        // Re-evaluate all cells after loading
        eval();
    }

    @Override
    public void save(String fileName) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            // Write header
            writer.println("I2CS ArielU: SpreadSheet (Ex2) assignment");

            // Save non-empty cells
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    Cell cell = get(i, j);
                    if (cell != null) {
                        String data = cell.getData();
                        // Only save non-empty cells
                        if (!data.equals(Ex2Utils.EMPTY_CELL)) {
                            // Save cell data, type and order
                            writer.printf("%d,%d,%s,%d,%d%n", i, j, data, cell.getType(), cell.getOrder());
                        }
                    }
                }
            }
        }
    }

    @Override
    public String eval(int x, int y) {
        if (!isIn(x, y)) return null;
        Cell cell = get(x, y);
        if (cell == null) return null;

        if (hasCycle(x, y, new boolean[width()][height()])) {
            cell.setType(Ex2Utils.ERR_CYCLE_FORM);
            return Ex2Utils.ERR_CYCLE;
        }

        switch (cell.getType()) {
            case Ex2Utils.TEXT:
            case Ex2Utils.NUMBER:
                return cell.getData();
            case Ex2Utils.FORM:
                Ex2F.setSpreadsheet(this);
                String data = cell.getData();

                if (data.startsWith("=if(")) {
                    String result = Ex2F.IfFunction(data);
                    if (!result.equals(Ex2Utils.IF_ERR)) {
                        return result;
                    }
                    cell.setType(Ex2Utils.ERR_FORM_FORMAT);
                    return Ex2Utils.ERR_FORM;
                }

                if (Ex2F.isFUNCTION(data)) {
                    Double result = Ex2F.computeFUNCTION(data);
                    if (result != null) {
                        return result.toString();
                    }
                    cell.setType(Ex2Utils.ERR_FORM_FORMAT);
                    return Ex2Utils.ERR_FORM;
                }

// Handling regular formulas
                Double result = Ex2F.computeForm(data);
                if (result != null) {
                    return result.toString();
                }
                cell.setType(Ex2Utils.ERR_FORM_FORMAT);
                return Ex2Utils.ERR_FORM;
            case Ex2Utils.ERR_CYCLE_FORM:
                return Ex2Utils.ERR_CYCLE;
            case Ex2Utils.ERR_FORM_FORMAT:
                return Ex2Utils.ERR_FORM;
            default:
                return "#ERR";
        }
    }

    private boolean hasCycle(int x, int y, boolean[][] visited) {
        Cell cell = get(x, y);

        // If cell is already marked as cyclic
        if (cell != null && cell.getType() == Ex2Utils.ERR_CYCLE_FORM) {
            return true;
        }

        // If we're visiting this cell again in the same path
        if (visited[x][y]) {
            return true;
        }

        visited[x][y] = true;

        // If it's a formula, check its dependencies
        if (cell != null && cell.getType() == Ex2Utils.FORM) {
            List<CellEntry> refs = findCellReferences(cell.getData());
            for (CellEntry ref : refs) {
                if (ref.isValid() && isIn(ref.getX(), ref.getY())) {
                    if (hasCycle(ref.getX(), ref.getY(), visited)) {
                        return true;
                    }
                }
            }
        }

        visited[x][y] = false;
        return false;
    }
// Finds all cell references within a given formula (e.g., =A1+B2, =sum(A1:B2), =if(...))
private List<CellEntry> findCellReferences(String formula) {
    List<CellEntry> refs = new ArrayList<>();
    if (formula == null || !formula.startsWith("=")) return refs;

    // Handle function with Range2D, e.g., =sum(A1:B2)
    if (Ex2F.isFUNCTION(formula)) {
        int openParen = formula.indexOf('(');
        int closeParen = formula.lastIndexOf(')');
        if (openParen == -1 || closeParen == -1 || closeParen <= openParen) return refs;

        String funcContent = formula.substring(openParen + 1, closeParen);
        if (funcContent.contains(":")) {
            try {
                Range2D range = new Range2D(funcContent);
                refs.addAll(range.getCellsInRange());
                return refs;
            } catch (Exception e) {
                // Invalid range – ignore
            }
        }
    }

    // Handle IF function: =if(condition, ifTrue, ifFalse)
    if (formula.startsWith("=if(")) {
        int closeParen = formula.lastIndexOf(')');
        if (closeParen == -1 || closeParen <= 4) return refs;

        String ifContent = formula.substring(4, closeParen); // safely extract inside of if(...)
        String[] parts = ifContent.split(",", -1); // include empty parts

        if (parts.length == 3) {
            for (String part : parts) {
                refs.addAll(findCellReferencesInExpression(part));
            }
            return refs;
        }
    }

    // Handle regular formulas (e.g., =A1+B2)
    refs.addAll(findCellReferencesInExpression(formula.substring(1)));
    return refs;
}


    // Helper function that parses an expression and extracts all valid cell references (e.g., A1, B2)

    private List<CellEntry> findCellReferencesInExpression(String expr) {
        List<CellEntry> refs = new ArrayList<>();

        if (expr == null || expr.isEmpty()) {
            return refs;
        }

        StringBuilder currentWord = new StringBuilder();
        boolean insideWord = false;

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (Character.isLetterOrDigit(c)) {
                currentWord.append(c);
                insideWord = true;
            } else {
                if (insideWord) {
                    // Building a potential cell reference token
                    String word = currentWord.toString();
                    if (Ex2F.isCellReference(word)) {
                        CellEntry entry = new CellEntry(word);
                        if (entry.isValid()) {
                            refs.add(entry);
                        }
                    }
                    // Reset the word

                    currentWord.setLength(0);
                    insideWord = false;
                }
            }
        }

        // Handle the last token if needed
        if (insideWord) {
            String word = currentWord.toString();
            if (Ex2F.isCellReference(word)) {
                CellEntry entry = new CellEntry(word);
                if (entry.isValid()) {
                    refs.add(entry);
                }
            }
        }

        return refs;
    }
}
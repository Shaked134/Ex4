package src;
// Add your documentation below:

public class CellEntry  implements Index2D {
    private final String entry;
    private final int x;
    private final int y;


    //  Constructor that takes a cell reference string
   // @param entry The cell reference (e.g., "B3")

    public CellEntry(String entry) {
        this.entry = entry;
        if (isValidFormat(entry)) {
            this.x = convertColumnToIndex(entry);
            this.y = convertRowToIndex(entry);
        } else {
            this.x = Ex2Utils.ERR;
            this.y = Ex2Utils.ERR;
        }
    }


   //  Constructor that takes x and y coordinates
   //  @param x Column index (0-based)
 //   @param y Row index

    public CellEntry(int x, int y) {
        this.x = x;
        this.y = y;
        this.entry = convertToString(x, y);
    }

    @Override
    public boolean isValid() {
        return x != Ex2Utils.ERR && y != Ex2Utils.ERR &&
                x >= 0 && x < Ex2Utils.ABC.length && y >= 0 && y < 100;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public String toString() {
        if (isValid()) {
            return entry;
        } else {
            return "";
        }

    }


    // Checks if the input string has valid format
    // @param s The input string to check
 //   @return true if format is valid

    private boolean isValidFormat(String s) {
        if (s == null || s.isEmpty()) return false;

        // First character must be a letter
        char first = s.charAt(0);
        if (!Character.isLetter(first)) return false;

        // Rest must be a valid number
        String numPart = s.substring(1);
        try {
            int num = Integer.parseInt(numPart);
            return num >= 0 && num < 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }


   //  Converts column letter to index (A=0, B=1, etc.)
    // @param s The cell reference string
  //   @return The column index or ERR if invalid

    private int convertColumnToIndex(String s) {
        char col = Character.toUpperCase(s.charAt(0));
        if (col >= 'A' && col <= 'Z') {
            return col - 'A';
        }
        return Ex2Utils.ERR;
    }


  //  Extracts the row number from the cell reference
  //  @param s The cell reference string
  //  @return The row number or ERR if invalid

    private int convertRowToIndex(String s) {
        try {
            String numPart = s.substring(1);
            int num = Integer.parseInt(numPart);
            if (num >= 0 && num < 100) {
                return num;
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            // Do nothing, will return ERR
        }
        return Ex2Utils.ERR;
    }


    //  Converts coordinates to cell reference string
      // @param x Column index
     // @param y Row index
      //@return The cell reference string

    private String convertToString(int x, int y) {
        if (x >= 0 && x < 26 && y >= 0 && y < 100) {
            char col = (char)('A' + x);
            return col + String.valueOf(y);
        }
        return "";
    }


}

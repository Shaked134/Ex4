import src.CellEntry;
import src.Range2D;

public class Range2Dtest {

        public static void main(String[] args) {
            // יצירת טווח לדוגמה
            Range2D range = new Range2D("A1:C3");

            // הדפסת התא ההתחלתי והתא הסופי
            System.out.println("Start Cell: " + range.getStartCell());
            System.out.println("End Cell: " + range.getEndCell());

            // בדיקת כל התאים בטווח
            System.out.println("Cells in Range:");
            for (CellEntry cell : range.getCellsInRange()) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }





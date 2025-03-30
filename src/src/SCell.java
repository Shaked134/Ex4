package src;

import java.util.ArrayList;
import java.util.List;
// Add your documentation below:

public class SCell implements Cell {
    private String line;
    private int type;
    private int order;
    private List<Cell> dependantCells;


    public SCell(String s) {
        this.dependantCells = new ArrayList<>();
        this.line = s;
        setData(s);
    }

    @Override
    public int getOrder() {
        if (type == Ex2Utils.TEXT || type == Ex2Utils.NUMBER) {
            return 0;
        }
        if (type == Ex2Utils.FORM) {
            int maxOrder = 0;
            for (int i = 0; i < dependantCells.size(); i++) {
                Cell dependantCell = this.dependantCells.get(i);
                maxOrder = Math.max(maxOrder, dependantCell.getOrder());
            }
            return maxOrder + 1;
        }
        return order;
    }

    @Override
    public String toString() {
        return getData();
    }

    @Override
    public void setData(String s) {


        if (s == null) {
            this.line = Ex2Utils.EMPTY_CELL;
            this.type = Ex2Utils.TEXT;
            return;
        }

        this.line = s;

        if (s.isEmpty()) {
            this.type = Ex2Utils.TEXT;
        } else if (Ex2F.isNumber(s)) {
            this.type = Ex2Utils.NUMBER;
        } else if (s.startsWith("=if(")) {
            // בדיקה אם זו פונקציית IF תקינה
            String result = Ex2F.IfFunction(s);



            if (result.equals(Ex2Utils.IF_ERR)) {
                this.type = Ex2Utils.ERR_FORM_FORMAT;
            } else {
                this.type = Ex2Utils.FORM;
            }
        } else if (Ex2F.isFUNCTION(s)) {
            Double result = Ex2F.computeFUNCTION(s);
            if (result == null) {
                this.type = Ex2Utils.ERR_FORM_FORMAT;
            } else {
                this.type = Ex2Utils.FORM;
            }
        } else if (Ex2F.isForm(s)) {
            this.type = Ex2Utils.FORM;
        } else if (Ex2F.isText(s)) {
            this.type = Ex2Utils.TEXT;
        } else {
            this.type = Ex2Utils.ERR_FORM_FORMAT;
        }
    }

    @Override
    public String getData() {
        return line;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        type = t;
    }

    @Override
    public void setOrder(int t) { this.order = t; }

    public List<Cell> getDependantCells() {
        return dependantCells;
    }
}

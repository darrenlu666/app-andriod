package com.codyy.erpsportal.statistics.models.entities;

/**
 * Created by gujiajia on 2016/5/31.
 */
public class StatRow extends AbsStatRow {

    private StatCell[] mStatCells;

    public StatRow() {
        super();
    }

    public StatRow(String rowTitle, StatCell... cells) {
        super(rowTitle);
        this.mStatCells = cells;
    }

    public StatRow(String rowTitle) {
        super(rowTitle);
    }

    public StatRow(String rowTitle, Object... numbers) {
        super(rowTitle);
        this.mStatCells = new StatCell[numbers.length];
        for (int i=0; i<numbers.length; i++) {
            mStatCells[i] = new StatCell(numbers[i].toString());
        }
    }

    public StatRow(String rowTitle, String... numbers) {
        super(rowTitle);
        this.mStatCells = new StatCell[numbers.length];
        for (int i=0; i<numbers.length; i++) {
            mStatCells[i] = new StatCell(numbers[i]);
        }
    }

    public StatCell[] getStatCells() {
        return mStatCells;
    }

    public void setStatCells(StatCell[] statCells) {
        mStatCells = statCells;
    }

    @Override
    public String getCellByIndex(int index) {
        if (mStatCells == null) return "";
        if (index >=0 && index < mStatCells.length) {
            return mStatCells[index].getText();
        } else {
            return "";
        }
    }

    @Override
    public int getCellStatus(int index) {
        if (mStatCells == null) return 0;
        if (index >=0 && index < mStatCells.length) {
            return mStatCells[index].getStatus();
        } else {
            return 0;
        }
    }
}

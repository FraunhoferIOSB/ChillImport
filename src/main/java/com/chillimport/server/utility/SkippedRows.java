package com.chillimport.server.utility;

public class SkippedRows {

    private int row;
    private Exception error;


    /**
     * Constructor. Assigns the row index and the according Exception to the class attributes.
     *
     * @param row   table row
     * @param error occured exception
     */
    public SkippedRows(int row, Exception error) {
        this.row = row;
        this.error = error;
    }

    /**
     * Returns the row index
     *
     * @return index
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the Exception that occurred
     *
     * @return occured Exception
     */
    public Exception getError() {
        return error;
    }
}

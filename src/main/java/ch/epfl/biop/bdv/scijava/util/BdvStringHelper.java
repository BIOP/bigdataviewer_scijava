package ch.epfl.biop.bdv.scijava.util;

import java.util.ArrayList;
import java.util.function.Consumer;

public class BdvStringHelper {

    /**
     * Convert a comma separated list of indexes into an arraylist of integer
     *
     * For instance 1,2,5-7,10-12,14 returns an ArrayList containing
     * [1,2,5,6,7,10,11,12,14]
     *
     * Invalid format are ignored and an error message is displayed
     *
     * @param expression
     * @return list of indexes in ArrayList
     */

    static public ArrayList<Integer> commaSeparatedListToArray(String expression) {
        Consumer<String> log = str -> System.err.println(str);

        String[] splitIndexes = expression.split(",");
        ArrayList<java.lang.Integer> arrayOfIndexes = new ArrayList<>();
        for (String str : splitIndexes) {
            str.trim();
            if (str.contains("-")) {
                // Array of source, like 2-5 = 2,3,4,5
                String[] boundIndex = str.split("-");
                if (boundIndex.length==2) {
                    try {
                        int binf = java.lang.Integer.valueOf(boundIndex[0].trim());
                        int bsup = java.lang.Integer.valueOf(boundIndex[1].trim());
                        for (int index = binf; index <= bsup; index++) {
                            arrayOfIndexes.add(index);
                        }
                    } catch (NumberFormatException e) {
                        log.accept("Number format problem with expression:"+str+" - Expression ignored");
                    }
                } else {
                    log.accept("Cannot parse expression "+str+" to pattern 'begin-end' (2-5) for instance, omitted");
                }
            } else {
                // Single source
                try {
                    int index = java.lang.Integer.valueOf(str.trim());
                    arrayOfIndexes.add(index);
                } catch (NumberFormatException e) {
                    log.accept("Number format problem with expression:"+str+" - Expression ignored");
                }
            }
        }
        return arrayOfIndexes;
    }
}

package nolscorecalculator;

import IofXml30.java.PersonResult;

import java.util.Comparator;

/**
 *
 * @author shep
 */
public class PersonResultCompare implements Comparator<PersonResult> {

    @Override
    public int compare(PersonResult r1, PersonResult r2) {

        // -1 if o1 comes before o2,
        // +1 if 01 comes after o2
        // 0 if o1 and o2 are equal

        if (r1.getResult().get(0).getTime() == null && r2.getResult().get(0).getTime() == null) {
            return 0;
        }
        else {
            if (r1.getResult().get(0).getTime() == null) {
                return 1;
            }
            if (r2.getResult().get(0).getTime() == null) {
                return -1;
            }
        }

        int r1Value = r1.getResult().get(0).getTime().intValue();
        int r2Value = r2.getResult().get(0).getTime().intValue();

        if (r1Value < r2Value){
            return -1;
        }
        else if (r1Value > r2Value){
            return 1;
        }
        else {
            return 0;
        }
    }
}
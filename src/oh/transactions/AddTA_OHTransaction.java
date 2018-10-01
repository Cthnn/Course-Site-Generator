package oh.transactions;

import jtps.jTPS_Transaction;
import oh.data.OfficeHoursData;
import oh.data.TeachingAssistantPrototype;
import oh.data.TimeSlot;

/**
 *
 * @author McKillaGorilla
 */
public class AddTA_OHTransaction implements jTPS_Transaction {
    private int col;
    TimeSlot times;
    TeachingAssistantPrototype ta;
    boolean added;
    OfficeHoursData data; 
    public AddTA_OHTransaction(int col, TimeSlot times, TeachingAssistantPrototype ta, OfficeHoursData data){
        this.col = col;
        this.times = times;
        this.ta = ta;
        this.data = data;
    }

    @Override
    public void doTransaction() {
        if(times.exists(ta,col)){
            times.removingTA(ta, col);
            added = false;
        }else{
            times.addToTAs(ta, col);  
            added = true;
        }
        for (int i = 0; i < data.getTAs().size(); i++) {
            if (data.getTAs().get(i).getName().equals(ta.getName())){
                if(added){
                    data.getTAs().get(i).setTimeSlot(data.getTAs().get(i).getTimeSlot() + 1);
                }else{
                    data.getTAs().get(i).setTimeSlot(data.getTAs().get(i).getTimeSlot() - 1);
                }
            }
        }
    }

    @Override
    public void undoTransaction() {
        if(added){
            times.removingTA(ta, col);
            added = false;
        }else{
            times.addToTAs(ta, col);
            added = true;
        }
        for (int i = 0; i < data.getTAs().size(); i++) {
            if (data.getTAs().get(i).getName().equals(ta.getName())){
                if(added){
                    data.getTAs().get(i).setTimeSlot(data.getTAs().get(i).getTimeSlot() + 1);
                }else{
                    data.getTAs().get(i).setTimeSlot(data.getTAs().get(i).getTimeSlot() - 1);
                }
            }
        }
    }

}

package in.izzulmak.inpenseexcomeoutsight;

import android.view.MenuItem;
import android.widget.CompoundButton;

import java.util.ArrayList;

/**
 * Created by makin on 11/08/15.
 */
public class listClickedClass implements CompoundButton.OnCheckedChangeListener {
    private static ArrayList<Integer> selected_ids;
    public static Integer selected_idsIndex(Integer x) {return selected_ids.indexOf(x);}
    public static void selected_idsAdd(Integer x) {selected_ids.add(x);}
    public static void selected_idsRemove(int index) {selected_ids.remove(index);}
    public static int selected_idsCount() { return selected_ids.size();}
    private Integer thisindex;
    public listClickedClass(Integer vthisindex)
    {
        thisindex = vthisindex;
        selected_ids = new ArrayList<Integer>();
    }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        MenuItem deletethese = EditexpenseActivity.editExpenseRoomMenu.findItem(R.id.action_editexpense_delete);
        MenuItem editthis = EditexpenseActivity.editExpenseRoomMenu.findItem(R.id.action_editexpense_edit);

        if (b) {
            if (this.selected_idsIndex(thisindex) == -1) {
                this.selected_idsAdd(thisindex);
            }
        } else {
            if (this.selected_idsIndex(thisindex) != -1) {
                this.selected_idsRemove(this.selected_idsIndex(thisindex));
            }
        }
        int checkedtotal = this.selected_idsCount();
        if (checkedtotal == 0) {
            editthis.setVisible(false);
            deletethese.setVisible(false);
        }
        else if (checkedtotal == 1) {
            editthis.setVisible(true);
            deletethese.setVisible(true);
        } else {
            editthis.setVisible(false);
            deletethese.setVisible(true);
        }
    }
}

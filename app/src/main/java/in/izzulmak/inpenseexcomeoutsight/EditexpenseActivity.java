package in.izzulmak.inpenseexcomeoutsight;

import android.app.ActionBar;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.ArrayList;
import java.util.Calendar;

public class EditexpenseActivity extends ActionBarActivity {
    public static Menu editExpenseRoomMenu;
    private ArrayList<Integer> incomesexpenseslist_ids; //-- carries the listed data from `incomesexpenses`
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editexpense);

        incomesexpenseslist_ids = new ArrayList<Integer>();
        LinearLayout container = (LinearLayout) findViewById(R.id.ll_Editexpense);
        SQLiteDatabase db = openOrCreateDatabase(getResources().getString(R.string.databasename), MODE_PRIVATE, null);
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH)+1;
        String thismonth = ""+mYear+"-"+String.format("%02d",mMonth)+"-01";
        Cursor dbv_dates = db.rawQuery("SELECT date FROM incomesexpenses WHERE type='EXPENSE' AND date BETWEEN DATE('"+thismonth+"') AND DATE('"+thismonth+"','+1 month', '-1 day') GROUP BY date; ",null);
        while (dbv_dates.moveToNext())
        {
            String thisdate = dbv_dates.getString(0);

            Button bt = new Button (container.getContext());
            bt.setText(thisdate);
            container.addView(bt);

            final LinearLayout ll = new LinearLayout(container.getContext());
            ll.setOrientation(LinearLayout.VERTICAL);

            final Cursor dbv_thisdate = db.rawQuery(
                    "SELECT incomesexpenses.*, accounts.name FROM incomesexpenses " +
                        "LEFT JOIN accounts ON incomesexpenses.from_account_id=accounts.id " +
                        "WHERE incomesexpenses.type='EXPENSE' " +
                            "AND incomesexpenses.date='"+thisdate+"' ORDER BY date;"
                    , null);
            int btcid = 500;
            while (dbv_thisdate.moveToNext())
            {
                Integer thisindex = dbv_thisdate.getInt(dbv_thisdate.getColumnIndex("id"));

                ToggleButton btc = new ToggleButton(ll.getContext());
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                btc.setLayoutParams(lp);
                btc.setId(100 + thisindex.intValue()); //-- the edit each item index is 100+`incomesexpenses`.`id`
                incomesexpenseslist_ids.add(thisindex);
                btc.setBackgroundResource(R.layout.listbutton);
                String thetext = dbv_thisdate.getString(dbv_thisdate.getColumnIndex("accounts.name")) + " \t " + dbv_thisdate.getString(dbv_thisdate.getColumnIndex("incomesexpenses.amount"));
                btc.setText(thetext);
                btc.setTextOff(thetext);
                btc.setTextOn("[ "+thetext+" ]");

                //btc.setText(thisindex.toString());/*/
                btc.setOnCheckedChangeListener(new listClickedClass(thisindex));//*/
                ll.addView(btc);
            }
            container.addView(ll);
            LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            ll.setLayoutParams(lllp);
            dbv_thisdate.close();
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ll.getLayoutParams().height == 0) {
                        LinearLayout.LayoutParams lllp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        ll.setLayoutParams(lllp2);
                    } else {
                        LinearLayout.LayoutParams lllp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                        ll.setLayoutParams(lllp2);
                    }
                }
            });
        }
        dbv_dates.close();
        db.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        editExpenseRoomMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editexpense, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void menuEditThis(MenuItem item)
    {
        /*Integer selectedItem;
        int i;
        for (i=0;i<incomesexpenseslist_ids.size();i+=1)
        {
            try {
                ToggleButton evaluatedbutton = ((ToggleButton) findViewById(incomesexpenseslist_ids.get(i) + 100));
                if (evaluatedbutton.isChecked()) {
                    selectedItem = i;
                    evaluatedbutton.setText("Terkeculah Button INI");
                    break;
                }
            }
            catch (Exception e)
            {
                continue;
            }
        }*/

    }

    public void menuDeleteThese(MenuItem item)
    {

    }
}

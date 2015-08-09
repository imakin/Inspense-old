package in.izzulmak.inpenseexcomeoutsight;

import android.app.ActionBar;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Calendar;


public class EditexpenseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editexpense);

        LinearLayout container = (LinearLayout) findViewById(R.id.ll_Editexpense);

        SQLiteDatabase db = openOrCreateDatabase(getResources().getString(R.string.databasename), MODE_PRIVATE, null);
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH)+1;
        String thismonth = ""+mYear+"-"+String.format("%02d",mMonth)+"-01";
        Cursor dbv_dates = db.rawQuery("SELECT date FROM incomesexpenses WHERE date BETWEEN DATE('"+thismonth+"') AND DATE('"+thismonth+"','+1 month', '-1 day') GROUP BY date; ",null);
        while (dbv_dates.moveToNext())
        {
            String thisdate = dbv_dates.getString(0);

            Button bt = new Button (container.getContext());
            bt.setText(thisdate);
            container.addView(bt);

            final LinearLayout ll = new LinearLayout(container.getContext());
            ll.setOrientation(LinearLayout.VERTICAL);

            Cursor dbv_thisdate = db.rawQuery(
                    "SELECT incomesexpenses.*, accounts.name FROM incomesexpenses " +
                        "LEFT JOIN accounts ON incomesexpenses.from_account_id=accounts.id " +
                        "WHERE incomesexpenses.type='EXPENSE' " +
                            "AND incomesexpenses.date='"+thisdate+"' ORDER BY date;"
                    , null);
            while (dbv_thisdate.moveToNext())
            {
                Button btc = new Button(ll.getContext());
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                btc.setLayoutParams(lp);
                btc.setBackgroundResource(R.layout.listbutton);
                btc.setText(dbv_thisdate.getString(dbv_thisdate.getColumnIndex("accounts.name")) + " \t " + dbv_thisdate.getString(dbv_thisdate.getColumnIndex("incomesexpenses.amount")));
                ll.addView(btc);
            }
            container.addView(ll);
            //ll.setVisibility(View.INVISIBLE);
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
}

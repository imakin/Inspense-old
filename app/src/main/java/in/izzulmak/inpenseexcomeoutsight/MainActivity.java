package in.izzulmak.inpenseexcomeoutsight;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class MainActivity extends ActionBarActivity {
    static int dbv_baseAccount_id;
    String dbv_baseAccount_name;
    static TextView tv_Accountname;
    final static int ROOM_ADDINCOME_ID = 1;
    final static int ROOM_CHANGEBASEACCOUNT_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button bt_Addincome = (Button) findViewById(R.id.bt_Addincome);
        tv_Accountname =((TextView) findViewById(R.id.tv_Accountname));
        //-- Database
        SQLiteDatabase db = openOrCreateDatabase(getResources().getString(R.string.databasename),MODE_PRIVATE,null);
        /* reset
        db.execSQL("DROP TABLE IF EXISTS accounts;");
        db.execSQL("DROP TABLE IF EXISTS account_balances;");
        db.execSQL("DROP TABLE IF EXISTS incomesexpenses;");
        db.execSQL("DROP TABLE IF EXISTS settings;");

        //*/
        Cursor dbv_Accounttable = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='accounts';",null);
        if (dbv_Accounttable.getCount()<1)
        {
            //-- no table yet, let's create all of them
            db.execSQL("CREATE TABLE IF NOT EXISTS accounts(id INT, name VARCHAR, type VARCHAR, balance INT, enabled BOOLEAN)"); //-- Main accounts
            db.execSQL("CREATE TABLE IF NOT EXISTS account_balances(id INT, base_account_id INT, balance_before INT, balance INT, date DATE)");
            db.execSQL("CREATE TABLE IF NOT EXISTS incomesexpenses(id INT, base_account_id INT, from_account_id INT, description VARCHAR, type VARCHAR, amount INT, date DATE)");
            db.execSQL("CREATE TABLE IF NOT EXISTS settings(name VARCHAR, value VARCHAR)");

            db.execSQL("INSERT INTO accounts VALUES(1, 'Cash in Hand', 'BASE', 0, 1)");
            db.execSQL("INSERT INTO accounts VALUES(2, 'Bank', 'BASE', 0 ,1)");

            //-- basic accounts
            db.execSQL("INSERT INTO accounts VALUES(3, 'Main Income',      'INCOME',   0, 1)");
            db.execSQL("INSERT INTO accounts VALUES(4, 'Job Salary',       'INCOME',   0, 1)");
            db.execSQL("INSERT INTO accounts VALUES(5, 'Remaining Cash',   'INCOME',   0, 1)"); //-- remaining cash in hand
            db.execSQL("INSERT INTO accounts VALUES(6, 'Eating',           'EXPENSE',  0, 1)");
            db.execSQL("INSERT INTO accounts VALUES(7, 'Transportation',   'EXPENSE',  0, 1)");

            db.execSQL("INSERT INTO settings VALUES('base_account', 1)");
        }


        Cursor dbv_baseAccount = db.rawQuery("SELECT settings.value,settings.name,accounts.name FROM settings,accounts WHERE settings.name='base_account' AND accounts.id=settings.value",null);
        dbv_baseAccount.moveToNext();
        dbv_baseAccount_id = dbv_baseAccount.getInt(0);
        dbv_baseAccount_name = dbv_baseAccount.getString(2);
        tv_Accountname.setText("Base account: "+dbv_baseAccount_name);
        db.close();
        updateIncomeTM();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode!= Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case (ROOM_CHANGEBASEACCOUNT_ID) :
            {
                int newbaseaccount = data.getIntExtra("v_new_baseaccount_name",-1);
                if (newbaseaccount!=-1) {
                    SQLiteDatabase db = openOrCreateDatabase(getResources().getString(R.string.databasename), MODE_PRIVATE, null);
                    db.execSQL("UPDATE settings SET value=" + newbaseaccount + " WHERE name='base_account';");

                    Cursor dbv_baseAccount = db.rawQuery("SELECT settings.value,settings.name,accounts.name FROM settings,accounts WHERE settings.name='base_account' AND accounts.id=settings.value", null);
                    dbv_baseAccount.moveToNext();
                    dbv_baseAccount_id = dbv_baseAccount.getInt(0);
                    dbv_baseAccount_name = dbv_baseAccount.getString(2);
                    tv_Accountname.setText("Base account: " + dbv_baseAccount_name);

                    db.close();
                }
                break;
            }

        }
        updateIncomeTM();


    }

    public void gotoAddincome(View view)
    {
        Intent mi = new Intent(MainActivity.this, AddincomeActivity.class);
        mi.putExtra("v_account", dbv_baseAccount_name);
        mi.putExtra("v_account_id", dbv_baseAccount_id);
        MainActivity.this.startActivityForResult(mi, ROOM_ADDINCOME_ID);
    }

    public void gotoChangebaseaccount(View view)
    {
        Intent mi = new Intent(MainActivity.this, ChangebaseaccountActivity.class);
        mi.putExtra("v_baseaccount_name",dbv_baseAccount_name);
        mi.putExtra("v_baseaccount_id",dbv_baseAccount_id);
        MainActivity.this.startActivityForResult(mi,ROOM_CHANGEBASEACCOUNT_ID);
    }

    public int getIncomeTM()
    {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH)+1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        String thismonth = ""+mYear+"-"+String.format("%02d",mMonth)+"-01";

        //dumpQuery("SELECT * FROM incomesexpenses WHERE type='INCOME' ; ");
        SQLiteDatabase db = openOrCreateDatabase(getResources().getString(R.string.databasename), MODE_PRIVATE, null);
        Cursor dbv_Income = db.rawQuery("SELECT SUM(amount) FROM incomesexpenses WHERE base_account_id='"+dbv_baseAccount_id+"' AND type='INCOME' AND date BETWEEN DATE('" + thismonth + "') AND DATE('" + thismonth + "','+1 month', '-1 day'); ", null);

        //return dbv_Income.getCount();
        ///*

        if (dbv_Income.moveToNext())
        {

            return dbv_Income.getInt(0);
        }
        return -1;//*/
    }

    public void updateIncomeTM()
    {
        ((TextView) findViewById(R.id.tv_SumIncome)).setText(""+String.valueOf(getIncomeTM()));
        return;
    }

    public void dumpQuery(String sqlitequery)
    {
        SQLiteDatabase db = openOrCreateDatabase(getResources().getString(R.string.databasename), MODE_PRIVATE, null);
        Cursor dbv = db.rawQuery(sqlitequery,null);
        String hasil = "";
        while (dbv.moveToNext())
        {
            for (int i=0; i<dbv.getColumnCount(); i++)
            {
                hasil += dbv.getString(i)+", ";
            }
            hasil += "\n";
        }

        ((TextView) findViewById(R.id.tv_Accountname)).setText(hasil);
        dbv.close();
        db.close();
        return;
    }
}

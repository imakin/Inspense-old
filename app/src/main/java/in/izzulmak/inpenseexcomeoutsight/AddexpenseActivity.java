package in.izzulmak.inpenseexcomeoutsight;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;


public class AddexpenseActivity extends ActionBarActivity {

    Button bt_Addexpense_Date_Pick;
    Button bt_Addexpense_Account;
    private int mYear, mMonth, mDay;
    static String v_ExpenseAmount;
    static int v_ExpenseAccount_id;
    static int v_BaseAccount_id;
    static String v_ExpenseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addexpense);

        Intent ti = getIntent();
        String v_account = ti.getStringExtra("v_account");
        v_BaseAccount_id= ti.getIntExtra("v_account_id",1);
        final TextView tv_Addexpense_Accountname = (TextView) findViewById(R.id.tv_Addexpense_Accountname);
        tv_Addexpense_Accountname.setText(this.getString(R.string.title_expense)+" "+v_account);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        bt_Addexpense_Date_Pick = (Button) findViewById(R.id.bt_Addexpense_Date_Pick);

        bt_Addexpense_Date_Pick.setText(String.format("%02d",mYear)+ "-" + String.format("%02d",mMonth+1) + "-" + String.format("%02d",mDay));
        bt_Addexpense_Date_Pick.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatePickerDialog dpd = new DatePickerDialog(AddexpenseActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        bt_Addexpense_Date_Pick.setText(String.format("%02d",year)+ "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d",dayOfMonth));
                                    }
                                },
                                mYear, mMonth, mDay
                        );
                        dpd.show();
                    }
                }
        );


        final CharSequence[] v_accounts_in = {"Main expense", "Cash in Hand", "Bank Withdraw"};
        final CharSequence[] v_accounts_out = {"Eating", "Transportation", "Infaq", "Personal Care", "Utilities"};

        SQLiteDatabase db = openOrCreateDatabase(getResources().getString(R.string.databasename),MODE_PRIVATE,null);
        Cursor dbv_accounts = db.rawQuery("SELECT * FROM accounts WHERE type='EXPENSE';",null);
        //-- Make ArrayList and push every needed row value
        ArrayList<CharSequence> ALaccounts_list = new ArrayList<CharSequence>();
        ArrayList<Integer> accounts_listID = new ArrayList<Integer>();
        while (dbv_accounts.moveToNext())
        {
            String row = dbv_accounts.getString(dbv_accounts.getColumnIndex("name"));
            ALaccounts_list.add(row);
            accounts_listID.add(dbv_accounts.getInt(dbv_accounts.getColumnIndex("id")));
        }
        db.close();
        //-- covert the ArrayList to an Array
        CharSequence[] accounts_list = new CharSequence[ALaccounts_list.size()];
        accounts_list = ALaccounts_list.toArray(accounts_list);
        final CharSequence[] finalAccounts_list = accounts_list; //-- The array is ready to use in AlertDialog.Builder.setItems
        final ArrayList<Integer> finalAccounts_listID = accounts_listID;


        bt_Addexpense_Account = (Button) findViewById(R.id.bt_Addexpense_Account);
        bt_Addexpense_Account.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddexpenseActivity.this);
                        builder.setTitle("Pick Expense Account Name");
                        builder.setItems(finalAccounts_list,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int item){
                                        bt_Addexpense_Account.setText(finalAccounts_list[item]);
                                        AddexpenseActivity.v_ExpenseAccount_id = finalAccounts_listID.get(item);
                                    }
                                }
                        );
                        builder.show();
                    }
                }
        );

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_addexpense, menu);
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

    public void saveData(View view) {
        String amount = ((EditText) findViewById(R.id.et_Addexpense_Amount)).getText().toString();
        String date = ((Button) findViewById(R.id.bt_Addexpense_Date_Pick)).getText().toString();
        String description = ((EditText) findViewById(R.id.et_Addexpense_Description)).getText().toString();

        SQLiteDatabase db = openOrCreateDatabase(getResources().getString(R.string.databasename),MODE_PRIVATE,null);
        Cursor c = db.rawQuery("SELECT * FROM incomesexpenses;",null);
        if (c.getCount()>0)
            db.execSQL("INSERT INTO incomesexpenses VALUES((SELECT id FROM incomesexpenses ORDER BY id DESC LIMIT 1)+1, '" +
                    v_BaseAccount_id+"','"+v_ExpenseAccount_id+"','"+description+"','EXPENSE','"+amount+"','"+date+"')");//--expensetype = 1
        else
            db.execSQL("INSERT INTO incomesexpenses VALUES(1,'"+v_BaseAccount_id+"','"+v_ExpenseAccount_id+"','"+description+"','EXPENSE','"+amount+"','"+date+"') ");
        db.execSQL("UPDATE accounts SET balance=balance-"+amount+" WHERE id="+v_BaseAccount_id+";");
        c.close();
        db.close();
        Intent i = new Intent();
        setResult(RESULT_OK,i);
        finish();
    }
}

package in.izzulmak.inpenseexcomeoutsight;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;


public class AddincomeActivity extends ActionBarActivity {

    Button bt_Addincome_Date_Pick;
    Button bt_Addincome_Account;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addincome);

        Intent ti = getIntent();
        String v_account = ti.getStringExtra("v_account");
        final TextView tv_Addincome_Accountname = (TextView) findViewById(R.id.tv_Addincome_Accountname);
        tv_Addincome_Accountname.setText(this.getString(R.string.title_income)+" "+v_account);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        bt_Addincome_Date_Pick = (Button) findViewById(R.id.bt_Addincome_Date_Pick);

        bt_Addincome_Date_Pick.setText(mDay+"-"+mMonth+1+"-"+mYear);
        bt_Addincome_Date_Pick.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatePickerDialog dpd = new DatePickerDialog(AddincomeActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                bt_Addincome_Date_Pick.setText(dayOfMonth + "-" + monthOfYear+1 + "-" + year);
                            }
                        },
                        mYear, mMonth, mDay
                    );
                    dpd.show();
                }
            }
        );


        final CharSequence[] v_accounts_in = {"Main Income", "Cash in Hand", "Bank Withdraw"};
        final CharSequence[] v_accounts_out = {"Eating", "Transportation", "Infaq", "Personal Care", "Utilities"};

        bt_Addincome_Account = (Button) findViewById(R.id.bt_Addincome_Account);
        bt_Addincome_Account.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddincomeActivity.this);
                        builder.setTitle("Pick Income Account Name");
                        builder.setItems(v_accounts_in,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item){
                                    bt_Addincome_Account.setText(v_accounts_in[item]);
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
        getMenuInflater().inflate(R.menu.menu_addincome, menu);
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

    }
}

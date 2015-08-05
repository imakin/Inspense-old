package in.izzulmak.inpenseexcomeoutsight;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class DebugActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_debug, menu);
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

    public void dumpDebugQuery(View view)
    {
        String sqlitequery = ((EditText) findViewById(R.id.et_Debug)).getText().toString();
        SQLiteDatabase db = openOrCreateDatabase(getResources().getString(R.string.databasename), MODE_PRIVATE, null);

        Cursor dbv;
        try {
            dbv = db.rawQuery(sqlitequery, null);
            String hasil = "";
            while (dbv.moveToNext())
            {
                for (int i=0; i<dbv.getColumnCount(); i++)
                {
                    hasil += dbv.getString(i)+", ";
                }
                hasil += "\n";
            }

            ((TextView) findViewById(R.id.tv_Debug)).setText(hasil);
            dbv.close();
            db.close();
        }
        catch (Exception e) {
            ((TextView) findViewById(R.id.tv_Debug)).setText(e.toString());
        }

        return;
    }
}

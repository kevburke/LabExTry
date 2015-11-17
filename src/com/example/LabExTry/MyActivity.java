package com.example.LabExTry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    int inArr=0;
    String fname = "";
    String mname = "";
    String surname = "";
    String ssn = "";
    String bdate = "";
    String address = "";
    String sex = "";
    String salary = "";
    String store;
    int valStartTag;
    private TextView txtMsg;
    Button btnGoParser;
    Button button2;
    EditText editText;
    SQLiteDatabase db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        txtMsg = (TextView) findViewById(R.id.txtMsg);
        btnGoParser = (Button) findViewById(R.id.butnGoParser);
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // db.query("people", columns, "fnanme =? And mname And surname",
              //  GetDatabaseEmployees();
            }
        });
        String path = Environment.getExternalStorageDirectory().getPath();
        String file = new String("/people");
        db = SQLiteDatabase.openDatabase(path + file, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //  db.execSQL("drop table people");

        createDataBaseTable();

        btnGoParser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnGoParser.setEnabled(false);
                // do slow XML reading in a separated thread
                Integer xmlResFile = R.xml.myxml;
                new backgroundAsyncTask().execute(xmlResFile);
                Toast.makeText(getApplicationContext(), "XML Parsed", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Successfully Created Database", Toast.LENGTH_SHORT).show();
               // insertDataToTable();
            }
        });
    }// onCreate

    public class backgroundAsyncTask extends
            AsyncTask<Integer, Void, StringBuilder> {

        ProgressDialog dialog = new ProgressDialog(MyActivity.this);

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            dialog.dismiss();
            //txtMsg.setText(result.toString());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        public StringBuilder doInBackground(Integer... params) {
            int xmlResFile = params[0];
            XmlPullParser parser = getResources().getXml(xmlResFile);

            StringBuilder stringBuilder = new StringBuilder();
            String nodeText = "";
            String nodeName = "";

            try {
                int eventType = -1;
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    eventType = parser.next();

                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        stringBuilder.append("\nSTART_DOCUMENT");
                        valStartTag = 0;
                    } else if (eventType == XmlPullParser.END_DOCUMENT) {
                        stringBuilder.append("\nEND_DOCUMENT");

                    } else if (eventType == XmlPullParser.START_TAG) {
                        valStartTag++;
                        nodeName = parser.getName();
                        stringBuilder.append("\nSTART_TAG: " + nodeName);
                        stringBuilder.append(getAttributes(parser));

                        if(valStartTag>2)
                        {
                            store = parser.getName();
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {

                        if(valStartTag==2){
                            insertDataToTable();
                            //valStartTag--;
                            inArr=0;
                            fname = "";
                            mname = "";
                            surname = "";
                            ssn = "";
                            bdate = "";
                            address = "";
                            sex = "";
                            salary = "";
                        }
                        valStartTag--;

                        //nodeName = parser.getName();
                        // stringBuilder.append("\nEND_TAG:   " + nodeName );

                    } else if (eventType == XmlPullParser.TEXT) {
                        if(valStartTag ==3) {

                            if (store.equalsIgnoreCase("fname"))
                                fname = parser.getText();

                            else if(store.equalsIgnoreCase("mname"))
                                mname = parser.getText();

                            else if(store.equalsIgnoreCase("surname"))
                                surname = parser.getText();

                            else if(store.equalsIgnoreCase("ssn"))
                                ssn = parser.getText();

                            else if(store.equalsIgnoreCase("bdate"))
                                bdate = parser.getText();

                            else if(store.equalsIgnoreCase("address"))
                                address = parser.getText();

                            else if(store.equalsIgnoreCase("sex"))
                                sex = parser.getText();

                            else if(store.equalsIgnoreCase("salary"))
                                salary = parser.getText();

                        }
                        //nodeText = parser.getText();

                        // stringBuilder.append("\n    TEXT: " + nodeText);

                    }
                }
            } catch (Exception e) {
                Log.e("<<PARSING ERROR>>", e.getMessage());

            }

            return stringBuilder;
            /*private String GetDatabaseEmployees(String fname){
            SQLiteDatabase db = this.openOrCreateDatabase(fname,MODE_PRIVATE,null);

            String show = "select * from employee";

            Cursor C1 = db.rawQuery(show, null);
            String listOfEmployee = showCursor(C1);

            return listOfEmployee;
        }*/



        }// doInBackgroun
        private String getAttributes(XmlPullParser parser) {
            StringBuilder stringBuilder = new StringBuilder();
            // trying to detect inner attributes nested inside a node tag
            String name = parser.getName();
            if (name != null) {
                int size = parser.getAttributeCount();

                for (int i = 0; i < size; i++) {
                    String attrName = parser.getAttributeName(i);
                    String attrValue = parser.getAttributeValue(i);
                    stringBuilder.append("\n    Attrib <key,value>= "
                            + attrName + ", " + attrValue);
                   // txtMsg.append(("\n    Attrib <key,value>= "
                            //+ attrName + ", " + attrValue));
                }
            }
            return stringBuilder.toString();
        }// innerElements
    }
    private void createDataBaseTable() {
        try{
            db.beginTransaction();

           // db.execSQL("drop table people");
            db.execSQL("create table people(recID integer PRIMARY KEY autoincrement "
                    + ",fname text"
                    + ",mname text"
                    + ",surname text"
                    + ",ssn text"
                    + ",bdate text"
                    + ",address text"
                    + ",sex text"
                    + ",salary text);");

            db.setTransactionSuccessful();

        }
        catch(SQLException e1){
            Toast.makeText(getApplicationContext(), "Not Successful", Toast.LENGTH_SHORT).show();

        }
        finally {
            db.endTransaction();

        }

    }

    private void insertDataToTable(){

        // String inner =  store[inArr];
        db.beginTransaction();
        try {

            //insert rows

            db.execSQL( "insert into people(fname, mname, surname, ssn, bdate, address, sex, salary) "
                    + " values ('"+fname+"', '"+mname +"' , '"+surname+"' , '"+ssn+"' , '"+bdate+"' , '"+address+"' , '"+sex+"' , '"+salary+"')" );
            db.setTransactionSuccessful();



        }catch (SQLException e1){

        }
        finally {

            db.endTransaction();
        }
    }

    private String showCursor( Cursor cursor) {
        // show SCHEMA (column names & types)
        cursor.moveToPosition(-1); //reset cursor's top
        String cursorData = "\nCursor: [";

        try {
            // get column names
            String[] colName = cursor.getColumnNames();
            for(int i=0; i<colName.length; i++){
                String dataType = getColumnType(cursor, i);
                cursorData += colName[i] + dataType;
                if (i<colName.length-1){
                    cursorData+= ", ";
                } }
        } catch (Exception e)
        { Log.e("<<SCHEMA>>", e.getMessage());
        }
        cursorData += "]";
// now get the rows
        cursor.moveToPosition(-1); //reset cursor's top
        while (cursor.moveToNext()) {
            String cursorRow = "\n[";
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                cursorRow += cursor.getString(i);
                if (i<cursor.getColumnCount()-1)
                    cursorRow += ", ";
            }
            cursorData += cursorRow + "]";
        }
        return cursorData + "\n";
    }


    private String getColumnType(Cursor cursor, int i) {
        try {
            //peek at a row holding valid data
            cursor.moveToFirst();
            int result = cursor.getType(i);
            String[] types = {":NULL", ":INT", ":FLOAT", ":STR", ":BLOB", ":UNK" };
            //backtrack - reset cursor's top
            cursor.moveToPosition(-1);
            return types[result]; } catch (Exception e) { return " "; }
    }

}


// ActivityMain



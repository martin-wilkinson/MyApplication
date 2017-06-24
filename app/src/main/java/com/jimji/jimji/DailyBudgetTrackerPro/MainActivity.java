package com.jimji.jimji.DailyBudgetTrackerPro;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public class MainActivity extends AppCompatActivity {
    //following fields are used to save the date and current budget value
    public static final String myPrefsName = "myPrefs";
    public static String savedDate = "dateKey";
    public static String savedBudget = "budgetKey";
    SharedPreferences dateSaved;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateSaved = getSharedPreferences(myPrefsName, MODE_PRIVATE);
        getDetails();

        //checkDate(currentDate);
    }

    private void getDetails() {
        SharedPreferences.Editor editor = dateSaved.edit();
        if (dateSaved.contains(savedBudget)) {
            String budgetCheck = dateSaved.getString(savedBudget, "no object");
            if (!budgetCheck.equals("no object")) {
                TextView currentBdgt = (TextView) findViewById(R.id.currentValueAdjust);
                currentBdgt.setText(budgetCheck);
            }

        }
    }

    protected void onStart ( Bundle savedInstanceState) {
        super.onStart();
        setContentView(R.layout.activity_main);

    }

    //checkdate finds out how many days have passed since the budget was last updated
    private int checkDate(DateTracker currentDate) {
        DateTracker currentValint;
        Gson gson = new Gson();
        if (dateSaved.contains(savedDate)){
            String currentValue = dateSaved.getString("dateKey", "no object");
            if (currentValue.equals("no object")) {
                updateValue(currentDate);
                return 0;
            }
            else {
                LocalDate oldDate = LocalDate.parse(currentValue, DateTimeFormat.forPattern("dd/MM/yyyy"));
                //currentValint = gson.fromJson(currentValue, DateTracker.class);
                int daysBetween = DateTracker.daysBetweenUsingJoda(oldDate, currentDate.getDate());
                if (daysBetween > 0) {
                    updateValue(currentDate);
                    return daysBetween;
                }
            }
        }
        else {
            updateValue(currentDate);
            return 0;
        }
        return 0;
    }

    private void updateValue(DateTracker date) {
        SharedPreferences.Editor editor = dateSaved.edit();
        String formattedDate = formatDate(date.getDate());
        editor.putString(savedDate, formattedDate);
        editor.apply();
    }


    public void addCost(View view){
        SharedPreferences.Editor editor = dateSaved.edit();
        EditText costView = (EditText) findViewById(R.id.inputExpenseText);
        TextView currentBudgt = (TextView) findViewById(R.id.currentValueAdjust);
        String cost = costView.getText().toString();
        String budgt = currentBudgt.getText().toString();
        Double costVal = Double.parseDouble(cost);
        Double budgtVal = Double.parseDouble(budgt);
        Double updatedVal = budgtVal - costVal;
        String updatedValStr = Double.toString(updatedVal);
        currentBudgt.setText(updatedValStr);
        editor.putString(savedBudget, updatedValStr);
        editor.apply();
    }
    //update method updates budget value based on how many days have passed since last updated
    public void update(View View) {
        DateTracker currentDate = new DateTracker();
        SharedPreferences.Editor editor = dateSaved.edit();
        Double budgetSaved = null;
        //catch error when update button is pressed with no budget set
        try {
            budgetSaved = Double.parseDouble(dateSaved.getString(savedBudget, "no budget"));
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(this, "Must Set Daily Budget First", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        int days = checkDate(currentDate);
        if (days > 0) {
            TextView currentVal = (TextView) findViewById(R.id.currentValueAdjust);
            Double additionalDollaz = budgetSaved + (days * 20);
            currentVal.setText(Double.toString(additionalDollaz));
            editor.putString(savedBudget, Double.toString(additionalDollaz));
            editor.apply();
        }
        else {
            Context context = getApplicationContext();
            CharSequence text = "Zero days have passed since last update";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }
    //sets layout that allows user to set their daily budget
    public void configure(View View) {
        setContentView(R.layout.configure_activity);
    }


    public void startApp (View View) {
        EditText budgetAmountView = (EditText) findViewById(R.id.editText);
        String budgetAmount = budgetAmountView.getText().toString();
        setContentView(R.layout.activity_main);
        TextView initialValueView = (TextView) findViewById(R.id.currentValueAdjust);
        initialValueView.setText(budgetAmount);
        SharedPreferences.Editor editor = dateSaved.edit();
        editor.putString(savedBudget, budgetAmount);
        editor.apply();

    }

    public void reset (View View) {
        SharedPreferences.Editor editor = dateSaved.edit();
        editor.clear();
        editor.apply();
        setContentView(R.layout.configure_activity);
    }

    public String formatDate(LocalDate dateToFormat) {
        String formattedDate = dateToFormat.toString("dd/MM/yyyy");
        return formattedDate;
    }
//Unit Test Add's extra days to current date to test that the update button works
    public void test (View View) {
        if (dateSaved.contains(savedDate)){
            String currentValueTest = dateSaved.getString("dateKey", "no object");
            if (!currentValueTest.equals("no object")) {
                LocalDate oldDate = LocalDate.parse(currentValueTest, DateTimeFormat.forPattern("dd/MM/yyyy"));
                DateTracker currentDate = new DateTracker();
                currentDate.setDate(oldDate.plusDays(1));
                int daysBetween = DateTracker.daysBetweenUsingJoda(oldDate, currentDate.getDate());
                if (daysBetween > 0) {
                    TextView currentVal = (TextView) findViewById(R.id.currentValueAdjust);
                    Double currentBudget = Double.parseDouble(dateSaved.getString(savedBudget,"no budget"));
                    Double additionalDollaz = (daysBetween * 20) + currentBudget;
                    currentVal.setText(Double.toString(additionalDollaz));
                }

            }
            else {
                Context context = getApplicationContext();
                CharSequence text = "Press Update first to save current Date";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
        Context context = getApplicationContext();
        CharSequence text = "Press Update first to save current Date";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
/* check day != previous day time only moves forward so this should be all I need for it to work*/
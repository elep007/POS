package com.example.rexpos;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.rexpos.api.ApiClient;
import com.example.rexpos.models.ReportItem;
import com.example.rexpos.models.ReportProductItem;
import com.example.rexpos.models.Transaction;
import com.example.rexpos.models.response.ResReport;
import com.example.rexpos.models.response.ResTableTransaction;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class
RoleActivity extends BaseActivity implements View.OnClickListener {


    TextView btnCashier, btnKitchen, btnLogout;
    ImageView _caleandar;
    TextView _salesReport,_dailyReport,_dateRange;

    String startDate,endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);
        btnCashier = findViewById(R.id.btn_cashier);
        btnCashier.setOnClickListener(this);
        btnKitchen = findViewById(R.id.btn_kitchen);
        btnKitchen.setOnClickListener(this);

        _caleandar=findViewById(R.id.imgCaleandar);
        _caleandar.setOnClickListener(this);

        _salesReport=findViewById(R.id.btn_sales_report);
        _salesReport.setOnClickListener(this);
        _dailyReport=findViewById(R.id.btn_daily_report);
        _dailyReport.setOnClickListener(this);

        _dateRange=findViewById(R.id.txtDateRange);

        btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);

        //doReport2("2019-09-29","2019-10-02");
        Date currentTime = Calendar.getInstance().getTime();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        startDate = dateFormat.format(currentTime);
        endDate=startDate;
        _dateRange.setText(startDate+" ~ "+endDate);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cashier:
                Intent intent = new Intent(this, OrderActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_kitchen:
                Intent intent1 = new Intent(this, DemoActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_logout:
                logout();
                break;
            case R.id.imgCaleandar:
                Intent intent2 = new Intent(this, SelectDateActivity.class);
                startActivityForResult(intent2,101);
                break;
            case R.id.btn_sales_report:
                doReport1(startDate,endDate);
                break;
            case R.id.btn_daily_report:
                doReport2(startDate,endDate);
                break;
                default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101 && resultCode==102){
            startDate=data.getStringExtra("startDate");
            endDate=data.getStringExtra("endDate");

            _dateRange.setText(startDate+" ~ "+endDate);
            //Log.d("start",startDate);
            //Log.d("end",endDate);
        }
    }

    private void logout(){
        mPrefs.setUserID(0);
        mPrefs.setUserShopName("");
        mPrefs.setUserShopId("");
        mPrefs.setUserEmail("");
        mPrefs.setUserPassword("");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void doReport1(String start_date, String end_date){

        String shop_id = mPrefs.getUserShopId();

        showProgressDialog("waiting");
        Call<ResReport> call = ApiClient.getApiClient(this).report1API(start_date,end_date);
        call.enqueue(new Callback<ResReport>() {
            @Override
            public void onResponse(Call<ResReport> call, Response<ResReport> response) {
                hideProgressDialog();
                ResReport result = response.body();
                if (result != null){
                    if (result.errorCode == 0){
                        ResReport.Result results = result.results;
                        List<ReportItem> reports = results.reports;

                        doPrintReport1(reports);
                    }else {
                        Toast.makeText(getBaseContext(), result.errMsg, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResReport> call, Throwable t) {
                hideProgressDialog();
                Toast.makeText(getBaseContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doPrintReport1(List<ReportItem> mReports) {

        DecimalFormat twoDForm = new DecimalFormat("#.##");

        String commandsToPrint = "<BIG><BOLD><CENTER>Sales Summary Report<BR>\n" +
                "<CENTER><IMAGE>" + mPrefs.getUserShopLogo() + "<BR>\n<BR>\n" ;

        double grant_total=0.0;
        for(ReportItem theReport : mReports) {
            commandsToPrint+= "<LEFT> Date: "+ theReport.date +"<BR>\n" +
                    "<BOLD><CENTER>Item Code                      Qty  Price <BR>\n";

            List<ReportProductItem> items = theReport.items;

            for (ReportProductItem item : items) {
                String print_name=String.format("%-30s", item.product_name);//-----------justify qty+price
                String print_quantity=String.format("%-3s", item.quantity);

                String price=twoDForm.format(item.price);
                price=String.format("%7s", price);//-----------justify qty+price
                commandsToPrint += "<CENTER>" + print_name +" "+ print_quantity+" "+ price + " <BR>\n";
            }

            grant_total+=Double.valueOf(String.format(Locale.US,"%.1f",theReport.total));
            commandsToPrint += "<BR>\n"+
                    "<BOLD><RIGHT>Total:    " + String.format(Locale.US,"%.1f",theReport.total)+"0" + " <BR>\n"+
                    "<BR>\n"+"<BR>\n";

        }

        commandsToPrint +=
                        "<RIGHT><BOLD>Grant Total:    "+ String.format(Locale.US,"%.1f",grant_total)+"0" +"<BR>\n" +
                        "<CUT>\n" +
                        "<DRAWER>\n" // OPEN THE CASH DRAWER
        ;

        //Log.d("print_content",commandsToPrint);

        try {
            Intent intent = new Intent("pe.diegoveloper.printing");
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, commandsToPrint);
            startActivityForResult(intent, 101);

        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=pe.diegoveloper.printerserverapp"));
            startActivity(intent);
        }
    }

    public void doReport2(String start_date, String end_date){

        String shop_id = mPrefs.getUserShopId();

        showProgressDialog("waiting");
        Call<ResReport> call = ApiClient.getApiClient(this).report2API(start_date,end_date);
        call.enqueue(new Callback<ResReport>() {
            @Override
            public void onResponse(Call<ResReport> call, Response<ResReport> response) {
                hideProgressDialog();
                ResReport result = response.body();
                if (result != null){
                    if (result.errorCode == 0){
                        ResReport.Result results = result.results;
                        List<ReportItem> reports = results.reports;

                        doPrintReport2(reports);
                    }else {
                        Toast.makeText(getBaseContext(), result.errMsg, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResReport> call, Throwable t) {
                hideProgressDialog();
                Toast.makeText(getBaseContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doPrintReport2(List<ReportItem> mReports) {

        DecimalFormat twoDForm = new DecimalFormat("#.##");

        String commandsToPrint = "<BIG><BOLD><CENTER>Daily Summary Report<BR>\n" +
                "<CENTER><IMAGE>" + mPrefs.getUserShopLogo() + "<BR>\n<BR>\n" ;

        double grant_total=0.0;

        String originalDate="";
        if(mReports.size()>0){
            originalDate=mReports.get(0).date.substring(0,10);
        }
        for(ReportItem theReport : mReports) {
            //print grant_total
            String currentDate=theReport.date.substring(0,10);
            if(!originalDate.equals(currentDate)){
                commandsToPrint+="<RIGHT><BOLD>Grant Total:    "+ String.format(Locale.US,"%.1f",grant_total)+"0" +"<BR>\n"+"<BR>\n"+"<BR>\n";
                originalDate=currentDate;
                grant_total=0.0;
            }

            commandsToPrint+= "<LEFT> Table: "+ theReport.table_name +"<BR>\n" +
                    "<LEFT> Date: "+ theReport.date +"<BR>\n" +
                    "<BOLD><CENTER>Item Code                      Qty  Price <BR>\n";

            List<ReportProductItem> items = theReport.items;

            for (ReportProductItem item : items) {
                String print_name=String.format("%-30s", item.product_name);//-----------justify qty+price
                String print_quantity=String.format("%-3s", item.quantity);
                String price=twoDForm.format(item.price);
                price=String.format("%7s", price);//-----------justify qty+price
                commandsToPrint += "<CENTER>" + print_name + ' '+print_quantity+" "+ price + " <BR>\n";
            }

            grant_total+=Double.valueOf(String.format(Locale.US,"%.1f",theReport.total));
            commandsToPrint += "<BR>\n"+
                    "<BOLD><RIGHT>Total:    " + String.format(Locale.US,"%.1f",theReport.total)+"0" + " <BR>\n"+
                    "<BR>\n"+"<BR>\n";

        }

        commandsToPrint +=
                "<RIGHT><BOLD>Grant Total:    "+ String.format(Locale.US,"%.1f",grant_total)+"0" +"<BR>\n" +
                        "<CUT>\n" +
                        "<DRAWER>\n" // OPEN THE CASH DRAWER
        ;

        //Log.d("print_content",commandsToPrint);

        try {
            Intent intent = new Intent("pe.diegoveloper.printing");
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, commandsToPrint);
            startActivityForResult(intent, 101);

        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=pe.diegoveloper.printerserverapp"));
            startActivity(intent);
        }
    }
}

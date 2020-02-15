package com.example.nslngiot;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nslngiot.Adapter.ManagerCalendarAdapter;
import com.example.nslngiot.Data.ManagerAddUserData;
import com.example.nslngiot.Data.ManagerCalendarData;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ManagerCalendarAddActivity extends AppCompatActivity {
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("YYYY년 MM월 dd일");
    TextView tv_date;

    Calendar c;
    int nYear,nMon,nDay;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    private EditText manager_title;
    private EditText manager_detail;
    private ImageButton btn_calendar;
    private Button btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_calendar_add);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 오늘 날짜 표현
        tv_date = findViewById(R.id.tv_date);
        tv_date.setText(getTime());

        manager_title = findViewById(R.id.manager_calendar_title);
        manager_detail = findViewById(R.id.manager_calendar_detail);
        btn_calendar = findViewById(R.id.btn_calendar);
        btn_add = findViewById(R.id.btn_add);

        // Calendar
        //DatePicker Listener
        mDateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        String strDate = String.valueOf(year) + "년 ";

                        if (monthOfYear + 1 > 0 && monthOfYear + 1 < 10)
                            strDate += "0" + String.valueOf(monthOfYear + 1) + "월 ";
                        else
                            strDate += String.valueOf(monthOfYear + 1) + "월 ";

                        if (dayOfMonth > 0 && dayOfMonth < 10)
                            strDate += "0" + String.valueOf(dayOfMonth) + "일";
                        else
                            strDate += String.valueOf(dayOfMonth) + "일";

                        tv_date.setText(strDate);
                    }
                };

        c = Calendar.getInstance();
        nYear = c.get(Calendar.YEAR);
        nMon = c.get(Calendar.MONTH);
        nDay = c.get(Calendar.DAY_OF_MONTH);

        btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog oDialog = new DatePickerDialog(getApplicationContext(),
                        mDateSetListener, nYear, nMon, nDay);

                oDialog.show();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 랩실 일정 등록
                manager_Meetlog_SaveRequest();

                finish(); // 이전 fragment로 이동
            }
        });
    }

    //일정 등록 통신
    private void manager_Meetlog_SaveRequest(){
        StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/Schedule.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response.trim()){
                            case "scheduleAddSuccess": // 일정 등록 성공했을 때
                                Toast.makeText(getApplicationContext(), "회의록을 등록하였습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "scheduleAlreadyEist": // 제목이 겹칠 때
                                Toast.makeText(getApplicationContext(), "이미 존재하는 일정입니다.", Toast.LENGTH_SHORT).show();
                            case "error": // 오류
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                                break;
                            default: // 접속 지연 시 확인 사항
                                Toast.makeText(getApplicationContext(), "default Error", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                // '일정등록'이라는 신호 정보 push 진행
                params.put("date", String.valueOf(tv_date));
                params.put("title", String.valueOf(manager_title));
                params.put("text", String.valueOf(manager_detail));
                params.put("type","scheduleAdd");

                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        VolleyQueueSingleTon.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }
}
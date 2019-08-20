package com.jc.aptdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.jc.aptannotations.OnClick;
import com.jc.aptannotations.ViewById;
import com.jc.aptapi.InjectorTool;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @ViewById(R.id.tv_test01)
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InjectorTool.bind(this);

    }

    @OnClick({R.id.bt_test02})
    public void onViewClicked() {
        String s = "com.wjc.api";
        int packageLen = s.length() + 1;
        String s2="com.wjc.api.test";
        String s1 = s2.substring(packageLen);
        Log.i(TAG, "onCreate: s1="+s1);
        Toast.makeText(this, "bt", Toast.LENGTH_LONG).show();
    }
}

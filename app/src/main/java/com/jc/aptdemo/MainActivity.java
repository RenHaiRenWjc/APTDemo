package com.jc.aptdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jc.aptannotations.OnClick;
import com.jc.aptannotations.ViewById;
import com.jc.aptapi.ProxyTool;


public class MainActivity extends AppCompatActivity {
    @ViewById(R.id.tv_test01)
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProxyTool.bind(this);
    }

    @OnClick({R.id.bt_test02})
    public void onViewClicked() {
        Toast.makeText(this, "bt", Toast.LENGTH_LONG).show();
    }
}

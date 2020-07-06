package jnu.ssc.client.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import jnu.ssc.client.R;
import jnu.ssc.client.controller.FunctionFactory;
import jnu.ssc.client.controller.NetworkProxy;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static String userId;
    //外部可读不可写
    public static String getUserId(){
        return userId;
    }

    private Button staffLoginButton;
    private Button administratorLoginButton;
    private EditText idEdit;
    private EditText passwordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //隐藏标题栏
        ActionBar actionbar=getSupportActionBar();
        if (actionbar!=null){
            actionbar.hide();
        }

        staffLoginButton=findViewById(R.id.button_staff_login);
        staffLoginButton.setOnClickListener(this);
        administratorLoginButton=findViewById(R.id.button_administrator_login);
        administratorLoginButton.setOnClickListener(this);
        idEdit=findViewById(R.id.edit_id);
        passwordEdit=findViewById(R.id.edit_password);
        passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());//密文显示
    }

    @Override
    public void onClick(final View v) {
        final String id=idEdit.getText().toString();
        final String password=passwordEdit.getText().toString();
        final boolean[] login = new boolean[1];
        //登录
        try{
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    if (v==staffLoginButton) {
                        try {
                            login[0] = NetworkProxy.staffLogin(id,password);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (v==administratorLoginButton){
                        try {
                            login[0]=NetworkProxy.administratorLogin(id,password);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (Exception e){
            e.printStackTrace();
        }
        //登录成功
        if (login[0]){
            userId=idEdit.getText().toString();
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            if (v==staffLoginButton){
                intent.putExtra("role", FunctionFactory.STAFF);
            }
            else if (v==administratorLoginButton){
                intent.putExtra("role",FunctionFactory.ADMINISTRATOR);
            }
            startActivity(intent);
        }
        //登录失败
        else{
            idEdit.setText("");
            passwordEdit.setText("");
            Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_LONG).show();
        }

    }
}

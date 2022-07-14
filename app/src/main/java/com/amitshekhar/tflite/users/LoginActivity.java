package com.amitshekhar.tflite.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amitshekhar.tflite.JsonPlaceHolderApi;
import com.amitshekhar.tflite.R;
import com.amitshekhar.tflite.RealMain;
import com.amitshekhar.tflite.RetrofitClient;
import com.amitshekhar.tflite.refri.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    final private static String TAG = "LoginActivity";
    private int RESULT_PERMISSIONS = 100;
    private RetrofitClient retrofitClient;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        requestPermissionCamera();


        // 위젯에 대한 참조.
        EditText idText = findViewById(R.id.login_email);
        EditText passwordText = findViewById(R.id.login_password);
        Button btn_login = findViewById(R.id.login_button);
        Button btn_join = findViewById(R.id.join_button);


        btn_login.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String id = idText.getText().toString();
                String pw = passwordText.getText().toString();
                //hideKeyboard();

                //로그인 정보 미입력 시
                if (id.trim().length() == 0 || pw.trim().length() == 0 || id == null || pw == null) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("알림")
                            .setMessage("로그인 정보를 입력바랍니다.")
                            .setPositiveButton("확인", null)
                            .create()
                            .show();
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                } else {
                    //로그인 통신
                    LoginResponse(id, pw);
                }
            }
        });

        btn_join.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });

    }



    public void LoginResponse(String id, String pw) {
        String userID = id.trim();
        String userPassword = pw.trim();

        //loginRequest에 사용자가 입력한 id와 pw를 저장
        LoginRequest loginRequest = new LoginRequest(userID, userPassword);

        //retrofit 생성
        retrofitClient = RetrofitClient.getInstance();
        jsonPlaceHolderApi = RetrofitClient.getRetrofitInterface();

        //loginRequest에 저장된 데이터와 함께 init에서 정의한 getMember 함수를 실행한 후 응답을 받음
        jsonPlaceHolderApi.getMember(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                Log.d("<< retrofit - Login >>", "Data fetch success");

                //통신 성공
                if (response.isSuccessful() && response.body() != null) {

                    //response.body()를 result에 저장
                    LoginResponse result = response.body();

                    Log.i(TAG, "response >> "+ result.toString());

                    String UserSeq = result.user_seq;
                    String UserName = result.user_nm;
                    String UserEmail = result.user_email;
                    String UserSex = result.user_sex;
                    String UserAge = result.user_age;

                    LoginData.loginUser = new User(UserSeq, UserEmail, UserName, UserSex, UserAge);

                    Log.i(TAG, "LoginData ####################" + UserName + UserEmail + UserSex + UserAge);


                    Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, RealMain.class);

//                    intent.putExtra("userId", userID);

                    intent.putExtra("UserEmail",UserEmail);
                    intent.putExtra("UserName", UserName);
                    intent.putExtra("UserSex", UserSex);
                    intent.putExtra("UserAge", UserAge);

                    startActivity(intent);


                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("알림")
                            .setMessage("이메일 혹은 비밀번호가 맞지 않습니다")
                            .setPositiveButton("확인", null)
                            .create()
                            .show();
                }
            }

            //통신 실패
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("알림")
                        .setMessage("예기치 못한 오류가 발생하였습니다.\n 고객센터에 문의바랍니다.")
                        .setPositiveButton("확인", null)
                        .create()
                        .show();
            }
        });
    }

    public boolean requestPermissionCamera(){
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        RESULT_PERMISSIONS);

            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 101:
                if(grantResults.length > 0){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "카메라 권한 사용자가 승인함",Toast.LENGTH_LONG).show();
                    }
                    else if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                        Toast.makeText(this, "카메라 권한 사용자가 허용하지 않음.",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(this, "수신권한 부여받지 못함.",Toast.LENGTH_LONG).show();
                    }
                }
        }
    }

}
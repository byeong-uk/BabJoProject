package com.amitshekhar.tflite.users;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amitshekhar.tflite.JsonPlaceHolderApi;
import com.amitshekhar.tflite.R;
import com.amitshekhar.tflite.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinActivity extends AppCompatActivity {

    private EditText join_email, join_password, join_name, join_pwck, join_sex, join_age;
    private Button join_button, check_button;
    private RetrofitClient retrofitClient;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

//    private AlertDialog dialog;
//    private boolean validate = false;

    final private static String TAG = "JoinActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_signup );


        join_email = findViewById(R.id.join_email);
        join_password = findViewById(R.id.join_password);
        join_name = findViewById(R.id.join_name);
        join_pwck = findViewById(R.id.join_pwck);
        join_sex = findViewById(R.id.join_sex);
        join_age = findViewById(R.id.join_age);

        check_button = findViewById(R.id.check_button);


        //회원가입 버튼 클릭 시 수행
        join_button = findViewById(R.id.join_button);
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //회원가입 정보 미입력 시
                if (join_email.getText().toString().trim().length() == 0 || join_password.getText().toString().trim().length() == 0 || join_email.getText().toString() == null || join_password.getText().toString() == null
                || join_name.getText().toString().trim().length() == 0 || join_name.getText().toString() == null || join_pwck.getText().toString().trim().length() == 0 || join_pwck.getText().toString() == null
                        || join_sex.getText().toString().trim().length() == 0 || join_sex.getText().toString() == null || join_age.getText().toString().trim().length() == 0
                        || join_age.getText().toString() == null) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setTitle("알림")
                            .setMessage("회원가입 정보를 모두 입력바랍니다.")
                            .setPositiveButton("확인", null)
                            .create()
                            .show();
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                } else if(join_password.getText().toString() == join_pwck.getText().toString()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setTitle("알림")
                            .setMessage("비밀번호와 비밀번호 확인 정보가 맞지 않습니다.")
                            .setPositiveButton("확인", null)
                            .create()
                            .show();
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else {
                    //로그인 통신
                    JoinResponse();
                }

            }
        });
    }

    public void JoinResponse(){
        String useremail = join_email.getText().toString();
        String userpwd = join_password.getText().toString();
        String usernm = join_name.getText().toString();
        String usersex = join_sex.getText().toString();
        String userage = join_age.getText().toString();

        //joinReqRes 객체에 사용자가 입력한 id와 pw를 저장
        JoinReqRes joinReqRes = new JoinReqRes(null, useremail, userpwd, usernm, usersex, userage);

        //retrofit 생성
        retrofitClient = RetrofitClient.getInstance();
        jsonPlaceHolderApi = RetrofitClient.getRetrofitInterface();

        jsonPlaceHolderApi.userAdd(joinReqRes).enqueue(new Callback<JoinReqRes>() {
            @Override
            public void onResponse(Call<JoinReqRes> call, Response<JoinReqRes> response) {
                Log.d("<< retrofit - Login >>", "Data fetch success");

                //통신 성공
                if (response.isSuccessful() && response.body() != null){
                    //response.body()를 result에 저장
                    JoinReqRes result = response.body();

                    Log.i(TAG, "response >> "+ result.toString());
                    String userNm = result.getUser_nm().toString();

                    Toast.makeText(JoinActivity.this, userNm + "님 환영합니다", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                    startActivity(intent);
                    JoinActivity.this.finish();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setTitle("알림")
                            .setMessage("회원 가입 정보 확인 부탁드립니다.")
                            .setPositiveButton("확인", null)
                            .create()
                            .show();
                }

            }

            @Override
            public void onFailure(Call<JoinReqRes> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                builder.setTitle("알림")
                        .setMessage("예기치 못한 오류가 발생하였습니다.\n 고객센터에 문의바랍니다.")
                        .setPositiveButton("확인", null)
                        .create()
                        .show();
            }
        });

    }


}

// -----------------------회원가입 처리--------------------------


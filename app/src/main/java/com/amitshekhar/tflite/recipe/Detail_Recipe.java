package com.amitshekhar.tflite.recipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amitshekhar.tflite.JsonPlaceHolderApi;
import com.amitshekhar.tflite.R;
import com.amitshekhar.tflite.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Detail_Recipe extends AppCompatActivity {


    TextView Dt_recipe_name;
    TextView Dt_recipe_dc;
    ImageView dt_img;

    private Integer recipe_seq;

    private RetrofitClient retrofitClient;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    private List<DCResponse> list = new ArrayList<DCResponse>(1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_recipe);

        Intent intent = getIntent();
        recipe_seq = intent.getExtras().getInt("recipe_seq");

        Log.d("recipe_seq >>" , ""+recipe_seq);

//        Dt_recipe_dc = findViewById(R.id.Dt_recipe_dc);
        Dt_recipe_name = (TextView) findViewById(R.id.Dt_recipe_name);
        Dt_recipe_dc = (TextView) findViewById(R.id.Dt_recipe_dc);
//        Dt_recipe_make = (ListView) findViewById(R.id.Dt_recipe_dc);
        dt_img = (ImageView) findViewById(R.id.Dt_img);

        DetailRecipe(recipe_seq);

    }

    public void DetailRecipe(Integer recipe_seq){
        //retrofit 생성
        retrofitClient = RetrofitClient.getInstance();
        jsonPlaceHolderApi = RetrofitClient.getRetrofitInterface();

        jsonPlaceHolderApi.getDetail(recipe_seq).enqueue(new Callback<List<DCResponse>>() {
            @Override
            public void onResponse(Call<List<DCResponse>> call, Response<List<DCResponse>> response) {
                Log.d("<< retrofit - DC >>", "Data fetch success");

                //통신 성공
                if (response.isSuccessful() && response.body() != null) {

                    //response.body()를 result에 저장
                    List<DCResponse> results = response.body();
                    String content = "" ;
                    String content1 = "" ;

                    for (DCResponse recipe : results) {


                        content1 += "  \n" + recipe.getCooking_no() + "   " + recipe.getCooking_dc() ;

                        //Dt_recipe_dc.append(content1);
                    }
                    Dt_recipe_dc.append(content1);
                    Dt_recipe_name.setText(results.get(0).getRecipe_nm());

                    switch (results.get(0).getRecipe_seq()){
                        case 1 :
                            dt_img.setBackgroundResource(R.drawable.bbb);
                            break;
                        case 2 :
                            dt_img.setBackgroundResource(R.drawable.eggroll);
                            break;
                        case 4 :
                            dt_img.setBackgroundResource(R.drawable.potato_stir);
                            break;
                        case 5 :
                            dt_img.setBackgroundResource(R.drawable.cccc);
                            break;
                        case 6 :
                            dt_img.setBackgroundResource(R.drawable.shrimp_rice1);
                            break;
                        case 3 :
                            dt_img.setBackgroundResource(R.drawable.gambas);
                            break;

                    }


                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(Detail_Recipe.this);
                    builder.setTitle("알림")
                            .setMessage("예기치 못한 오류가 발생하였습니다. /n 고객센터에 문의바랍니다.")
                            .setPositiveButton("확인", null)
                            .create()
                            .show();
                }
            }

            @Override
            public void onFailure(Call<List<DCResponse>> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Detail_Recipe.this);
                builder.setTitle("알림")
                        .setMessage("예기치 못한 오류가 발생하였습니다. /n 고객센터에 문의바랍니다.")
                        .setPositiveButton("확인", null)
                        .create()
                        .show();
            }
        });
    }

}
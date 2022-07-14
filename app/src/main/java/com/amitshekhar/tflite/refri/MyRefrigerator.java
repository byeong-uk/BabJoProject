package com.amitshekhar.tflite.refri;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import com.amitshekhar.tflite.JsonPlaceHolderApi;
import com.amitshekhar.tflite.R;
import com.amitshekhar.tflite.RetrofitClient;
import com.amitshekhar.tflite.users.LoginData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyRefrigerator#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyRefrigerator extends Fragment implements View.OnClickListener{

    private Classifier classifier;
    private static final String MODEL_PATH = "model.tflite"; //학습 모델
    private static final boolean QUANT = true; //양자화 여부
    private static final String LABEL_PATH = "labels2.txt"; //라벨
    private static final int INPUT_SIZE = 224; //입력 사이즈 크기
    
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    final private static String TAG = "MyRefrigerator";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RetrofitClient retrofitClient;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    String match = "[^\uAC00-\uD7A30-9a-zA-Z\\s]";

    private Context context;
    public MyRefrigerator() {
        // Required empty public constructor
    }


    private Executor executor = Executors.newSingleThreadExecutor();
    TextView textViewResult; //객체 인식 결과가 올라올 곳
    Uri photoUri; //포토 uri
    ImageView refrigV; //불러온 이미지가 올라올 곳
    Button refrigBT, sendref; //냉장고 불러오기 버튼

    // TODO: Rename and change types and number of parameters
    public static MyRefrigerator newInstance(String param1, String param2) {
        MyRefrigerator fragment = new MyRefrigerator();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, " : onCreateView() 내 냉장고 페이지");
        View view = inflater.inflate(R.layout.fragment_my_refrigerator, container, false);
        refrigBT = (Button)view.findViewById(R.id.refrigBT);
        sendref = (Button)view.findViewById(R.id.sendref);
        refrigV = (ImageView) view.findViewById(R.id.refrigV);
        textViewResult = (TextView) view.findViewById(R.id.detectResult);

        refrigBT.setOnClickListener(this);
        sendref.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.refrigBT: {
                Log.i(TAG, ": 내 냉장고 불러오기 버튼 클리");
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                Log.i(TAG, ": 이미지 선택");

                Log.i(TAG, ": initTensorFlowAdnLoadModel 진입");
                initTensorFlowAndLoadModel();
                Log.i(TAG, ": 모델 설정 완료");

                Log.i(TAG, ": startActivityResult 진입");
                startActivityResult.launch(intent);

                break;
            }

            case R.id.sendref: {
                Log.i(TAG, "식재료 서버로 전송");


//                String refriUserSeq = String.valueOf(1); //회원냉장고관리번호 (일단 지금은 1로 설정)
                String refriUserSeq = LoginData.loginUser.getUserSeq();
                String foodNm = textViewResult.getText().toString(); //[식재료1, 식재료 2, ..

                FoodAddResponse(refriUserSeq, foodNm);
            }


        }
    }

    public void FoodAddResponse(String refriUserSeq,String foodNm){

        foodNm =foodNm.replaceAll(match, ""); // 특수문자 없앰 -> 식재료1 식재료2 ...
        Log.i(TAG, "특수문자 없앰" + foodNm);
        String[] splitcdNm = foodNm.split(" "); // 최종 식재료 배열

        List<FoodRequest> requests = new ArrayList<FoodRequest>(1);

        for(int i=0;i< splitcdNm.length;i++){
            Log.i(TAG, "식재료 " + splitcdNm[i]);
            FoodRequest foodRequest = new FoodRequest(refriUserSeq, splitcdNm[i]);
            Log.i(TAG, "foodRequest " + foodRequest.getCdNm());
            Log.i(TAG, "foodRequest " + foodRequest.getRefriUserSeq());
            requests.add(foodRequest);
        }

        //retrofit 생성
        retrofitClient = RetrofitClient.getInstance();
        jsonPlaceHolderApi = RetrofitClient.getRetrofitInterface();


        jsonPlaceHolderApi.foodUpdate(requests).enqueue(new Callback<List<FoodResponse>>() {
            @Override
            public void onResponse(Call<List<FoodResponse>> call, Response<List<FoodResponse>> responses) {
                Log.d("<< retrofit - Update >>", "Data fetch success");

                //통신 성공
                if (responses.isSuccessful() && responses.body() != null) {

                    //response.body()를 result에 저장
                    List<FoodResponse> results = responses.body();

                    Log.i(TAG, "response >> "+ results.toString());


                    Toast.makeText(getActivity(), "식재료 업데이트 성공", Toast.LENGTH_LONG).show();

                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("알림")
                            .setMessage("예기치 못한 오류가 발생하였습니다. /n고객센터에 문의바랍니다.")
                            .setPositiveButton("확인", null)
                            .create()
                            .show();
                }
            }

            @Override
            public void onFailure(Call<List<FoodResponse>> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("알림")
                        .setMessage("예기치 못한 오류가 발생하였습니다.\n 고객센터에 문의바랍니다.")
                        .setPositiveButton("확인", null)
                        .create()
                        .show();
            }
        });
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if ( result.getResultCode()== RESULT_OK && result.getData() != null){

                        photoUri = result.getData().getData(); //선택한 데이터(이미지)에 대하여 uri를 얻어옴

                        try{
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri); //uri에서 비트맵 얻음
                            refrigV.setImageBitmap(bitmap);
                            Log.i(TAG, ": 이미지 올리기 완료");


                            bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false); //해당 매개변수로 크기를 재조정
                            Log.i(TAG, ": 비트맵 크기 재조정");

                            final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
                            Log.i(TAG, ": 객체 인식 완료");

                            textViewResult.setText(results.toString());
                            Log.i(TAG, "##################################" + results.toString());


                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

    );


    private void initTensorFlowAndLoadModel() {

        AssetManager manager=getResources().getAssets();
        executor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            manager,
                            MODEL_PATH,
                            LABEL_PATH,
                            INPUT_SIZE,
                            QUANT);
                    makeButtonVisible();
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    private void makeButtonVisible() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                refrigBT.setVisibility(View.VISIBLE);
            }
        });
    }
}
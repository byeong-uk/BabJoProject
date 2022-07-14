package com.amitshekhar.tflite.refri;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amitshekhar.tflite.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPage#newInstance} factory method to
 * create an instance of this fragment.
 */

//사진 촬영 페이지
public class AddPage extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    final private static String TAG = "AddPage";
    private int RESULT_PERMISSIONS = 100;

    Button camBT;
    ImageView camV;
    Uri photoUri;
    String mCurrentPhotoPath;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddPage.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPage newInstance(String param1, String param2) {
        AddPage fragment = new AddPage();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_page, container, false);
        camBT = (Button) view.findViewById(R.id.camBT); //카메라 버튼
        camV = (ImageView) view.findViewById(R.id.camV); //카메라로 찍은 이미지가 올라갈 곳
        camBT.setOnClickListener(this);
        return view;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camBT:
                Log.i(TAG, " onClick() amBT Click 카메라 불러옴");
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //카메라 쵤영으로 이동할 intent (파일 공유 방식)
                File photoFile = null; //이미지가 담길 파일
                try{
                    photoFile = createImageFile();
                } catch (IOException ex) {
                }

                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(getActivity(),  "com.amitshekhar.tflite.fileprovider", photoFile); //파일로부터 uri를 얻어옴
                    i.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //찍은 사진을 저장할 위치로 photoUri를 넘겨받음
                    startActivityResult.launch(i); //촬영 시작
                } break;
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); //사진을 찍는 순간의 시간으로 설정
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES); //이미지를 저장할 수 있는 디렉토리
        File image = File.createTempFile( imageFileName, ".jpg", storageDir ); //이미지 파일을 저장하기 위한 임시 파일
        mCurrentPhotoPath = image.getAbsolutePath(); //사진이 저장될 file객체를 넘겨줌
        Log.i(TAG, "createImageFile success, path. " + mCurrentPhotoPath);
        return image;
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    File file = new File(mCurrentPhotoPath);
                    Bitmap bitmap;
                    Log.i(TAG, "onactivityResult bitmap");
                    if (Build.VERSION.SDK_INT >= 29) {
                        ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), Uri.fromFile(file));
                        try {
                            bitmap = ImageDecoder.decodeBitmap(source);
                            if (bitmap != null)
                                camV.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    // 이미지뷰에 파일경로의 사진을 가져와 출력
                    camV.setImageURI(photoUri);
                }
            });
}
package com.amitshekhar.tflite.recipe;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amitshekhar.tflite.R;
import com.amitshekhar.tflite.users.LoginData;
import com.amitshekhar.tflite.users.User;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    private final String TAG = "RecyclerAdapter";

    // adapter에 들어갈 list 입니다.
    private ArrayList<RecycleData> listData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.

        holder.onBind(listData.get(position)); //화면에 아이템 bind

        holder.layout1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                int mPosition = holder.getAdapterPosition();
                Intent intent = new Intent(view.getContext(), Detail_Recipe.class);
                intent.putExtra("recipe_seq", listData.get(mPosition).getRe_seq());
                view.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }


    public void addItem(RecycleData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }


    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView textView1;
        private TextView textView2;
        private ImageView imageView;
        private LinearLayout layout1;

        ItemViewHolder(View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.re_name);
            textView2 = itemView.findViewById(R.id.re_content);
            imageView = itemView.findViewById(R.id.re_profile);
            layout1 = itemView.findViewById(R.id.recipeLayout);

        }

        void onBind(RecycleData data) {

            textView1.setText(data.getRe_name());
            textView2.setText(data.getRe_content());
            switch (data.getRe_seq()){
                case 1 :
                    imageView.setBackgroundResource(R.drawable.bbb);
                    break;
                case 2 :
                    imageView.setBackgroundResource(R.drawable.eggroll);
                    break;
                case 4 :
                    imageView.setBackgroundResource(R.drawable.potato_stir);
                    break;
                case 5 :
                    imageView.setBackgroundResource(R.drawable.cccc);
                    break;
                case 6 :
                    imageView.setBackgroundResource(R.drawable.shrimp_rice1);
                    break;
                case 3 :
                    imageView.setBackgroundResource(R.drawable.gambas);
                    break;

            }

        }
    }
}


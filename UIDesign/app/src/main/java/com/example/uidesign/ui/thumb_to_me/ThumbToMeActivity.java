package com.example.uidesign.ui.thumb_to_me;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.uidesign.data.Comment;
import com.example.uidesign.data.Like;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.data.database.DatabaseManager;
import com.example.uidesign.data.database.Entity_Comment;
import com.example.uidesign.data.database.Entity_Like;
import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityThumbToMeBinding;
import com.example.uidesign.ui.comment_to_me.CommentToMeAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ThumbToMeActivity extends BaseActivity {

    private ActivityThumbToMeBinding binding;
    private Context thisContext=this;
    private Activity thisActivity=this;

    private List<Like> likeList;
    private ThumbToMeAdapter thumbToMeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityThumbToMeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.likeRecyclerView.setLayoutManager(new LinearLayoutManager(thisContext));
        likeList=new ArrayList<Like>();
        List<Entity_Like> entity_likes= DatabaseManager.getAppDatabase().dao_like().getLikes(LogginedUser.getInstance().getUid());
        for(Entity_Like e:entity_likes)
        {
            Like like=new Like();
            like.from=e.from;
            like.fromName=e.fromName;
            like.nowDate=new Date(e.date);
        }

        thumbToMeAdapter=new ThumbToMeAdapter(likeList,thisContext);
        binding.likeRecyclerView.setAdapter(thumbToMeAdapter);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisActivity.finish();
            }
        });
    }
}
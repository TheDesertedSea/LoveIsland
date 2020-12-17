package com.example.uidesign.ui.comment_to_me;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.view.View;


import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.uidesign.data.Comment;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.data.database.DatabaseManager;
import com.example.uidesign.data.database.Entity_Comment;
import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityCommentToMeBinding;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentToMeActivity extends BaseActivity {

    private ActivityCommentToMeBinding binding;
    private Context thisContext=this;
    private Activity thisActivity=this;

    private List<Comment> commentList;
    private CommentToMeAdapter commentToMeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCommentToMeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.commentRecyclerView.setLayoutManager(new LinearLayoutManager(thisContext));
        commentList=new ArrayList<Comment>();
        List<Entity_Comment> entity_comments= DatabaseManager.getAppDatabase().dao_comment().getComments(LogginedUser.getInstance().getUid());
        for(Entity_Comment e:entity_comments)
        {
            Comment comment=new Comment();
            comment.from=e.from;
            comment.fromName=e.fromName;
            comment.com=e.content;
            comment.nowDate=new Date(e.date);
            commentList.add(comment);
        }

        commentToMeAdapter=new CommentToMeAdapter(commentList,thisContext);
        binding.commentRecyclerView.setAdapter(commentToMeAdapter);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisActivity.finish();
            }
        });

    }
}
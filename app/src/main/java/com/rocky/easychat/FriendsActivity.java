package com.rocky.easychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<User> users;
    private ProgressBar progressBar;
    private UserAdapter usersAdapter;
    UserAdapter.OnUserClickListener onUserClickListener;
    private SwipeRefreshLayout swipeRefreshLayout;

    String myImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        progressBar = findViewById(R.id.progressBar);
        users = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);

      //  Glide.with(FriendsActivity.this).load(FirebaseDatabase.getInstance().getReference("user").child("profilePicture")).placeholder(R.drawable.account_img).error(R.drawable.account_img).into(R.menu.menu_item_profile);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsers();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        onUserClickListener = new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClicked(int position) {
                startActivity(new Intent(FriendsActivity.this,MessageActivity.class)
                        .putExtra("username_of_roommate",users.get(position).getUsername())
                        .putExtra("email_of_roommate",users.get(position).getEmail())
                        .putExtra("img_of_roommate",users.get(position).getProfilePicture())
                        .putExtra("my_img",myImageUrl)

                );

            }
        };

        getUsers();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.profile_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menu_item_profile){

            startActivity(new Intent(FriendsActivity.this,Profile.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void getUsers(){
        users.clear();


        FirebaseDatabase.getInstance().getReference("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    users.add(dataSnapshot.getValue(User.class));
                }
                usersAdapter = new UserAdapter(users,FriendsActivity.this,onUserClickListener);
                recyclerView.setLayoutManager(new LinearLayoutManager(FriendsActivity.this));
                recyclerView.setAdapter(usersAdapter);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                for (User user: users){
                    if(user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                        myImageUrl = user.getProfilePicture();
                        return;
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
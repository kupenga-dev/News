package com.nopalyer.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;
import com.nopalyer.newsapp.Model.Articles;
import com.nopalyer.newsapp.Model.Headlines;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
{
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    Dialog dialog;
    final String API_KEY = "ApiKey";
    Adapter adapter;
    List<Articles>  articles = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        recyclerView = findViewById(R.id.recyclerView);
        dialog = new Dialog(MainActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final String country = "us";
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh() {
                retrieveJson("",country,API_KEY);
            }
        });
        retrieveJson("",country,API_KEY);
    }

    public void retrieveJson(String query ,String country, String apiKey)
    {
        swipeRefreshLayout.setRefreshing(true);
        Call<Headlines> call;
        call= ApiClient.getInstance().getApi().getHeadlines(country,apiKey);
        call.enqueue(new Callback<Headlines>() {
            @Override
            public void onResponse(Call<Headlines> call, Response<Headlines> response)
            {
                if (response.isSuccessful() && response.body().getArticles() != null)
                {
                    swipeRefreshLayout.setRefreshing(false);
                    articles.clear();
                    articles = response.body().getArticles();
                    adapter = new Adapter(MainActivity.this,articles);
                    recyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<Headlines> call, Throwable t)
            {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

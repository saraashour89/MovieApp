package mal.com.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import mal.com.DB.DBOperations;
import mal.com.Interfaces.MovieInterface;
import mal.com.fragment.MovieDetailsFragment;
import mal.com.fragment.MovieFragment;

public class MainActivity extends AppCompatActivity implements MovieInterface {

    private static boolean isTowPane = false;
    SharedPreferences sharedPreferences;
    public static final String SHARED_NAME = "CHECK_FIRST";
    private boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            first = savedInstanceState.getBoolean("first", true);
        }

        checkFirstRun();
        initView();


    }

    private void checkFirstRun() {

        sharedPreferences = getSharedPreferences(SHARED_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean("first", true)) {
            Log.e("first", "true");
            editor.putBoolean("first", false);
            editor.commit();
            DBOperations.CreateDB(this);
        } else {
            Log.e("first", "false");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    private void initView() {
        MovieFragment movieFragment;
        if (first) {
            isTowPane = false;
            movieFragment = new MovieFragment();

            getFragmentManager().beginTransaction()
                    .add(R.id.fl_frgList, movieFragment, "MovieFragment").commit();

            if (null != findViewById(R.id.fl_frgDetails)) {
                isTowPane = true;
                Log.e("tablet", "true");
            }
        } else {
            movieFragment = (MovieFragment)
                    getFragmentManager().findFragmentByTag("MovieFragment");
        }
        movieFragment.setMovieInterface(this);

    }

    @Override
    public void sendMovie(int id, String title, String img, String overView, String date, double vote) {

        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putInt("id", id);
        bundle.putString("img", img);
        bundle.putString("overview", overView);
        bundle.putString("date", date);
        bundle.putDouble("vote", vote);

        if (isTowPane) {
            MovieDetailsFragment detailsFragment = new MovieDetailsFragment();
            detailsFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.fl_frgDetails, detailsFragment, "")
                    .commit();
        } else {

            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("first", false);
        super.onSaveInstanceState(outState);
    }
}


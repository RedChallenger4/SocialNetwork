package demo.appium.com.socialnetwork;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
{
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private Toolbar mToolbar;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef; // users reference

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // creates a new instance for user
        mAuth = FirebaseAuth.getInstance();
        // node where users information is stored
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        // added toolbar to main activity
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Home");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_closed);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header); // storing navigation header

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                // in charge of directing actions from user menu
                UserMenuSelector(item);
                return false;
            }
        });
    }

    // when the app starts, this method will be called automatically
    @Override
    protected void onStart() {
        super.onStart();

        // so it won't crash, Firebase instance is declared again for checks
        if(mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        // checks if there is an active user instance
        // if not, null means user is not connected to Firebase
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // check if token/user exists
        if(currentUser == null) {
            // sends user to Login Screen right away
            SendUserToLoginActivity();
        } else {
            CheckUserExistence();
        }
    }

    // WARNING: MOST IMPORTANT b/c of user authentication
    private void CheckUserExistence() {
        // gets user id
        final String current_user_id = mAuth.getCurrentUser().getUid();

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // if user id doesn't exist in Firebase but is authenticated
                //  it needs to have record in Firebase Database
                if (!dataSnapshot.hasChild(current_user_id)) {

                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToSetupActivity() {
        // directs where User will go from start to finish
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);

        // not allow the user to return to Main Activity
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(setupIntent);

        // closes Main Activity so you cannot go back
        finish();
    }

    private void SendUserToLoginActivity() {

        // directs where User will go from start to finish
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);

        // not allow the user to return to Main Activity (unless he already logged in account)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(loginIntent);

        // closes Main Activity so you cannot go back
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // enable toggle for actionbar drawer
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item)
    {
        // selecting menu item
        switch( item.getItemId() )
        {
            case R.id.nav_profile:
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_friends:
                Toast.makeText(this, "Friend List", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_find_friends:
                Toast.makeText(this, "Find Friends", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_messages:
                Toast.makeText(this, "Messages", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_Logout:
                mAuth.signOut(); // signs out user from firebase
                SendUserToLoginActivity(); // sends user back to LoginActivity
                break;

            default:
                Toast.makeText(this, "Default", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

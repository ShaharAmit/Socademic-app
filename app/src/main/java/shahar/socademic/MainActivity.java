package shahar.socademic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.leakcanary.LeakCanary;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference dbRef ;
    private static final int SIGN_IN_REQUEST_CODE = 234;

    RecyclerView gRecycle;
    ArrayList<gItem> groups = new ArrayList<>();
    private RecyclerView.LayoutManager gLayoutManager;
    private RecyclerView.Adapter gAdapter;
    Intent intent;
    Toolbar toolbar_bottom;
    Toolbar toolbar_top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(getApplication());
        toolbar_bottom = (Toolbar) findViewById(R.id.toolbar_bottom);
        toolbar_top = (Toolbar) findViewById(R.id.toolbar_top);

        setSupportActionBar(toolbar_top);
        getSupportActionBar().setTitle("Socademic");
        setSupportActionBar(toolbar_bottom);

        intent = new Intent(this,chat.class);
        gRecycle = (RecyclerView) findViewById(R.id.recycleList);
        gLayoutManager = new LinearLayoutManager(this);
        gRecycle.setLayoutManager(gLayoutManager);
        gRecycle.addItemDecoration(new SimpleDividerItemDecoration(this));
        gAdapter = new gAdapter(groups, gClickListener);
        gRecycle.setAdapter(gAdapter);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser temp = firebaseAuth.getCurrentUser();
        if (firebaseAuth.getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .build(), SIGN_IN_REQUEST_CODE);
        } else {
            Log.d("login" ,"already signed in!");
            getChatList();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("login" ,"Signed in successful!");
                getChatList();
            } else {
                Log.d("login" ,"Signed in failed!");

                // Close the app
                finish();
            }
        }
    }

    void getChatList(){
        database = FirebaseDatabase.getInstance();
        Log.d("login","succeeded");
        dbRef = database.getReference().child("chatList");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groups.clear();
                if(gAdapter.getItemCount()!=0)
                    gRecycle.removeAllViewsInLayout();
                for (DataSnapshot item : dataSnapshot.getChildren()){
                    groups.add(new gItem(item.getKey()));
                }
                gAdapter = new gAdapter(groups, gClickListener);
                gRecycle.setAdapter(gAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    View.OnClickListener gClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            gItem clicked = (gItem) view.getTag();
            if (clicked.getLabel().isEmpty()) {
                Log.d("chatClicked","some thing went wrong");
            } else {
                Log.d("chatClicked","succeeded");
                intent.putExtra("groupId",clicked.getLabel());
                startActivity(intent);
            }
        }
    };

    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = ContextCompat.getDrawable(context,R.drawable.line_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

}

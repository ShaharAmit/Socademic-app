package shahar.socademic;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class chat extends AppCompatActivity {
    private FirebaseListAdapter<ChatMessage> adapter;
    private ListView listView;
    private EditText input;
    private FloatingActionButton fab;
    private String loggedInUserName = "";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef ;
    Toolbar toolbar_bottom;
    Toolbar toolbar_top;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        listView = (ListView) findViewById(R.id.list);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        input = (EditText) findViewById(R.id.input);
        Intent intent = getIntent();
        dbRef = database.getReference().child("chatList");
        String groupId = intent.getStringExtra("groupId");
        dbRef = dbRef.child(groupId);

        toolbar_bottom = (Toolbar) findViewById(R.id.toolbar_bottom);
        toolbar_top = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar_top);
        getSupportActionBar().setTitle(groupId);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportActionBar(toolbar_bottom);

        adapter = new MessageAdapter(this, ChatMessage.class, R.layout.item_in_message,dbRef.getRef());
        listView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input.getText().toString().trim().equals("")) {
                    Toast.makeText(chat.this , "Please enter some texts!", Toast.LENGTH_SHORT).show();
                } else {
                    dbRef.getRef()
                            .push()
                            .setValue(new ChatMessage(input.getText().toString(),
                                    FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                    FirebaseAuth.getInstance().getCurrentUser().getUid())
                            );
                    input.setText("");
                }
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}

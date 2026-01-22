package ro.pub.cs.systems.eim.practicaltest02v9.views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import ro.pub.cs.systems.eim.practicaltest02v9.R;
import ro.pub.cs.systems.eim.practicaltest02v9.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02v9.network.ClientThread;
import ro.pub.cs.systems.eim.practicaltest02v9.network.ServerThread;

public class PracticalTest02v9MainActivity extends AppCompatActivity {

    private ServerThread serverThread;
    private ClientThread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_practical_test02v9_main);

        // server widgets
        EditText serverPortEditText = (EditText) findViewById(R.id.server_port_edit_text);
        Button connectButton = (Button) findViewById(R.id.connect_button);

        // client widgets
        EditText worlEditText = (EditText) findViewById(R.id.word);
        EditText letterNrEditText = (EditText) findViewById(R.id.letter_nr);
        Button getResponseEditText = (Button) findViewById(R.id.get_result);
        EditText serverEdittextPort = findViewById(R.id.server_port_edit_text);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverPortString = serverPortEditText.getText().toString();

                if (serverPortString.isEmpty()) {
                    Log.e(Constants.TAG, "onClick: Server port should be filled!");
                    return;
                }

                serverThread = new ServerThread(8888);

                if (serverThread.getServerSocket() == null) {
                    Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                    return;
                }

                serverThread.start();
            }
        });

        getResponseEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = worlEditText.getText().toString();
                String letter_nr = letterNrEditText.getText().toString();
                getResponseEditText.setText(Constants.EMPTY_STRING);

                clientThread = new ClientThread("localhost", 8888, word, letter_nr, getResponseEditText);
                clientThread.start();
            }
        });
    }
}

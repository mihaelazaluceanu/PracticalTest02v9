package ro.pub.cs.systems.eim.practicaltest02v9.network;

import android.util.Log;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ro.pub.cs.systems.eim.practicaltest02v9.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02v9.general.Utilities;

public class CommunicationThread extends Thread {
    private ServerThread serverThread = null;
    private Socket socket = null;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[comm thread] run: Socket is null");
            return;
        }

        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            String word = bufferedReader.readLine();
            String letter_nr = bufferedReader.readLine();

            Log.d(Constants.TAG, "Clientul a cerut " + word + " " + letter_nr);

//            word = "nevermind";
            String query = Constants.WEB_SERVICE_ADDRESS +
                    ":" + word;

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(query)
                    .build();

            String pageSourceCode = "";
            try (Response response = client.newCall(request).execute()) {
                pageSourceCode = response.body().string();
            }

            if (pageSourceCode == null) {
                Log.e(Constants.TAG, "[comm thread]: error getting the info from the webservice");
                return;
            }

            Log.i(Constants.TAG, "[comm thread]: got the response from webservice");

            JSONObject content = new JSONObject(pageSourceCode);
            Log.d(Constants.TAG, pageSourceCode);
            if (content.has("cod") && content.getString("cod").equals("404")) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] '" + word + "' not found!");
                printWriter.println("'" + word + "' not found!");
                printWriter.flush();
                return;
            }

            JSONArray resultArray = content.getJSONArray("all");
            JSONObject res;

            String result = "";

            for (int i = 0; i < resultArray.length(); i++) {
                if (resultArray.getString(i).length() > Integer.parseInt(letter_nr)) {
                    result += resultArray.getString(i) + " ";
                }
            }

            printWriter.println(result);
            printWriter.flush();

        } catch (IOException | JSONException e) {
            Log.e(Constants.TAG, "[comm thread ]: io exception " + e.getMessage());
        }
    }
}

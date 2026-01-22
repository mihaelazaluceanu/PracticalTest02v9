package ro.pub.cs.systems.eim.practicaltest02v9.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02v9.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02v9.general.Utilities;

public class ClientThread extends Thread{
    private String address;
    private int port;

    private String word;

    private String letter_nr;
    private TextView getResponseResult;

    private Socket socket;

    public ClientThread(String address, int port, String word, String letter_nr, TextView getResponseResult) {
        this.address = address;
        this.port = port;
        this.word = word;
        this.letter_nr = letter_nr;
        this.getResponseResult = getResponseResult;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            
            PrintWriter printWriter = Utilities.getWriter(socket);

            printWriter.println(word);
            printWriter.flush();

            printWriter.println(letter_nr);
            printWriter.flush();

            BufferedReader bufferedReader = Utilities.getReader(socket);
            String response;

            while ((response = bufferedReader.readLine()) != null) {
                final String weatherInformation = response;

                getResponseResult.post(new Runnable() {
                    @Override
                    public void run() {
                        getResponseResult.setText(weatherInformation);
                    }
                });
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e(Constants.TAG, "run: Exception has occurred " + e.getMessage());
                }
            }
        }
    }
}

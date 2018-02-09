package com.example.stingy.client;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    Socket socket;
    public String debuggingString = "DEBUG";
    public String hostname = "10.10.0.185";
    public int portNumber = 4444;
    public String code;
    public String message;
    public String movieTitle;
    TextView movieView;
    public char[] charCode = new char[1024];
    public int mistake = 0;
    ImageView hangPicture;
    TextView mistakeView;
    Button btnSend;
    String messageLetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setRequestedOrientation(MainActivity.SCREEN_ORIENTATION_PORTRAIT);
        hangPicture = (ImageView) findViewById(R.id.imageView);
        mistakeView = (TextView) findViewById(R.id.textView4);
        hangPicture.setImageResource(R.drawable.cool_pic1);
        btnSend = (Button)findViewById(R.id.save);
        new Thread() {
            @Override
            public void run() {
                try {
                    //connecting
                    Log.e(debuggingString, "Attempting to connect to server");
                    socket = new Socket(hostname, portNumber);

                    //Send message to server
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    bw.write("Network established.");
                    bw.newLine();
                    bw.flush();

                    //Receive message from server
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    movieTitle = br.readLine();
                    code = br.readLine();
                    movieView = (TextView) findViewById(R.id.textView2);
                    movieView.setText(code);
                    setCode();
                    mistakeView.setText(Integer.toString(mistake));

                    while ((message = br.readLine()) != null) {
                        // primanje poruka sa servera, trenutno ne potrebno
                    }



                } catch (
                        Exception e
                        ) {
                    Log.e(debuggingString, e.getMessage());
                }
            }
        }.start();
    }

    boolean provjeraSlova(char letter) {
        for (int i = 0; i < movieTitle.length(); i++) {
            if (letter == movieTitle.toLowerCase().charAt(i))
                return true;
        }
        return false;
    }

    void decode(char letter) {
        for (int i = 0; i < movieTitle.length(); i++) {
            if (letter == movieTitle.toLowerCase().charAt(i))
                charCode[i] = movieTitle.charAt(i);
        }
        code = new String(charCode, 0, movieTitle.length());
    }

    void setCode() {
        for (int i = 0; i < movieTitle.length(); i++) {
            if (!Character.isLetter(movieTitle.charAt(i)))
                charCode[i] = movieTitle.charAt(i);
            else
                charCode[i] = '*';
        }
    }

    public void sendMessage(View v) {
        EditText editText = (EditText) findViewById(R.id.edittext);
        char letterLocal;
        BufferedWriter bw = null;
        messageLetter = editText.getText().toString();

        if (messageLetter.matches("")) {
            Toast.makeText(this, "You did not enter letter", Toast.LENGTH_SHORT).show();
            return;
        }

        //Toast.makeText(MainActivity.this, "Letter send", Toast.LENGTH_LONG).show();
        try {
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(messageLetter);
            editText.setText("");
            bw.newLine();
            bw.flush();

            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            Log.e(debuggingString, e.getMessage());
        }

        letterLocal = messageLetter.charAt(0);

        if (provjeraSlova(letterLocal) == true && messageLetter.length() > 0) {
            decode(letterLocal);
            Toast.makeText(MainActivity.this, "Nice!", Toast.LENGTH_SHORT).show();
        } else {
            mistake++;

            if (mistake == 1) {
                hangPicture.setImageResource(R.drawable.cool_pic2);
                Toast.makeText(MainActivity.this, "Wrong!", Toast.LENGTH_SHORT).show();
            } else if (mistake == 2) {
                hangPicture.setImageResource(R.drawable.cool_pic3);
                Toast.makeText(MainActivity.this, "Wrong!", Toast.LENGTH_SHORT).show();
            } else if (mistake == 3) {
                hangPicture.setImageResource(R.drawable.cool_pic4);
                Toast.makeText(MainActivity.this, "Wrong!", Toast.LENGTH_SHORT).show();
            } else if (mistake == 4) {
                hangPicture.setImageResource(R.drawable.cool_pic5);
                Toast.makeText(MainActivity.this, "Wrong!", Toast.LENGTH_SHORT).show();
            } else if (mistake == 5) {
                hangPicture.setImageResource(R.drawable.cool_pic6);
                Toast.makeText(MainActivity.this, "Wrong!", Toast.LENGTH_SHORT).show();
            } else if (mistake == 6) {
                hangPicture.setImageResource(R.drawable.cool_pic7);
                Toast.makeText(MainActivity.this, "Wrong!", Toast.LENGTH_SHORT).show();
            } else if (mistake == 7) {
                hangPicture.setImageResource(R.drawable.cool_pic8);
                Toast.makeText(MainActivity.this, "End Game!", Toast.LENGTH_SHORT).show();
                editText.setEnabled(false);
                editText.setHint("Game Over!");
            }
        }

        if (code.equals(movieTitle)) {
            Toast.makeText(MainActivity.this, "You won!", Toast.LENGTH_LONG).show();
            editText.setEnabled(false);
            editText.setHint("Game finished!");
        }
        //Toast.makeText(MainActivity.this, code, Toast.LENGTH_LONG).show();

        movieView.setText(code);
        mistakeView.setText(Integer.toString(mistake));
    }
}
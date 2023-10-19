package ng.ojibe.storeme;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {
    EditText et_phone, et_pin;
    Button loginBtn;
    SharedPreferences temp_store;
    SharedPreferences.Editor store_editor;
    Context ctx;
    OkHttpClient client = new OkHttpClient();

    private ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ctx = this;
        temp_store = this.getSharedPreferences("temp_store", Context.MODE_PRIVATE);
        store_editor = temp_store.edit();

        et_phone = findViewById(R.id.et_phone);
        et_pin = findViewById(R.id.et_pin);
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v -> {
            String phone = et_phone.getText().toString();
            String pin = et_pin.getText().toString();
            if(!phone.isEmpty()){
                store_editor.putString("phone", phone);
                store_editor.apply();

                if(!pin.isEmpty()){
                    getAPIObject(phone,pin);
                }else{
                    et_pin.setError("This is required");
                }
            }else{
                et_phone.setError("This is required");
            }
        });
        String phone = temp_store.getString("phone", "00");
        if(!phone.equals("00"))et_phone.setText(phone);


    }

    private void getAPIObject(String ...params){
        FormBody.Builder body = new FormBody.Builder();
        body.add("phone", params[0]);
        body.add("pin", params[1]);
        RequestBody requestBody = body.build();
        Request request = Tools.newAPIRequest("post", "login", requestBody);
        Thread thread = new Thread(() -> {
            try (Response response = client.newCall(request).execute()) {
                if(response.isSuccessful()){
                    String responseString = Objects.requireNonNull(response.body()).string();
                    runOnUiThread(() -> {
                        try {
                            JSONObject js = new JSONObject(responseString);
                            if(js.getBoolean("status")){
                                Intent intent = new Intent(ctx, Dashboard.class);
                                //intent.putExtra("phone", phone);
                                startActivity(intent);
                            }else{
                                Toast.makeText(ctx, "Invalid credentials", Toast.LENGTH_LONG).show();
                                Log.e("Response", "Invalid Credentials");
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }else{
                    Log.e("FAILED", "API call failed");
                }
            }catch (IOException e){
                Log.e("FAILED", e.getMessage());
            }
        });
        thread.start();
    }
}
package ng.ojibe.storeme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class Dashboard extends AppCompatActivity {
    Button fetchButton;
    EditText et_regNo;
    String phone;
    SharedPreferences temp_store;
    SharedPreferences.Editor store_editor;
    OkHttpClient client = new OkHttpClient();
    Context ctx;
    DBGuy db;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ctx = this;

        db = new DBGuy(ctx);
        temp_store = this.getSharedPreferences("temp_store", Context.MODE_PRIVATE);
        store_editor = temp_store.edit();

        phone = temp_store.getString("phone", "00");
        if(phone.equals("00"))finish();

        fetchButton = findViewById(R.id.fetchBtn);
        et_regNo =  findViewById(R.id.et_regNo);

        fetchButton.setOnClickListener(v -> {
            String regNo = et_regNo.getText().toString();
            if(!regNo.isEmpty()){
                getAPIObject("fetch-student",new String[]{phone,regNo});
            }else{
                et_regNo.setError("This is required");
            }
        });
    }

    void getAPIObject(String endpoint, String ...args){

        FormBody.Builder params = new FormBody.Builder();
        int c=0;
        for(String arg: args){
            params.add("p"+c, arg);
            c++;
        }
        RequestBody body = params.build();
        Request request = Tools.newAPIRequest("post",endpoint,body);
        new Thread(()->{
            try (Response response = client.newCall(request).execute()) {
                if(response.isSuccessful()){
                    String res = response.body().string();
                    runOnUiThread(()->{
                        try {
                            if (endpoint.equals("fetch-student")) {
                                JSONObject res_j = new JSONObject(res);
                                JSONObject data = res_j.getJSONObject("data");
                                Student stud = new Student(data.getString("regNo"),
                                        data.getString("name"),
                                        data.getString("dept"),
                                        data.getInt("age"),
                                        data.getInt("level"));
                                db.addStudent(stud);
                                Intent intent = new Intent(ctx, StudentData.class);
                                intent.putExtra("student", res);
                                startActivity(intent);
                            } else {
                                //do this other thing
                            }
                        }catch (JSONException ignored){}
                    });
                }else{
                    Log.e("Fatatlity", "Can't connect to API");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
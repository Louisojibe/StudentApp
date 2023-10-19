package ng.ojibe.storeme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StudentData extends AppCompatActivity {
    String regNo;
    String studentName;
    SharedPreferences temp_store;
    TextView tv_regNo,tv_studentName,tv_age,tv_dept,tv_level;
    EditText et_updateLevel;
    Button levelUpdateBtn;
    OkHttpClient client = new OkHttpClient();
    DBGuy db;

    Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_data);

        ctx = this;
        db = new DBGuy(ctx);
        temp_store = this.getSharedPreferences("temp_store", Context.MODE_PRIVATE);

        Bundle extra = getIntent().getExtras();
        try {
            tv_regNo = findViewById(R.id.tv_regNo);
            tv_studentName = findViewById(R.id.tv_studentName);
            tv_age = findViewById(R.id.tv_age);
            tv_level = findViewById(R.id.tv_level);
            tv_dept = findViewById(R.id.tv_dept);
            et_updateLevel = findViewById(R.id.et_updateLevel);
            levelUpdateBtn = findViewById(R.id.levelUpdateBtn);

            JSONObject student = new JSONObject(extra.getString("student"));
            Log.e("Student", student.toString());
            if(student.getBoolean("status")){
                JSONObject data = student.getJSONObject("data");

                String regNo = data.getString("regNo");
                Student db_student = db.getStudent(regNo);
                tv_regNo.setText(regNo);
                tv_dept.setText(db_student.getDept());
                tv_level.setText(db_student.getLevel()+"");
                tv_studentName.setText(db_student.getName());
                tv_age.setText(db_student.getAge()+"");

                levelUpdateBtn.setOnClickListener(v -> {
                    String updateLevel = et_updateLevel.getText().toString();
                    if(!updateLevel.isEmpty()){
                            getAPIObject("update-level", new String[]{regNo,updateLevel});
                    }else {
                        et_updateLevel.setError("This is required!");
                    }
                });
            }else{
                tv_regNo.setText("");
                tv_dept.setText("");
                tv_level.setText("");
                tv_studentName.setText("");
                tv_age.setText("");
                Tools.displayMessage(ctx,"Failed!",
                        "Something went wrong at the server",true);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

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
                        tv_level.setText(args[1]);
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
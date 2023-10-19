package ng.ojibe.storeme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import okhttp3.Request;
import okhttp3.RequestBody;

public class Tools {
    public static void displayMessage(Context ctx, String title, String message, boolean finish){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Okay", (dialog, which) -> {
            dialog.cancel();
            if(finish)((Activity) ctx).finish();
        });
        builder.show();
    }
    public static Request newAPIRequest(String method, String endpoint, RequestBody body){
        String baseURL = "http://192.168.84.129:8080/SVSAPI/start/";
        if(method.equals("post")) {
            return new Request.Builder()
                    .url(baseURL + endpoint)
                    .post(body)
                    .build();
        }else{
            return new Request.Builder()
                    .url(baseURL + endpoint)
                    .get()
                    .build();
        }
    }
}

package interaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.gamevoip.epicorg.gamevoip.R;

/**
 * Created by Luca on 30/03/2015.
 */
public class CustomAlertDialog {

    private String title;
    private String message;
    private String button;
    private Context context;
    private AlertDialog alertDialog;

    public CustomAlertDialog(String title, String message, String button, Context context) {
        this.title = title;
        this.message = message;
        this.button = button;
        this.context = context;
        create();

    }

    private void create() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title);
        builder.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog = builder.create();
    }

    public void show(){
        alertDialog.show();
    }
}

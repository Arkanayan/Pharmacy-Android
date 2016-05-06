package bankura.pharmacy.pharmacyapp.Utils;

import android.app.Activity;
import android.content.Context;

import com.cloudinary.Cloudinary;

import bankura.pharmacy.pharmacyapp.App;
import bankura.pharmacy.pharmacyapp.R;
import bankura.pharmacy.pharmacyapp.activities.LoginActivity;

/**
 * Created by arka on 4/29/16.
 */
public class Utils {

    public static String getStringOrEmpty(String string) {

        return string != null ? string : "";
    }

    public static void checkLogin(Context context) {
        if (App.getFirebase().getAuth() == null) {
            context.startActivity(LoginActivity.getInstance(context));
            ((Activity) context).finish();
        }
    }

    public static Cloudinary getCloudinary() {

        return new Cloudinary(App.getContext().getString(R.string.cloudinary_url));
    }
}

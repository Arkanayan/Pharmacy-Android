package bankura.pharmacy.pharmacyapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;

import java.util.UUID;

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

    public static String generateOrderId() {

        //generate random uuid for order
        String randomUuid = UUID.randomUUID().toString();
        String orderId = "OD" + randomUuid.substring(randomUuid.length() - 10).toUpperCase();

        return orderId;

    }

    public static String getImageLowerUrl(String publidId, @Nullable Cloudinary cloudinary) {

        Cloudinary cloud = (cloudinary != null) ? cloudinary : getCloudinary();

        return cloud.url()
                .transformation(new Transformation().width(0.3).quality(30).crop("scale"))
                .generate(publidId);
    }
}

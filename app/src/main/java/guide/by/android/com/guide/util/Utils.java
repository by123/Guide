package guide.by.android.com.guide.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import static android.R.string.no;

/**
 * Created by by.huang on 2016/11/18.
 */

public class Utils {

    public static void showInputMethod(Activity activity) {
        if (activity == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
    }


    public static void hideInputMethod(Activity activity) {
        if (activity == null) {
            return;
        }
        View view = activity.getWindow().getDecorView();
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void setTTF (TextView textView,Context context, String name)
    {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/"+ name);
        textView.setTypeface(typeface);
    }

}

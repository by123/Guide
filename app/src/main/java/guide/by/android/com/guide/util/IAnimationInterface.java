package guide.by.android.com.guide.util;

import android.view.View;

/**
 * Created by by.huang on 2016/11/17.
 */

public interface IAnimationInterface {

    void OnAnimationHideStart(View view);
    void OnAnimationHideCompelete(View view);
    void OnAnimationShowStart(View view);
    void OnAnimationShowCompelete(View view);
}

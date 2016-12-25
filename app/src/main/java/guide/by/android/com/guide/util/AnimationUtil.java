package guide.by.android.com.guide.util;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by by.huang on 2016/11/17.
 */

public class AnimationUtil {

    private static AnimationUtil mIntance;

    public static AnimationUtil getInstance() {

        if (mIntance == null) {
            synchronized (AnimationUtil.class) {
                if (mIntance == null) {
                    mIntance = new AnimationUtil();
                }
            }
        }
        return mIntance;
    }


    public void AnimAlphaHide(final View hideview, final IAnimationInterface mIAnimationInterface) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(hideview, "alpha", 1, 0));
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (mIAnimationInterface != null) {
                    mIAnimationInterface.OnAnimationHideStart(hideview);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (mIAnimationInterface != null) {
                    mIAnimationInterface.OnAnimationHideCompelete(hideview);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();

    }


    public void AnimAlphaShow(final View showview,final IAnimationInterface mIAnimationInterface) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(showview, "alpha", 0, 1));
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (mIAnimationInterface != null) {
                    mIAnimationInterface.OnAnimationShowStart(showview);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (mIAnimationInterface != null) {
                    mIAnimationInterface.OnAnimationShowCompelete(showview);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();

    }


}

package com.zl.basesizexian;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * create user zhaolei  代码无bug
 * 时间:2017/10/18
 */

public class PpView extends RelativeLayout {

    private Drawable[] drawable;
    private int dWidth;
    private int dHeight;
    private LayoutParams lp;
    private int mWidth;
    private int mHeight;
    private Random random;
    private Interpolator[] interpolators;

    private Interpolator line = new LinearInterpolator();//线性
    private Interpolator acc = new AccelerateInterpolator();//加速
    private Interpolator dce = new DecelerateInterpolator();//减速
    private Interpolator accdec = new AccelerateDecelerateInterpolator();//先加速后减速

    public PpView(Context context) {
        super(context);
        init();
    }

    public PpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PpView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        random = new Random();
        drawable = new Drawable[3];

        drawable[0] = getResources().getDrawable(R.drawable.pl_blue);
        drawable[1] = getResources().getDrawable(R.drawable.pl_red);
        drawable[2] = getResources().getDrawable(R.drawable.pl_yellow);

        dWidth = drawable[1].getIntrinsicWidth();
        dHeight = drawable[1].getIntrinsicWidth();


        LayoutParams lp = new LayoutParams(dWidth, dHeight);
        lp.addRule(CENTER_HORIZONTAL, TRUE);
        lp.addRule(ALIGN_PARENT_BOTTOM, TRUE);

        // 初始化插补器
        interpolators = new Interpolator[4];
        interpolators[0] = line;
        interpolators[1] = acc;
        interpolators[2] = dce;
        interpolators[3] = accdec;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    public void addHeat() {
        //设置一张图片
        ImageView image = new ImageView(getContext());
        //具体图片
        image.setImageDrawable(drawable[random.nextInt(3)]);
        //设置图片位置
        image.setLayoutParams(lp);
        addView(image);
        //获取动画，合集 里面有估值器 里面的赛贝尔曲线 ，根据监听设置路径，范围飘的从下向上
        Animator set = getAnimator(image);
        //
        set.addListener(new AnimEndListener(image));
        set.start();

    }


    private Animator getAnimator(View target) {
        AnimatorSet set = getEnterAnimtor(target);

        ValueAnimator bezierValueAnimator = getBezierValueAnimator(target);

        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playSequentially(set);
        finalSet.playSequentially(set, bezierValueAnimator);
        finalSet.setInterpolator(interpolators[random.nextInt(4)]);
        finalSet.setTarget(target);
        return finalSet;
    }


    private AnimatorSet getEnterAnimtor(final View target) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(target, View.ALPHA, 0.2f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(target, View.SCALE_X, 0.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(target, View.SCALE_Y, 0.2f, 1f);
        AnimatorSet enter = new AnimatorSet();
        enter.setDuration(500);
        enter.setInterpolator(new LinearInterpolator());
        enter.playTogether(alpha, scaleX, scaleY);
        enter.setTarget(target);
        return enter;
    }
    //向上飘的动画
    private ValueAnimator getBezierValueAnimator(View target) {

        //初始化一个贝塞尔计算器- - 传入
        BezierEvaluator evaluator = new BezierEvaluator(getPointF(2), getPointF(1));

        //这里最好画个图 理解一下 传入了起点 和 终点 然后传入了估值器  从下向上然后算出第二个点高出第一个点
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, new PointF((mWidth - dWidth) / 2, mHeight - dHeight), new PointF(random.nextInt(getWidth()), 0));
        animator.addUpdateListener(new BezierListener(target));
        animator.setTarget(target);
        animator.setDuration(3000);
        return animator;
    }
    /**
     * 获取中间的两个 点
     *
     * @param scale
     */
    private PointF getPointF(int scale) {
       //初始化话那个类
        PointF pointF = new PointF();
        //x轴的话可以就是左右随机  这里就是宽度范围
        pointF.x = random.nextInt((mWidth - 100));//减去100 是为了控制 x轴活动范围,看效果 随意~~
        //再Y轴上 为了确保第二个点 在第一个点之上,我把Y分成了上下两半 这样动画效果好一些  也可以用其他方法
        pointF.y = random.nextInt((mHeight - 100)) / scale;
        return pointF;
    }
    private class BezierListener implements ValueAnimator.AnimatorUpdateListener {

        private View target;

        public BezierListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            //这里获取到贝塞尔曲线计算出来的的x y值 赋值给view 这样就能让爱心随着曲线走啦
            PointF pointF = (PointF) animation.getAnimatedValue();
            target.setX(pointF.x);
            target.setY(pointF.y);
            // 这里顺便做一个alpha动画
            target.setAlpha(1 - animation.getAnimatedFraction());
        }
    }


    private class AnimEndListener extends AnimatorListenerAdapter {
        private View target;

        public AnimEndListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            //因为不停的add 导致子view数量只增不减,所以在view动画结束后remove掉
            removeView((target));
        }
    }
}

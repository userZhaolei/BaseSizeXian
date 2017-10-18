package com.zl.basesizexian;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * create user zhaolei  代码无bug
 * 时间:2017/10/18
 */

public class BezierEvaluator implements TypeEvaluator<PointF> {
    //经过点的坐标
    private PointF pointF1;
    //传入的开始点的坐标
    private PointF pointF2;


    public BezierEvaluator(PointF pointF1,PointF pointF2){
        this.pointF1 = pointF1;
        this.pointF2 = pointF2;
    }
    @Override
    public PointF evaluate(float time, PointF startValue,
                           PointF endValue) {
        //按照三阶的贝塞尔曲线  的那个1-t
        float timeLeft = 1.0f - time;
        PointF point = new PointF();//结果

        //按照三阶的贝塞尔曲线求出x轴的坐标
        point.x = timeLeft * timeLeft * timeLeft * (startValue.x)
                + 3 * timeLeft * timeLeft * time * (pointF1.x)
                + 3 * timeLeft * time * time * (pointF2.x)
                + time * time * time * (endValue.x);

        point.y = timeLeft * timeLeft * timeLeft * (startValue.y)
                + 3 * timeLeft * timeLeft * time * (pointF1.y)
                + 3 * timeLeft * time * time * (pointF2.y)
                + time * time * time * (endValue.y);
        //返回具体的类
        return point;
    }
}

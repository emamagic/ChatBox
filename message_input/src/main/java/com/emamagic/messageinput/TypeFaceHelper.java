package com.emamagic.messageinput;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.widget.TextView;

public class TypeFaceHelper {

    private static Typeface mTypeFaceNormal;
    private static Typeface mTypeFaceBold;

    public static void generateTypeface(Context context) {
        mTypeFaceNormal = Typeface.createFromAsset(
                context.getAssets(),
                "fonts/IRANSansMobile.ttf");
    }

    public static void applyTypeface(TextView textView) {
        textView.setTypeface(textView.getTypeface() == null
                || textView.getTypeface().getStyle() == Typeface.NORMAL
                ? mTypeFaceNormal : mTypeFaceBold);
    }

    public static void applyTypeface(TextPaint textPaint) {
        textPaint.setFakeBoldText(true);
        textPaint.setTypeface(mTypeFaceBold);
    }

    public static Typeface getTypeface() {
        return mTypeFaceNormal;
    }

    public static void applyNormalTypeface(TextView textView) {
        textView.setTypeface(mTypeFaceNormal);
    }

    public static void applyBoldTypeface(TextView textView) {
        textView.setTypeface(mTypeFaceBold);
    }
}

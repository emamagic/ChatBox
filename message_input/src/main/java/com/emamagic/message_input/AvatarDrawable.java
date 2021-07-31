package com.emamagic.message_input;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.emamagic.chatbox.Logger;
import com.emamagic.chatbox.ViewHelper;

import java.util.Locale;

public class AvatarDrawable extends Drawable {

    private static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static int[] arrColors = {0xffe56555, 0xfff28c48, 0xffeec764, 0xff76c84d, 0xff5fbed5, 0xff549cdd, 0xff8e85ee, 0xfff2749a};
    private static int[] arrColorsProfiles = {0xFF94D6D7, 0xFFAF9FE7, 0xFF7CC863, 0xFF67AADB, 0xFFEE7CAE, 0xFFE07177, 0xFFFAA876, 0xFFF1B7BA, 0xFFF3C2A2};
    private String firstName;
    private String lastName;

    private int color;
    private String text;

    public AvatarDrawable(String firstName, String lastName) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        init();
    }

    public AvatarDrawable(String displayName) {
        this(displayName, null);
    }


    private static int getColorIndex(int id) {
        if (id >= 0 && id < 8) {
            return id;
        }
        try {
            String str;
            if (id >= 0) {
                str = String.format(Locale.US, "%d%d", id, 1/*UserConfig.getClientUserId()*/);
            } else {
                str = String.format(Locale.US, "%d", id);
            }
            if (str.length() > 15) {
                str = str.substring(0, 15);
            }
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(str.getBytes());
            int b = digest[Math.abs(id % 16)];
            if (b < 0) {
                b += 256;
            }
            return Math.abs(b) % arrColors.length;
        } catch (Exception e) {
            Logger.exception(e);
        }
        return id % arrColors.length;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height();
        paint.setColor(color);
        canvas.save();
        canvas.translate(bounds.left, bounds.top);
        canvas.drawCircle(width / 2, width / 2, width / 2, paint);

        StaticLayout textLayout = getTextLayout();
        if (textLayout != null) {
            canvas.translate((width - textLayout.getLineWidth(0)) / 2 - textLayout.getLineLeft(0),
                    (height - textLayout.getHeight()) / 2);
            textLayout.draw(canvas);
        }
        canvas.restore();
    }

    /*
     * @return the textLayout to be drawn in canvas,textPaint must be adjusted first
     */
    @Nullable
    private StaticLayout getTextLayout() {
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(0xffffffff);
        TypeFaceHelper.applyTypeface(textPaint);
        textPaint.setTextSize(calculateTextSize(textPaint));
        try {
            return new StaticLayout(text,
                    textPaint,
                    ViewHelper.dpToPx(textPaint.measureText(text)),
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f, 0.0f, true);

        } catch (Exception e) {
            Logger.exception(e);
        }
        return null;
    }

    /*
     * @return the text size based on boundaries of the circle
     */
    private float calculateTextSize(TextPaint paint) {
        Rect viewBounds = getBounds();
        Rect desiredBound = new Rect(viewBounds.left, viewBounds.top,
                viewBounds.right - ViewHelper.dpToPx(viewBounds.width() / 7),
                viewBounds.bottom - ViewHelper.dpToPx(viewBounds.height() / 7));
        Rect textBounds = new Rect();
        float textSize = ViewHelper.dpToPx(desiredBound.width());
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), textBounds);

        while (textBounds.width() > desiredBound.width() || textBounds.height() > desiredBound.height()) {
            textSize--;
            paint.setTextSize(textSize);
            paint.getTextBounds(text, 0, text.length(), textBounds);
        }

        return textSize;
    }

    @Override
    public void setAlpha(int i) {
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    public void setColor(int value) {
        color = value;
    }

    public void setProfile(boolean isProfile) {
        int id = getId(firstName, lastName);
        if (isProfile) {
            color = arrColorsProfiles[getColorIndex(id)];
        } else {
            color = arrColors[getColorIndex(id)];
        }
    }

    private void init() {
        color = arrColors[getColorIndex(getId(firstName, lastName))];
        if (firstName == null || firstName.length() == 0) {
            firstName = lastName;
            lastName = null;
        }

        StringBuilder stringBuilder = new StringBuilder(5);
        stringBuilder.setLength(0);
        if (firstName != null && firstName.length() > 0) {
            stringBuilder.append(firstName.substring(0, 1));
        }
        if (lastName != null && lastName.length() > 0) {
            stringBuilder.append("\u200C");
            stringBuilder.append(lastName.substring(0, 1));
        } else if (firstName != null && firstName.length() > 0) {
            for (int a = firstName.length() - 1; a >= 0; a--) {
                if (firstName.charAt(a) == ' ') {
                    if (a != firstName.length() - 1 && firstName.charAt(a + 1) != ' ') {
                        stringBuilder.append("\u200C");
                        stringBuilder.append(firstName.substring(a + 1, a + 2));
                        break;
                    }
                }
            }
        }

        text = stringBuilder.toString().toUpperCase();
    }

    private int getId(String str1, String str2) {
        if (InputHelper.isEmpty(str1)) {
            str1 = " ";
        }
        if (InputHelper.isEmpty(str2)) {
            str2 = " ";
        }
        return ((int) str1.charAt(0) + (int) str2.charAt(0)) % arrColorsProfiles.length;
    }
}

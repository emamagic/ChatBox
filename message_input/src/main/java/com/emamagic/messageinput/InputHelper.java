package com.emamagic.messageinput;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.emamagic.chatbox.App;
import com.emamagic.chatbox.Logger;
import com.google.android.material.textfield.TextInputLayout;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class InputHelper {

    public static String getString(@StringRes int drawableRes) {
        return App.getInstance().getString(drawableRes);
    }

    private static boolean isWhiteSpaces(@Nullable String s) {
        return s != null && s.matches("\\s+");
    }

    public static boolean isEmpty(@Nullable List list) {
        return list == null
                || list.isEmpty();
    }

    public static boolean isEmpty(@Nullable String text) {
        return text == null
                || TextUtils.isEmpty(text)
                || isWhiteSpaces(text)
                || text.equalsIgnoreCase("null");
    }

    public static boolean isEmpty(@Nullable Object text) {
        return text == null || isEmpty(text.toString());
    }

    public static boolean isEmpty(@Nullable EditText text) {
        return text == null || isEmpty(text.getText().toString());
    }

    public static boolean isEmpty(@Nullable TextView text) {
        return text == null || isEmpty(text.getText().toString());
    }

    public static boolean isEmpty(@Nullable TextInputLayout txt) {
        return txt == null || isEmpty(txt.getEditText());
    }

    public static String toString(@NonNull EditText editText) {
        return editText.getText().toString();
    }

    public static String toString(@NonNull TextView editText) {
        return editText.getText().toString();
    }

    public static String toString(@NonNull TextInputLayout textInputLayout) {
        return textInputLayout.getEditText() != null ? toString(textInputLayout.getEditText()) : "";
    }

    @NonNull
    public static String toString(@Nullable Object object) {
        return !isEmpty(object) ? object.toString() : "";
    }

    public static long toLong(@NonNull TextView textView) {
        return toLong(toString(textView));
    }

    public static long toLong(@NonNull String text) {
        if (!isEmpty(text)) {
            try {
                return Long.valueOf(text.replaceAll("[^0-9]", ""));
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }

    public static int getSafeIntId(long id) {
        return id > Integer.MAX_VALUE ? (int) (id - Integer.MAX_VALUE) : (int) id;
    }

    public static void insertAtCursor(EditText editText, String text) {
        String oriContent = editText.getText().toString();
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        Logger.e(start, end);
        if (start >= 0 && end > 0 && start != end) {
            editText.setText(editText.getText().replace(start, end, text));
        } else {
            int index = editText.getSelectionStart() >= 0 ? editText.getSelectionStart() : 0;
            Logger.e(start, end, index);
            StringBuilder builder = new StringBuilder(oriContent);
            builder.insert(index, text);
            editText.setText(builder.toString());
            editText.setSelection(index + text.length());
        }
    }

    public static boolean isPhoneNumValid(String phoneNum) {
        return !isEmpty(phoneNum)
                && phoneNum.length() == 10
                && phoneNum.substring(0, 1).equals("9");
    }

    public static boolean isOtpValid(String otp) {
        return !isEmpty(otp)
                && otp.length() == 5;
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isTownSquare(String displayName) {
        return InputHelper.isEmpty(displayName)
                && (displayName.toLowerCase().equals("Town Square".toLowerCase()) ||
                displayName.toLowerCase().equals("Town-Square".toLowerCase()));
    }


}

package com.toong.androidcurrencyedittext;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by PhanVanLinh on 25/07/2017.
 * phanvanlinh.94vn@gmail.com
 */

public class CurrencyEditText extends android.support.v7.widget.AppCompatEditText {
    private static String prefix = "VND ";
    private static final int MAX_LENGTH = 20;
    private static final int MAX_DECIMAL = 3;
    private CurrencyTextWatcher mCurrencyTextWatcher = new CurrencyTextWatcher(this, prefix);

    public CurrencyEditText(Context context) {
        this(context, null);
    }

    public CurrencyEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.editTextStyle);
    }

    public CurrencyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        this.setHint(prefix);
        this.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_LENGTH) });
    }

    private static class CurrencyTextWatcher implements TextWatcher {
        private final EditText mEditText;
        private String prevString;
        private String prefix;

        CurrencyTextWatcher(EditText editText, String prefix) {
            mEditText = editText;
            this.prefix = prefix;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = editable.toString();
            if (str.length() < prefix.length()) {
                mEditText.setText(prefix);
                mEditText.setSelection(prefix.length());
                return;
            }
            if (str.equals(prefix)) {
                return;
            }
            // cleanString this the string which not contain prefix and ,
            String cleanString = str.replace(prefix, "").replaceAll("[,]", "");
            // for prevent afterTextChanged recursive call
            if (cleanString.equals(prevString) || cleanString.isEmpty()) {
                return;
            }
            prevString = cleanString;

            String formattedString;
            if (cleanString.contains(".")) {
                formattedString = formatDecimal(cleanString);
            } else {
                formattedString = formatInteger(cleanString);
            }
            mEditText.removeTextChangedListener(this); // Remove listener
            mEditText.setText(formattedString);
            if (formattedString.length() <= MAX_LENGTH) {
                mEditText.setSelection(formattedString.length());
            }else{
                mEditText.setSelection(MAX_LENGTH);
            }
            mEditText.addTextChangedListener(this); // Add back the listener
        }

        private String formatInteger(String str) {
            BigDecimal parsed = new BigDecimal(str);
            DecimalFormat formatter;
            formatter = new DecimalFormat(prefix + "#,###");
            return formatter.format(parsed);
        }

        private String formatDecimal(String str) {
            if (str.equals(".")) {
                return prefix + ".";
            }
            BigDecimal parsed = new BigDecimal(str);
            DecimalFormat formatter;
            // example patter VND #,###.00
            formatter = new DecimalFormat(prefix + "#,###." + getDecimalPattern(str));
            return formatter.format(parsed);
        }

        /**
         * It will return suitable pattern for format decimal
         * For example: 10.2 -> return 0 | 10.23 -> return 00, | 10.235 -> return 000
         */
        private String getDecimalPattern(String str) {
            int decimalCount = str.length() - 1 - str.indexOf(".");
            String decimalPattern = "";
            for (int i = 0; i < decimalCount && i < MAX_DECIMAL; i++) {
                decimalPattern += "0";
            }
            return decimalPattern;
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            this.addTextChangedListener(mCurrencyTextWatcher);
            if (getText().toString().isEmpty()) {
                setText(prefix);
            }
        } else {
            this.removeTextChangedListener(mCurrencyTextWatcher);
            if (getText().toString().equals(prefix)) {
                setText("");
            }
        }
    }
}

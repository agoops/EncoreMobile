package Fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: babakpourkazemi
 * Date: 9/7/13
 * Time: 7:50 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class BaseListElement  {
    private Drawable icon;
    private String text1;
    private String text2;
    private int requestCode;
    private BaseAdapter adapter;

    public BaseListElement(Drawable icon, String text1, String text2, int requestCode) {
        super();
        this.icon = icon;
        this.text1 = text1;
        this.text2 = text2;
        this.requestCode = requestCode;
    }

    protected abstract View.OnClickListener getOnClickListener();

    protected void onActivityResult(Intent data) {}

    protected void onSaveInstanceState(Bundle bundle) {}

    protected boolean restoreState(Bundle savedState) {
        return false;
    }

    protected void notifyDataChanged() {
        adapter.notifyDataSetChanged();
    }

    public void setBaseAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getText1() {
        return text1;
    }

    public String getText2() {
        return text2;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setIcon(Drawable newIcon) {
        this.icon = newIcon;
    }

    public void setText1(String newText1) {
        this.text1 = newText1;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void setText2(String newText2) {
        this.text2 = newText2;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void setRequestCode(int newRequestCode) {
        this.requestCode = newRequestCode;
    }
}
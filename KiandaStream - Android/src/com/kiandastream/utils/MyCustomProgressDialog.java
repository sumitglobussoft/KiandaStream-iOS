package com.kiandastream.utils;



import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.kiandastream.R;



public class MyCustomProgressDialog extends ProgressDialog {
  private AnimationDrawable animation;

  public static ProgressDialog ctor(Context context) {
    MyCustomProgressDialog dialog = new MyCustomProgressDialog(context);
    dialog.setIndeterminate(true);
    dialog.setCancelable(false);
    return dialog;
  }

  public MyCustomProgressDialog(Context context) {
    super(context);
  }

  public MyCustomProgressDialog(Context context, int theme) {
    super(context, theme);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.custom_progress_dialog);

    ImageView la = (ImageView) findViewById(R.id.animation);
    la.setBackgroundResource(R.anim.progress_dialog_anim);
    animation = (AnimationDrawable) la.getBackground();
  }

  @Override
  public void show() {
    super.show();
    animation.start();
  }

  @Override
  public void dismiss() {
    super.dismiss();
    animation.stop();
  }
}

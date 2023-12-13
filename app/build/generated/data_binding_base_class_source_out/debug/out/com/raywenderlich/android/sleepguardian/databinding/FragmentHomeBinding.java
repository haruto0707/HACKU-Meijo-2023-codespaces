// Generated by view binder compiler. Do not edit!
package com.raywenderlich.android.sleepguardian.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.raywenderlich.android.sleepguardian.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentHomeBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final Button cancelAlarmBtn;

  @NonNull
  public final Button selectTimeBtn;

  @NonNull
  public final TextView selectedTime;

  @NonNull
  public final Button setAlarmBtn;

  private FragmentHomeBinding(@NonNull FrameLayout rootView, @NonNull Button cancelAlarmBtn,
      @NonNull Button selectTimeBtn, @NonNull TextView selectedTime, @NonNull Button setAlarmBtn) {
    this.rootView = rootView;
    this.cancelAlarmBtn = cancelAlarmBtn;
    this.selectTimeBtn = selectTimeBtn;
    this.selectedTime = selectedTime;
    this.setAlarmBtn = setAlarmBtn;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentHomeBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentHomeBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_home, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentHomeBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cancelAlarmBtn;
      Button cancelAlarmBtn = ViewBindings.findChildViewById(rootView, id);
      if (cancelAlarmBtn == null) {
        break missingId;
      }

      id = R.id.selectTimeBtn;
      Button selectTimeBtn = ViewBindings.findChildViewById(rootView, id);
      if (selectTimeBtn == null) {
        break missingId;
      }

      id = R.id.selectedTime;
      TextView selectedTime = ViewBindings.findChildViewById(rootView, id);
      if (selectedTime == null) {
        break missingId;
      }

      id = R.id.setAlarmBtn;
      Button setAlarmBtn = ViewBindings.findChildViewById(rootView, id);
      if (setAlarmBtn == null) {
        break missingId;
      }

      return new FragmentHomeBinding((FrameLayout) rootView, cancelAlarmBtn, selectTimeBtn,
          selectedTime, setAlarmBtn);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}

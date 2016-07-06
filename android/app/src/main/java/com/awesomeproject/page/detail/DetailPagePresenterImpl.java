package com.awesomeproject.page.detail;

import android.support.annotation.NonNull;
import com.awesomeproject.JSEventDispatcher;
import com.awesomeproject.MyNavigator.NavTag;
import com.awesomeproject.page.JSPagePresenterImplBase;
import java.util.Arrays;

public class DetailPagePresenterImpl //
    extends JSPagePresenterImplBase<DetailPageView> implements DetailPagePresenter {

  public DetailPagePresenterImpl( //
      @NonNull JSEventDispatcher eventDispatcher, //
      @NonNull DetailPageView view, //
      @NonNull NavTag tag) {
    super(eventDispatcher, view, tag);

    registerSetters(Arrays.asList( //
        new JsSetter1<Integer>("setButtonColor", JS_TO_CLR) {
          @Override
          public void set(Integer color) {
            view().setButtonColor(color);
          }
        }, //
        new JsSetter1<String>("setButtonText", JS_TO_STR) {
          @Override
          public void set(String text) {
            view().setButtonText(text);
          }
        }, //
        new JsSetter1<String>("setLabelText", JS_TO_STR) {
          @Override
          public void set(String text) {
            view().setLabelText(text);
          }
        }));
  }

  @Override
  public void buttonClicked() {
    dispatchToJs("buttonClicked");
  }

  @Override
  public void labelClicked() {
    dispatchToJs("labelClicked");
  }
}

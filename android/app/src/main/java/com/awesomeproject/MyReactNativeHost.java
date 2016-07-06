package com.awesomeproject;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.LifecycleState;
import com.facebook.react.LiteReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import java.util.Arrays;
import java.util.List;

public class MyReactNativeHost extends ReactNativeHost {

  private final MyApp app;
  private final MyReactPackage myReactPackage;

  public MyReactNativeHost(MyApp application, MyReactPackage myReactPackage) {
    super(application);
    this.app = application;
    this.myReactPackage = myReactPackage;
  }

  @Override
  protected boolean getUseDeveloperSupport() {
    return BuildConfig.DEBUG;
  }

  @Override
  protected List<ReactPackage> getPackages() {
    return Arrays.asList(myReactPackage, new MainReactPackage());
  }

  @Override
  public LiteReactInstanceManager getReactInstanceManager() {
    return (LiteReactInstanceManager) super.getReactInstanceManager();
  }

  @Override
  protected LiteReactInstanceManager createReactInstanceManager() {
    LiteReactInstanceManager.Builder builder = LiteReactInstanceManager.liteBuilder()
        .setApplication(app)
        .setJSMainModuleName(getJSMainModuleName())
        .setUseDeveloperSupport(getUseDeveloperSupport())
        .setInitialLifecycleState(LifecycleState.BEFORE_CREATE);

    for (ReactPackage reactPackage : getPackages()) {
      builder.addPackage(reactPackage);
    }

    String jsBundleFile = getJSBundleFile();
    if (jsBundleFile != null) {
      builder.setJSBundleFile(jsBundleFile);
    } else {
      builder.setBundleAssetName(Assertions.assertNotNull(getBundleAssetName()));
    }

    return builder.build();
  }
}

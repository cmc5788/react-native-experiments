package com.facebook.react;

import android.app.Activity;
import android.app.Application;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.NativeModuleCallExceptionHandler;
import com.facebook.react.bridge.NotThreadSafeBridgeIdleDebugListener;
import com.facebook.react.devsupport.DevSupportManager;
import com.facebook.react.devsupport.RedBoxHandler;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.uimanager.UIImplementationProvider;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public abstract class LiteReactInstanceManager extends ReactInstanceManager {

  public abstract void attachMeasuredRootView(LiteReactRootView rootView);

  public abstract void detachRootView(LiteReactRootView rootView);

  public static Builder liteBuilder() {
    return new Builder();
  }

  public static class Builder {

    protected final List<ReactPackage> mPackages = new ArrayList<>();

    protected @Nullable String mJSBundleFile;
    protected @Nullable String mJSMainModuleName;
    protected @Nullable NotThreadSafeBridgeIdleDebugListener mBridgeIdleDebugListener;
    protected @Nullable Application mApplication;
    protected boolean mUseDeveloperSupport;
    protected @Nullable LifecycleState mInitialLifecycleState;
    protected @Nullable UIImplementationProvider mUIImplementationProvider;
    protected @Nullable NativeModuleCallExceptionHandler mNativeModuleCallExceptionHandler;
    protected @Nullable JSCConfig mJSCConfig;
    protected @Nullable Activity mCurrentActivity;
    protected @Nullable DefaultHardwareBackBtnHandler mDefaultHardwareBackBtnHandler;
    protected @Nullable RedBoxHandler mRedBoxHandler;
    protected boolean mUseNewBridge;

    protected Builder() {
    }

    /**
     * Sets a provider of {@link com.facebook.react.uimanager.UIImplementation}.
     * Uses default provider if null is passed.
     */
    public Builder setUIImplementationProvider(
        @Nullable UIImplementationProvider uiImplementationProvider) {
      mUIImplementationProvider = uiImplementationProvider;
      return this;
    }

    /**
     * Name of the JS bundle file to be loaded from application's raw assets.
     * Example: {@code "index.android.js"}
     */
    public Builder setBundleAssetName(String bundleAssetName) {
      return this.setJSBundleFile(bundleAssetName == null
          ? null
          : "assets://" + bundleAssetName);
    }

    /**
     * Path to the JS bundle file to be loaded from the file system.
     *
     * Example: {@code "assets://index.android.js" or "/sdcard/main.jsbundle"}
     */
    public Builder setJSBundleFile(String jsBundleFile) {
      mJSBundleFile = jsBundleFile;
      return this;
    }

    /**
     * Path to your app's main module on the packager server. This is used when
     * reloading JS during development. All paths are relative to the root folder
     * the packager is serving files from.
     * Examples:
     * {@code "index.android"} or
     * {@code "subdirectory/index.android"}
     */
    public Builder setJSMainModuleName(String jsMainModuleName) {
      mJSMainModuleName = jsMainModuleName;
      return this;
    }

    public Builder addPackage(ReactPackage reactPackage) {
      mPackages.add(reactPackage);
      return this;
    }

    public Builder setBridgeIdleDebugListener(
        NotThreadSafeBridgeIdleDebugListener bridgeIdleDebugListener) {
      mBridgeIdleDebugListener = bridgeIdleDebugListener;
      return this;
    }

    /**
     * Required. This must be your {@code Application} instance.
     */
    public Builder setApplication(Application application) {
      mApplication = application;
      return this;
    }

    public Builder setCurrentActivity(Activity activity) {
      mCurrentActivity = activity;
      return this;
    }

    public Builder setDefaultHardwareBackBtnHandler(
        DefaultHardwareBackBtnHandler defaultHardwareBackBtnHandler) {
      mDefaultHardwareBackBtnHandler = defaultHardwareBackBtnHandler;
      return this;
    }

    /**
     * When {@code true}, developer options such as JS reloading and debugging are enabled.
     * Note you still have to call {@link #showDevOptionsDialog} to show the dev menu,
     * e.g. when the device Menu button is pressed.
     */
    public Builder setUseDeveloperSupport(boolean useDeveloperSupport) {
      mUseDeveloperSupport = useDeveloperSupport;
      return this;
    }

    /**
     * Sets the initial lifecycle state of the host. For example, if the host is already resumed at
     * creation time, we wouldn't expect an onResume call until we get an onPause call.
     */
    public Builder setInitialLifecycleState(LifecycleState initialLifecycleState) {
      mInitialLifecycleState = initialLifecycleState;
      return this;
    }

    /**
     * Set the exception handler for all native module calls. If not set, the default
     * {@link DevSupportManager} will be used, which shows a redbox in dev mode and rethrows
     * (crashes the app) in prod mode.
     */
    public Builder setNativeModuleCallExceptionHandler(NativeModuleCallExceptionHandler handler) {
      mNativeModuleCallExceptionHandler = handler;
      return this;
    }

    public Builder setJSCConfig(JSCConfig jscConfig) {
      mJSCConfig = jscConfig;
      return this;
    }

    public Builder setRedBoxHandler(@Nullable RedBoxHandler redBoxHandler) {
      mRedBoxHandler = redBoxHandler;
      return this;
    }

    public Builder setUseNewBridge() {
      mUseNewBridge = true;
      return this;
    }

    /**
     * Instantiates a new {@link ReactInstanceManagerImpl}.
     * Before calling {@code build}, the following must be called:
     * <ul>
     * <li> {@link #setApplication}
     * <li> {@link #setCurrentActivity} if the activity has already resumed
     * <li> {@link #setDefaultHardwareBackBtnHandler} if the activity has already resumed
     * <li> {@link #setJSBundleFile} or {@link #setJSMainModuleName}
     * </ul>
     */
    public LiteReactInstanceManager build() {
      Assertions.assertCondition(mUseDeveloperSupport || mJSBundleFile != null,
          "JS Bundle File has to be provided when dev support is disabled");

      Assertions.assertCondition(mJSMainModuleName != null || mJSBundleFile != null,
          "Either MainModuleName or JS Bundle File needs to be provided");

      if (mUIImplementationProvider == null) {
        // create default UIImplementationProvider if the provided one is null.
        mUIImplementationProvider = new UIImplementationProvider();
      }

      if (mUseNewBridge) {
        return new LiteXReactInstanceManagerImpl(Assertions.assertNotNull(mApplication,
            "Application property has not been set with this builder"), mCurrentActivity,
            mDefaultHardwareBackBtnHandler, mJSBundleFile, mJSMainModuleName, mPackages,
            mUseDeveloperSupport, mBridgeIdleDebugListener,
            Assertions.assertNotNull(mInitialLifecycleState, "Initial lifecycle state was not set"),
            mUIImplementationProvider, mNativeModuleCallExceptionHandler, mJSCConfig,
            mRedBoxHandler);
      } else {
        return new LiteReactInstanceManagerImpl(Assertions.assertNotNull(mApplication,
            "Application property has not been set with this builder"), mCurrentActivity,
            mDefaultHardwareBackBtnHandler, mJSBundleFile, mJSMainModuleName, mPackages,
            mUseDeveloperSupport, mBridgeIdleDebugListener,
            Assertions.assertNotNull(mInitialLifecycleState, "Initial lifecycle state was not set"),
            mUIImplementationProvider, mNativeModuleCallExceptionHandler, mJSCConfig,
            mRedBoxHandler);
      }
    }
  }
}

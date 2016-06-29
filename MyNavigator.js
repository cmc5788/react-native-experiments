import { NativeModules } from 'react-native';

if (!NativeModules.MyNavigator.___transformed) {
  NativeModules.MyNavigator.___transformed = true;
  const navFuncStrMeta = NativeModules.MyNavigator.navigate;
  NativeModules.MyNavigator.navigate = (target, extras, meta) => {
    return navFuncStrMeta(target, extras, JSON.stringify(meta));
  };
}

module.exports = NativeModules.MyNavigator;

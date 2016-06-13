import { NativeModules } from 'react-native';
module.exports = (tag) => ({
  send(data) {
    return NativeModules.JSEventReceiver.sendToView(tag, data);
  }
});

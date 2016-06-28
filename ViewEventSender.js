import { NativeModules } from 'react-native';
module.exports = (tag) => ({
  send(data) {
    return NativeModules.JSEventReceiver.sendToView(tag, data);
  },
  sendBatch(data) {
    console.log(`sendBatch sending ${JSON.stringify(data)}`);
    return NativeModules.JSEventReceiver.sendBatchToView(tag, data);
  }
});
